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

package com.tle.web.kaltura.guice;

import com.google.inject.name.Names;
import com.tle.web.kaltura.section.KalturaServerEditorSection;
import com.tle.web.kaltura.section.KalturaServerListSection;
import com.tle.web.kaltura.section.RootKalturaServerSection;
import com.tle.web.sections.equella.guice.SectionsModule;

@SuppressWarnings("nls")
public class KalturaModule extends SectionsModule
{
	@Override
	protected void configure()
	{
		bind(Object.class).annotatedWith(Names.named("/access/kalturasettings")).toProvider(kalturaTree());
	}

	private NodeProvider kalturaTree()
	{
		NodeProvider node = node(RootKalturaServerSection.class);
		node.innerChild(KalturaServerEditorSection.class);
		node.child(KalturaServerListSection.class);
		return node;
	}
}
