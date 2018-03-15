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

package com.tle.common.kaltura.admin.control;

import com.dytech.edge.wizard.beans.control.CustomControl;
import com.tle.common.wizard.controls.universal.UniversalSettings;

@SuppressWarnings("nls")
public class KalturaSettings extends UniversalSettings
{
	public enum KalturaOption
	{
		UPLOAD, EXISTING;
	}

	public static final String KEY_SERVER_UUID = "kalturaServerUUID";
	public static final String KEY_SERVER_RESTRICTION = "kalturaServerRestriction";

	public KalturaSettings(CustomControl wrapped)
	{
		super(wrapped);
	}

	public String getServerUuid()
	{
		return (String) wrapped.getAttributes().get(KEY_SERVER_UUID);
	}

	public void setServerUuid(String serverUuid)
	{
		wrapped.getAttributes().put(KEY_SERVER_UUID, serverUuid);
	}

	public String getRestriction()
	{
		return (String) wrapped.getAttributes().get(KEY_SERVER_RESTRICTION);
	}

	public void setRestriction(String restriction)
	{
		if( restriction == null )
		{
			wrapped.getAttributes().remove(KEY_SERVER_RESTRICTION);
		}
		else
		{
			wrapped.getAttributes().put(KEY_SERVER_RESTRICTION, restriction);
		}
	}

}
