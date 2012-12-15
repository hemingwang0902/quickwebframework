package com.quickwebframework.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoUtil {

	/**
	 * 复制进度回调接口
	 * 
	 * @author 鹏
	 * 
	 */
	public interface CopyProgressCallback {

		/**
		 * 报告进度
		 * 
		 * @param downloadedBytesCount
		 *            已下载字节数
		 * @param totalBytesCount
		 *            总字节数
		 */
		public void reportProgress(Long downloadedBytesCount,
				Long totalBytesCount);
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {
		int bufferSize = 4096;
		copyStream(input, output, bufferSize);
	}

	public static void copyStream(InputStream input, OutputStream output,
			int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];

		int currentReadSize;
		do {
			currentReadSize = bufferSize;
			if (input.available() < bufferSize) {
				currentReadSize = input.available();
			}

			if (currentReadSize == 0) {
				break;
			}

			int readCount = input.read(buffer, 0, currentReadSize);
			output.write(buffer, 0, readCount);
		} while (true);
	}

	public static void copyStream(InputStream input, OutputStream output,
			long totalLength) throws IOException {
		int bufferSize = 4096;
		copyStream(input, output, bufferSize, totalLength);
	}

	public static void copyStream(InputStream input, OutputStream output,
			int bufferSize, long totalLength) throws IOException {
		copyStream(input, output, bufferSize, totalLength, null);
	}

	public static void copyStream(InputStream input, OutputStream output,
			int bufferSize, long totalLength,
			CopyProgressCallback copyProgressCallback) throws IOException {
		long position = 0;
		byte[] buffer = new byte[bufferSize];
		do {
			int CurrentReadSize = bufferSize;
			if ((totalLength - position) < bufferSize) {
				CurrentReadSize = (int) (totalLength - position);
			}
			if (CurrentReadSize > 0) {
				int readCount = input.read(buffer, 0, CurrentReadSize);
				output.write(buffer, 0, readCount);
				position += readCount;
				// 报告进度
				if (copyProgressCallback != null)
					copyProgressCallback.reportProgress(position, totalLength);
			}
		} while (position < totalLength);
	}

	public static byte[] readAllBytes(String fileName) {
		File file = new File(fileName);
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		byte[] buffer = new byte[(int) file.length()];
		try {
			FileInputStream fs = new FileInputStream(file);
			fs.read(buffer);
			fs.close();
			return buffer;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void writeAllText(String fileName, String content) {
		writeAllText(fileName, content, "utf-8");
	}

	public static void writeAllText(String fileName, String content,
			String encoding) {
		try {
			byte[] buffer = content.getBytes(encoding);
			writeAllBytes(fileName, buffer);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void writeAllBytes(String fileName, byte[] data) {
		File file = new File(fileName);
		try {
			file.createNewFile();
			FileOutputStream fs = new FileOutputStream(file);
			fs.write(data);
			fs.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// 删除文件或目录
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] subFolderFileArray = file.listFiles();
				for (File subFolderFile : subFolderFileArray) {
					deleteFile(subFolderFile.getAbsolutePath());
				}
			}
			file.delete();
		}
	}

	// 复制文件或目录
	public static void copyFile(String srcFilePath, String desFilePath) {
		try {
			File srcFile = new File(srcFilePath);
			if (srcFile.exists()) {
				File desFolderFile = new File(desFilePath/*
														 * + File.separator +
														 * srcFile.getName()
														 */);
				if (srcFile.isDirectory()) {
					// 如果目的目录不存在
					if (!desFolderFile.exists()) {
						// 创建目录
						if (!desFolderFile.mkdirs()) {
							throw new IOException(String.format("创建目录[%s]失败",
									desFolderFile.getAbsolutePath()));
						}
					}
					File[] subFolderFileArray = srcFile.listFiles();
					for (File subFolderFile : subFolderFileArray) {
						copyFile(
								subFolderFile.getAbsolutePath(),
								desFolderFile.getAbsolutePath()
										+ File.separator
										+ subFolderFile.getName());
					}
				} else {
					File desFile = new File(desFilePath
					/*
					 * + File.separator + srcFile.getName()
					 */);

					File desFileParentFile = desFile.getParentFile();
					// 查看父目录是否存在，如果不存在，则创建
					if (!desFileParentFile.isDirectory()) {
						desFileParentFile.mkdirs();
					}
					// 创建文件
					if (!desFile.createNewFile()) {
						throw new IOException(String.format("创建文件[%s]失败",
								desFile.getAbsolutePath()));
					}
					InputStream inputStream = new FileInputStream(srcFile);
					OutputStream outputStream = new FileOutputStream(desFile);
					copyStream(inputStream, outputStream);
					inputStream.close();
					outputStream.close();
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}