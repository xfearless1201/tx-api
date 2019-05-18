package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.CagentQrcodepayEntity;

/**
 * 
 * @ClassName CagentQrcodepayMapper
 * @Description 平台扫描支付配置接口
 * @author Hardy
 * @Date 2019年1月11日 下午3:08:11
 * @version 1.0.0
 */
public interface CagentQrcodepayDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CagentQrcodepayEntity record);

    int insertSelective(CagentQrcodepayEntity record);

    CagentQrcodepayEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CagentQrcodepayEntity record);

    int updateByPrimaryKey(CagentQrcodepayEntity record);
    
    List<CagentQrcodepayEntity> findAllByIds(@Param("ids") List<String> ids);
}