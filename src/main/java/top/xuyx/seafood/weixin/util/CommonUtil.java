package top.xuyx.seafood.weixin.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtil {
	
	/**
	 * 产生100000-999999的随机数
	 * @return
	 */
	public static int random6Num() {
		int code = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
		if(Constant.devMode) {
			code = 123456; //开发测试时，验证码写死
		}
		return code;
	}
	
	/**
	 * 判断是否有效手机号
	 * @param phone
	 * @return
	 */
	public static boolean isMobilePhone(String phone) {
		if (phone == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[3-8])|(18[0-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}
	
	/**
	 * 构建xml
	 * @param tags
	 * @param values
	 * @return
	 */
	public static String buildXml(String[] tags, Map<String, Object> values) {
		StringBuffer xml = new StringBuffer();
		xml.append("<xml>");
		for(String tag : tags) {
			if(values.get(tag) != null) {
				xml.append(String.format("<%s>%s</%s>", tag, values.get(tag), tag));
			}
		}
		xml.append("</xml>");
		return xml.toString();
	}
}
