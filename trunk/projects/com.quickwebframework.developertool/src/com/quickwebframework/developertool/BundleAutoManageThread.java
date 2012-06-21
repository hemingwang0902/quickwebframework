package com.quickwebframework.developertool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class BundleAutoManageThread extends Thread {

	public static Log log = LogFactory.getLog(BundleAutoManageThread.class);

	private BundleContext bundleContext;
	private String bundleFolderPath;

	public BundleAutoManageThread(BundleContext bundleContext,
			String bundleFolderPath) {
		this.bundleContext = bundleContext;
		this.bundleFolderPath = bundleFolderPath;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);

				File folderInfo = new File(bundleFolderPath);
				// 如果目录不存在
				if (!folderInfo.exists() || !folderInfo.isDirectory())
					continue;

				File[] files = folderInfo.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						if (!pathname.isFile())
							return false;
						return pathname.getName().toLowerCase()
								.endsWith(".jar");
					}
				});

				for (File file : files) {
					ZipFile zipFile = null;
					try {
						zipFile = new ZipFile(file);
						ZipEntry zipEntry = zipFile
								.getEntry("META-INF/MANIFEST.MF");
						if (zipEntry == null || zipEntry.isDirectory())
							continue;
						InputStream inputStream = zipFile
								.getInputStream(zipEntry);
						Properties prop = new Properties();
						prop.load(inputStream);

						// 得到插件的名称和版本
						String bundleName = prop
								.getProperty("Bundle-SymbolicName");
						Version bundleVersion = Version.parseVersion(prop
								.getProperty("Bundle-Version"));

						Bundle preBundle = null;
						Bundle[] bundles = bundleContext.getBundles();
						for (Bundle bundle : bundles) {
							if (bundle.getSymbolicName().equals(bundleName)) {
								preBundle = bundle;
								break;
							}
						}

						// 如果之前没有此插件，则安装
						if (preBundle == null) {
							log.info("自动安装新插件：" + bundleName + "  "
									+ bundleVersion);
							bundleContext.installBundle(file.getName());
						}// 否则更新
						else {
							if (bundleVersion.compareTo(preBundle.getVersion()) > 0) {
								log.info("自动将插件：" + bundleName + " 由 "
										+ preBundle.getVersion() + "更新到"
										+ bundleVersion);
								FileInputStream fileInputStream = new FileInputStream(
										file);
								preBundle.update(fileInputStream);
								fileInputStream.close();
							} else {
								log.info("插件：" + bundleName + "的版本"
										+ bundleVersion + "未大于已安装的版本"
										+ preBundle.getVersion() + "，没有应用更新！");
							}
						}
					} catch (Exception ex) {
						log.error(ex);
					} finally {
						if (zipFile != null) {
							try {
								zipFile.close();
							} catch (Exception ex) {
								log.error(ex);
							}
						}
					}
					file.delete();
				}
			}
		} catch (InterruptedException e) {
			log.info("插件自动管理线程接到线程中止命令，线程已终止！");
		}
	}
}
