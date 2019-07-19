package top.xuyx.seafood.weixin.model;

public enum EventType {
	 
	Text("text", "文本类型"), 
	Event("event", "事件类型"),
	Image("image", "图片类型"),
	SubscribeEvent("subscribe", "关注事件"),
	UnsubscribeEvent("unsubscribe", "取消关注事件"), 
	ScanEvent("SCAN", "扫码事件 扫码(用户已关注)"), 
	LocationEvent("LOCATION", "地理位置上报事件"), 
	ClickEvent("CLICK", "点击click事件"), 
	TempSendFinish("TEMPLATESENDJOBFINISH", "模版消息推送完毕通知"), 
	Other("Other", "其他类型") ;

	private final String code;
	private final String description;

	private EventType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + ": " + description;
	}
}
