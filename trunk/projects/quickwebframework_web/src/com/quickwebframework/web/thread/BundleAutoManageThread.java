package com.quickwebframework.web.thread;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class BundleAutoManageThread extends Thread {
	private BundleContext bundleContext;
	public String bundleFolderPath;

	public BundleAutoManageThread(BundleContext bundleContext,
			String bundleFolderPath) {
		this.bundleContext = bundleContext;
		this.bundleFolderPath = bundleFolderPath;
	}

	@Override
	public void run() {
		try {
			System.out.println("quickwebframework_web:插件自动管理线程已启动！");
			System.out.println("插件目录：" + bundleFolderPath);
			while (true) {
				Thread.sleep(1000);
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

				List<Bundle> newBundleList = new ArrayList<Bundle>();
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
							System.out.println("自动安装新插件：" + bundleName + "  "
									+ bundleVersion);
							FileInputStream fileInputStream = new FileInputStream(
									file);
							newBundle = bundleContext.installBundle(
									file.getName(), fileInputStream);
							fileInputStream.close();
						}// 否则更新
						else {
							if (bundleVersion.compareTo(preBundle.getVersion()) > 0) {
								System.out.println("自动将插件：" + bundleName
										+ " 由 " + preBundle.getVersion()
										+ "更新到" + bundleVersion);
								FileInputStream fileInputStream = new FileInputStream(
										file);
								preBundle.update(fileInputStream);
								newBundle = preBundle;
								fileInputStream.close();
							} else {
								System.out.println("插件：" + bundleName + "的版本"
										+ bundleVersion + "未大于已安装的版本"
										+ preBundle.getVersion() + "，没有应用更新！");
							}
						}
						// 尝试启动插件
						if (newBundle != null) {
							newBundleList.add(newBundle);
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

				// 尝试启动所有的插件
				boolean isAllBundleStartSuccess;
				for (int i = 0; i < newBundleList.size(); i++) {
					isAllBundleStartSuccess = true;
					for (Bundle newBundle : newBundleList) {
						if (newBundle.getState() != Bundle.ACTIVE
								&& newBundle.getState() != Bundle.STARTING) {
							try {
								newBundle.start();
							} catch (BundleException e) {
								isAllBundleStartSuccess = false;
							}
						}
					}
					if (isAllBundleStartSuccess) {
						break;
					}
				}

				// 启动尝试启动失败的插件以打印异常信息
				for (Bundle newBundle : newBundleList) {
					if (newBundle.getState() != Bundle.ACTIVE
							&& newBundle.getState() != Bundle.STARTING) {
						try {
							newBundle.start();
						} catch (BundleException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (InterruptedException e) {
			System.out.println("quickwebframework_web:插件自动管理线程接到线程中止命令，线程已终止！");
		}
	}
}
