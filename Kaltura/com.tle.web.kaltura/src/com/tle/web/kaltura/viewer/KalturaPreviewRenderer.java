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

import com.tle.web.sections.SectionWriter;
import com.tle.web.sections.render.CombinedRenderer;
import com.tle.web.sections.standard.renderers.AbstractComponentRenderer;
import com.tle.web.sections.standard.renderers.DivRenderer;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import javax.inject.Inject;
import com.tle.beans.item.attachments.Attachment;
import com.tle.beans.item.attachments.IAttachment;
import com.tle.common.Check;
import com.tle.common.kaltura.KalturaUtils;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.core.guice.Bind;
import com.tle.core.kaltura.service.KalturaService;
import com.tle.web.searching.VideoPreviewRenderer;
import com.tle.web.sections.events.PreRenderContext;
import com.tle.web.sections.events.RenderContext;
import com.tle.web.sections.render.SectionRenderable;
import com.tle.web.sections.render.TagRenderer;
import com.tle.web.sections.standard.model.HtmlComponentState;
import com.tle.web.viewable.ViewableItem;

/**
 * @author Peng
 */
@Bind
public class KalturaPreviewRenderer implements VideoPreviewRenderer
{
	@Inject
	private KalturaService kalturaService;

	@Override
	public void preRender(PreRenderContext context) { }

	@Override
	public SectionRenderable renderPreview(RenderContext context, Attachment attachment, ViewableItem<?> vitem,
		String mimeType)
	{
		if( supports(mimeType) )
		{
			String entryId = (String) attachment.getData(KalturaUtils.PROPERTY_ENTRY_ID);
			String uuid = (String) attachment.getData(KalturaUtils.PROPERTY_KALTURA_SERVER);

			if( !Check.isEmpty(entryId) && !Check.isEmpty(uuid) )
			{
				String uiConfId = (String) attachment.getData(KalturaUtils.PROPERTY_CUSTOM_PLAYER);

				String playerId = kalturaService.kalturaPlayerId();

				// This is the div where the video is embedded into.
				DivRenderer playerDiv = new DivRenderer(new HtmlComponentState());
				playerDiv.setId(playerId);
				playerDiv.setStyles("width: 320px; height: 180px", null, null);

				// This is script tag that points to Kaltura player embed URL.
				TagRenderer playerScript = new AbstractComponentRenderer(new HtmlComponentState()) {
					@Override
					protected String getTag() {
						return "script";
					}

					@Override
					protected void prepareFirstAttributes(SectionWriter writer, Map<String, String> attrs)
							throws IOException {
						super.prepareFirstAttributes(writer, attrs);
						attrs.put("src", kalturaService.createPlayerEmbedUrl(attachment, playerId, uiConfId));
					}
				};

				// Combine above two tags into one renderer.
				CombinedRenderer combined = new CombinedRenderer();
				combined.addRenderer(playerDiv);
				combined.addRenderer(playerScript);

				return combined;
			}
		}

		return null;
	}

	private String createHtml5embed(KalturaServer ks, String kdpUiConfId)
	{
		return MessageFormat.format("{0}/p/{1}/embedIframeJs/uiconf_id/{2}/partner_id/{1}", ks.getEndPoint(),
			Integer.toString(ks.getPartnerId()), kdpUiConfId);
	}

	private String createFlashEmbed(KalturaServer ks, String kdpUiConfId, String entryId)
	{
		return MessageFormat.format("{0}/kwidget/wid/_{1}/uiconf_id/{2}/entry_id/{3}", ks.getEndPoint(),
			Integer.toString(ks.getPartnerId()), kdpUiConfId, entryId);
	}

	private String getKdpUiConfId(KalturaServer ks, IAttachment a)
	{
		// // Attachment custom
		String uiConfId = (String) a.getData(KalturaUtils.PROPERTY_CUSTOM_PLAYER);

		if( !Check.isEmpty(uiConfId) && kalturaService.hasConf(ks, uiConfId) )
		{
			return uiConfId;
		}

		// Server default
		uiConfId = Integer.toString(ks.getKdpUiConfId());
		if( !Check.isEmpty(uiConfId) && kalturaService.hasConf(ks, uiConfId) )
		{
			return uiConfId;
		}

		// EQUELLA default
		uiConfId = Integer.toString(kalturaService.getDefaultKdpUiConf(ks).getId());
		return uiConfId;
	}

	@Override
	public boolean supports(String mimeType)
	{
		if( mimeType.contains("equella/attachment-kaltura") )
		{
			return true;
		}
		return false;
	}
}
