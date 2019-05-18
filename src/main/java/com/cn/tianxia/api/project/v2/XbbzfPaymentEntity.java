package com.cn.tianxia.api.project.v2;

/**
 * 新币宝支付查询会员实体类
 * @author TX
 *
 */
public class XbbzfPaymentEntity {

	private String id;
	private String username;
	private String uid;
	private String address;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
