package top.xuyx.seafood.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.xuyx.seafood.controller.BaseController;
import top.xuyx.seafood.weixin.WeiXinSDK;
import top.xuyx.seafood.weixin.util.Constant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Component
public class WxInterceptor extends HandlerInterceptorAdapter {
	@Value("${website.domain}")
	private String domain;
	public String getDomain() {
		return domain;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		CORS(request, response);
		//从cookie里获取openID
		String openId = BaseController.getCookieValue(request, Constant.WEIXIN_LOGIN_COOKIE_NAME);
		String url = String.format("http://%s%s", domain, request.getRequestURI());

		String queryStr = request.getQueryString();
		if (queryStr != null && queryStr.trim().length() > 0) {
			url = String.format("%s?%s", url, queryStr);
		}
		log.info(String.format("微信获取openID请求起始地址:%s", url));

		// 获取openId（需要先取得code，然后用code换取openID）
		if (openId == null) {
			String code = request.getParameter("code");
			String state = request.getParameter("state");
			if (code == null || state == null) {
				//取code
				url = WeiXinSDK.getCodeWeixinURL(false, "wx_state", url);
				log.info(String.format("微信请求openid=null 微信重定向(%s)", url));
				response.sendRedirect(url);
				return false;
			} else {
				//换取openID
				log.info(String.format("微信授权通知code=%s", code));
				openId = WeiXinSDK.codeNotify(code, state);
				//存到cookie 60天（微信如果登录别的账号后，cookie会清除）
				BaseController.addCookie(response, Constant.WEIXIN_LOGIN_COOKIE_NAME, openId, null, "/",60*60*24l*60);
			}
		}
		log.info(String.format("微信已授权:openId=%s", openId));
		request.setAttribute(Constant.WEIXIN_LOGIN_COOKIE_NAME, openId);
		return true;
	}
	
	public static void CORS(HttpServletRequest request, HttpServletResponse response) {

		if (request.getParameter("callback") != null) {
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0);
			String Referer = request.getHeader("Referer");
			if (Referer != null) {
				try {
					URL url = new URL(Referer);
					Referer = String.format("%s://%s", url.getProtocol(), url.getHost());
				} catch (MalformedURLException e) {
					Referer = null;
				}
			}

			// if (Referer != null) {
			// response.setHeader("Access-Control-Allow-Origin", Referer);
			// } else {
			response.setHeader("Access-Control-Allow-Origin", "*");
			// }
			response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS,DELETE");
			response.setHeader("Access-Control-Allow-Credentials", "true");// 跨域携带cookie
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
