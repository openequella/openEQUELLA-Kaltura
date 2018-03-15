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

package com.tle.core.kaltura;

import com.tle.common.settings.ConfigurationProperties;
import com.tle.common.settings.annotation.Property;

@Deprecated
public class KalturaSettings implements ConfigurationProperties
{
	private static final long serialVersionUID = 1L;

	@Property(key = "kaltura.enabled")
	private boolean enabled;

	@Property(key = "kaltura.endpoint")
	private String endPoint;

	@Property(key = "kaltura.partnerid")
	private String partnerId;

	@Property(key = "kaltura.adminsecret")
	private String adminSecret;

	@Property(key = "kaltura.usersecret")
	private String userSecret;

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getPartnerId()
	{
		return partnerId;
	}

	public void setPartnerId(String partnerId)
	{
		this.partnerId = partnerId;
	}

	public String getAdminSecret()
	{
		return adminSecret;
	}

	public void setAdminSecret(String adminSecret)
	{
		this.adminSecret = adminSecret;
	}

	public String getUserSecret()
	{
		return userSecret;
	}

	public void setUserSecret(String userSecret)
	{
		this.userSecret = userSecret;
	}

	public String getEndPoint()
	{
		return endPoint;
	}

	public void setEndPoint(String endPoint)
	{
		this.endPoint = endPoint;
	}
}
