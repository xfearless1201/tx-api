package com.cn.tianxia.api.game;

public interface OBService {

	/**
	 * 查询系统盘口信息
	 * @return
	 */
	String queryhandicap();

	/**
	 * 创建账号
	 * @return
	 */
	String check_or_create(String username, String password);

	/**
	 * 注销游戏
	 * @return
	 */
	String logout_game(String username);

	/**
	 * 余额查询
	 * @return
	 */
	String get_balance(String username, String password);

	/**
	 * 转账
	 * @param username
	 * @param billno
	 * @param openflag 1为存入,0为提取
	 * @param credit
	 * @return
	 */
	String agent_client_transfer(String username, String billno, String openflag, String credit);

	/**
	* 跳转游戏
	* @param username
	* @param password
	* @return
	*/
	String forward_game(String username, String password,String model);
	
	/**
	    * 转账确认状态
	    * @param username
	    * @param password
	    * @return
	    */
	String queryOrder(long random,String sn);

}