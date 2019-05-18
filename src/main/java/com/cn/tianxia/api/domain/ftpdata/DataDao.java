package com.cn.tianxia.api.domain.ftpdata;


import java.util.List;
import java.util.Map;
 

/**
 * 功能概要：数据报表的DAO类
 *  
 */
public interface DataDao {
	 
	 List<Map<String, String>> selectBetList(Map<String, Object> map); 
	 
	 List<Map<String, String>> selectBetCount(Map<String, Object> map); 
	 
	 Map<String, Double> selectBetSum(Map<String, Object> map);
}
