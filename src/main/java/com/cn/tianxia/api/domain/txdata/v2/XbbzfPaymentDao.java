package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.XbbzfPaymentEntity;

/**
 * 新币宝支付 查询会员信息
 * @author TX
 *
 */
public interface XbbzfPaymentDao {

	XbbzfPaymentEntity selectUserName(@Param(value = "uid") int uid);

	int insertXbbzfPaymentEntity(XbbzfPaymentEntity entity);
}
