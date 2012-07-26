package com.quickwebframework.mvc.spring.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.quickwebframework.util.BundleUtil;

/**
 * 插件资源解析器
 * 
 * @author aaa
 * 
 */
public class BundleResourceResolver {

	private Bundle bundle;

	public BundleResourceResolver(Bundle bundle) {
		this.bundle = bundle;
	}

	private List<Resource> doJustGetBundleClassResources(
			Bundle toGetResourceBundle, String path, boolean recurse) {
		return doJustGetBundleClassResources(toGetResourceBundle,
				new String[] { path }, recurse);
	}

	private List<Resource> doJustGetBundleClassResources(
			Bundle toGetResourceBundle, String[] paths, boolean recurse) {

		List<Resource> resourceList = new ArrayList<Resource>();
		// 搜索Class文件
		Enumeration<?> enume = toGetResourceBundle.findEntries("", "*.class",
				true);
		if (enume == null)
			return null;
		while (enume.hasMoreElements()) {
			URL url = (URL) enume.nextElement();
			String urlPath = url.getPath();
			for (String path : paths) {
				if (!path.startsWith("/"))
					path = "/" + path;
				if (urlPath.startsWith(path)) {
					// 如果不遍历子路径,且后面还有目录
					if (!recurse) {
						String subPath = urlPath.substring(path.length());
						if (subPath.startsWith("/"))
							subPath = subPath.substring(1);
						if (subPath.contains("/"))
							continue;
					}
					resourceList.add(new UrlResource(url));
				}
			}
		}
		return resourceList;
	}

	// 得到Bundle依赖Bundle的资源
	private List<Resource> doGetBundleRequiredResources(Bundle currentBundle) {
		List<Resource> resourceList = new ArrayList<Resource>();
		// 先扫描依赖的Bundle
		Bundle[] reuiredBundles = BundleUtil
				.getBundleRequiredBundles(currentBundle);
		if (reuiredBundles != null) {
			for (Bundle requireBundle : reuiredBundles) {
				// 递归处理依赖的依赖
				doGetBundleRequiredResources(requireBundle);
				// 处理reuiredBundles中的资源
				String[] requireBundleExportPackages = BundleUtil
						.getBundleExportPackageList(requireBundle, true);
				if (requireBundleExportPackages == null)
					continue;
				for (int i = 0; i < requireBundleExportPackages.length; i++) {
					// 将.替换成/，用于资源搜索
					requireBundleExportPackages[i] = requireBundleExportPackages[i]
							.replace('.', '/');
				}

				List<Resource> requireBundleResource = doJustGetBundleClassResources(
						requireBundle, requireBundleExportPackages, false);
				if (requireBundleResource == null)
					continue;
				resourceList.addAll(requireBundleResource);
			}
		}
		// 然后扫描导入的包
		String[] importPackages = BundleUtil
				.getBundleImportPackagePackageList(currentBundle);
		if (importPackages != null) {
			Bundle[] allBundles = currentBundle.getBundleContext().getBundles();
			for (String importPackage : importPackages) {
				Bundle importPackageBundle = BundleUtil
						.getBundleByExportPackage(allBundles, importPackage);

				if (importPackageBundle == null)
					continue;

				String justPackageName = importPackage.split(",")[0];
				justPackageName = justPackageName.replace('.', '/');

				List<Resource> requireBundleResource = doJustGetBundleClassResources(
						importPackageBundle, justPackageName, false);
				if (requireBundleResource == null)
					continue;
				resourceList.addAll(requireBundleResource);
			}
		}
		return resourceList;
	}

	/**
	 * 得到资源
	 * 
	 * @param path
	 * @return
	 */
	public Resource[] getResources(String path) {
		// 得到本Bundle的资源
		String centerPath = null;

		// 如果是寻找所有的classpath
		if (path.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
			centerPath = path
					.substring(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
							.length());

			if (centerPath.startsWith("*")) {
				centerPath = "/";
			} else {
				centerPath = centerPath.substring(0, centerPath.indexOf("*"));
			}
		} else if (path
				.startsWith(ResourcePatternResolver.CLASSPATH_URL_PREFIX)) {
			throw new RuntimeException("暂未实现的path:" + path);
		} else {
			throw new RuntimeException("未知的path:" + path);
		}

		List<Resource> resourceList = doGetBundleRequiredResources(bundle);
		resourceList.addAll(doJustGetBundleClassResources(bundle, centerPath,
				true));
		return resourceList.toArray(new Resource[0]);
	}
}
