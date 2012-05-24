package com.quickwebframework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;

public class BundleUtil {

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
}
