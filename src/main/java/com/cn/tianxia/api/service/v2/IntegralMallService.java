package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.PluCateVO;
import com.cn.tianxia.api.vo.PluInfoVO;
import com.cn.tianxia.api.vo.PluOrderVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface IntegralMallService {

	/**
	 * 根据平台商名称获取商品类型
	 * @param cagentName
	 * @return
	 */
	JSONArray getTypeByCagentName(String cagentName);
    /**
     * 获取所有的商品信息
     * @param paramMap
     * @return
     */
	JSONObject getGoodsList(PluInfoVO pluInfoVO);
	/**
	 * 获取商品类别
	 * @param paramMap
	 * @return
	 */
	JSONObject getGoodsType(PluCateVO pluCateVO);
	/**
	 * 获取单个商品信息
	 * @param paramMap
	 * @return
	 */
	JSONObject getGoodsInfo(Integer id);
	/**
	 * 创建订单
	 * @param paramMap
	 * @return
	 */
	JSONObject createOrder(PluOrderVO pluOrderVO);
	/**
	 * 获取历史订单
	 * @param paramMap
	 * @param uid
	 * @return
	 */
	JSONObject getOrderHistory(String uid,String btime,String etime,Integer pageNo,Integer pageSize);
	/**
	 * 获取兑换排行榜
	 * @param paramMap
	 * @return
	 */
	JSONArray getExchangeRankList(PluInfoVO pluInfoVO);

}
