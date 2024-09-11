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

  public void setExternalId(int partnerId, int uiConfId, String version) {
    this.externalId = String.format("%d/%d-%s/%s", partnerId, uiConfId, version, mediaId);
  }

  @Override
  public Optional<String> getExternalId() {
    return Optional.of(externalId);
  }
}
