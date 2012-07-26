package com.quickwebframework.mvc.spring.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	// 得到Bundle导入包的资源
	private List<Resource> doGetImportPackageResource() {
		List<Resource> resourceList = new ArrayList<Resource>();
		String[] importPackages = BundleUtil
				.getBundleImportPackagePackageList(bundle);
		if (importPackages != null) {
			Bundle[] allBundles = bundle.getBundleContext().getBundles();

			List<String> importPackageList = new ArrayList<String>();
			for (String key : importPackages)
				importPackageList.add(key);

			Map<Bundle, List<String>> bundleClassPathListMap = new HashMap<Bundle, List<String>>();

			for (Bundle bundle : allBundles) {
				String[] bundleExportPackageInfos = BundleUtil
						.getBundleExportPackageList(bundle, false);
				for (String bundleExportPackageInfo : bundleExportPackageInfos) {
					// 已找到对应Bundle的路径列表
					List<String> foundClassPathList = new ArrayList<String>();
					for (String importPackage : importPackageList) {
						if (bundleExportPackageInfo.startsWith(importPackage)) {
							List<String> bundleClassPathList = null;
							if (bundleClassPathListMap.containsKey(bundle)) {
								bundleClassPathList = bundleClassPathListMap
										.get(bundle);
							} else {
								bundleClassPathList = new ArrayList<String>();
								bundleClassPathListMap.put(bundle,
										bundleClassPathList);
							}
							String classPath = importPackage.split(",")[0];
							classPath = "/" + classPath.replace('.', '/');
							bundleClassPathList.add(classPath);
							foundClassPathList.add(importPackage);
						}
					}
					for (String importPackage : foundClassPathList) {
						importPackageList.remove(importPackage);
					}
					if (importPackageList.isEmpty())
						break;
				}
				if (importPackageList.isEmpty())
					break;
			}

			for (Bundle bundle : bundleClassPathListMap.keySet()) {
				String[] classPaths = bundleClassPathListMap.get(bundle)
						.toArray(new String[0]);
				List<Resource> requireBundleResource = doJustGetBundleClassResources(
						bundle, classPaths, false);
				if (requireBundleResource == null)
					continue;
				resourceList.addAll(requireBundleResource);
			}
		}
		return resourceList;
	}

	// 得到Bundle依赖Bundle的资源
	private List<Resource> doGetBundleRequiredResources() {
		List<Resource> resourceList = new ArrayList<Resource>();

		// 得到当前Bundle依赖的Bundles
		Bundle[] reuiredBundles = BundleUtil.getBundleRequiredBundles(bundle);
		if (reuiredBundles != null) {
			for (Bundle requireBundle : reuiredBundles) {
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

		List<Resource> resourceList = doJustGetBundleClassResources(bundle,
				centerPath, true);
		resourceList.addAll(doGetImportPackageResource());
		resourceList.addAll(doGetBundleRequiredResources());

		return resourceList.toArray(new Resource[0]);
	}
}
