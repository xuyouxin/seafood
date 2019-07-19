package top.xuyx.seafood.weixin.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author:
 * @Date: 12-4-24 Time: 上午10:24
 * @Description: to write something
 */
public class OrderUtils {
	static SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");

	/**
	 * 生成下一个编号 yyMMddHHmmss+4位随机码
	 */
	public static synchronized String getLinkNo() {

		String code = format.format(Calendar.getInstance().getTime()) + getRandomString(4).toUpperCase();
		return code;
	}

	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getRandomNum(int length) {
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(System.currentTimeMillis());

			System.out.println(getLinkNo());
		}
	}

}
