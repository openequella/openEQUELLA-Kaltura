/*
 * Copyright 2017 Apereo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tle.core.kaltura.service;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Resources;
import com.google.inject.Singleton;
import com.kaltura.client.APIOkRequestsExecutor;
import com.kaltura.client.services.SystemService;
import com.kaltura.client.types.APIException;
import com.kaltura.client.Client;
import com.kaltura.client.Configuration;
import com.kaltura.client.enums.EntryStatus;
import com.kaltura.client.enums.MediaType;
import com.kaltura.client.enums.SessionType;
import com.kaltura.client.enums.UiConfCreationMode;
import com.kaltura.client.enums.UiConfObjType;
import com.kaltura.client.services.MediaService;
import com.kaltura.client.services.SessionService;
import com.kaltura.client.services.UiConfService;
import com.kaltura.client.types.FilterPager;
import com.kaltura.client.types.ListResponse;
import com.kaltura.client.types.MediaEntry;
import com.kaltura.client.types.MediaEntryFilter;
import com.kaltura.client.types.UiConf;
import com.kaltura.client.types.UiConfFilter;
import com.kaltura.client.utils.request.RequestElement;
import com.kaltura.client.utils.response.base.Response;
import com.tle.beans.entity.BaseEntityLabel;
import com.tle.common.Check;
import com.tle.common.EntityPack;
import com.tle.common.beans.exception.InvalidDataException;
import com.tle.common.beans.exception.ValidationError;
import com.tle.common.i18n.LangUtils;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.common.security.PrivilegeTree.Node;
import com.tle.core.entity.EntityEditingBean;
import com.tle.core.entity.EntityEditingSession;
import com.tle.core.entity.service.impl.AbstractEntityServiceImpl;
import com.tle.core.guice.Bind;
import com.tle.core.kaltura.KalturaConstants;
import com.tle.core.kaltura.dao.KalturaDao;
import com.tle.core.security.impl.SecureEntity;
import com.tle.core.security.impl.SecureOnReturn;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.springframework.transaction.annotation.Transactional;

@Bind(KalturaService.class)
@Singleton
@SuppressWarnings("nls")
@SecureEntity("KALTURA")
public class KalturaServiceImpl
    extends AbstractEntityServiceImpl<EntityEditingBean, KalturaServer, KalturaService>
    implements KalturaService {
  protected final KalturaDao kalturaDao;

  private APIOkRequestsExecutor executor = new APIOkRequestsExecutor();

  @Inject
  public KalturaServiceImpl(KalturaDao dao) {
    super(Node.KALTURA, dao);
    kalturaDao = dao;
  }

  // Increase number on the end to replace
  private static final String EQUELLA_KDP_UICONF = "EQUELLA-KDP-UICONF_5.2-114";

  // A cache to protect against excessive calls to Kaltura. There is a small risk of a period when
  // incorrect results could then be returned - however if needs be a restart can navigate it.
  private final Cache<String, UiConf> defaultUiConfCache =
      CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

  private <T> T execute(RequestElement<T> request) throws APIException {
    Response<?> res = executor.execute(request);
    if (res.isSuccess()) {
      return (T) res.results;
    }

    throw res.error;
  }

  @Override
  public ListResponse<MediaEntry> searchMedia(
      Client client, Collection<String> keywords, int page, int limit) {
    MediaEntryFilter ef = new MediaEntryFilter();
    String joined = "";

    if (!Check.isEmpty(keywords) && keywords != null) {
      joined = Joiner.on(',').join(keywords).trim();
      joined = joined.replaceAll("[\\*\\?\\~]", "");
      if (!Objects.equals(joined, "")) {
        ef.setTagsNameMultiLikeOr(joined.toLowerCase());
        ef.setMediaTypeIn(
            Joiner.on(",").join(MediaType.VIDEO.getValue(), MediaType.AUDIO.getValue()));
        ef.setOrderBy("+name");
        FilterPager pager = new FilterPager();
        pager.setPageSize(limit);
        pager.setPageIndex(page);

        try {
          return execute(MediaService.list(ef, pager).build(client));
        } catch (APIException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // No results (or blank search)
    return null;
  }

  @Override
  public Client getKalturaClient(KalturaServer kalturaServer, SessionType type)
      throws APIException {
    int pid = kalturaServer.getPartnerId();
    String secret =
        type.equals(SessionType.ADMIN)
            ? kalturaServer.getAdminSecret()
            : kalturaServer.getUserSecret();

    Configuration kConfig = new Configuration();
    kConfig.setEndpoint(kalturaServer.getEndPoint());

    Client kClient = new Client(kConfig);

    try {
      String ks = execute(SessionService.start(secret, "", type, pid).build(kClient));

      kClient.setSessionId(ks);

      return kClient;
    } catch (Exception ex) {
      throw new APIException();
    }
  }

  @Override
  public boolean isUp(KalturaServer ks) {
    if (ks == null) {
      return false;
    }

    Configuration config = new Configuration();
    config.setEndpoint(ks.getEndPoint());

    try {
      execute(SystemService.ping().build(new Client(config)));
    } catch (APIException e) {
      return false;
    }
    return true;
  }

  @Override
  public MediaEntry getMediaEntry(Client client, String entryId) {
    try {
      return execute(MediaService.get(entryId).build(client));
    } catch (APIException e) {
      throw new RuntimeException(e);
    }
  }

  private UiConf fetchCachedUiConf(KalturaServer ks) {
    return Optional.ofNullable(ks.getUuid()).map(defaultUiConfCache::getIfPresent).orElse(null);
  }

  private UiConf putCachedUiConf(KalturaServer ks, UiConf conf) {
    Optional.ofNullable(ks.getUuid()).ifPresent(uuid -> defaultUiConfCache.put(uuid, conf));

    return conf;
  }

  @Override
  public UiConf getDefaultKdpUiConf(KalturaServer ks) {
    UiConf conf = fetchCachedUiConf(ks);
    if (conf != null) {
      return conf;
    }

    try {
      Client kc = getKalturaClient(ks, SessionType.ADMIN);

      UiConfFilter kcf = new UiConfFilter();
      kcf.setNameLike("EQUELLA-KDP-UICONF_5.2");
      kcf.setObjTypeEqual(UiConfObjType.PLAYER_V3);

      ListResponse<UiConf> uiList = execute(UiConfService.list(kcf).build(kc));

      if (uiList.getTotalCount() == 0) {
        // No Configs add default
        conf = createDefaultKDPUiConf(kc);
      } else if (uiList.getTotalCount() > 1) {
        // More than 1 delete all and add default
        for (UiConf uiConf : uiList.getObjects()) {
          execute(UiConfService.delete(uiConf.getId()).build(kc));
        }

        conf = createDefaultKDPUiConf(kc);
      } else {
        // If there is one is it the latest
        conf = uiList.getObjects().get(0);
        if (!conf.getName().equals(EQUELLA_KDP_UICONF)) {
          execute(UiConfService.delete(conf.getId()).build(kc));
          conf = createDefaultKDPUiConf(kc);
        }
      }

      return putCachedUiConf(ks, conf);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private UiConf createDefaultKDPUiConf(Client client) throws IOException, APIException {
    UiConf equellaKdpUiConf = new UiConf();
    equellaKdpUiConf.setObjType(UiConfObjType.PLAYER_V3);
    equellaKdpUiConf.setCreationMode(UiConfCreationMode.ADVANCED);
    equellaKdpUiConf.setSwfUrl("/flash/kdp3/v3.5.35/kdp3.swf");
    equellaKdpUiConf.setConfFile(readUiConfXml("default_kdp_ui_conf.xml"));
    equellaKdpUiConf.setName(EQUELLA_KDP_UICONF);
    equellaKdpUiConf.setTags("kdp3,player");
    equellaKdpUiConf.setUseCdn(true);

    return execute(UiConfService.add(equellaKdpUiConf).build(client));
  }

  private String readUiConfXml(String filename) throws IOException {
    return Resources.toString(KalturaServiceImpl.class.getResource(filename), Charsets.UTF_8);
  }

  @Override
  public boolean testKalturaSetup(KalturaServer kalturaServer, SessionType type)
      throws APIException {
    Client kclient = null;
    try {
      kclient = getKalturaClient(kalturaServer, type);
      return !Check.isEmpty(kclient.getSessionId());
    } finally {
      if (kclient != null) {
        execute(SessionService.end().build(kclient));
      }
    }
  }

  @Override
  public ListResponse<MediaEntry> getMediaEntries(Client client, List<String> entryIds) {
    try {
      MediaEntryFilter filter = new MediaEntryFilter();
      filter.setIdIn(Joiner.on(',').join(entryIds));
      filter.setStatusIn(
          Joiner.on(',')
              .join(
                  EntryStatus.READY.getValue(),
                  EntryStatus.PENDING.getValue(),
                  EntryStatus.PRECONVERT.getValue()));

      ListResponse<MediaEntry> list = execute(MediaService.list(filter).build(client));

      if (list == null || list.getTotalCount() != entryIds.size()) {
        // Get each individually
        list = new ListResponse<>();
        for (String id : entryIds) {
          list.getObjects().add(execute(MediaService.get(id).build(client)));
        }
      }

      return list;
    } catch (APIException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void doValidation(
      EntityEditingSession<EntityEditingBean, KalturaServer> session,
      KalturaServer ks,
      List<ValidationError> errors) {
    addIfEmpty(errors, LangUtils.isEmpty(ks.getName()), "name");
    addIfEmpty(errors, Check.isEmpty(ks.getEndPoint()), "endpoint");
    addIfEmpty(errors, ks.getPartnerId() == 0, "partnerid");
    addIfInvalid(errors, ks.getPartnerId() == -1, "partnerid");
    addIfInvalid(errors, ks.getSubPartnerId() == -1, "subpartnerid");
    addIfEmpty(errors, Check.isEmpty(ks.getAdminSecret()), "adminsecret");
    addIfEmpty(errors, Check.isEmpty(ks.getUserSecret()), "usersecret");
  }

  private void addIfEmpty(List<ValidationError> errors, boolean empty, String field) {
    if (empty) {
      errors.add(new ValidationError(field, "mandatory"));
    }
  }

  private void addIfInvalid(List<ValidationError> errors, boolean invalid, String field) {
    if (invalid) {
      errors.add(new ValidationError(field, "invalid"));
    }
  }

  @Override
  public boolean canDelete(BaseEntityLabel kalturaServer) {
    return canDelete((Object) kalturaServer);
  }

  private boolean canDelete(Object server) {
    Set<String> privs = new HashSet<String>();
    privs.add(KalturaConstants.PRIV_DELETE_KALTURA);
    return !aclManager.filterNonGrantedPrivileges(server, privs).isEmpty();
  }

  @Override
  public boolean canEdit(BaseEntityLabel kalturaServer) {
    return canEdit((Object) kalturaServer);
  }

  @Override
  public boolean canEdit(KalturaServer kalturaServer) {
    return canEdit((Object) kalturaServer);
  }

  private boolean canEdit(Object kalturaServer) {
    Set<String> privs = new HashSet<String>();
    privs.add(KalturaConstants.PRIV_EDIT_KALTURA);
    return !aclManager.filterNonGrantedPrivileges(kalturaServer, privs).isEmpty();
  }

  @Override
  @SecureOnReturn(priv = KalturaConstants.PRIV_EDIT_KALTURA)
  public KalturaServer getForEdit(String kalturaServerUuid) {
    return getByUuid(kalturaServerUuid);
  }

  @Override
  public EntityPack<KalturaServer> startEdit(KalturaServer entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public KalturaServer stopEdit(EntityPack<KalturaServer> pack, boolean unlock) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String addKalturaServer(KalturaServer kalturaServer) {
    EntityPack<KalturaServer> pack = new EntityPack<KalturaServer>();
    pack.setEntity(kalturaServer);
    return add(pack, false).getUuid();
  }

  @Override
  @Transactional
  public void editKalturaServer(String uuid, KalturaServer newServer) throws InvalidDataException {
    KalturaServer oldServer = getForEdit(uuid);

    // Common details
    editCommonFields(oldServer, newServer);

    // Other details
    oldServer.setEndPoint(newServer.getEndPoint());
    oldServer.setPartnerId(newServer.getPartnerId());
    oldServer.setSubPartnerId(newServer.getSubPartnerId());
    oldServer.setAdminSecret(newServer.getAdminSecret());
    oldServer.setUserSecret(newServer.getUserSecret());
    oldServer.setKdpUiConfId(newServer.getKdpUiConfId());

    // Validate
    validate(null, oldServer);

    kalturaDao.update(oldServer);
  }

  // The method 'throws RuntimeException', but seeing as that throws
  // declaration is technically superfluous, we can omit it to keep Sonar
  // happy
  @Override
  public List<UiConf> getPlayers(KalturaServer ks) {
    Client kc;
    try {
      kc = getKalturaClient(ks, SessionType.ADMIN);
      UiConfFilter uiConfFilter = new UiConfFilter();
      uiConfFilter.setObjTypeEqual(UiConfObjType.PLAYER);
      return execute(UiConfService.list(uiConfFilter).build(kc)).getObjects();
    } catch (APIException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @Transactional
  public void enable(KalturaServer ks, boolean enable) {
    ks.setEnabled(enable);
    kalturaDao.update(ks);
  }

  @Override
  public boolean hasConf(KalturaServer ks, String confId) {
    Client client = null;

    try {
      client = getKalturaClient(ks, SessionType.ADMIN);
      UiConf conf = execute(UiConfService.get(Integer.parseInt(confId)).build(client));
      return conf != null;
    } catch (APIException e) {
      return false;
    }
  }
}
