package top.xuyx.seafood.weixin.util;

 
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtils {
	
	public static String sendGet(String url, String param) {
		return sendGet(url + "?" + param);
	}
	public static String sendGet(String url) {
		String result = "";// 访问返回结果
		BufferedReader in = null;// 读取访问结果
		try {
			String urlNameString = url;// 创建url
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();// 打开连接
			connection.setConnectTimeout(10 * 1000);//设置连接主机超时（单位：毫秒）
			connection.setReadTimeout(10 * 1000);//设置从主机读取数据超时（单位：毫秒）
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();// 建立连接
			
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 定义 BufferedReader输入流来读取URL的响应
			log.info("sendGet>>" + url);
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("sendGet error！", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				log.error("sendGet error, can not close inputstream！" + e2);
			}
		}
		log.info("sendGet success" );
		return result;
	}
	
	public static String httpPostBody(String url, String body) {
		return sendRequest("POST", url, null, body);
	}
	
	/*
	 * method :请求类型，post、put、delete
	 * urlString : 请求地址
	 * header ： 请求头
	 * body ：请求数据
	 */
	public static String sendRequest(String method, String urlString, HashMap<String,String> header, String body) {
		URL url = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		int code = 0;
		try {
			log.info("\nurlString>> " + urlString);
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			if(header != null) {
				for (String key : header.keySet()) {
					conn.setRequestProperty(key, header.get(key));
				}
			}
			conn.connect();
			if(StringUtils.isNotEmpty(body)) {
				log.info("body>> " + body);
				byte[] data = body.getBytes();
				dos = new DataOutputStream(conn.getOutputStream());
				dos.write(data);
				dos.flush();
			}
			code = conn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == code) {
				dis = new DataInputStream(conn.getInputStream());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = dis.read(buff)) != -1) {
					baos.write(buff, 0, len);
				}
				String result = new String(baos.toByteArray(), "utf-8");
				log.info("result>> " + result);
				return result;
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}

			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
		return null;
	}

}
