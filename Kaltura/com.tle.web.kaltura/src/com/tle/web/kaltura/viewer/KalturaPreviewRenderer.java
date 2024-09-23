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
import java.util.Map;
import javax.inject.Inject;
import com.tle.beans.item.attachments.Attachment;
import com.tle.common.Check;
import com.tle.common.kaltura.KalturaUtils;
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
	public SectionRenderable renderPreview(RenderContext context, Attachment attachment,
			ViewableItem<?> vitem,
			String mimeType) {
		if (supports(mimeType)) {
			String entryId = (String) attachment.getData(KalturaUtils.PROPERTY_ENTRY_ID);
			String uuid = (String) attachment.getData(KalturaUtils.PROPERTY_KALTURA_SERVER);

			if (!Check.isEmpty(entryId) && !Check.isEmpty(uuid)) {
				String playerId = kalturaService.kalturaPlayerId();

				// This is the div where the video is embedded into.
				DivRenderer playerDiv = new DivRenderer(new HtmlComponentState());
				playerDiv.setId(playerId);
				playerDiv.setStyles("width: 320px; height: 180px", null, null);

				// This is script that points to Kaltura player embed URL.
				TagRenderer playerScript = new AbstractComponentRenderer(new HtmlComponentState()) {
					@Override
					protected String getTag() {
						return "script";
					}

					@Override
					protected void prepareFirstAttributes(SectionWriter writer, Map<String, String> attrs)
							throws IOException {
						super.prepareFirstAttributes(writer, attrs);
						String uiConfId = (String) attachment.getData(KalturaUtils.PROPERTY_CUSTOM_PLAYER);
						attrs.put("src",
								kalturaService.createPlayerEmbedUrl(attachment, playerId, true, uiConfId));
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

	@Override
	public boolean supports(String mimeType)
	{
    return mimeType.contains("equella/attachment-kaltura");
  }
}
