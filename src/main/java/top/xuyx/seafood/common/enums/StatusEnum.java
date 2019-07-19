package top.xuyx.seafood.common.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
	code_200(200, "success"),
	code_201(201, "操作失败"),
	code_202(202, "json格式不正确"),
	code_203(203, "商品id不存在"),
	code_204(204, "参数不能为空"),
	code_205(205, "用户id不存在"),
	code_206(206, "地址id不存在"),
	code_207(207, "订单金额核对不上，请重试"),
	code_208(208, "商品库存不足"),
	code_209(209, "订单id不存在"),
	code_210(210, "验证码不正确"),
	code_211(211, "您今天获取验证码次数已达上限，请明天重试"),
	code_212(212, "请输入正确的手机号码"),
	code_213(213, "短信验证码已过期"),
	code_214(214, "验证码错误"),
	code_215(215, "商品下架中，无法购买"),
	code_216(216, "生成支付订单失败"),
	code_217(217, "原手机号不正确"),
	code_218(218, "订单不是待收货状态，不能确认收货"),

	code_301(301, "token不正确"),

	code_400(400, "服务异常"),
	code_404(404, "找不到接口或文件"),
	code_500(500, "内部服务器错误"),
	code_501(501, "非法参数"),
	code_502(502, "参数验证失败");
	
	private int code;
	private String message;
	private StatusEnum(int code, String msg) {
		this.code = code;
		this.message = msg;
	}
}
