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

import com.kaltura.client.types.ListResponse;
import java.util.Collection;
import java.util.List;

import com.tle.common.beans.exception.InvalidDataException;
import com.kaltura.client.types.APIException;
import com.kaltura.client.Client;
import com.kaltura.client.enums.SessionType;
import com.kaltura.client.types.MediaEntry;
import com.kaltura.client.services.MediaService.ListMediaBuilder;
import com.kaltura.client.types.UiConf;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.common.kaltura.service.RemoteKalturaService;
import com.tle.core.entity.service.AbstractEntityService;
import com.tle.core.entity.EntityEditingBean;

public interface KalturaService extends AbstractEntityService<EntityEditingBean, KalturaServer>, RemoteKalturaService
{
	ListResponse<MediaEntry> searchMedia(Client client, Collection<String> keywords, int page, int limit);

	MediaEntry getMediaEntry(Client client, String entryId);

	ListResponse<MediaEntry> getMediaEntries(Client client, List<String> entryIds);

	Client getKalturaClient(KalturaServer kalturaServer, SessionType type) throws APIException;

	UiConf getDefaultKdpUiConf(KalturaServer ks);

	boolean testKalturaSetup(KalturaServer kalturaServer, SessionType type) throws APIException;

	String addKalturaServer(KalturaServer kalturaServer) throws InvalidDataException;

	void editKalturaServer(String ksUuid, KalturaServer kalturaServer) throws InvalidDataException;

	KalturaServer getForEdit(String kalturaServerUuid);

	List<UiConf> getPlayers(KalturaServer ks);

	void enable(KalturaServer ks, boolean enable);

  /**
   * Get the configuration of the selected Kaltura player.
   */
  UiConf getUIConfig(KalturaServer ks);

	boolean hasConf(KalturaServer ks, String confId);

	boolean isUp(KalturaServer ks);
}
