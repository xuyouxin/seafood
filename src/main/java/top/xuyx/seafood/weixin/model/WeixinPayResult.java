package top.xuyx.seafood.weixin.model;

public class WeixinPayResult {

	private String linkNo;// 交易订单单号或流水号

	private boolean isOk=false;// 是否支持成功
	
	private String returnInfo="<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名或参数错误]]></return_msg></xml>";// 返回结果,给微信的

	public String getLinkNo() {
		return linkNo;
	}

	public void setLinkNo(String linkNo) {
		this.linkNo = linkNo;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}
	
	public String toString( ) {
		return String.format("linkNo=%s isOk=%s returnInfo=%s", linkNo,isOk,returnInfo);
	}

}
