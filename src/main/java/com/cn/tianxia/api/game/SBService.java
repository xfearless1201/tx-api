package com.cn.tianxia.api.game;

public interface SBService {

	/**
	 * 获取平台授权
	 * @return
	 */
	String getAccToken();

	/**
	 * 获取用户授权
	 * @param ip
	 * @param username
	 * @param userid
	 * @param acctoken
	 * @return
	 */
	String getUserToken(String ip, String username, String userid, String acctoken,String handicap,String model);

	/**
	 * 获取用户余额
	 * @param userid
	 * @param acctoken
	 * @return
	 */
	String getBalance(String userid, String token);

	/**
	 * 充值
	 * @param userid
	 * @param billno
	 * @param amt
	 * @param timestamp
	 * @param acctoken
	 * @return
	 */
	String WalletCredit(String userid, String billno, String amt, String timestamp, String acctoken);

	/**
	 * 提现转出
	 * @param userid
	 * @param billno
	 * @param amt
	 * @param timestamp
	 * @param acctoken
	 * @return
	 */
	String WalletDebit(String userid, String billno, String amt, String timestamp, String acctoken);

	/**
	 * 获取游戏跳转连接
	 * @param usertoken
	 * @return
	 */
	String getGameUrl(String usertoken,String gameID);

}