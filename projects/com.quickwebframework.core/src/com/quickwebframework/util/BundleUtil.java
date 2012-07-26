package com.quickwebframework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

public class BundleUtil {

	// 插件方法URL模板
	public static String bundleMethodUrlTemplate;

	/**
	 * 得到Bundle中的路径列表
	 * 
	 * @param bundle
	 *            Bundle对象
	 * @param startPath
	 *            起始路径，一般为"/"
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getPathListInBundle(Bundle bundle,
			String startPath) {
		List<String> rtnList = new ArrayList<String>();
		Enumeration urlEnum = bundle.getEntryPaths(startPath);
		while (urlEnum.hasMoreElements()) {
			String url = (String) urlEnum.nextElement();
			rtnList.add(url);
			// 如果是目录
			if (url.endsWith("/")) {
				rtnList.addAll(getPathListInBundle(bundle, url));
			}
		}
		return rtnList;
	}

	/**
	 * 得到Bundle的header映射
	 * 
	 * @param bundle
	 * @return
	 */
	public static Map<String, String> getBundleHeadersMap(Bundle bundle) {
		Dictionary<String, String> headersDict = bundle.getHeaders();
		Map<String, String> headersMap = new HashMap<String, String>();
		Enumeration<String> keyEnumeration = headersDict.keys();
		while (keyEnumeration.hasMoreElements()) {
			String key = keyEnumeration.nextElement();
			headersMap.put(key, headersDict.get(key));
		}
		return headersMap;
	}

	/**
	 * 从MANIFEST.MF文件中的Require-Bundle的一行得到Bundle
	 * 
	 * @param bundles
	 * @param bundlePropertyLine
	 * @return
	 */
	public static Bundle resloveRequireBundleLineInManifest(Bundle[] bundles,
			String bundlePropertyLine) {
		String[] tmpArray = bundlePropertyLine.split(";");
		String requireBundleName = tmpArray[0];

		Bundle requireBundle = null;
		for (Bundle tmpBundle : bundles) {
			if (tmpBundle.getSymbolicName().equals(requireBundleName)) {
				// 暂不考虑版本匹配问题,以后要考虑
				requireBundle = tmpBundle;
				break;
			}
		}
		return requireBundle;
	}

	/**
	 * 得到Bundle需要的其他Bundle数组
	 * 
	 * @param bundle
	 * @return
	 */
	public static Bundle[] getBundleRequiredBundles(Bundle bundle) {
		Map<String, String> headersMap = BundleUtil.getBundleHeadersMap(bundle);
		List<Bundle> bundleList = new ArrayList<Bundle>();
		// 如果有引用其他的Bundle
		if (headersMap.containsKey("Require-Bundle")) {
			String requireBundlesString = headersMap.get("Require-Bundle");
			String[] requireBundleArray = requireBundlesString.split(",");

			Bundle[] allBundle = bundle.getBundleContext().getBundles();

			for (String requireBundleLine : requireBundleArray) {
				Bundle requireBundle = resloveRequireBundleLineInManifest(
						allBundle, requireBundleLine);
				bundleList.add(requireBundle);
			}
		}
		return bundleList.toArray(new Bundle[0]);
	}

	/**
	 * 得到Bundle的导出包名列表
	 * 
	 * @param bundle
	 * @return
	 */
	public static String[] getBundleExportPackageList(Bundle bundle) {
		Map<String, String> headersMap = BundleUtil.getBundleHeadersMap(bundle);
		List<String> list = new ArrayList<String>();
		if (headersMap.containsKey("Export-Package")) {
			String exportPackagesString = headersMap.get("Export-Package");
			String[] exportPackageLineArray = exportPackagesString.split(",");
			for (String exportPackageLine : exportPackageLineArray) {
				list.add(exportPackageLine.split(";")[0]);
			}
		}
		return list.toArray(new String[0]);
	}

	/**
	 * 解压Bundle的文件
	 * 
	 * @param bundle
	 *            Bundle对象
	 * @param dirPath
	 *            解压到的目录
	 */
	public static void extractBundleFiles(Bundle bundle, String dirPath) {
		try {
			List<String> urlList = getPathListInBundle(bundle, "/");
			for (String url : urlList) {
				// 如果是目录
				if (url.endsWith("/")) {
					File folder = new File(dirPath + "/" + url);
					if (!folder.exists()) {
						folder.mkdirs();
					}
				} else {
					InputStream inputStream = bundle.getResource(url)
							.openStream();
					File file = new File(dirPath + "/" + url);
					file.createNewFile();
					FileOutputStream outputStream = new FileOutputStream(file);

					IoUtil.copyStream(inputStream, outputStream);
					outputStream.close();
					inputStream.close();
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 得到插件方法的URL
	 * 
	 * @param bundleName
	 * @param methodName
	 * @return
	 */
	public static String getBundleMethodUrl(String bundleName, String methodName) {
		if (bundleMethodUrlTemplate == null
				|| bundleMethodUrlTemplate.isEmpty())
			return "Missing bundleMethodUrlTemplate";
		return String.format(bundleMethodUrlTemplate, bundleName, methodName);
	}
}
