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

package com.tle.web.kaltura

import com.tle.core.application.StartupBean
import com.tle.core.kaltura.KalturaConstants.{PRIV_CREATE_KALTURA, PRIV_EDIT_KALTURA}
import com.tle.legacy.LegacyGuice
import com.tle.web.resources.ResourcesService
import com.tle.web.settings.{SettingsList, SettingsPage}

object KalturaExtensions extends StartupBean {

  val kalturaSettings = SettingsPage(ResourcesService.getResourceHelper(getClass),
    "kaltura", "general", "settings.title", "settings.description",
    "access/kalturasettings.do", "web", () => !LegacyGuice.aclManager.filterNonGrantedPrivileges(PRIV_CREATE_KALTURA, PRIV_EDIT_KALTURA).isEmpty)

  override def startup(): Unit = {
    SettingsList += kalturaSettings
  }
}
