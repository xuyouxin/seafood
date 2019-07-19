package top.xuyx.seafood.controller;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.xuyx.seafood.common.JsonUtil;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.controller.BaseController;
import top.xuyx.seafood.dbservice.entity.OrderDo;
import top.xuyx.seafood.dbservice.mapper.OrderMapper;
import top.xuyx.seafood.model.in.RequestCommonModel;
import top.xuyx.seafood.weixin.WeiXinSDK;
import top.xuyx.seafood.weixin.WeiXinService;
import top.xuyx.seafood.weixin.XmlUtils;
import top.xuyx.seafood.weixin.model.WeixinPayResult;
import top.xuyx.seafood.weixin.util.Constant;
import top.xuyx.seafood.weixin.util.OrderUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/wx")
public class WeiXinController extends BaseController {
	
	@Value("${website.domain}")
	private String websiteDomain;// 当前站点域名
	@Autowired
	private WeiXinSDK weixinSdk;
	
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private WeiXinService weixinService;

	@RequestMapping(value = "/getOpenId")
	public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = request.getParameter("url");
		if (StringUtils.isBlank(url)) {
			url = String.format("http://%s", websiteDomain);
		}
		response.sendRedirect(url);
		log.info("redirect url>>" + url);
	}

	@RequestMapping(value = "/getUserInfo")
	@ResponseBody
	public Response getUserInfo(HttpServletRequest request) {
		String openid = getCookieValue(request, Constant.WEIXIN_LOGIN_COOKIE_NAME);
		if (openid != null) {
			return Response.ok(weixinSdk.getWeixinUserInfo(openid));
		} else {
			return Response.fail("获取openId失败");
		}
	}

	/**
	 * 订单微信支付
	 */
	@RequestMapping(value = "/orderPay")
	@ResponseBody
	public Response orderPay(HttpServletRequest request, @RequestBody String json) {
//	public @ResponseBody Response orderPay(@RequestBody String json) {
//	public @ResponseBody Response orderPay(HttpServletRequest request) {
//		String json = "{\"orderId\":\"000d6aa3202043ceb8171bea669f9294\"}";
		RequestCommonModel rcm = JsonUtil.jsonToObject(json, RequestCommonModel.class);
		if(rcm == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(StringUtils.isAnyBlank(rcm.getOrderId(), rcm.getOpenId())) {
			return Response.fail(StatusEnum.code_204);
		}

		OrderDo order = orderMapper.selectById(rcm.getOrderId());
		if(order == null) {
			return Response.fail(StatusEnum.code_209);
		}
		String title = "购买商品";
//		String openId = "ohj5_0ewhWugmPpwYtKuCv2FsfbU";
//		String ip = "47.100.210.123";
//		String openId = (String) request.getAttribute(Constant.WEIXIN_LOGIN_COOKIE_NAME);
		String ip = getIpAddr(request);
		return weixinService.orderPay(title, rcm.getOpenId(), ip, order);
	}

	/**
	 * 微信H5支付-微信支付结果通知
	 *
	 * @param request
	 * @param response
	 * @param timestamp
	 * @param nonceStr
	 * @return
	 */
	@RequestMapping(value = "/orderNotify", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody Object orderNotify(HttpServletRequest request, HttpServletResponse response, String timestamp, String nonceStr) {

		WeixinPayResult result = new WeixinPayResult();
		try {
			final String body = this.readBody(request, "utf-8");
			log.error("支付结果通知>>" + body);

			Map<String, Object> xmlMap = XmlUtils.xmlBody2map(body, "xml");

			result = WeiXinSDK.orderNotify(xmlMap);

			weixinService.updateOrderPay(result.isOk(), result.getLinkNo());

		} catch (Exception e) {
			log.error("", e);
		}

		log.info("orderNotify " + result);
		return result.getReturnInfo();
	}
	
	/**
	 * 微信分享前端JS
	 *
	 * @param request
	 * @param response
	 * @param shareLink
	 * @return
	 */
	@RequestMapping(value = "/weixinShareJs", method = {RequestMethod.GET})
	public @ResponseBody Object weixinShareJs(final HttpServletRequest request, final HttpServletResponse response, 
			@RequestParam(required = false)  String shareLink) {

		Map<String, Object> resultMap = new HashMap<String, Object>(2);

		// 随机字符串
		String nonceStr = OrderUtils.getRandomString(10);
		// 时间戳
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);// 时间戳
		String signature = "";
		 
		{
			String queryStr = request.getQueryString();
			queryStr = queryStr == null ? "" : "?" + queryStr;
			// 当前页面所在url
			String url = String.format("http://%s%s%s",websiteDomain,request.getRequestURI(), queryStr);
			
			if(shareLink!=null&&shareLink.trim().length()>0){
				url=shareLink=shareLink.trim();
			} 

			Map<String, Object> map = new HashMap<String, Object>();
			 
		    map.put("jsapi_ticket", weixinSdk.getJsApiTicket());
			 
			map.put("noncestr", nonceStr);// 注意小写
			map.put("timestamp", timestamp);// 注意小写
			map.put("url", url);

			signature = WeiXinSDK.signSHA1(map);

		}
		resultMap.put("signature", signature);
		resultMap.put("nonceStr", nonceStr);
		resultMap.put("timestamp", timestamp);
		resultMap.put("appId", WeiXinSDK.AppID);

		return   resultMap ;
	}

	/**
	 * 微信H5支付-页面
	 *
	 * @param request
	 * @param response
	 * @param state
	 * @param queryString
	 * @param fromAccountId
	 * @param activityCode
	 * @return
	 */
	@RequestMapping(value = "/weixinPay", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView weixinPay(final HttpServletRequest request, final HttpServletResponse response, String state, String queryString, String fromAccountId, String activityCode) {

		Map<String, Object> resultMap = new HashMap<String, Object>(2);

		// 随机字符串
		String nonceStr = OrderUtils.getRandomString(10);
		// 时间戳
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);// 时间戳
		String signature = "";
		String openid = request.getAttribute(Constant.WEIXIN_LOGIN_COOKIE_NAME).toString();
		{

			String queryStr = request.getQueryString();
			queryStr = queryStr == null ? "" : "?" + queryStr;
			// 当前页面所在url
			String url = String.format("http://%s%s%s",websiteDomain,request.getRequestURI(), queryStr);

			Map<String, Object> map = new HashMap<String, Object>();
			 
		    map.put("jsapi_ticket", weixinSdk.getJsApiTicket());
			 
			map.put("noncestr", nonceStr);// 注意小写
			map.put("timestamp", timestamp);// 注意小写
			map.put("url", url);

			signature = WeiXinSDK.signSHA1(map);

		}
		resultMap.put("signature", signature);
		resultMap.put("nonceStr", nonceStr);
		resultMap.put("timestamp", timestamp);
		resultMap.put("openId", openid);
		resultMap.put("channelNo", "aaa");
		resultMap.put("appId", WeiXinSDK.AppID);

		return new ModelAndView("weixinPay", resultMap);
	}


	/**
	 * 强制更新TOKEN
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/clearToken", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody Object clearToken(HttpServletRequest request, HttpServletResponse response) {

		try {
			if (weixinSdk.clearToken()) {
				return "ok";
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return "error";
	}
	
//	@RequestMapping(value = "/QrNotify", method = {RequestMethod.POST,RequestMethod.GET})
//	@ApiOperation(value = "微信扫码支付-支付回调通知", notes = "接收用户扫码后微信支付系统发送的数据",   produces = MediaType.APPLICATION_XML_VALUE)
//	public @ResponseBody String QrNotify(HttpServletRequest request, HttpServletResponse response, String timestamp, String nonceStr) {
//		//response.setContentType("text/xml;charset=UTF-8");
//		WeixinPayResult result=new WeixinPayResult();
//		try {
//			final String body = this.readBody(request, "utf-8");
//			log.info(body);
//
//			Map<String, Object> xmlMap = XmlUtils.xmlBody2map(body, "xml"); 
//
//			result = WeiXinSDK.orderNotify(xmlMap);
//
//			 
//			weixinService.updateIsPay(result.isOk(), result.getLinkNo());
//
//			 
//		} catch (Exception e) {
//			log.error("", e);
//		}
//
//		log.info("QrNotify " + result);
//		
//		 
//		return result.getReturnInfo();
//	}
	
	 
	@RequestMapping(value = "/toPage/{page}",method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView flowList(final HttpServletRequest request, final HttpServletResponse response,@PathVariable("page") String page) {

		Map<String, Object> resultMap = new HashMap<String, Object>(2);

		try {
			
		} catch (Exception e) {
			log.error("", e);
		}

		return new ModelAndView(String.format("weixin/%s", page), resultMap);
	}

}
