package com.cn.tianxia.api.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cn.tianxia.api.domain.ftpdata.DataDao;
import com.cn.tianxia.api.service.DataService;
 

/**
 * 功能概要：UserService实现类
 *  
 */
@Service
public class DataServiceImpl implements DataService{
	@Resource
	private DataDao dataDao;

	@Override
	public List<Map<String, String>> selectBetList(Map<String, Object> map) {
		return dataDao.selectBetList(map);
	}

	@Override
	public List<Map<String, String>> selectBetCount(Map<String, Object> map) {
		return dataDao.selectBetCount(map);
	}

	@Override
	public Map<String, Double> selectBetSum(Map<String, Object> map) {
		return dataDao.selectBetSum(map);
	}  
}
