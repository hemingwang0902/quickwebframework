package com.quickwebframework.web.thread;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

	public final static String METAINF_FILE_PATH = "META-INF/MANIFEST.MF";

	public String bundleFolderPath;

	public BundleAutoManageThread(String bundleFolderPath) {
		this.bundleFolderPath = bundleFolderPath;
	}

	private Bundle installOrUpdateBundle(BundleContext bundleContext,
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
				preBundle.stop();
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
		return newBundle;
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
		// 导出包名与Bundle名对应Map
		Map<String, String> exportPackageBundleNameMap = new HashMap<String, String>();
		for (BundleInfo bundleInfo : list) {
			for (String exportPackage : bundleInfo.getExportPackageList()) {
				exportPackageBundleNameMap.put(exportPackage,
						bundleInfo.getBundleName());
			}
		}

		// 开始排序
		for (int i = 0; i < list.size();) {
			// 是否有对象移动
			boolean isItemMoved = false;

			BundleInfo bundleInfo = list.get(i);

			// 根据Require-Bundle排序
			for (String requireBundleName : bundleInfo
					.getRequireBundleNameList()) {
				// 如果依赖的Bundle不在要安装的插件列表中，则忽略
				if (!bundleNameList.contains(requireBundleName))
					continue;

				int requireBundleIndex = bundleNameList
						.indexOf(requireBundleName);

				// 如果需要的包在此包后面，则移动到前面
				if (requireBundleIndex > i) {
					BundleInfo requireBundleInfo = list.get(requireBundleIndex);
					list.remove(requireBundleIndex);
					list.add(i, requireBundleInfo);

					bundleNameList = getBundleNameList(list);
					isItemMoved = true;
					i++;
					System.out
							.println(String
									.format("安装/更新顺序自动计算算法：因为插件[%s]需要插件[%s]，所以将插件[%s]移动到[%s]前面。",
											bundleInfo.getBundleName(),
											requireBundleName,
											requireBundleName,
											bundleInfo.getBundleName()));
				}
			}

			// 根据Import-Package排序
			for (String importPackage : bundleInfo.getImportPackageList()) {
				// 如果导入的包不在要安装的插件的导出包列表中，则忽略
				if (!exportPackageBundleNameMap.containsKey(importPackage))
					continue;
				String importPackageBelongBundleName = exportPackageBundleNameMap
						.get(importPackage);
				int importPackageBelongBundleIndex = bundleNameList
						.indexOf(importPackageBelongBundleName);

				// 如果需要的包在此包后面，则移动到前面
				if (importPackageBelongBundleIndex > i) {
					BundleInfo importPackageBelongBundle = list
							.get(importPackageBelongBundleIndex);
					list.remove(importPackageBelongBundleIndex);
					list.add(i, importPackageBelongBundle);

					bundleNameList = getBundleNameList(list);
					isItemMoved = true;
					i++;
					System.out
							.println(String
									.format("安装/更新顺序自动计算算法：因为插件[%s]导入了插件[%s]的包[%s]，所以将插件[%s]移动到[%s]前面。",
											bundleInfo.getBundleName(),
											importPackageBelongBundleName,
											importPackage,
											importPackageBelongBundleName,
											bundleInfo.getBundleName()));
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
						ZipEntry zipEntry = zipFile.getEntry(METAINF_FILE_PATH);
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

				// 按照顺序安装，注意:此处只是安装并不启动插件
				for (int i = 0; i < bundleInfoList.size(); i++) {
					BundleInfo bundleInfo = bundleInfoList.get(i);
					installOrUpdateBundle(bundleContext, bundleInfo);
				}

				// 删除这些jar文件
				for (File file : files) {
					file.delete();
				}

				// 排出启动或刷新顺序
				List<BundleInfo> shouldStartBundleInfoList = getShouldRereshBundleInfoList(bundleInfoList);
				orderBundleInstallList(shouldStartBundleInfoList);
				// 按照逆序停止Bundle
				for (int i = 0; i < shouldStartBundleInfoList.size(); i++) {
					BundleInfo bundleInfo = shouldStartBundleInfoList
							.get(shouldStartBundleInfoList.size() - i - 1);
					String bundleName = bundleInfo.getBundleName();
					Bundle bundle = getBundleByName(bundleName);
					if (bundle.getState() == Bundle.ACTIVE) {
						System.out.println(String.format(
								"插件自动安装线程：根据依赖关系准备停止[%s]插件！", bundleName));
						bundle.stop();
					}
				}
				// 按照顺序启动或重启Bundle
				for (int i = 0; i < shouldStartBundleInfoList.size(); i++) {
					BundleInfo bundleInfo = shouldStartBundleInfoList.get(i);
					String bundleName = bundleInfo.getBundleName();
					Bundle bundle = getBundleByName(bundleName);
					if (bundle == null) {
						System.out.println(String
								.format("插件自动安装线程警告：在OSGi容器中未发现名称为[%s]的插件！",
										bundleName));
						continue;
					}
					if (bundle.getState() == Bundle.ACTIVE) {
						System.out.println(String.format(
								"插件自动安装线程警告：[%s]插件在启动之前，已经是启动状态！", bundleName));
					}
					System.out.println(String.format("插件自动安装线程：准备启动[%s]插件！",
							bundleName));
					bundle.start();
				}
			}
		} catch (InterruptedException e) {
			System.out.println("quickwebframework_web:插件自动管理线程接到线程中止命令，线程已终止！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据名称得到Bundle
	private Bundle getBundleByName(String bundleName) {
		BundleContext bundleContext = QuickWebFrameworkLoaderListener
				.getBundleContext();
		if (bundleContext == null) {
			return null;
		}
		Bundle[] bundles = bundleContext.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals(bundleName))
				return bundle;
		}
		return null;
	}

	// 得到OSGi容器中已安装的全部插件的信息列表
	private List<BundleInfo> getAllBundleInfoList() {
		BundleContext bundleContext = QuickWebFrameworkLoaderListener
				.getBundleContext();
		if (bundleContext == null) {
			return null;
		}

		List<BundleInfo> list = new ArrayList<BundleInfo>();

		Bundle[] bundles = bundleContext.getBundles();
		for (Bundle bundle : bundles) {
			try {
				URL url = bundle.getResource(METAINF_FILE_PATH);
				InputStream inputStream = url.openStream();
				list.add(new BundleInfo(inputStream));
			} catch (Exception ex) {
				System.out.println(String.format(
						"插件自动安装线程警告：读取插件[%s]的资源文件[%s]时出错，原因：[%s]",
						bundle.getSymbolicName(), METAINF_FILE_PATH, ex));
			}
		}
		return list;
	}

	// 得到应该刷新的Bundle
	private List<BundleInfo> getShouldRereshBundleInfoList(
			List<BundleInfo> installedBundleInfoList) {
		// 已安装全部插件信息列表
		List<BundleInfo> allBundleInfoList = getAllBundleInfoList();
		// 已安装全部插件信息Map
		Map<String, BundleInfo> allBundleInfoMap = new HashMap<String, BundleInfo>();
		for (BundleInfo bundleInfo : allBundleInfoList) {
			allBundleInfoMap.put(bundleInfo.getBundleName(), bundleInfo);
		}

		// 应该刷新的插件名称列表
		List<String> shouldRefreshBundleNameList = new ArrayList<String>();
		// 应该刷新的插件信息列表
		List<BundleInfo> shouldRefreshBundleInfoList = new ArrayList<BundleInfo>();

		for (BundleInfo bundleInfo : installedBundleInfoList) {
			shouldRefreshBundleNameList.add(bundleInfo.getBundleName());
			shouldRefreshBundleInfoList.add(bundleInfo);
		}

		for (int i = 0; i < shouldRefreshBundleInfoList.size(); i++) {
			BundleInfo bundleInfo = shouldRefreshBundleInfoList.get(i);
			String bundleName = bundleInfo.getBundleName();
			List<String> bundleExportPackageList = bundleInfo
					.getExportPackageList();

			// 搜索全部的Bundle
			for (BundleInfo tmpBundleInfo : allBundleInfoList) {
				boolean isTrue = false;

				String tmpBundleName = tmpBundleInfo.getBundleName();

				// 如果满足Require-Bundle依赖关系
				if (tmpBundleInfo.getRequireBundleNameList().contains(
						bundleName)) {
					isTrue = true;
				}
				// 否则如果满足Import-Package依赖关系
				else {
					List<String> tmpBundleInfoImportPackageList = tmpBundleInfo
							.getImportPackageList();
					for (String tmpBundleInfoImportPackage : tmpBundleInfoImportPackageList) {
						if (bundleExportPackageList
								.contains(tmpBundleInfoImportPackage)) {
							isTrue = true;
							break;
						}
					}
				}
				// 如果满足依赖关系，并且未加入应该刷新的列表中
				if (isTrue
						&& !shouldRefreshBundleNameList.contains(tmpBundleName)) {
					shouldRefreshBundleNameList.add(tmpBundleName);
					shouldRefreshBundleInfoList.add(tmpBundleInfo);
				}
			}
		}
		return shouldRefreshBundleInfoList;
	}
}
