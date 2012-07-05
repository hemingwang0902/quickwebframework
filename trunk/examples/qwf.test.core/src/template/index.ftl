<html>
	<head>
		<title>qwf.test.core index</title>
	</head>
	<body>
		<p>Hello,this is a test bundle for QuickWebFramework!</p>
<#list bundleNameUrlListMap?keys as bundleName>
		<div>
			<h1>${bundleName}</h1>
	<#list bundleNameUrlListMap[bundleName] as url>
			<p>&nbsp;&nbsp;<a href="${url}">${url}</a></p>
	</#list>
		</div>
</#list>
	</body>
</html>