package com.cn.tianxia.api.service;

import java.util.List;
import java.util.Map;
 

/**
 * 功能概要：DataService接口类
 *  
 */
public interface DataService {
	
	List<Map<String, String>> selectBetList(Map<String, Object> map); 
	List<Map<String, String>> selectBetCount(Map<String, Object> map); 
	
	 Map<String, Double> selectBetSum(Map<String, Object> map);
}
