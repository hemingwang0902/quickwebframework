package com.quickwebframework.web.thread;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class BundleAutoManageThread extends Thread {
	public String bundleFolderPath;

	public BundleAutoManageThread(String bundleFolderPath) {
		this.bundleFolderPath = bundleFolderPath;
	}

	private void installOrUpdateBundle(BundleContext bundleContext,
			BundleInfo bundleInfo) throws BundleException, IOException {
		String bundleName = bundleInfo.getBundleName();
		Version bundleVersion = bundleInfo.getBundleVersion();
		File file = new File(bundleInfo.getBundleFilePath());

		Bundle preBundle = null;
		Bundle[] bundles = bundleContext.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals(bundleName)) {
				preBundle = bundle;
				break;
			}
		}

		Bundle newBundle = null; // 如果之前没有此插件，则安装
		if (preBundle == null) {
			System.out.println("自动安装新插件：" + bundleName + "  " + bundleVersion);
			FileInputStream fileInputStream = new FileInputStream(file);
			newBundle = bundleContext.installBundle(file.getName(),
					fileInputStream);
			fileInputStream.close();
		}// 否则更新
		else {
			if (bundleVersion.compareTo(preBundle.getVersion()) >= 0) {
				System.out.println("自动将插件：" + bundleName + " 由 "
						+ preBundle.getVersion() + "更新到" + bundleVersion);
				FileInputStream fileInputStream = new FileInputStream(file);
				preBundle.update(fileInputStream);
				newBundle = preBundle;
				fileInputStream.close();
			} else {
				System.out.println("插件：" + bundleName + "的版本" + bundleVersion
						+ "小于已安装的版本" + preBundle.getVersion() + "，没有应用更新！");
			}
		}
		// 尝试启动插件
		if (newBundle != null) {
			try {
				newBundle.start();
			} catch (Exception ex) {
			}
		}
	}

	// 得到Bundle名称列表(主要是为了得到最新的list中的顺序)
	private List<String> getBundleNameList(List<BundleInfo> list) {
		List<String> bundleNameList = new ArrayList<String>();
		for (BundleInfo bundleInfo : list) {
			bundleNameList.add(bundleInfo.getBundleName());
		}
		return bundleNameList;
	}

	// 根据Bundle的依赖关系，排列出安装顺序
	private void orderBundleInstallList(List<BundleInfo> list) {
		// 用于查询
		List<String> bundleNameList = getBundleNameList(list);

		for (int i = 0; i < list.size();) {
			// 是否有对象移动
			boolean isItemMoved = false;

			BundleInfo bundleInfo = list.get(i);
			for (String requireBundleName : bundleInfo
					.getRequireBundleNameList()) {
				// 如果依赖的包不在要安装的插件列表中，则忽略
				if (!bundleNameList.contains(requireBundleName))
					continue;

				int requireBundleIndex = bundleNameList
						.indexOf(requireBundleName);
				BundleInfo requireBundleInfo = list.get(requireBundleIndex);

				// 如果需要的包在此包后面，则移动到前面
				if (requireBundleIndex > i) {
					list.remove(requireBundleIndex);
					list.add(i, requireBundleInfo);

					bundleNameList = getBundleNameList(list);
					isItemMoved = true;
					i++;
					System.out.println("安装/更新顺序自动计算算法：将插件 " + requireBundleName
							+ " 移动到 " + bundleInfo.getBundleName() + " 前面。");
				}
			}
			if (isItemMoved) {
				i = 0;
			} else {
				i++;
			}
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("quickwebframework_web:插件自动管理线程已启动！");
			System.out.println("插件目录：" + bundleFolderPath);
			while (true) {
				Thread.sleep(1000);
				BundleContext bundleContext = QuickWebFrameworkLoaderListener
						.getBundleContext();
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

				List<BundleInfo> bundleInfoList = new ArrayList<BundleInfo>();
				for (File file : files) {
					ZipFile zipFile = null;
					try {
						try {
							zipFile = new ZipFile(file);
						} catch (Exception ex) {
							continue;
						}
						ZipEntry zipEntry = zipFile
								.getEntry("META-INF/MANIFEST.MF");
						if (zipEntry == null || zipEntry.isDirectory())
							continue;
						InputStream inputStream = zipFile
								.getInputStream(zipEntry);
						// 得到插件的信息
						BundleInfo bundleInfo = new BundleInfo(inputStream);
						bundleInfo.setBundleFilePath(file.getAbsolutePath());
						bundleInfoList.add(bundleInfo);
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
				}
				// 排出安装顺序
				orderBundleInstallList(bundleInfoList);
				// 按照顺序安装并启动
				for (int i = 0; i < bundleInfoList.size(); i++) {
					BundleInfo bundleInfo = bundleInfoList.get(i);
					System.out.println(i + ":" + bundleInfo.getBundleName());
					installOrUpdateBundle(bundleContext, bundleInfo);
				}
				// 删除这些jar文件
				for (File file : files) {
					file.delete();
				}
			}
		} catch (InterruptedException e) {
			System.out.println("quickwebframework_web:插件自动管理线程接到线程中止命令，线程已终止！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
