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

package com.tle.web.kaltura.viewer;

import com.kaltura.client.types.UiConf;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import com.tle.beans.item.attachments.IAttachment;
import com.tle.common.Check;
import com.tle.common.kaltura.KalturaUtils;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.core.guice.Bind;
import com.tle.core.kaltura.service.KalturaService;
import com.tle.core.mimetypes.MimeTypeService;
import com.tle.web.sections.SectionInfo;
import com.tle.web.sections.SectionResult;
import com.tle.web.sections.events.RenderContext;
import com.tle.web.template.Decorations;
import com.tle.web.viewitem.viewer.AbstractViewerSection;
import com.tle.web.viewurl.ResourceViewerConfig;
import com.tle.web.viewurl.ViewItemResource;
import com.tle.web.viewurl.ViewableResource;

@Bind
@SuppressWarnings("nls")
public class KalturaViewerSection extends AbstractViewerSection<KalturaViewerSection.KalturaViewerSectionModel>
{
	@Inject
	private MimeTypeService mimeTypeService;
	@Inject
	private KalturaService kalturaService;

	@Override
	public Collection<String> ensureOnePrivilege()
	{
		return VIEW_ITEM_AND_VIEW_ATTACHMENTS_PRIV;
	}

	@Override
	public SectionResult view(RenderContext info, ViewItemResource resource)
	{
		Decorations.getDecorations(info).clearAllDecorations();

		String height = null;
		String width = null;

		ResourceViewerConfig config = getResourceViewerConfig(mimeTypeService, resource, "kalturaViewer");

		if( config != null )
		{
			Map<String, Object> attr = config.getAttr();
			height = (String) attr.get("kalturaHeight");
			width = (String) attr.get("kalturaWidth");
		}

		if( Check.isEmpty(width) || Objects.equals(width, "undefined") )
		{
			width = "100%";
		}

		if( Check.isEmpty(height) || Objects.equals(height, "undefined") )
		{
			height = "100%";
		}

		setupKalturaKdp(info, resource, width, height);

		return viewFactory.createTemplateResult("viewer/kalturaviewer.ftl", this);
	}

	private KalturaServer getKalturaServer(String uuid)
	{
		return kalturaService.getByUuid(uuid);
	}

	private void setupKalturaKdp(SectionInfo info, ViewItemResource resource, String width, String height)
	{
		final IAttachment a = getAttachment(resource);

		KalturaViewerSectionModel model = getModel(info);
		model.setWidth(width);
		model.setHeight(height);

		String playerId = kalturaService.kalturaPlayerId();
		model.setPlayerId(playerId);

		String uiConfId  = (String) a.getData(KalturaUtils.PROPERTY_CUSTOM_PLAYER);
		model.setViewerUrl(kalturaService.createPlayerEmbedUrl(a, playerId, uiConfId));
	}

	private IAttachment getAttachment(ViewItemResource resource)
	{
		final ViewableResource viewableResource = resource.getAttribute(ViewableResource.class);
		return viewableResource.getAttachment();
	}

	@Override
	public Object instantiateModel(SectionInfo info) {
		return new KalturaViewerSectionModel();
	}

	public static final class KalturaViewerSectionModel {
		private String width = "100%";
		private String height = "100%";
		private String viewerUrl;
		private String playerId;

		public String getWidth() {
			return width.endsWith("%") ? width : width + "px";
		}

		public void setWidth(String width) {
			this.width = width;
		}

		public String getHeight() {
			return height.endsWith("%") ? height : height + "px";
		}

		public void setHeight(String height) {
			this.height = height;
		}

		public String getViewerUrl() {
			return viewerUrl;
		}

		public void setViewerUrl(String viewerUrl) {
			this.viewerUrl = viewerUrl;
		}

		public String getPlayerId() {
			return playerId;
		}

		public void setPlayerId(String playerId) {
			this.playerId = playerId;
		}
	}
}
