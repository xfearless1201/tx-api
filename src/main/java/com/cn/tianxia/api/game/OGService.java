package com.cn.tianxia.api.game;

public interface OGService {

	/**
	 * 检查账户是否存在
	 * @param username
	 * @param password
	 * @return
	 */
	String CheckMem(String username, String password);

	/**
	 * 登陆游戏,返回游戏跳转连接
	 * @param username
	 * @param password
	 * @return
	 */
	String Logingame(String username, String password,String gameID);

	/**
	 * 查询余额
	 * @param username
	 * @param password
	 * @return
	 */
	String getBalance(String username, String password);

	/**
	 * 创建账户
	 * @param username
	 * @param password
	 * @return
	 */
	String CreateMem(String username, String password);

	/**
	 * 存款
	 * @param username
	 * @param password
	 * @param billno
	 * @param credit
	 * @return
	 */
	String DEPOSIT(String username, String password, String billno, String credit);

	/**
	 * 取款
	 * @param username
	 * @param password
	 * @param billno
	 * @param credit
	 * @return
	 */
	String WITHDRAW(String username, String password, String billno, String credit);
	/**
	 * 查询订单
	 * @param userName
	 * @param password
	 * @param billno
	 * @param inOut
	 * @return
	 */
	public String orderQuery(String userName,String password,String billno,String inOut) throws Exception;

}