package com.quickwebframework.web.thread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Version;

import com.quickwebframework.web.util.IoUtil;

public class BundleInfo {
	private String bundleFilePath;
	private String bundleName;
	private Version bundleVersion;
	// 需要的Bundle名称列表
	private List<String> requireBundleNameList;
	// 导入的包列表
	private List<String> importPackageList;
	// 导出的包列表
	private List<String> exportPackageList;

	private Properties getBundleManifestProperties(InputStream inputStream)
			throws IOException {
		Properties prop = new Properties();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IoUtil.copyStream(inputStream, outputStream);

		byte[] buffer = outputStream.toByteArray();
		String text = new String(buffer, "utf-8");
		text = text.replace("\r\n ", "").replace("\n ", "");

		StringReader reader = new StringReader(text);
		prop.load(reader);
		return prop;
	}

	public BundleInfo(InputStream inputStream) throws IOException {
		requireBundleNameList = new ArrayList<String>();
		importPackageList = new ArrayList<String>();
		exportPackageList = new ArrayList<String>();

		Properties prop = getBundleManifestProperties(inputStream);

		// 得到插件的名称和版本
		bundleName = prop.getProperty("Bundle-SymbolicName");
		bundleVersion = Version
				.parseVersion(prop.getProperty("Bundle-Version"));

		// 需要的Bundle
		String requireBundleAllString = prop.getProperty("Require-Bundle");
		if (requireBundleAllString != null) {
			String[] lineArray = requireBundleAllString.split(",");
			for (String line : lineArray) {
				line = line.trim();

				String tmpName = null;
				if (line.contains(";")) {
					tmpName = line.split(";")[0].trim();
				} else {
					tmpName = line;
				}
				requireBundleNameList.add(tmpName);
			}
		}

		// 导入的包
		String importPackageAllString = prop.getProperty("Import-Package");
		if (importPackageAllString != null) {
			String[] lineArray = importPackageAllString.split(",");
			for (String line : lineArray) {
				line = line.trim();

				String tmpName = null;
				if (line.contains(";")) {
					tmpName = line.split(";")[0].trim();
				} else {
					tmpName = line;
				}
				importPackageList.add(tmpName);
			}
		}
		// 导出的包
		String exportPackageAllString = prop.getProperty("Export-Package");
		if (exportPackageAllString != null) {
			String[] lineArray = exportPackageAllString.split(",");
			for (String line : lineArray) {
				line = line.trim();

				String tmpName = null;
				if (line.contains(";")) {
					tmpName = line.split(";")[0].trim();
				} else {
					tmpName = line;
				}
				exportPackageList.add(tmpName);
			}
		}
	}

	public String getBundleFilePath() {
		return bundleFilePath;
	}

	public void setBundleFilePath(String bundleFilePath) {
		this.bundleFilePath = bundleFilePath;
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public Version getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(Version bundleVersion) {
		this.bundleVersion = bundleVersion;
	}

	public List<String> getRequireBundleNameList() {
		return requireBundleNameList;
	}

	public void setRequireBundleNameList(List<String> requireBundleNameList) {
		this.requireBundleNameList = requireBundleNameList;
	}

	public List<String> getImportPackageList() {
		return importPackageList;
	}

	public void setImportPackageList(List<String> importPackageList) {
		this.importPackageList = importPackageList;
	}

	public List<String> getExportPackageList() {
		return exportPackageList;
	}

	public void setExportPackageList(List<String> exportPackageList) {
		this.exportPackageList = exportPackageList;
	}
}
