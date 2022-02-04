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

package com.tle.web.controls.kaltura;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Singleton;
import com.tle.beans.item.attachments.Attachment;
import com.tle.beans.item.attachments.CustomAttachment;
import com.tle.common.kaltura.KalturaUtils;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.core.guice.Bind;
import com.tle.core.item.edit.ItemEditor;
import com.tle.core.item.serializer.AbstractAttachmentSerializer;
import com.tle.core.kaltura.service.KalturaService;
import com.tle.web.api.item.equella.interfaces.beans.EquellaAttachmentBean;
import java.util.Date;
import java.util.Map;
import javax.inject.Inject;

@Bind
@Singleton
public class KalturaAttachmentSerializer extends AbstractAttachmentSerializer
{
	@Inject
	private KalturaService kalturaService;

	@Override
	public EquellaAttachmentBean serialize(Attachment attachment)
	{
		CustomAttachment cattach = (CustomAttachment) attachment;
		KalturaAttachmentBean kbean = new KalturaAttachmentBean();
		kbean.setKalturaServer((String) cattach.getData(KalturaUtils.PROPERTY_KALTURA_SERVER));
		kbean.setMediaId((String) cattach.getData(KalturaUtils.PROPERTY_ENTRY_ID));
		kbean.setTitle((String) cattach.getData(KalturaUtils.PROPERTY_TITLE));
		kbean.setDescription((String) cattach.getData(KalturaUtils.PROPERTY_DESCRIPTION));
		kbean.setThumbUrl((String) cattach.getData(KalturaUtils.PROPERTY_THUMB_URL));
		kbean.setTags((String) cattach.getData(KalturaUtils.PROPERTY_TAGS));
		final Long date = (Long) cattach.getData(KalturaUtils.PROPERTY_DATE);
		if( date != null )
		{
			kbean.setUploadedDate(new Date(date));
		}

		final Object duration = cattach.getData(KalturaUtils.PROPERTY_DURATION);
		if( duration != null )
		{
			int durInt;
			if (duration instanceof String)
			{
				durInt = Integer.parseInt((String) duration);
			}
			else
			{
				durInt = ((Number)duration).intValue();
			}
			kbean.setDuration(durInt);
		}

		KalturaServer ks = kalturaService.getByUuid(kbean.getKalturaServer());
		kbean.setExternalId(ks.getPartnerId(), kalturaService.getDefaultKdpUiConf(ks).getId());

		return kbean;
	}

	@SuppressWarnings("nls")
	@Override
	public Map<String, Class<? extends EquellaAttachmentBean>> getAttachmentBeanTypes()
	{
		Builder<String, Class<? extends EquellaAttachmentBean>> builder = ImmutableMap.builder();
		builder.put("kaltura", KalturaAttachmentBean.class);
		return builder.build();
	}

	@Override
	public String deserialize(EquellaAttachmentBean bean, ItemEditor itemEditor)
	{
		String uuid = bean.getUuid();
		KalturaAttachmentEditor editor = itemEditor.getAttachmentEditor(uuid, KalturaAttachmentEditor.class);
		KalturaAttachmentBean kBean = (KalturaAttachmentBean) bean;
		editStandard(editor, kBean);
		editor.editKalturaServer(kBean.getKalturaServer());
		editor.editMediaId(kBean.getMediaId());
		editor.editTitle(kBean.getTitle());
		editor.editDescription(kBean.getDescription());
		editor.editThumbUrl(kBean.getThumbUrl());
		editor.editTags(kBean.getTags());
		editor.editUploadedDate(kBean.getUploadedDate());
		editor.editDuration(kBean.getDuration());
		return uuid;
	}

	@Override
	public boolean exportable(EquellaAttachmentBean bean)
	{
		// TODO: some may be exportable?
		return false;
	}
}
