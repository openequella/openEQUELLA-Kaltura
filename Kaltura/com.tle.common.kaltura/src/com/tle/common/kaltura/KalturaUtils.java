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

package com.tle.common.kaltura;


@SuppressWarnings("nls")
public final class KalturaUtils
{
	public static final String KALTURA_SAAS_DEFAULT_PLAYER_ID = "1913582";
	public static final String KALTURA_SAAS_ENDPOINT = "http://www.kaltura.com";

	public static final String ATTACHMENT_TYPE = "kaltura";
	public static final String MIME_TYPE = "equella/attachment-kaltura";
	public static final String MIME_DESC = "Kaltura media";

	public static final String PROPERTY_KALTURA_SERVER = "kalturaServer";

	public static final String PROPERTY_ENTRY_ID = "entryId";
	public static final String PROPERTY_TITLE = "title";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_DATE = "uploaded";
	public static final String PROPERTY_THUMB_URL = "thumbUrl";
	public static final String PROPERTY_TAGS = "tags";
	public static final String PROPERTY_DURATION = "duration"; // seconds
	public static final String PROPERTY_CUSTOM_PLAYER = "customPlayer";

	// This was used for the first release of Kaltura in 5.2 when the
	// attachments stored the static URL to their respective servers
	@Deprecated
	public static final String PROPERTY_DATA_URL = "dataUrl";

	// Noli me tangere constructor, because Sonar likes it that way for
	// non-instantiated utility classes
	private KalturaUtils()
	{
		throw new Error();
	}
}
