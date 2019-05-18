package com.cn.tianxia.api.service.v2.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.UserWalletDao;
import com.cn.tianxia.api.service.v2.UserCapitalService;

/**
 * 
 * @ClassName UserCapitalServiceImpl
 * @Description 用户资金接口实现类
 * @author Hardy
 * @Date 2019年3月11日 下午5:46:20
 * @version 1.0.0
 */
@Service
public class UserCapitalServiceImpl implements UserCapitalService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserCapitalServiceImpl.class);
    
    @Autowired
    private NewUserDao newUserDao;
    
    @Autowired
    private UserWalletDao userWalletDao;
    
    @Override
    public synchronized Double handleUserWalletBalance(Integer uid, Double money, Integer type) throws Exception{
        logger.info("调用用户【"+uid+"】资金业务,操作用户钱包余额接口开始==================START=============");
        try {
            
            if(uid == null){
                throw new Exception("操作用户钱包余额业务异常：用户ID不能为空");
            }
            
            if(type == 1){
                //加钱
                logger.info("调用用户【"+uid+"】资金业务给用户加钱");
                if(money > 0){
                    newUserDao.plusUserBalance(uid, money);
                }
            }
            
            if(type == 2){
                //扣钱
                logger.info("调用用户【"+uid+"】资金业务扣减用户余额");
                if(money > 0){
                    newUserDao.subtractUserBalance(uid, money);
                }else{
                    throw new Exception("调用用户【"+uid+"】资金业务扣减用户钱包余额异常,金额不能小于或等于零,操作金额【"+money+"】");
                }
            }
            
            //查询用户余额
            return newUserDao.queryUserBalance(uid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用用户【"+uid+"】资金业务,操作用户钱包余额接口异常:{}",e.getMessage());
            throw new Exception("调用用户【"+uid+"】资金业务,操作用户钱包余额接口异常");
        }
    }

    @Override
    public synchronized Double handleUserIntegralBalance(Integer uid, Double integral, Integer type) throws Exception{
        logger.info("调用用户【"+uid+"】资金业务,操作用户积分余额接口开始==================START=============");
        try {
            if(uid == null){
                throw new Exception("操作用户钱包余额业务异常：用户ID不能为空");
            }
            
            if(type == 1){
                //加钱
                logger.info("调用用户【"+uid+"】资金业务给用户加钱");
                if(integral > 0){
                    userWalletDao.plusUserIntegralBalance(uid, integral);
                }
            }
            
            if(type == 2){
                //扣钱
                logger.info("调用用户【"+uid+"】资金业务扣减用户余额");
                if(integral > 0){
                    userWalletDao.deductUserIntegralBalance(uid, integral);
                }else{
                    throw new Exception("调用用户【"+uid+"】资金业务扣减用户 积分余额异常,金额不能小于或等于零,操作金额【"+integral+"】");
                }
            }
            //查询用户余额
            return userWalletDao.getIntegralBalance(uid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用用户【"+uid+"】资金业务,操作用户积分余额接口异常:{}",e.getMessage());
            throw new Exception("调用用户【"+uid+"】资金业务,操作用户积分余额接口异常");
        }
    }

}
