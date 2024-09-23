<#include "/com.tle.web.freemarker@/macro/sections.ftl" />
<#include "/com.tle.web.sections.equella@/macro/settings.ftl" />
<#include m.commonIncludePath />
<#import "/com.tle.web.sections.standard@/ajax.ftl" as a />

<@css path="kaltura.css" hasRtl=true />

<@detailArea >
	<@a.div id="mediapreview">
		<div class="preview-container">
			<div id="${m.playerId}" style="width: 300px; height: 200px"></div>
			<script src="${m.viewerUrl}"></script>
		</div>
	</@a.div>
	<@editArea>
		<#if m.showPlayers>
			<@setting label=b.key('edit.players.label') labelFor=s.players help=b.key('edit.players.help.label')>
					 <@render section=s.players />
				</@setting>
		</#if>		
	</@editArea>
</@detailArea>

<@detailList />

<br clear="both">