package top.xuyx.seafood.weixin.model;

public class WeiXinUserInfo {

	private int subscribe;// 是否关注 0否 1是;
	private String openid;
	private String nickname;//
	private int sex;// 1时是男性，值为2时是女性，值为0时是未知
	private String language;// zh_CN",
	private String city;// 广州",
	private String province;// 广东",
	private String country;// 中国",
	private String headimgurl;// 用户头像用户头像 若用户更换头像，原有头像URL将失效。
	private long subscribe_time;// 用户关注时间
	private String unionid;
	private String remark;// 对粉丝添加备注
	private int groupid;
	private int[] tagid_list;

	public int getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public long getSubscribe_time() {
		return subscribe_time;
	}

	public void setSubscribe_time(long subscribe_time) {
		this.subscribe_time = subscribe_time;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public int[] getTagid_list() {
		return tagid_list;
	}

	public void setTagid_list(int[] tagid_list) {
		this.tagid_list = tagid_list;
	}

}
