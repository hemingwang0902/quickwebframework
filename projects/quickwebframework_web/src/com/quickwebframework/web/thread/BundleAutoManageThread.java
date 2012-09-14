package com.quickwebframework.web.thread;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.osgi.framework.BundleContext;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;
import com.quickwebframework.web.util.BundleUtil;

public class BundleAutoManageThread extends Thread {

	public final static String METAINF_FILE_PATH = "META-INF/MANIFEST.MF";

	public String bundleFolderPath;

	public BundleAutoManageThread(String bundleFolderPath) {
		this.bundleFolderPath = bundleFolderPath;
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
				// 如果没有文件
				if (files == null || files.length == 0)
					continue;

				try {
					BundleUtil.installOrUpdateBundle(bundleContext, files);
				} catch (IOException ex) {
					System.out.println("插件自动管理线程：安装或更新插件时出现IOException异常,原因："
							+ ex.getMessage());
					continue;
				} catch (Throwable ex) {
					System.out.println("插件自动管理线程：安装或更新插件时出错。");
					ex.printStackTrace();
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
