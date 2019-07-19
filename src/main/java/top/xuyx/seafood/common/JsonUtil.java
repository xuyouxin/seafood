package top.xuyx.seafood.common;

import com.alibaba.fastjson.JSON;

public class JsonUtil {
	/**
	 * 字符串转对象
	 * @param json
	 * @param c
	 * @return
	 */
	public static <T> T jsonToObject(String json, Class<T> c) {
		try {
			return JSON.parseObject(json, c);
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * 对象转字符串
	 * @param object
	 * @return
	 */
	public static String objectToJson(Object object) {
		return JSON.toJSONString(object);
	}

}
