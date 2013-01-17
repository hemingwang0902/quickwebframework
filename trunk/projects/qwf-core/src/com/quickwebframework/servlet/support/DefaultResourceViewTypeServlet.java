package com.quickwebframework.servlet.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;

import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;

public class DefaultResourceViewTypeServlet extends ViewTypeServlet {

	private static final long serialVersionUID = -1402692734489050382L;

	public static final String RESOURCE_SERVLET = "qwf-core.DefaultResourceViewTypeServlet";
	public static final String RESOURCE_PATH_PREFIX_PROPERTY_KEY = "qwf-core.DefaultResourceViewTypeServlet.resourcePathPrefix";
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-core.DefaultResourceViewTypeServlet.viewTypeName";

	private Map<String, String> mimeMap;
	// 资源路径统一前缀
	private String resourcePathPrefix;

	public DefaultResourceViewTypeServlet(String viewTypeName) {
		super(viewTypeName);

		mimeMap = new HashMap<String, String>();
		resourcePathPrefix = WebContext
				.getQwfConfig(RESOURCE_PATH_PREFIX_PROPERTY_KEY);
		intitDefaultMime();
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 获取插件名称与路径到request的属性中
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();

		// 如果有统一前缀，则添加统一前缀
		if (resourcePathPrefix != null) {
			pathName = resourcePathPrefix + pathName;
		}

		// 安全检测

		// 如果请求的资源路径没有后缀，则不允许访问
		if (!pathName.contains(".")) {
			// 返回400 Bad Request
			response.sendError(400, "请求的资源[" + pathName + "]未包括后缀，不允许访问！");
			return;
		}

		// 查找资源
		Bundle bundle = OsgiContext.getBundleByName(pluginName);
		if (bundle == null) {
			response.sendError(404, String.format("未找到名称为[%s]的插件!", pluginName));
			return;
		}
		URL url = bundle.getResource(pathName);
		if (url == null) {
			response.sendError(404,
					String.format("在插件[%s]中未找到资源[%s]!", pluginName, pathName));
			return;
		}
		// 设置Content-Type
		String[] tmpStrs = StringUtils.split(pathName, ".");
		if (tmpStrs.length > 1) {
			String fileExtenion = tmpStrs[tmpStrs.length - 1];
			if (mimeMap.containsKey(fileExtenion)) {
				response.setContentType(mimeMap.get(fileExtenion));
			}
		}
		// 输出
		InputStream input = url.openStream();
		OutputStream output = response.getOutputStream();
		IOUtils.copyLarge(input, output);
		output.flush();
		input.close();
	}

