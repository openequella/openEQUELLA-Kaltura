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
