package top.xuyx.seafood.model.in;

import lombok.Data;

@Data
public class RequestCommonModel {
	private String id;
	private String userId;
	private String categoryId;
	private String goodsId;
	private String orderId;
	private String mobile;
	private String openId;
	private Integer status;

	//分页
	private Integer current;
	private Integer size;
	private Integer start;

	//登陆
	private String name;
	private String password;
}
