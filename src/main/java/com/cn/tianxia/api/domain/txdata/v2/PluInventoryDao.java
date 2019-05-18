package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.PluInventoryEntity;

public interface PluInventoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PluInventoryEntity record);

    int insertSelective(PluInventoryEntity record);

    PluInventoryEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PluInventoryEntity record);

    int updateByPrimaryKey(PluInventoryEntity record);
    
    //通过商品ID查询商品库存
    PluInventoryEntity selectByPluid(@Param("pluid") Integer pluid,@Param("cid") Integer cid);
}