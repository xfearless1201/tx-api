package com.cn.tianxia.api.domain.txdata;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 功能概要：积分商城DAO类
 * 
 */
public interface LuckyDrawDao {
	//根据来源域名查询平台活动
	List<Map<String,Object>> selectLuckyDrawStatus(@Param("domain") String domain);
	
	//根据活动ID查询活动明细
	List<Map<String,Object>> selectLuckyDrawDetail(@Param("lid") int lid); 
	
	//查询会员可抽奖次数
	Integer selectUserTimes(Map<String, Object> map);

	//查询今次已经抢红包的次数
	List<Map<String, Object>> selectUserLuckDrawTodayTimes(@Param("lid") String lid, @Param("uid") String uid, @Param("begintime") String begintime, @Param("endtime") String endtime);

	//查询截止目前为止会员抢红包的总次数
	List<Map<String, Object>> selectUserLuckDrawTotalTimes(@Param("lid") String lid, @Param("uid") String uid);

	void selectUserLuckDrawTimes(Map<String, Object> luckrdrawLogMap);

	Map<String, Object> selectByPrimaryKey(@Param("uid") String uid);

	Map<String, Object> selectByCidCagentStoredvalue(@Param("cid") Integer cid);

	void insertUserTreasure(Map<String, String> userWalletLog);

	void updateByPrimaryKeySelective(Map<String, Object> updateUserWallet);

	Map<String, Object> getUserWalletId(@Param("uid") String uid, @Param("number") String number);

	void insertUserLuckrdrawLog(Map<String, Object> luckrdrawLogMap);

	void insertStoredvalueLog(Map<String, String> hashMap);

	void updateStoredvalue(Map<String, String> storedvalueMap);

	int updateLuckydraw(Map<String, String> storedvalueMap);

	String selectUserDetail(Map<String, Object> userTimesMap);

	List<Map<String, Object>> selectUserValidBetTimes(Map<String, Object> userTimesMap);
	
	int updateStatusByAmount(@Param("id")Integer id,@Param("status")String status);
	
	Double selectLuckyDrawsByValidBetAmount(@Param("lid") String lid);
	
	Integer selectLuckyDrawsTotalTimes(@Param("lid") String lid, @Param("validBetAmount") Double validBetAmount);
}