	// 初始化默认的MIME类型
	private void intitDefaultMime() {
		mimeMap.put("323", "text/h323");
		mimeMap.put("acx", "application/internet-property-stream");
		mimeMap.put("ai", "application/postscript");
		mimeMap.put("aif", "audio/x-aiff");
		mimeMap.put("aifc", "audio/x-aiff");
		mimeMap.put("aiff", "audio/x-aiff");
		mimeMap.put("asf", "video/x-ms-asf");
		mimeMap.put("asr", "video/x-ms-asf");
		mimeMap.put("asx", "video/x-ms-asf");
		mimeMap.put("au", "audio/basic");
		mimeMap.put("avi", "video/x-msvideo");
		mimeMap.put("axs", "application/olescript");
		mimeMap.put("bas", "text/plain");
		mimeMap.put("bcpio", "application/x-bcpio");
		mimeMap.put("bin", "application/octet-stream");
		mimeMap.put("bmp", "image/bmp");
		mimeMap.put("c", "text/plain");
		mimeMap.put("cat", "application/vndms-pkiseccat");
		mimeMap.put("cdf", "application/x-cdf");
		mimeMap.put("cer", "application/x-x509-ca-cert");
		mimeMap.put("class", "application/octet-stream");
		mimeMap.put("clp", "application/x-msclip");
		mimeMap.put("cmx", "image/x-cmx");
		mimeMap.put("cod", "image/cis-cod");
		mimeMap.put("cpio", "application/x-cpio");
		mimeMap.put("crd", "application/x-mscardfile");
		mimeMap.put("crl", "application/pkix-crl");
		mimeMap.put("crt", "application/x-x509-ca-cert");
		mimeMap.put("csh", "application/x-csh");
		mimeMap.put("css", "text/css");
		mimeMap.put("dcr", "application/x-director");
		mimeMap.put("der", "application/x-x509-ca-cert");
		mimeMap.put("dir", "application/x-director");
		mimeMap.put("dll", "application/x-msdownload");
		mimeMap.put("dms", "application/octet-stream");
		mimeMap.put("doc", "application/msword");
		mimeMap.put("dot", "application/msword");
		mimeMap.put("dvi", "application/x-dvi");
		mimeMap.put("dxr", "application/x-director");
		mimeMap.put("eps", "application/postscript");
		mimeMap.put("etx", "text/x-setext");
		mimeMap.put("evy", "application/envoy");
		mimeMap.put("exe", "application/octet-stream");
		mimeMap.put("fif", "application/fractals");
		mimeMap.put("flr", "x-world/x-vrml");
		mimeMap.put("gif", "image/gif");
		mimeMap.put("gtar", "application/x-gtar");
		mimeMap.put("gz", "application/x-gzip");
		mimeMap.put("h", "text/plain");
		mimeMap.put("hdf", "application/x-hdf");
		mimeMap.put("hlp", "application/winhlp");
		mimeMap.put("hqx", "application/mac-binhex40");
		mimeMap.put("hta", "application/hta");
		mimeMap.put("htc", "text/x-component");
		mimeMap.put("htm", "text/html");
		mimeMap.put("html", "text/html");
		mimeMap.put("htt", "text/webviewhtml");
		mimeMap.put("ico", "image/x-icon");
		mimeMap.put("ief", "image/ief");
		mimeMap.put("iii", "application/x-iphone");
		mimeMap.put("ins", "application/x-internet-signup");
		mimeMap.put("isp", "application/x-internet-signup");
		mimeMap.put("jfif", "image/pipeg");
		mimeMap.put("jpe", "image/jpeg");
		mimeMap.put("jpeg", "image/jpeg");
		mimeMap.put("jpg", "image/jpeg");
		mimeMap.put("js", "application/x-javascript");
		mimeMap.put("latex", "application/x-latex");
		mimeMap.put("lha", "application/octet-stream");
		mimeMap.put("lsf", "video/x-la-asf");
		mimeMap.put("lsx", "video/x-la-asf");
		mimeMap.put("lzh", "application/octet-stream");
		mimeMap.put("m13", "application/x-msmediaview");
		mimeMap.put("m14", "application/x-msmediaview");
		mimeMap.put("m3u", "audio/x-mpegurl");
		mimeMap.put("man", "application/x-troff-man");
		mimeMap.put("mdb", "application/x-msaccess");
		mimeMap.put("me", "application/x-troff-me");
		mimeMap.put("mht", "message/rfc822");
		mimeMap.put("mhtml", "message/rfc822");
		mimeMap.put("mid", "audio/mid");
		mimeMap.put("mny", "application/x-msmoney");
		mimeMap.put("mov", "video/quicktime");
		mimeMap.put("movie", "video/x-sgi-movie");
		mimeMap.put("mp2", "video/mpeg");
		mimeMap.put("mp3", "audio/mpeg");
		mimeMap.put("mpa", "video/mpeg");
		mimeMap.put("mpe", "video/mpeg");
		mimeMap.put("mpeg", "video/mpeg");
		mimeMap.put("mpg", "video/mpeg");
		mimeMap.put("mpp", "application/vndms-project");
		mimeMap.put("mpv2", "video/mpeg");
		mimeMap.put("ms", "application/x-troff-ms");
		mimeMap.put("mvb", "application/x-msmediaview");
		mimeMap.put("nws", "message/rfc822");
		mimeMap.put("oda", "application/oda");
		mimeMap.put("p10", "application/pkcs10");
		mimeMap.put("p12", "application/x-pkcs12");
		mimeMap.put("p7b", "application/x-pkcs7-certificates");
		mimeMap.put("p7c", "application/x-pkcs7-mime");
		mimeMap.put("p7m", "application/x-pkcs7-mime");
		mimeMap.put("p7r", "application/x-pkcs7-certreqresp");
		mimeMap.put("p7s", "application/x-pkcs7-signature");
		mimeMap.put("pbm", "image/x-portable-bitmap");
		mimeMap.put("pdf", "application/pdf");
		mimeMap.put("pfx", "application/x-pkcs12");
		mimeMap.put("pgm", "image/x-portable-graymap");
		mimeMap.put("pko", "application/yndms-pkipko");
		mimeMap.put("pma", "application/x-perfmon");
		mimeMap.put("pmc", "application/x-perfmon");
		mimeMap.put("pml", "application/x-perfmon");
		mimeMap.put("pmr", "application/x-perfmon");
		mimeMap.put("pmw", "application/x-perfmon");
		mimeMap.put("png", "image/png");
		mimeMap.put("pnm", "image/x-portable-anymap");
		mimeMap.put("pot", "application/vndms-powerpoint");
		mimeMap.put("ppm", "image/x-portable-pixmap");
		mimeMap.put("pps", "application/vndms-powerpoint");
		mimeMap.put("ppt", "application/vndms-powerpoint");
		mimeMap.put("prf", "application/pics-rules");
		mimeMap.put("ps", "application/postscript");
		mimeMap.put("pub", "application/x-mspublisher");
		mimeMap.put("qt", "video/quicktime");
		mimeMap.put("ra", "audio/x-pn-realaudio");
		mimeMap.put("ram", "audio/x-pn-realaudio");
		mimeMap.put("ras", "image/x-cmu-raster");
		mimeMap.put("rgb", "image/x-rgb");
		mimeMap.put("rmi", "audio/mid");
		mimeMap.put("roff", "application/x-troff");
		mimeMap.put("rtf", "application/rtf");
		mimeMap.put("rtx", "text/richtext");
		mimeMap.put("scd", "application/x-msschedule");
		mimeMap.put("sct", "text/scriptlet");
		mimeMap.put("setpay", "application/set-payment-initiation");
		mimeMap.put("setreg", "application/set-registration-initiation");
		mimeMap.put("sh", "application/x-sh");
		mimeMap.put("shar", "application/x-shar");
		mimeMap.put("sit", "application/x-stuffit");
		mimeMap.put("snd", "audio/basic");
		mimeMap.put("spc", "application/x-pkcs7-certificates");
		mimeMap.put("spl", "application/futuresplash");
		mimeMap.put("src", "application/x-wais-source");
		mimeMap.put("sst", "application/vndms-pkicertstore");
		mimeMap.put("stl", "application/vndms-pkistl");
		mimeMap.put("stm", "text/html");
		mimeMap.put("svg", "image/svg+xml");
		mimeMap.put("sv4cpio", "application/x-sv4cpio");
		mimeMap.put("sv4crc", "application/x-sv4crc");
		mimeMap.put("swf", "application/x-shockwave-flash");
		mimeMap.put("t", "application/x-troff");
		mimeMap.put("tar", "application/x-tar");
		mimeMap.put("tcl", "application/x-tcl");
		mimeMap.put("tex", "application/x-tex");
		mimeMap.put("texi", "application/x-texinfo");
		mimeMap.put("texinfo", "application/x-texinfo");
		mimeMap.put("tgz", "application/x-compressed");
		mimeMap.put("tif", "image/tiff");
		mimeMap.put("tiff", "image/tiff");
		mimeMap.put("tr", "application/x-troff");
		mimeMap.put("trm", "application/x-msterminal");
		mimeMap.put("tsv", "text/tab-separated-values");
		mimeMap.put("txt", "text/plain");
		mimeMap.put("uls", "text/iuls");
		mimeMap.put("ustar", "application/x-ustar");
		mimeMap.put("vcf", "text/x-vcard");
		mimeMap.put("vrml", "x-world/x-vrml");
		mimeMap.put("wav", "audio/x-wav");
		mimeMap.put("wcm", "application/vndms-works");
		mimeMap.put("wdb", "application/vndms-works");
		mimeMap.put("wks", "application/vndms-works");
		mimeMap.put("wmf", "application/x-msmetafile");
		mimeMap.put("wps", "application/vndms-works");
		mimeMap.put("wri", "application/x-mswrite");
		mimeMap.put("wrl", "x-world/x-vrml");
		mimeMap.put("wrz", "x-world/x-vrml");
		mimeMap.put("xaf", "x-world/x-vrml");
		mimeMap.put("xbm", "image/x-xbitmap");
		mimeMap.put("xla", "application/vndms-excel");
		mimeMap.put("xlc", "application/vndms-excel");
		mimeMap.put("xlm", "application/vndms-excel");
		mimeMap.put("xls", "application/vndms-excel");
		mimeMap.put("xlt", "application/vndms-excel");
		mimeMap.put("xlw", "application/vndms-excel");
		mimeMap.put("xof", "x-world/x-vrml");
		mimeMap.put("xpm", "image/x-xpixmap");
		mimeMap.put("xwd", "image/x-xwindowdump");
		mimeMap.put("z", "application/x-compress");
		mimeMap.put("zip", "application/zip");
	}

	@Override
	public String[] getUrls() {
		// TODO Auto-generated method stub
		return null;
	}
}
