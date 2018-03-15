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

import java.util.Collection;
import java.util.List;

import com.tle.common.beans.exception.InvalidDataException;
import com.kaltura.client.KalturaApiException;
import com.kaltura.client.KalturaClient;
import com.kaltura.client.enums.KalturaSessionType;
import com.kaltura.client.types.KalturaMediaEntry;
import com.kaltura.client.types.KalturaMediaListResponse;
import com.kaltura.client.types.KalturaUiConf;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.common.kaltura.service.RemoteKalturaService;
import com.tle.core.entity.service.AbstractEntityService;
import com.tle.core.entity.EntityEditingBean;

public interface KalturaService extends AbstractEntityService<EntityEditingBean, KalturaServer>, RemoteKalturaService
{
	KalturaMediaListResponse searchMedia(KalturaClient client, Collection<String> keywords, int page, int limit);

	KalturaMediaEntry getMediaEntry(KalturaClient client, String entryId);

	KalturaMediaListResponse getMediaEntries(KalturaClient client, List<String> entryIds);

	KalturaClient getKalturaClient(KalturaServer kalturaServer, KalturaSessionType type) throws KalturaApiException;

	KalturaUiConf getDefaultKcwUiConf(KalturaClient client);

	KalturaUiConf getDefaultKdpUiConf(KalturaServer ks);

	boolean testKalturaSetup(KalturaServer kalturaServer, KalturaSessionType type) throws KalturaApiException;

	String addKalturaServer(KalturaServer kalturaServer) throws InvalidDataException;

	void editKalturaServer(String ksUuid, KalturaServer kalturaServer) throws InvalidDataException;

	KalturaServer getForEdit(String kalturaServerUuid);

	List<KalturaUiConf> getPlayers(KalturaServer ks);

	void enable(KalturaServer ks, boolean enable);

	boolean hasConf(KalturaServer ks, String confId);

	boolean isUp(KalturaServer ks);
}
