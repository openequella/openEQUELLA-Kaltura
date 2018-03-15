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

package com.tle.web.kaltura.migration;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.tle.beans.mime.MimeEntry;
import com.tle.common.kaltura.KalturaUtils;
import com.tle.common.filesystem.handle.SubTemporaryFile;
import com.tle.common.filesystem.handle.TemporaryFileHandle;
import com.tle.core.guice.Bind;
import com.tle.core.institution.convert.ConverterParams;
import com.tle.core.institution.convert.InstitutionInfo;
import com.tle.core.institution.convert.XmlMigrator;
import com.tle.core.mimetypes.MimeTypeConstants;
import com.tle.core.mimetypes.MimeTypeService;
import com.tle.core.mimetypes.institution.MimeEntryConverter;
import com.tle.core.xml.service.XmlService;
import com.tle.web.viewurl.ResourceViewerConfig;

@Bind
@Singleton
@SuppressWarnings("nls")
public class AddKalturaMimeTypeXmlMigration extends XmlMigrator
{
	@Inject
	private XmlService xmlService;
	@Inject
	private MimeTypeService mimeService;

	@Override
	public void execute(TemporaryFileHandle staging, InstitutionInfo instInfo, ConverterParams params)
	{
		SubTemporaryFile mimeFolder = MimeEntryConverter.getMimeFolder(staging);
		MimeEntry mimeEntry = new MimeEntry();
		mimeEntry.setType(KalturaUtils.MIME_TYPE);
		mimeEntry.setDescription(KalturaUtils.MIME_DESC);
		mimeEntry.setAttribute(MimeTypeConstants.KEY_DEFAULT_VIEWERID, "kalturaViewer");
		mimeEntry.setAttribute(MimeTypeConstants.KEY_ENABLED_VIEWERS, "[\"kalturaViewer\"]");
		mimeEntry.setAttribute(MimeTypeConstants.KEY_DISABLE_FILEVIEWER, "true");

		ResourceViewerConfig rvc = new ResourceViewerConfig();
		rvc.setThickbox(true);
		rvc.setWidth("800");
		rvc.setHeight("600");
		rvc.setOpenInNewWindow(true);

		HashMap<String, Object> attrs = Maps.newHashMap();
		attrs.put("kalturaWidth", "800");
		attrs.put("kalturaHeight", "600");
		rvc.setAttr(attrs);

		mimeService.setBeanAttribute(mimeEntry, "viewerConfig-kalturaViewer", rvc);

		String filename = MimeEntryConverter.getFilenameForEntry(mimeEntry);
		if( !fileExists(mimeFolder, filename) )
		{
			xmlHelper.writeFile(mimeFolder, filename, xmlService.serialiseToXml(mimeEntry));
		}
	}
}
