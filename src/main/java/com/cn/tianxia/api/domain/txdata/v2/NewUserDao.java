package com.cn.tianxia.api.domain.txdata.v2;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.project.v2.UserEntity;

public interface NewUserDao {
    int deleteByPrimaryKey(Integer uid);

    int insert(UserEntity record);

    int insertSelective(UserEntity record);

    UserEntity selectByPrimaryKey(Integer uid);

    int updateByPrimaryKeySelective(UserEntity record);

    int updateByPrimaryKey(UserEntity record);

    double selectAgentRechargeQuotaByUid(@Param("uid") String uid);
    
    double queryUserBalance(@Param("uid") int uid);
    
    int subtractUserBalance(@Param("uid") Integer uid,@Param("money") Double money);
    
    int plusUserBalance(@Param("uid") Integer uid,@Param("money") Double money);
    
    UserEntity getUserInfoByUsername(@Param("username") String username,@Param("cagent") String cagent,@Param("flag") Integer flag);

    /**
     *
     * @Description 通过登录账号查询用户信息
     * @param username
     * @return
     */
    UserEntity selectByUsername(@Param("username") String username);

    /**
     *
     * @Description 验证用户名是否在游离表中
     * @param userName
     * @return
     */
    UserEntity selectDisUserByUserName(@Param("userName") String userName);

    /**
     *
     * @Description 验证手机号是否被注册
     */
    UserEntity selectUserByMobile(@Param("cagent") String cagent,@Param("mobile") String mobile);

    List<Map<String,String>> selectProxyByCagent(@Param("cid") Integer cid);
    
    List<UserEntity> findAllByPage(@Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize);
    
    /**
     * 
     * @Description 修改用户的电话号码
     * @param uid
     * @param phoneNo
     * @return
     */
    int updateUserPhoneNo(@Param("uid") String uid,@Param("phoneNo") String phoneNo);
    
    List<Integer> findAllUserId();
}