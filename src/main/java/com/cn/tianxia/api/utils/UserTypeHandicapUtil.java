package com.cn.tianxia.api.utils;

import java.util.Map;

import com.cn.tianxia.api.service.UserService;

/**
 * 查询用户盘口
 * 
 * @author zw
 *
 */
public class UserTypeHandicapUtil {
	private static final String AG = "AGIN";
	private static final String IGPJHKC = "IGPJHKC";
	private static final String IGHKC = "IGHKC";
	private static final String IGPJSSC = "IGPJSSC";
	private static final String IGSSC = "IGSSC";
	private static final String SB = "SB";
	private static final String VR = "VR";

	public String getHandicap(String gameType, String uid, UserService userService) {
		String game = "";
		if ("AGIN".equals(gameType) || "AG".equals(gameType) || "TASSPTA".equals(gameType) || "YOPLAY".equals(gameType)) {
			game = AG;
		} else if ("IGPJLOTTO".equals(gameType)) {
			game = IGPJHKC;
		} else if ("IGPJLOTTERY".equals(gameType)) {
			game = IGPJSSC;
		}else if ("IGLOTTO".equals(gameType)) {
			game = IGHKC;
		}  else if ("IGLOTTERY".equals(gameType)) {
			game = IGSSC;
		} else if ("SB".equals(gameType)) {
			game = SB;
		} else if ("VR".equals(gameType)) {
			game = VR;
		} else {
			return "";
		}

		Map<String, Object> map = userService.selectUserTypeHandicap(game, uid);

		if (map==null || map.size() == 0) {
			return "";
		}
		String handicap = map.get("Handicap").toString();
		return handicap;
	}
}
