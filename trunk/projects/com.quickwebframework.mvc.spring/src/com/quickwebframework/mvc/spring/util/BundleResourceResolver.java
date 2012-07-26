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
		List<Resource> resourceList = new ArrayList<Resource>();
		// 搜索Class文件
		Enumeration<?> enume = toGetResourceBundle.findEntries(path, "*.class",
				recurse);
		if (enume == null)
			return null;
		while (enume.hasMoreElements()) {
			resourceList.add(new UrlResource((URL) enume.nextElement()));
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
						.getBundleExportPackageList(requireBundle);
				if (requireBundleExportPackages == null)
					continue;
				for (String requireBundleExportPackage : requireBundleExportPackages) {
					// 将.替换成/，用于资源搜索
					requireBundleExportPackage = requireBundleExportPackage
							.replace('.', '/');
					List<Resource> requireBundleResource = doJustGetBundleClassResources(
							requireBundle, requireBundleExportPackage, false);
					if (requireBundleResource == null)
						continue;
					resourceList.addAll(requireBundleResource);
				}
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
