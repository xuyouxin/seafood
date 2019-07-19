package top.xuyx.seafood.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.xuyx.seafood.weixin.model.EventType;
import top.xuyx.seafood.weixin.model.WeiXinUserInfo;
import top.xuyx.seafood.weixin.model.WeixinPayResult;
import top.xuyx.seafood.weixin.util.*;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Component
public class WeiXinSDK {

	@Autowired
	private MyCache cache;

	private static final ResourceBundle RB = ResourceBundle.getBundle("config/weixin/weixin", Locale.getDefault(), WeiXinSDK.class.getClassLoader());
	// 本机外网IP
	public static String myWebIP = ServerHost.getWebIp();

	// 自动初始化Token (自动维护token 有效性)
	public static boolean AutoInitToken = Boolean.valueOf(RB.getString("AutoInitToken").trim());
	// 必须在微信中打开
	public static boolean WeixinIsMust = Boolean.valueOf(RB.getString("WeixinIsMust").trim());
	// AppID(应用ID)
	public static String AppID = RB.getString("AppID").trim();// wxf0b731ec2a2c7734
	// 受理商ID，身份标识
	public static String MCHID = RB.getString("MCHID").trim();// 微信支付商户号

	// 商户支付密钥Key。审核通过后，在微信发送的邮件中查看
	public static String KEY = RB.getString("ShangHu_API_KEY").trim();

	// JSAPI接口中获取openid，审核后在公众平台开启开发模式后可查看
	public static String APPSECRET = RB.getString("APPSECRET").trim();

	private static String JsApiTicket = null;
	// private static String AccessToken = null;

	// Code 授权后重定向的回调链接地址
	public static String RedirectUrl = RB.getString("RedirectUrl").trim();

	// 统一下单接口 订单通知地址
	public static String OrderNotify = RB.getString("OrderNotify").trim();

	// 独立的扫码通知地址 当公众平台接到扫码支付请求时，会回调此URL传递订单信息
	public static String QrNotify = RB.getString("QrNotify").trim();

	public static String Trade_Type_JSAPI = "JSAPI";// JSAPI--公众号支付
	public static String Trade_Type_NATIVE = "NATIVE";// NATIVE--原生扫码支付
	public static String Trade_Type_APP = "APP";// APP--app支付

	// 微信下单地址:JS下单
	final static String WeiXin_Order_Url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	// 微信下单地址:扫码下单
	final static String WeiXin_Order_Url_San = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	// 微信退款地址
	final static String WeiXin_Refund_Url = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	// 推送模版信息地址
	final static String WeiXin_Send_Template_Url_Format = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

	// 微信推送 接受地址
	final static String Server_URL = RB.getString("Server_URL").trim();
	// 微信推送 Token
	final static String Server_TOKEN = RB.getString("Server_TOKEN").trim();
	// 微信推送 消息加密密钥
	final static String Server_EncodingAESKey = RB.getString("Server_EncodingAESKey").trim();

	// 关注返回信息
	final static String join_return_content = RB.getString("join_return_content").trim();

	// 推送模版信息 统一备注信息
	final static String send_msg_remark = RB.getString("send_msg_remark").trim();

	// 微信场景值ID88:生成关注二维码,临时二维码图场景
	// final static String scene_id_tmp88 = "88";

