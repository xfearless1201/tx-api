package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.PluCateEntity;
import com.cn.tianxia.api.vo.PluCateVO;

public interface PluCateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PluCateEntity record);

    int insertSelective(PluCateEntity record);

    PluCateEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PluCateEntity record);

    int updateByPrimaryKey(PluCateEntity record);
    /**
     * 根据平台名称查询商品类别
     * @param cagentName
     * @return
     */
  	List<PluCateEntity> selectTypeByCagentName(@Param("cagentName") String cagentName);
	/**
	 * 获取商品类别
	 * @param paramMap
	 * @return
	 */
	List<PluCateEntity> getGoodsType(PluCateVO pluCateVO);
}