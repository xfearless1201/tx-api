package com.cn.tianxia.api.project;

/**
 * User映射类
 *  
 */
public class AgTransfer {
	private int id;
	private int uid;
	private String Billno;
	private String transfer_time;
	private String result;
	private double old_money;
	private String transfer_num;
	private int ag_type;
	private String error_msg;
	private double transfer_money;
	private String username;
	private String transfer_username;
	private int ok;
	private double new_money;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getBillno() {
		return Billno;
	}
	public void setBillno(String billno) {
		Billno = billno;
	}
	public String getTransfer_time() {
		return transfer_time;
	}
	public void setTransfer_time(String transfer_time) {
		this.transfer_time = transfer_time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public double getOld_money() {
		return old_money;
	}
	public void setOld_money(double old_money) {
		this.old_money = old_money;
	}
	public String getTransfer_num() {
		return transfer_num;
	}
	public void setTransfer_num(String transfer_num) {
		this.transfer_num = transfer_num;
	}
	public int getAg_type() {
		return ag_type;
	}
	public void setAg_type(int ag_type) {
		this.ag_type = ag_type;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public double getTransfer_money() {
		return transfer_money;
	}
	public void setTransfer_money(double transfer_money) {
		this.transfer_money = transfer_money;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTransfer_username() {
		return transfer_username;
	}
	public void setTransfer_username(String transfer_username) {
		this.transfer_username = transfer_username;
	}
	public int getOk() {
		return ok;
	}
	public void setOk(int ok) {
		this.ok = ok;
	}
	public double getNew_money() {
		return new_money;
	}
	public void setNew_money(double new_money) {
		this.new_money = new_money;
	}

}
