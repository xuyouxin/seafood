package top.xuyx.seafood.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
	WAIT_PAY(1, "未付款"),
	WAIT_SEND(2, "已付款");
	
	private int code;
	private String message;
	private OrderStatusEnum(int code, String msg) {
		this.code = code;
		this.message = msg;
	}
}
