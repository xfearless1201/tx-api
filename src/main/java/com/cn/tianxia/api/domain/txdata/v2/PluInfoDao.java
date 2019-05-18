package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import com.cn.tianxia.api.project.v2.PluInfoEntity;
import com.cn.tianxia.api.vo.PluInfoVO;

public interface PluInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PluInfoEntity record);

    int insertSelective(PluInfoEntity record);

    PluInfoEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PluInfoEntity record);

    int updateByPrimaryKey(PluInfoEntity record);
    /**
     * 统计商品数量
     * @param paramMap
     * @return
     */
    int countGoods(PluInfoVO pluInfoVO);
    /**
     * getAllGoods
     * @param paramMap
     * @return
     */
  	List<PluInfoEntity> getAllGoods(PluInfoVO pluInfoVO);
  	/**
  	 * 获取单个商品信息
  	 * @param paramMap
  	 * @return
  	 */
  	PluInfoVO getGoodsInfo(Integer id);
    /**
     * 获取兑换排行列表
     * @param paramMap
     * @return
     */
	List<PluInfoEntity> getExchangeRankList(PluInfoVO pluInfoVO);
}