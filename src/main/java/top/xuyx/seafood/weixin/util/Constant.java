package top.xuyx.seafood.weixin.util;

public interface Constant {
	// 是否为开发模式（开发模式下，验证码短信不会下发，默认123456）
	boolean devMode = false;
	
	// openID存放在cookie中
	String WEIXIN_LOGIN_COOKIE_NAME = "shxs.tooken.openid";

	
	/**
	 * 支付状态
	 *
	 */
	public interface PayStatus {
		int NO = 0; //未支付
		int YES = 1; //已支付
		int FAIL = 2; //支付失败
		int BACK = 3; //已退款
		int BACK_FAIL = 4; //退款失败
	}
}
