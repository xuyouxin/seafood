package top.xuyx.seafood.controller;

import java.io.BufferedReader;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {
	/** 生成主键
	 * @return
	 */
	protected String generateId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 获取客户端IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * 获取ua
	 * @param request
	 * @return
	 */
	public static String getUserAgent(HttpServletRequest request) { 
		return request.getHeader("User-Agent"); 
	}
	
	/** 查询Cookie */
	public static String getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() != null && name != null) {
			for (Cookie one : request.getCookies()) {
				if (name.equals(one.getName())) {
					return one.getValue();
				}
			}
		}
		return null;
	}
	/** 添加Cookie */
	public static void addCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/** 添加Cookie */
	public static void addCookie2(HttpServletResponse response, String name, String value, String domain, String path) {
		Cookie cookie = new Cookie(name, value);
		if (path != null)
			cookie.setPath(path);
		if (domain != null)
			cookie.setDomain(domain);
		response.addCookie(cookie);
	}

	/** 添加Cookie */
	public static void addCookie(HttpServletResponse response, String name, String value, String domain, String path,
			Long timeOut) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(false);
		if (path != null)
			cookie.setPath(path);
		if (domain != null)
			cookie.setDomain(domain);

		if (timeOut != null) {
			cookie.setMaxAge(timeOut.intValue());
		}
		response.addCookie(cookie);
	}
	
	/**
	 * 以指定字符集读取请求体(Post)
	 * 
	 * @throws Exception
	 */
	public String readBody(HttpServletRequest request, String charset) throws Exception {
		if (charset == null || charset.trim().equals("")) {
			charset = "UTF-8";
		}
		request.setCharacterEncoding(charset);
		BufferedReader br = request.getReader();

		StringBuffer sb = new StringBuffer();
		int len = -1;
		char[] c = new char[256];

		try {
			while ((len = br.read(c)) != -1) {
				sb.append(c, 0, len);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			br.close();
		}

		return sb.toString();

	}
}
