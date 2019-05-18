package com.cn.tianxia.api.domain.txdata.v2;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.JuniorProxyUserEntity;
import com.cn.tianxia.api.vo.v2.JuniorProxyUserVO;

public interface JuniorProxyUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(JuniorProxyUserEntity record);

    int insertSelective(JuniorProxyUserEntity record);

    JuniorProxyUserEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(JuniorProxyUserEntity record);

    int updateByPrimaryKey(JuniorProxyUserEntity record);

    /**
     * 根据推荐码与平台cid查询二级代理用户
     * @param referralCode
     * @return
     */
    JuniorProxyUserVO getJuniorProxyUserByrefererCode(@Param("referralCode") String referralCode, @Param("cagent") String cagent);


    /**
     * 根据推荐码查询二级代理用户
     * @param referralCode
     * @return
     */
    JuniorProxyUserVO findJuniorProxyUserByrefererCode(@Param("referralCode") String referralCode);



    /**
     * 根据代理账户用户名和平台号查询二级代理用户
     * @param proxyname 二级代理账号用户名
     * @param cid 平台id
     * @return
     */
    JuniorProxyUserVO getJuniorProxyUser(@Param("proxyname") String proxyname, @Param("cid") Integer cid);

}