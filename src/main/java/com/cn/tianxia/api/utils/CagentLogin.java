package com.cn.tianxia.api.utils;

/**
 * 
 * @Description:TODO
 * 
 * @author:zouwei
 * 
 * @time:2017年8月10日 下午4:55:08
 * 
 */

public class CagentLogin {
	private static final String[] cagents = { "TXW", "TXC", "BL1" };
	/**
	 * 指定代理账户类型
	 * @param cagent
	 * @param old_type
	 * @param new_type
	 * @return
	 */
	public static String getCagentLoginType(String cagent, String old_type, String new_type) {
		for (int i = 0; i < cagents.length; i++) {
			if (cagents[0].equals(cagent)) {
				return new_type;
			}
		}
		return old_type;
	}
	/**
	 * 根据代理获取游戏平台配置key
	 * @param cagent
	 * @return
	 */
	public static Boolean getPlatformConfigKey(String cagent) {
		for (int i = 0; i < cagents.length; i++) {
			if (cagents[i].equals(cagent)) {
				return true;
			}
		}
		return false;
	}
}
