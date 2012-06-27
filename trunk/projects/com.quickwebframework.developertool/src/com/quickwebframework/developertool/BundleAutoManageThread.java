package com.quickwebframework.developertool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class BundleAutoManageThread extends Thread {

	public static Logger log = Logger.getLogger(BundleAutoManageThread.class
			.getName());

	public String bundleFolderPath;

	public BundleAutoManageThread(String bundleFolderPath) {
		this.bundleFolderPath = bundleFolderPath;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
				BundleContext bundleContext = Activator.getContext();
				if (bundleContext == null) {
					continue;
				}
				
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

						Bundle newBundle = null;
						// 如果之前没有此插件，则安装
						if (preBundle == null) {
							log.info("自动安装新插件：" + bundleName + "  "
									+ bundleVersion);
							FileInputStream fileInputStream = new FileInputStream(
									file);
							newBundle = bundleContext.installBundle(
									file.getName(), fileInputStream);
							fileInputStream.close();
						}// 否则更新
						else {
							if (bundleVersion.compareTo(preBundle.getVersion()) > 0) {
								log.info("自动将插件：" + bundleName + " 由 "
										+ preBundle.getVersion() + "更新到"
										+ bundleVersion);
								FileInputStream fileInputStream = new FileInputStream(
										file);
								preBundle.update(fileInputStream);
								newBundle = preBundle;
								fileInputStream.close();
							} else {
								log.info("插件：" + bundleName + "的版本"
										+ bundleVersion + "未大于已安装的版本"
										+ preBundle.getVersion() + "，没有应用更新！");
							}
						}
						// 尝试启动插件
						if (newBundle != null
								&& newBundle.getState() != Bundle.ACTIVE
								&& newBundle.getState() != Bundle.STARTING) {
							newBundle.start();
						}
					} catch (IllegalStateException ex) {
						continue;
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (zipFile != null) {
							try {
								zipFile.close();
							} catch (Exception ex) {
								ex.printStackTrace();
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