	// MD5 签名
	public static String _md5Sign(Map<String, Object> values) {
		SortedMap<String, Object> smap = new TreeMap<String, Object>(values);
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> m : smap.entrySet()) {
			sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
		}
		sb.delete(sb.length() - 1, sb.length());
		String sign = EncryptionUtils.getMD5(sb.toString());
		return sign;
	}

	/**
	 * 获取 access_token 有效期7200秒
	 * @return
	 */
	public synchronized String doGetAccessToken(){

		String json = "";
		try {
			String weixinTokenKey = "weixin_access_token";
			String access_token = cache.get(weixinTokenKey);

			if (access_token == null) {// 缓存没有了再去获取
				String grant_type = "client_credential";// 获取access_token填写client_credential
				String appId = AppID;
				String secret = APPSECRET;
				String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s", grant_type, appId, secret);

				json = HttpUtils.sendGet(url);
				if (json != null && json.trim().length() > 0 && json.contains("{") && json.contains("}")) {
					if (json.contains("errcode")) {
						log.error(json);
					}
					JSONObject demoJson = JSON.parseObject(json);

					access_token = demoJson.getString("access_token");

					cache.set(weixinTokenKey, access_token, 7000);
					doGetTicket();
				} else {
					log.error("获取 access_token 失败 " + json);
				}
			}
			return access_token;

		} catch (Exception e) {
			log.error(json, e);
		}

		return null;

	}

	/**
	 * 获取jsapi_ticket 有效期7200秒 JSAPI 支付需要
	 * @return
	 */
	public synchronized String doGetTicket(){
		String json = "";
		try {
			String weixinTicketKey = "weixin_jsapi_ticket";
			String ticket = cache.get(weixinTicketKey);

			if (ticket == null) {// 缓存没有了再去获取

				String accessToken = doGetAccessToken();
				String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";// 这个url链接和参数不能变

				json = HttpUtils.sendGet(url);
				log.info(String.format("WeiXin doGetTicket:  %s ", json));
				JSONObject demoJson = JSONObject.parseObject(json);
				ticket = demoJson.getString("ticket");

				cache.set(weixinTicketKey, ticket, 7000);
			}
			return ticket;
		} catch (Exception e) {
			log.error(json, e);
		}

		return null;
	}

	/**
	 * 强制更新TOKEN
 	 */
	public synchronized boolean clearToken() throws Exception {
		String weixinTokenKey = "weixin_access_token";
		String access_token = cache.get(weixinTokenKey);

		// 缓存没有了再去获取
		String grant_type = "client_credential";// 获取access_token填写client_credential
		String appId = AppID;
		String secret = APPSECRET;
		String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s", grant_type, appId, secret);

		String json = HttpUtils.sendGet(url);
		JSONObject demoJson = JSON.parseObject(json);

		access_token = demoJson.getString("access_token");

		cache.set(weixinTokenKey, access_token, 7000);

		String weixinTicketKey = "weixin_jsapi_ticket";
		String ticket = cache.get(weixinTicketKey);

		String accessToken = doGetAccessToken();
		url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";// 这个url链接和参数不能变

		json = HttpUtils.sendGet(url);
		log.info(String.format("WeiXin doGetTicket:  %s ", json));
		demoJson = JSON.parseObject(json);
		ticket = demoJson.getString("ticket");

		cache.set(weixinTicketKey, ticket, 7000);

		log.info(String.format("WeiXin-clearToken access_token={%s} MD5={%s}", access_token, ticket));
		return true;

	}

	/**
	 * 微信通用MD5签名算法 map 生序 转a=b&b=c&c=d
	 * @param map
	 * @return
	 */
	public static String signMD5(Map<String, Object> map) {
		SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
		StringBuffer sbs = new StringBuffer();
		for (Map.Entry<String, Object> m : smap.entrySet()) {
			sbs.append(m.getKey()).append("=").append(m.getValue()).append("&");
		}
		sbs.append("key=").append(WeiXinSDK.KEY);

		String sign = EncryptionUtils.getMD5(sbs.toString());
		log.info(String.format("WeiXin signMD5:deeplink={%s} MD5={%s}", sbs, sign));
		return sign;
	}

	public static String signSHA1(Map<String, Object> map) {

		SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
		StringBuffer sbs = new StringBuffer();
		for (Map.Entry<String, Object> m : smap.entrySet()) {
			sbs.append(m.getKey()).append("=").append(m.getValue()).append("&");
		}
		sbs.delete(sbs.length() - 1, sbs.length());
		String decript = sbs.toString();
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer sign = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					sign.append(0);
				}
				sign.append(shaHex);
			}

			return sign.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String signSHA1(String str) {
		String decript = str;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer sign = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					sign.append(0);
				}
				sign.append(shaHex);
			}

			return sign.toString();

		} catch (Exception e) {
			log.info("WeiXin signSHA1 ", e);
		}
		return "";
	}

	public String getJsApiTicket() {
		if (JsApiTicket == null) {
			JsApiTicket = doGetTicket();
		}
		return JsApiTicket;
	}

	/**
	 * 获取关注的微信用户信息
	 *
	 * @param openID
	 * @return
	 */
	public WeiXinUserInfo getWeixinUserInfo(String openID) {

		try {
			String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

			String json = HttpUtils.sendGet(String.format(url, doGetAccessToken(), openID));
			if (json.contains("errcode")) {
				log.error(String.format("WeiXin 获取用户信息失败:%s", json));
			} else {
				json = EmojiFilter.filterEmoji(json);

				WeiXinUserInfo user = JSON.parseObject(json, WeiXinUserInfo.class);
				return user;
			}

		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}
	////////////// 微信公众号菜单相关////////////////

	/**
	 * 创建或更新菜单
	 * 
	 * @param menuJson
	 *            菜单 json格式
	 * @return
	 */
	public boolean menuCreate(String menuJson) {

		String json = "";
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";

			String body = menuJson;

			json = HttpUtils.httpPostBody(String.format(url, doGetAccessToken()), body);

			if (json != null && json.trim().length() > 0 && json.contains("{") && json.contains("}")) {

				JSONObject jsObj = JSON.parseObject(json);
				String code = jsObj.getString("errcode");
				if (code.equals("0")) {
					return true;
				}

			} else {
				log.error(json);
			}

		} catch (Exception e) {
			log.error(json, e);
		}

		return false;
	}

	/**
	 * 查询菜单
	 * 
	 * @return menuJson
	 */
	public String menuGet() {

		String json = "";
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=%s";

			json = HttpUtils.sendGet(String.format(url, doGetAccessToken()));

			if (json != null && json.trim().length() > 0 && json.contains("{") && json.contains("}")) {

				return json;

			} else {
				log.error(json);
			}

		} catch (Exception e) {
			log.error(json, e);
		}

		return json;
	}

	////////////// 微信公众号信息推送相关////////////////

	/**
	 * 获取临时关注二维码
	 * 
	 * @param timeOut
	 *            二维码超时时间-默认1小时
	 * @param sceneId
	 *            场景ID-可用于标示用户(支持1--100000)
	 * @return
	 */
	private JSONObject doTmpQR_SCENE(String timeOut, int sceneId) {

		long outSecond = timeOut == null ? 60 * 60l : Long.parseLong(timeOut);
		if (sceneId < 0 || sceneId > 100000) {
			return null;
		}
		String json = "";
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";

			String jsonFomat = "{\"expire_seconds\": %s, \"action_name\": \"QR_SCENE\",\"action_info\": {\"scene\": {\"scene_id\": %s}}}";
			String body = String.format(jsonFomat, outSecond, sceneId);

			json = HttpUtils.httpPostBody(String.format(url, doGetAccessToken()), body);

			if (json != null && json.trim().length() > 0 && json.contains("{") && json.contains("}")) {

				if (json.contains("errcode")) {
					log.error(json);
				}
				JSONObject jsObj = JSON.parseObject(json);
				return jsObj;
			} else {
				log.error(json);
			}

		} catch (Exception e) {
			log.error(json, e);
		}

		return null;
	}

	/**
	 * 返回微信提供的关注二维码图片链接地址
	 *
	 * @param timeOut
	 * @param sceneId
	 * @return
	 */
	public String getWeiXinTmpQR_Link(String timeOut, int sceneId) {

		try {
			JSONObject jsObj = doTmpQR_SCENE(timeOut, sceneId);
			if (jsObj != null) {
				String ticket = (String) jsObj.get("ticket");
				if (ticket != null) {
					ticket = java.net.URLEncoder.encode(ticket, "UTF-8");
					return String.format("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s", ticket);
				}
			}

		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * 返回平台自身提供的关注二维码图片 （根据URL 生成二维码图）
	 *
	 * @param timeOut
	 * @param sceneId
	 * @return
	 */
	public String getWeiXinTmpQR_URL(String timeOut, int sceneId) {

		try {
			JSONObject jsObj = doTmpQR_SCENE(timeOut, sceneId);
			if (jsObj != null) {
				String url = (String) jsObj.get("url");
				return url;
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 验证微信推送
	 *
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	public static boolean signature(String signature, String timestamp, String nonce, String echostr) {

		try {
			String[] check = { WeiXinSDK.Server_TOKEN, timestamp, nonce };
			Arrays.sort(check);
			StringBuilder str = new StringBuilder();
			str.append(check[0]).append(check[1]).append(check[2]);
			String sign = WeiXinSDK.signSHA1(str.toString());

			if (sign.equals(signature)) {
				return true;
			} else {
				log.error(String.format("WeiXin 推送签名失败:{signature=%s}{timestamp=%s}{nonce=%s}{echostr=%s}", signature, timestamp, nonce, echostr));
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return false;
	}

	/**
	 * 接收微信推送信息
	 *
	 * @param xmlMap
	 * @return
	 */
	public static EventType getEventType(Map<String, Object> xmlMap) {

		try {
			Map<String, Object> map = xmlMap;

			String MsgType = (String) map.get("MsgType");

			if (MsgType.equalsIgnoreCase("text")) {
				return EventType.Text;
			} else if (MsgType.equalsIgnoreCase("event")) {
				String Event = (String) map.get("Event");
				return isEvent(Event);
			} else if (MsgType.equalsIgnoreCase("image")) {
				return EventType.Image;
			}
		} catch (Exception e) {
			log.error(String.format("接收微信推送信息 异常:xml(%s)", xmlMap), e);
		}

		return EventType.Other;
	}

	/**
	 * * 如果是事件 获取事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
	 * 
	 * <pre>
	 * <xml>
	 * <ToUserName><![CDATA[toUser]]></ToUserName>
	 * <FromUserName><![CDATA[FromUser]]></FromUserName>
	 * <CreateTime>123456789</CreateTime>
	 * <MsgType><![CDATA[event]]></MsgType>
	 * <Event><![CDATA[SCAN]]></Event>
	 * <EventKey><![CDATA[SCENE_VALUE]]></EventKey>
	 * <Ticket><![CDATA[TICKET]]></Ticket>
	 * </xml>
	 * </pre>
	 */
	public static String getEventKey(Map<String, Object> xmlMap) {
		Map<String, Object> map = xmlMap;
		String key = (String) map.get("EventKey");
		if (key != null) {
			key = key.replace("qrscene_", "");
		}
		return key;
	}

	/**
	 * 获取二维码的ticket，可用来换取二维码图片
	 *
	 * @param xmlMap
	 * @return
	 */
	public static String getEventTicket(Map<String, Object> xmlMap) {
		Map<String, Object> map = xmlMap;
		String Ticket = (String) map.get("Ticket");
		return Ticket;
	}

	/**
	 * 获取OpenID
	 *
	 * @param xmlMap
	 * @return
	 */
	public static String getOpenID(Map<String, Object> xmlMap) {
		Map<String, Object> map = xmlMap;
		String openId = (String) map.get("FromUserName");
		return openId;
	}

	/**
	 * <pre>
	 * 	 <ToUserName><![CDATA[toUser]]></ToUserName>
	 * 	 <FromUserName><![CDATA[fromUser]]></FromUserName>
	 * 	 <CreateTime>1348831860</CreateTime>
	 * 	 <MsgType><![CDATA[text]]></MsgType>
	 * 	 <Content><![CDATA[this is a test]]></Content>
	 * 	 <MsgId>1234567890123456</MsgId>
	 * 	 </xml>
	 * </pre>
	 */
	private static Object isText(final String openid, final Map<String, Object> map) {

		try {
			// TODO
		} catch (Exception e) {
			log.error("", e);
		}

		return "";
	}

	/**
	 * <pre>
	 * 	 <ToUserName><![CDATA[toUser]]></ToUserName>
	 * 	 <FromUserName><![CDATA[fromUser]]></FromUserName>
	 * 	 <CreateTime>1348831860</CreateTime>
	 * 	 <MsgType><![CDATA[text]]></MsgType>
	 * 	 <Content><![CDATA[this is a test]]></Content>
	 * 	 <MsgId>1234567890123456</MsgId>
	 * 	 </xml>
	 * </pre>
	 */
	private static Object isImage(final String openid, final Map<String, Object> map) {

		try {
			// TODO
		} catch (Exception e) {
			log.error("", e);
		}

		return "";
	}

	/**
	 * <pre>
	 * <xml>
	 * <ToUserName><![CDATA[toUser]]></ToUserName>
	 * <FromUserName><![CDATA[FromUser]]></FromUserName>
	 * <CreateTime>123456789</CreateTime>
	 * <MsgType><![CDATA[event]]></MsgType>
	 * <Event><![CDATA[subscribe]]></Event>
	 * </xml>
	 * </pre>
	 */
	private static EventType isEvent(String Event) {

		try {

			if (Event == null) {
				return EventType.Other;
			} else if ("subscribe".equals(Event)) {
				return EventType.SubscribeEvent;
			} else if ("unsubscribe".equals(Event)) {// 取消关注事件

				return EventType.UnsubscribeEvent;
			} else if ("SCAN".equals(Event)) {// 二维码扫描
				return EventType.ScanEvent;

			} else if ("LOCATION".equals(Event)) {// 登录地理上报事件
				return EventType.LocationEvent;

			} else if ("CLICK".equals(Event)) {// 菜单点击

				return EventType.ClickEvent;

			} else if ("TEMPLATESENDJOBFINISH".equals(Event)) {// 模版消息发送任务完成通知
				return EventType.TempSendFinish;
			} else {

			}

		} catch (Exception e) {
			log.error("", e);
		}

		return EventType.Other;
	}

	////////////// 授权相关////////////////

	/**
	 * 微信Code请求:用户同意授权，获取code
	 * 
	 * @param isScope
	 *            false 静默授权; true 手动人工确认授权
	 * @param state
	 *            重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
	 * @param redirecyURL
	 *            授权后重定向的回调链接地址，请使用urlencode对链接进行处理
	 *            (redirect_uri/?code=CODE&state=STATE)
	 * @return
	 * @throws IOException
	 */
	public static String getCodeWeixinURL(boolean isScope, String state, String redirecyURL) throws IOException {
		String scope = "";
		if (isScope) {
			scope = "snsapi_userinfo";// 授权需要用户手动同意,已关注公众号的用户 是静默的无需手动同意
		} else {
			scope = "snsapi_base";// 静默授权 :不弹出授权页面
		}
		if (state == null) {
			state = "";
		}

		try {
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
			String redirect_uri = java.net.URLEncoder.encode(redirecyURL, "UTF-8");// 需要
																					// 网页授权域名
			return String.format(url, WeiXinSDK.AppID, redirect_uri, scope, state);
		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * 微信Code通知: 并根据 code换取网页授权access_token 和openid
	 * 
	 * @param code
	 *            微信平台推送过来的 code
	 * @param state
	 *            重定向后会带上state参数
	 * @return 用户openid
	 * @throws IOException
	 */
	public static String codeNotify(String code, String state) throws IOException {

		try {
			if (state == null) {
				state = "";
			}
			if (code != null && code.length() > 0) { // 换取token 和 openid
				String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

				String json = HttpUtils.sendGet(String.format(url, WeiXinSDK.AppID, WeiXinSDK.APPSECRET, code));
				log.error(String.format("WeiXin Code换取openId结果: %s", json));
				JSONObject jsObj = JSON.parseObject(json);

				String openId = jsObj.getString("openid");
				if (openId != null) {
					return openId;
				}

			}

		} catch (Exception e) {
			log.error("", e);
		}
		log.error("WeiXin Code获取失败:(用户取消授权或网络异常),系统自动再次跳转至获取页");

		return null;
	}

	///////////// 订单相关/////////////////

	/**
	 * 统一接口下单
	 *
	 * @param title
	 * @param priceRMB
	 * @param openId
	 * @param linkNo
	 * @param IP
	 * @return
	 */
	public static String WXOrderJSAPI(String title, BigDecimal priceRMB, String openId, String linkNo, String IP) {

		return WXOrderJSAPI(title, priceRMB, openId, linkNo, IP, null);

	}

	/**
	 * 统一接口下单 JSAPI
	 *
	 * @param title
	 * @param priceRMB
	 * @param openId
	 * @param linkNo
	 * @param IP
	 * @param notifyUrl
	 * @return
	 */
	public static String WXOrderJSAPI(String title, BigDecimal priceRMB, String openId, String linkNo, String IP, String notifyUrl) {

		if (notifyUrl == null) {
			notifyUrl = WeiXinSDK.OrderNotify;
		}

		// 统一下单接口,获取微信订单ID
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("appid", WeiXinSDK.AppID);// 公众账号ID
		model.put("attach", "wx");// 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
		model.put("body", title);// 商品描述
		model.put("mch_id", WeiXinSDK.MCHID);// 商户号

		model.put("nonce_str", OrderUtils.getRandomString(10));// 随机串,主要保证签名不可预测
		model.put("notify_url", notifyUrl);//
		model.put("openid", openId); // trade_type=JSAPI时（即公众号支付），此参数必传 用户标识:
										// 需要用户关注后取得唯一标示（获取方式分移动和PC）

		model.put("out_trade_no", linkNo);// 商户订单号
		model.put("spbill_create_ip", IP);// APP和网页支付提交用户端ip
		model.put("total_fee", priceRMB.multiply(new BigDecimal(100)).intValue()); // 总金额：

		model.put("trade_type", WeiXinSDK.Trade_Type_JSAPI); // 支付类型

		String sign = WeiXinSDK.signMD5(model);

		model.put("sign", sign); // 微信签名
		try {
			String xml = buildOrderXml(model);

			log.error(String.format("WeiXin统一下单请求:(%s)", xml));

			String reXml = HttpUtils.httpPostBody(WeiXin_Order_Url, xml);
			log.error(String.format("WeiXin统一下单返回:(%s)", reXml));

			return reXml;

		} catch (Exception e) {

			log.error("", e);
		}
		return "";
	}
	
	private static String buildOrderXml(Map<String, Object> model) {
		String[] tags = {"appid", "attach", "body", "mch_id", "nonce_str", "notify_url", "openid", "product_id", 
				"out_trade_no", "spbill_create_ip", "total_fee", "trade_type", "sign"};
		return CommonUtil.buildXml(tags, model);
	}

	/**
	 * 组装H5支付需要的map
	 *
	 * @param xmlBody
	 * @return
	 */
	public static Map<String, Object> combineH5PayObject(String xmlBody) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String beginStr = "<return_msg><![CDATA[";
		String endStr = "]]></return_msg>";
		String status = StringUtils.substringBetween(xmlBody, beginStr, endStr);
		if("OK".equals(status)) {
			beginStr = "<prepay_id><![CDATA[";
			endStr = "]]></prepay_id>";
			String prepayId = StringUtils.substringBetween(xmlBody, beginStr, endStr);
			retMap.put("appId", WeiXinSDK.AppID);
			retMap.put("nonceStr", OrderUtils.getRandomString(10));
			retMap.put("package", "prepay_id="+prepayId);

			retMap.put("signType", "MD5");
			retMap.put("timeStamp", String.valueOf(System.currentTimeMillis()/1000));

			String sign = signMD5(retMap);
			retMap.put("paySign", sign);
		}

		return retMap;

	}

	/**
	 * 统一接口下单 NATIVE扫码订单
	 * 
	 * @param linkNo
	 *            订单号
	 * @param title
	 *            订单标题
	 * @param priceRMB
	 *            订单总金额 (分)
	 * @param notifyUrl
	 *            支付通知接受地址
	 * @return
	 */
	public static String WXOrderNATIVE(String linkNo, String title, long priceRMB, String notifyUrl) {

		// if (notifyUrl == null) {
		//
		// notifyUrl = WeiXinSDK.QrNotify;
		//
		// notifyUrl = WeiXinSDK.OrderNotify;
		// }

		notifyUrl = notifyUrl == null ? QrNotify : notifyUrl == null ? OrderNotify : notifyUrl;

		// 统一下单接口,获取微信订单ID
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("appid", WeiXinSDK.AppID);// 公众账号ID
		model.put("attach", "wx");// 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
		model.put("body", title);// 商品描述
		model.put("mch_id", WeiXinSDK.MCHID);// 商户号

		model.put("nonce_str", OrderUtils.getRandomString(10));// 随机串,主要保证签名不可预测
		model.put("notify_url", notifyUrl);
		model.put("product_id", linkNo);

		model.put("out_trade_no", linkNo);// 商户订单号
		model.put("spbill_create_ip", WeiXinSDK.myWebIP);// 调用微信接口的 服务器地址
		model.put("total_fee", priceRMB); // 总金额：

		model.put("trade_type", WeiXinSDK.Trade_Type_NATIVE); // 支付类型

		String sign = WeiXinSDK.signMD5(model);

		model.put("sign", sign); // 微信签名

		try {
			String xml = buildOrderXml(model);

			log.info(String.format("WeiXin统一下单请求:(%s)", xml));

			String reXml = HttpUtils.httpPostBody(WeiXin_Order_Url, xml);
			log.info(String.format("WeiXin统一下单返回:(%s)", reXml));
			return reXml;

		} catch (Exception e) {

			log.info("", e);
		}
		return "";

	}

	// 统一接口下单: 获取预支付交易会话标识 用于后续接口调用中使用，该值有效期为2小时
	public static String getWXOrderPrepayId(String xmlBody) {
		String beginStr = "<prepay_id><![CDATA[";
		String endStr = "]]></prepay_id>";
		String prepayId = StringUtils.substringBetween(xmlBody, beginStr, endStr);
		if (prepayId.length() > 0) {
			return prepayId;
		}
		return null;

	}

	// 统一接口下单: 二维码链接，展示给用户进行扫码支付
	public static String getWXOrderQrUrl(String xmlBody) {
		String beginStr = "<code_url><![CDATA[";
		String endStr = "]]></code_url>";
		String codeUrl = StringUtils.substringBetween(xmlBody, beginStr, endStr);
		if (codeUrl.length() > 0) {
			return codeUrl;
		}
		return null;

	}

	/**
	 * 统一接口下单: 获取失败信息
	 *
	 * @param xmlBody
	 * @return
	 */
	public static String getWXOrderErrMsg(String xmlBody) {
		String beginStr = "<return_msg><![CDATA[";
		String endStr = "]]></return_msg>";
		String err = StringUtils.substringBetween(xmlBody, beginStr, endStr);
		return err;
	}

	/**
	 * 统一接口下单: 获取失败信息
	 *
	 * @param xmlMap
	 * @return
	 */
	public static boolean getWXIsPayOk(Map<String, Object> xmlMap) {
		Object result_code = xmlMap.get("result_code");

		if ("SUCCESS".equals(result_code)) {// 用户付款成功,调充值接口
			return true;
		}
		return false;
	}

	/**
	 * 统一接口下单: 支付成功后 获取订单ID 或流水号
	 *
	 * @param xmlBody
	 * @return
	 */
	public static String getWXLinkNo(String xmlBody) {

		/**
		 * <out_trade_no><![CDATA[160818175723WOT7]]></out_trade_no>
		 * <result_code><![CDATA[SUCCESS]]></result_code>
		 **/

		String okStr = "<result_code><![CDATA[SUCCESS]]></result_code>";
		if (xmlBody != null && xmlBody.indexOf(okStr) > 0) {

			String beginStr = "<out_trade_no><![CDATA[";
			String endStr = "]]></out_trade_no>";

			String id = StringUtils.substringBetween(xmlBody, beginStr, endStr);
			return id;
		}

		return null;
	}

	/**
	 * 微信订单通知:接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
	 * 
	 * @param weixinXML
	 *            微信post 的xml
	 * @return< 订单号，支付成功ture或失败false>
	 * 
	 */
	public static WeixinPayResult orderNotify(Map<String, Object> xmlMap) throws IOException {
		WeixinPayResult result = new WeixinPayResult();
		try {

			Map<String, Object> map = xmlMap;

			Object result_code = map.get("result_code");

			if ("SUCCESS".equals(result_code)) {// 用户付款成功,调充值接口

				String sign = map.remove("sign").toString();

				String mgSign = WeiXinSDK.signMD5(map);

				if (mgSign.equals(sign)) {// 验证签名

					String linkNo = map.get("out_trade_no").toString();

					result.setLinkNo(linkNo);
					result.setReturnInfo("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
					result.setOk(true);
					// 返回接受成功
					return result;
				} else {
					log.info(String.format("WeiXin 通知订号 验证签名错误,非法通知"));
				}
			}

		} catch (Exception e) {
			log.error("", e);
		}

		result.setReturnInfo("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名或参数错误]]></return_msg></xml>");
		result.setOk(false);

		return result;

	}

	/**
	 * 生产微信商品订单二维码
	 * 
	 * @param timeStamp
	 *            微信 时间戳
	 * @param nonceStr
	 *            随机字符串
	 * @param productId
	 *            商品id 或者订单号
	 * @return null 失败 ;返回二维码链接地址( 需要工具生成二维码图片)
	 */
	public static String creatPayQR(String timeStamp, String nonceStr, String productId) {

		String format = "appid=%s&mch_id=%s&nonce_str=%s&product_id=%s&time_stamp=%s";

		String param = String.format(format, WeiXinSDK.AppID, WeiXinSDK.MCHID, nonceStr, productId, timeStamp);
		format = "weixin://wxpay/bizpayurl?%s&sign=%s";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appid", WeiXinSDK.AppID);
		map.put("mch_id", WeiXinSDK.MCHID);
		map.put("nonce_str", nonceStr);
		map.put("product_id", productId);
		map.put("time_stamp", timeStamp);

		String sign = WeiXinSDK.signMD5(map);

		return String.format(format, param, sign);

	}

	/**
	 * 退款申请接口
	 *
	 * @param priceRMB
	 * @param linkNo
	 * @return
	 */
	public static boolean refund(long priceRMB, String linkNo) {

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("appid", WeiXinSDK.AppID);// 公众账号ID

		model.put("mch_id", WeiXinSDK.MCHID);// 商户号
		model.put("op_user_id", WeiXinSDK.MCHID);// 操作员帐号, 默认为商户号

		model.put("nonce_str", OrderUtils.getRandomString(10));// 随机串,主要保证签名不可预测

		model.put("out_trade_no", linkNo);// 商户订单号
		model.put("out_refund_no", linkNo);// 商户退款单号

		model.put("total_fee", priceRMB); // 总金额
		model.put("refund_fee", priceRMB); // 退款金额

		String sign = WeiXinSDK.signMD5(model);

		model.put("sign", sign); // 微信签名

		String xml = "";
		String reXml = "";
		try {
//			xml = freeMarker.processString("refund.xml", model);
			log.info(String.format("WeiXin退款请求:(%s)", xml));

			reXml = doRefund(WeiXin_Refund_Url, xml);
			log.info(String.format("WeiXin退款返回:(%s)", reXml));
		} catch (Exception e) {
			log.error("", e);
		}
		return refundCheck(reXml);

	}

	private static String doRefund(String url, String reuqestXml) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");

		String path = new WeiXinSDK().getClass().getResource("/").getFile() + "apiclient_cert.p12";
		FileInputStream instream = new FileInputStream(new File(path));// 放退款证书的路径
		try {
			keyStore.load(instream, WeiXinSDK.MCHID.toCharArray());
		} finally {
			instream.close();
		}

		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, WeiXinSDK.MCHID.toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		try {

			HttpPost httpPost = new HttpPost(url);// 退款接口

			System.out.println("executing request" + httpPost.getRequestLine());
			StringEntity reqEntity = new StringEntity(reuqestXml);
			// 设置类型
			reqEntity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				// HttpEntity entity = response.getEntity();
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

	}

	/**
	 * 推送退款信息
	 *
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public String sendRefundMsg(String json) throws Exception {

		// Map<String, Object> model = new HashMap<String, Object>();
		//
		// Map<String, String> dataMap = new HashMap<String, String>();
		//
		// model.put("template_id",
		// "UODbQ0Drj5cPVnjodlqONxs5apW0ZqzBtu4k7kaT3ys");// 退款模版
		// model.put("touser", touser);// 退款人
		// model.put("url", "http://popo.leyif.com/weixin/orderList");// 订单中心
		//
		// dataMap.put("first", "您好,您的流量未充值成功,系统已自动退款,请注意查收!");// 内容描述
		// dataMap.put("reason", "流量充值失败");// 退款原因
		// dataMap.put("refund", String.format("%.2f", priceRMB / 100f));// 退款金额
		// dataMap.put("remark", "(退款订单号:" + linkNo + ")" + send_msg_remark);//
		// 备注
		// model.put("dataMap", dataMap);
		//
		// String json = freeMarker.processString("weixin_template.json",
		// model);

		String rejson = HttpUtils.httpPostBody(String.format(WeiXin_Send_Template_Url_Format, doGetAccessToken()), json);

		return rejson;

	}

	/**
	 * 退款申请是否成功检查
	 *
	 * @param xml
	 * @return
	 */
	public static boolean refundCheck(String xml) {

		String has1 = "<return_code><![CDATA[SUCCESS]]></return_code>";
		String has2 = "<result_code><![CDATA[SUCCESS]]></result_code>";
		if (xml != null && xml.contains(has1) && xml.contains(has2)) {
			return true;
		}
		return false;
	}

	public static void main2(String[] args) {

		try {
			WeiXinSDK sdk = new WeiXinSDK();
			// 1、获取AccessToken
			String accessToken = sdk.doGetAccessToken();

			// 2、获取Ticket
			String jsapi_ticket = sdk.doGetTicket();

			// 随机字符串
			String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);// 随机字符串
			// 时间戳
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);// 时间戳

			// 当前页面所在url
			String url = "http://wwww.com/weixin/order";

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("jsapi_ticket", jsapi_ticket);
			map.put("noncestr", noncestr);
			map.put("timestamp", timestamp);
			map.put("url", url);

			// 3、将字符串进行sha1加密
			String signature = signSHA1(map);
			System.out.println("参数：" + map + "\n签名：" + signature);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void main4(String[] args) {

		creatPayQR("1471511703", "sad", "dsfsds");

		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("appId", "wx475ceed370216aa2");
			map.put("nonceStr", "e61463f8efa94090b1f366cccfbbb444");
			map.put("package", "prepay_id=wx20180110185708a5e21025250979246265");

			map.put("signType", "MD5");
			map.put("timeStamp", "1515581888");

			String sss = signMD5(map);
			System.out.println("参数：" + map + "\n签名：" + sss);

			String sssss = "appid=wxf0b731ec2a2c7745&nonceStr=st6l9demdw&package=prepay_id=wx2016081817150878efa321a10327413544&signType=MD5&timeStamp=1471511703&key=m9turA2UEzUWF4X79oNT2VGBBcfKGsPr";
			String sign = EncryptionUtils.getMD5(sssss.toString()).toUpperCase();

			System.out.println("参数：" + map + "\n签名：" + sign);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {

		refund(1, "WX180112155104HWWB");

	}

}
