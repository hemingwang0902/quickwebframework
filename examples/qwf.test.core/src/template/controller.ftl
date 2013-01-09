<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>控制器信息</title>
</head>
<body>
	<p>
		<b>控制器信息</b>
	</p>
	<#include "qwf.test.core:navbar">
	<table>
		<tbody>
			<#list bundleNameHttpMethodInfoListMap?keys as bundleName>
			
			<#if bundleNameHttpMethodInfoListMap[bundleName]?size gt 0>
			<tr>
				<td><b>插件[${bundleName}]</b></td>
			</tr>
			<#list bundleNameHttpMethodInfoListMap[bundleName] as httpMethodInfo>
			<tr>
				<td><a style="margin-left: 20px"
					href="${httpMethodInfo.mappingUrl}">${httpMethodInfo.mappingUrl}</a>(${httpMethodInfo.httpMethod})</td>
			</tr>
			</#list>
			</#if>
			
			</#list>
		</tbody>
	</table>
</body>
</html>