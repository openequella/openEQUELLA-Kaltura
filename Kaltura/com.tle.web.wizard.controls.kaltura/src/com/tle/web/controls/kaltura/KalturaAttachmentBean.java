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

import com.tle.web.api.item.equella.interfaces.beans.EquellaAttachmentBean;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("nls")
public class KalturaAttachmentBean extends EquellaAttachmentBean {

  private String mediaId;
  private String title;
  private Date uploadedDate;
  private String thumbUrl;
  private String kalturaServer;
  private String tags;
  private long duration;

  /**
   * A string which is made up of the core elements required to generate a embedded Kaltura viewer.
   * It has the format of {@code <partner_id>/<uiconf_id>/<entryId>}.
   */
  private String externalId;

  /**
   * More details of the Kaltura player configured in the Kaltura Studio Management page.
   */
  private PlayerConfig playerConfig;

  @Override
  public String getRawAttachmentType() {
    return "custom/kaltura";
  }

  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getUploadedDate() {
    return uploadedDate;
  }

  public void setUploadedDate(Date uploadedDate) {
    this.uploadedDate = uploadedDate;
  }

  public String getThumbUrl() {
    return thumbUrl;
  }

  public void setThumbUrl(String thumbUrl) {
    this.thumbUrl = thumbUrl;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getKalturaServer() {
    return kalturaServer;
  }

  public void setKalturaServer(String kalturaServer) {
    this.kalturaServer = kalturaServer;
  }

  public void setExternalId(int partnerId, int uiConfId) {
    this.externalId = String.format("%d/%d/%s", partnerId, uiConfId, mediaId);
  }

  @Override
  public Optional<String> getExternalId() {
    return Optional.of(externalId);
  }

  public void setPlayerConfig(int width, int height, boolean isV7Player) {
    PlayerConfig config = new PlayerConfig();
    config.setHeight(height);
    config.setWidth(width);
    config.setVersion(isV7Player ? PlayerConfig.VERSION_V7 : PlayerConfig.VERSION_V2);
    this.playerConfig = config;
  }


  @Override
  public Optional<Map<String, String>> getViewerConfig() {
    Map<String, String> viewerConfig =
        Map.of("width", Integer.toString(playerConfig.getWidth()),
            "height", Integer.toString(playerConfig.getHeight()),
            "version", playerConfig.getVersion());

    return Optional.of(viewerConfig);
  }

  private static final class PlayerConfig {

    private static final String VERSION_V2 = "V2";
    private static final String VERSION_V7 = "V7";

    private int width;
    private int height;
    private String version;

    public int getWidth() {
      return width;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public int getHeight() {
      return height;
    }

    public void setHeight(int height) {
      this.height = height;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }
  }
}
