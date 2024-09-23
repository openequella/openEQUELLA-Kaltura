<#ftl strip_whitespace=true />
<#include "/com.tle.web.freemarker@/macro/sections.ftl" />

<@css "kalturaiframe.css" />

<#assign TEMP_body>
	<div id="${m.playerId}" style="width: ${m.width}; height: ${m.height}"></div>
	<script src="${m.viewerUrl}"></script>
</#assign>