package top.xuyx.seafood.weixin;


import org.dom4j.*;
import top.xuyx.seafood.weixin.model.WeixinPayResult;

import java.io.IOException;
import java.util.*;


public class XmlUtils {
	public static void main(String[] args) throws DocumentException, IOException {
		String body = "<xml><appid><![CDATA[wx5a8ffaa872145cac]]></appid>" + 
				"<attach><![CDATA[wx]]></attach>" + 
				"<bank_type><![CDATA[CFT]]></bank_type>" + 
				"<cash_fee><![CDATA[10]]></cash_fee>" + 
				"<fee_type><![CDATA[CNY]]></fee_type>" + 
				"<is_subscribe><![CDATA[Y]]></is_subscribe>" + 
				"<mch_id><![CDATA[1503201241]]></mch_id>" + 
				"<nonce_str><![CDATA[hj1fetk5ce]]></nonce_str>" + 
				"<openid><![CDATA[ohj5_0ewhWugmPpwYtKuCv2FsfbU]]></openid>" + 
				"<out_trade_no><![CDATA[wx20180731234707794]]></out_trade_no>" + 
				"<result_code><![CDATA[SUCCESS]]></result_code>" + 
				"<return_code><![CDATA[SUCCESS]]></return_code>" + 
				"<sign><![CDATA[4A6ED5721EE4AF7CF8700531C9D58FBC]]></sign>" + 
				"<time_end><![CDATA[20180731234712]]></time_end>" + 
				"<total_fee>10</total_fee>" + 
				"<trade_type><![CDATA[JSAPI]]></trade_type>" + 
				"<transaction_id><![CDATA[4200000110201807317373561416]]></transaction_id>" + 
				"</xml>";
		Map<String, Object> xmlMap = xmlBody2map(body, "xml");
		WeixinPayResult result = WeiXinSDK.orderNotify(xmlMap);
		System.out.println("result>>" + result);
	}

	static Map<String, Object> xml2map(String xmlString) throws DocumentException {
		Document doc = DocumentHelper.parseText(xmlString);
		Element rootElement = doc.getRootElement();
		Map<String, Object> map = new HashMap<String, Object>();
		ele2map(map, rootElement);

		return map;
	}

	/**
	 * 根据xml消息体转化为Map
	 * 
	 * @param xml
	 * @param rootElement
	 * @return
	 * @throws DocumentException
	 */
	public static Map xmlBody2map(String xml, String rootElement) throws DocumentException {
		org.dom4j.Document doc = DocumentHelper.parseText(xml);
		Element body = (Element) doc.selectSingleNode("/" + rootElement);
		Map vo = __buildXmlBody2map(body);
		return vo;
	}

	private static Map __buildXmlBody2map(Element body) {
		Map vo = new HashMap();
		if (body != null) {
			List<Element> elements = body.elements();
			for (Element element : elements) {
				String key = element.getName();
				if (key != null && key.trim().length() > 0) {
					String type = element.attributeValue("type", "java.lang.String");
					String text = element.getText().trim();
					Object value = null;
					if (String.class.getCanonicalName().equals(type)) {
						value = text;
					} else if (Character.class.getCanonicalName().equals(type)) {
						value = new Character(text.charAt(0));
					} else if (Boolean.class.getCanonicalName().equals(type)) {
						value = new Boolean(text);
					} else if (Short.class.getCanonicalName().equals(type)) {
						value = Short.parseShort(text);
					} else if (Integer.class.getCanonicalName().equals(type)) {
						value = Integer.parseInt(text);
					} else if (Long.class.getCanonicalName().equals(type)) {
						value = Long.parseLong(text);
					} else if (Float.class.getCanonicalName().equals(type)) {
						value = Float.parseFloat(text);
					} else if (Double.class.getCanonicalName().equals(type)) {
						value = Double.parseDouble(text);
					} else if (java.math.BigInteger.class.getCanonicalName().equals(type)) {
						value = new java.math.BigInteger(text);
					} else if (java.math.BigDecimal.class.getCanonicalName().equals(type)) {
						value = new java.math.BigDecimal(text);
					} else if (Map.class.getCanonicalName().equals(type)) {
						value = __buildXmlBody2map(element);
					} else {
					}
					vo.put(key, value);
				}
			}
		}
		return vo;
	}

	static void ele2map(Map<String, Object> map, Element ele) {
		System.out.println(ele);
		// 获得当前节点的子节点
		List<Element> elements = ele.elements();
		if (elements.size() == 0) {
			// 没有子节点说明当前节点是叶子节点，直接取值即可
			map.put(ele.getName(), ele.getText());
		} else if (elements.size() == 1) {
			// 只有一个子节点说明不用考虑list的情况，直接继续递归即可
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ele2map(tempMap, elements.get(0));
			map.put(ele.getName(), tempMap);
		} else {
			// 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
			// 构造一个map用来去重
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (Element element : elements) {
				tempMap.put(element.getName(), null);
			}
			Set<String> keySet = tempMap.keySet();
			for (String string : keySet) {
				Namespace namespace = elements.get(0).getNamespace();
				List<Element> elements2 = ele.elements(new QName(string, namespace));
				// 如果同名的数目大于1则表示要构建list
				if (elements2.size() > 1) {
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for (Element element : elements2) {
						Map<String, Object> tempMap1 = new HashMap<String, Object>();
						ele2map(tempMap1, element);
						list.add(tempMap1);
					}
					map.put(string, list);
				} else {
					// 同名的数量不大于1则直接递归去
					Map<String, Object> tempMap1 = new HashMap<String, Object>();
					ele2map(tempMap1, elements2.get(0));
					map.put(string, tempMap1);
				}
			}
		}
	}
}