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

package com.tle.common.kaltura.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.AccessType;

import com.tle.beans.entity.BaseEntity;

@Entity
@AccessType("field")
public class KalturaServer extends BaseEntity
{
	private static final long serialVersionUID = 1L;

	private boolean enabled;

	@Column(length = 1024, nullable = false)
	private String endPoint;
	private int partnerId;
	private int subPartnerId;
	private String adminSecret;
	private String userSecret;
	private int kdpUiConfId;

	public KalturaServer()
	{
		// for hibernate
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getEndPoint()
	{
		return endPoint;
	}

	public void setEndPoint(String endPoint)
	{
		this.endPoint = endPoint;
	}

	public int getPartnerId()
	{
		return partnerId;
	}

	public void setPartnerId(int partnerId)
	{
		this.partnerId = partnerId;
	}

	public int getSubPartnerId()
	{
		return subPartnerId;
	}

	public void setSubPartnerId(int subPartnerId)
	{
		this.subPartnerId = subPartnerId;
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

	public int getKdpUiConfId()
	{
		return kdpUiConfId;
	}

	public void setKdpUiConfId(int kdpUiConfId)
	{
		this.kdpUiConfId = kdpUiConfId;
	}
}
