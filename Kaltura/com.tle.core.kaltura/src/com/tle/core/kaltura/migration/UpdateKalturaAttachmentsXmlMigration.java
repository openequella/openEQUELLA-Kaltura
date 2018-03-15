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

package com.tle.core.kaltura.migration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.w3c.dom.Node;

import com.dytech.devlib.PropBagEx;
import com.tle.common.Check;
import com.tle.common.kaltura.KalturaUtils;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.core.xml.XmlDocument;
import com.tle.core.xml.XmlDocument.NodeListIterable;
import com.tle.common.filesystem.handle.ImportFile;
import com.tle.common.filesystem.handle.SubTemporaryFile;
import com.tle.core.guice.Bind;
import com.tle.core.institution.convert.XmlHelper;
import com.tle.core.institution.convert.ConverterParams;
import com.tle.core.institution.convert.AbstractItemXmlMigrator;

@Bind
@Singleton
@SuppressWarnings("nls")
public class UpdateKalturaAttachmentsXmlMigration extends AbstractItemXmlMigrator
{
	@Inject
	private XmlHelper xmlHelper;

	@Override
	public boolean migrate(ConverterParams params, PropBagEx xml, SubTemporaryFile file, String filename)
		throws Exception
	{
		boolean modified = false;

		XmlDocument itemXml = new XmlDocument(xml.toString());
		if( itemXml
			.node("com.tle.beans.item.Item/attachments/com.tle.beans.item.attachments.CustomAttachment[value1=\"kaltura\"]") != null )
		{
			System.out.print(true);
			NodeListIterable kalturaAttachments = itemXml
				.nodeList("com.tle.beans.item.Item/attachments/com.tle.beans.item.attachments.CustomAttachment[value1=\"kaltura\"]");

			// Add server uuid to each attachment xml
			for( Node att : kalturaAttachments )
			{
				String fPath = file.getAbsolutePath();
				String ksPath = fPath.substring(7, fPath.length() - 6);
				SubTemporaryFile ksFolder = new SubTemporaryFile(new ImportFile(ksPath), "kalturaserver");
				List<String> servers = xmlHelper.getXmlFileList(ksFolder);

				if( !Check.isEmpty(servers) )
				{
					String serverPath = servers.get(0);
					KalturaServer ks = (KalturaServer) xmlHelper.readXmlFile(ksFolder, serverPath);

					// Create entry in data map
					Node data = itemXml.node("data", att);

					// add attribute
					Node entry = itemXml.createNode(data, "entry");
					Node key = itemXml.createNode(entry, "string");
					Node value = itemXml.createNode(entry, "string");

					key.setTextContent(KalturaUtils.PROPERTY_KALTURA_SERVER);
					value.setTextContent(ks.getUuid());

					// remove attribute
					itemXml.deleteAll("entry[string=\"dataUrl\"]", data);

					modified = true;
				}
			}
		}

		if( modified )
		{
			xml.setXML(itemXml.toString());
		}

		return modified;
	}
}
