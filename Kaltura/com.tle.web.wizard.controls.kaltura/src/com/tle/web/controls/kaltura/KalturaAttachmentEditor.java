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

import java.util.Date;

import com.tle.common.kaltura.KalturaUtils;
import com.tle.core.guice.Bind;
import com.tle.core.item.edit.attachment.AbstractCustomAttachmentEditor;

@Bind
public class KalturaAttachmentEditor extends AbstractCustomAttachmentEditor
{
	@Override
	public String getCustomType()
	{
		return KalturaUtils.ATTACHMENT_TYPE;
	}

	public void editKalturaServer(String serverUuid)
	{
		editCustomData(KalturaUtils.PROPERTY_KALTURA_SERVER, serverUuid);
	}

	public void editMediaId(String mediaId)
	{
		editCustomData(KalturaUtils.PROPERTY_ENTRY_ID, mediaId);
	}

	public void editTitle(String title)
	{
		editCustomData(KalturaUtils.PROPERTY_TITLE, title);
	}

	public void editUploadedDate(Date uploadedDate)
	{
		editCustomData(KalturaUtils.PROPERTY_DATE, uploadedDate.getTime());
	}

	public void editThumbUrl(String thumbUrl)
	{
		editCustomData(KalturaUtils.PROPERTY_THUMB_URL, thumbUrl);
	}

	public void editTags(String tags)
	{
		editCustomData(KalturaUtils.PROPERTY_TAGS, tags);
	}

	public void editDuration(long durationSeconds)
	{
		editCustomData(KalturaUtils.PROPERTY_DURATION, durationSeconds);
	}
}
