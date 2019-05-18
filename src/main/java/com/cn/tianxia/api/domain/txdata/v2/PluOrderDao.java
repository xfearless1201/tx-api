package com.cn.tianxia.api.domain.txdata.v2;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.PluOrderEntity;
import com.cn.tianxia.api.vo.PluOrderVO;

public interface PluOrderDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PluOrderEntity record);

    int insertSelective(PluOrderEntity record);

    PluOrderEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PluOrderEntity record);

    int updateByPrimaryKey(PluOrderEntity record);
    /**
     * 获取历史订单
     * @param paramMap
     * @return
     */
    List<PluOrderEntity> getHistoryOrder(@Param("uid") String uid,@Param("bdate") Date bdate,@Param("edate") Date edate,
                                                                @Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize);
    /**
     * 统计订单
     * @param paramMap
     * @return
     */
	int countHistoryOrder(@Param("uid") String uid,@Param("bdate") Date bdate,@Param("edate") Date edate);
	/**
	 * 生成订单
	 * @param pluOrderVO
	 * @return
	 */
	String generatorOrder(PluOrderVO pluOrderVO);
}