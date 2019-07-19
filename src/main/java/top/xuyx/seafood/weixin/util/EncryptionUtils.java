package top.xuyx.seafood.weixin.util;


import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class EncryptionUtils {

	/**
	 * 字符串MD5加密,全部转化成大写
	 * 
	 * @param sourceStr
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getMD5(String sourceStr) {
		String resultStr = "";
		try {
			byte[] temp = sourceStr.getBytes();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(temp);
			// resultStr = new String(md5.digest());
			byte[] b = md5.digest();
			for (int i = 0; i < b.length; i++) {
				char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
				char[] ob = new char[2];
				ob[0] = digit[(b[i] >>> 4) & 0X0F];
				ob[1] = digit[b[i] & 0X0F];
				resultStr += new String(ob);
			}
			return resultStr;
		} catch (NoSuchAlgorithmException e) {
			log.error("getMD5 error with sourceStr=" + sourceStr + ";", e);
			return "";
		}
	}

	public static void main(String[] args) {

		System.out.println(getMD5("123456"));
	}
}
