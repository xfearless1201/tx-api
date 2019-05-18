package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 功能概要：积分商城DAO类
 * 
 */
public interface IntegralShopDao {
	//根据平台名称查询商品类别
	List<Map<String,Object>> selectTypeByCagentName(@Param("cagentName") String cagentName);
	
	//根据条件查询商品类别
	List<Map<String,Object>> selectTypeByCondition(Map<String,Object> paramMap);
	
	//根据条件查询所有商品
	List<Map<String,Object>> selectGoodsByCondition(Map<String,Object> paramMap);
	int countGoodsByCondition(Map<String,Object> paramMap);
	
	//生成订单
	void insertOrder(Map<String,Object> paramMap);
	
	//根据条件查询订单记录
	List<Map<String,Object>> selectOrderByCondition(Map<String,Object> paramMap);
	
	Map<String,Object> goodsDetails(Map<String,Object> paramMap);
	
	//根据uid查询用户剩余积分
	Map<String,Object> selectUserWallet(Map<String,Object> paramMap);
	
	//更新积分
	void updateWallet(Map<String,Object> paramMap);
	
	//记录积分流水
	void insertWalletLog(Map<String,Object> paraMap);

	//生成订单
	String generatorOrder(Map<String,Object> paraMap);
	
	//订单历史
	List<Map<String,Object>> orderHistory(Map<String,Object> paramMap);
	int countOrderHistory(Map<String,Object> paramMap);
	
	//查询兑换排行榜
	List<Map<String,Object>> rankingList(Map<String,Object> paramMap);
}
