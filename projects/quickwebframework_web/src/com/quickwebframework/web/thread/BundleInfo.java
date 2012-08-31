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
	private List<String> requireBundleNameList;

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

		Properties prop = getBundleManifestProperties(inputStream);

		// 得到插件的名称和版本
		bundleName = prop.getProperty("Bundle-SymbolicName");
		bundleVersion = Version
				.parseVersion(prop.getProperty("Bundle-Version"));
		String requireBundleAllString = prop.getProperty("Require-Bundle");

		if (requireBundleAllString != null) {
			String[] requireBundleVersionStringArray = requireBundleAllString
					.split(",");
			for (String requireBundleVersionString : requireBundleVersionStringArray) {
				requireBundleVersionString = requireBundleVersionString.trim();

				String requireBundleName = null;
				if (requireBundleVersionString.contains(";")) {
					requireBundleName = requireBundleVersionString.split(";")[0]
							.trim();
				} else {
					requireBundleName = requireBundleVersionString;
				}
				requireBundleNameList.add(requireBundleName);
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

}
