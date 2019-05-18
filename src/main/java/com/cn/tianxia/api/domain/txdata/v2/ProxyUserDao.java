package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.ProxyUserEntity;
import com.cn.tianxia.api.vo.v2.ProxyUserVO;

public interface ProxyUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ProxyUserEntity record);

    int insertSelective(ProxyUserEntity record);

    ProxyUserEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProxyUserEntity record);

    int updateByPrimaryKey(ProxyUserEntity record);

    /**
     * 根据推荐码与平台cid查询
     * @param referralCode
     * @return
     */
    ProxyUserVO getProxyUserByrefererCode(@Param("referralCode") String referralCode, @Param("cagent") String cagent);

    /**
     * 根据推荐码查询代理用户
     * @param referralCode
     * @return
     */
    ProxyUserVO findProxyUserByrefererCode(@Param("referralCode") String referralCode);


    /**
     * 根据代理账号用户名和平台号查询代理商用户
     * @param proxyname 一级代理账号用户名
     * @param cid 平台id
     * @return
     */
    ProxyUserVO getProxyUser(@Param("proxyname") String proxyname, @Param("cid") Integer cid);
}