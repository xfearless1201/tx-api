package com.cn.tianxia.api.service.v2.impl;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.po.v2.PasswordResponse;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.service.v2.PasswordService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.vo.v2.PasswordVO;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PasswordServiceImpl
 * @Description 密码接口实现类
 * @author Hardy
 * @Date 2019年2月1日 下午6:21:14
 * @version 1.0.0
 */
@Service
public class PasswordServiceImpl implements PasswordService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
    
    @Autowired
    private NewUserDao newUserDao;
    
    private static DESEncrypt desEncrypt;
    
    static{
        desEncrypt = new DESEncrypt(KeyConstant.DESKEY);
    }

    /**
     * 修改用户登录密码
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public JSONObject updateLoginPassword(PasswordVO passwordVO) {
        logger.info("调用修改用户登录密码业务开始===============START=================");
        try {
            
            //通过用户ID查询用户信息
            UserEntity user = newUserDao.selectByPrimaryKey(Integer.parseInt(passwordVO.getUid()));
            if(user == null){
                logger.info("查询用户信息失败,非法用户");
                return PasswordResponse.error("查询用户信息失败,非法用户【"+passwordVO.getUid()+"】");
            }
            
            //密码加密
            String password = desEncrypt.encrypt(passwordVO.getPassword());
            //判断用户登录密码是否正确
            if(!password.equals(user.getPassword())){
                logger.info("用户输入的原始登录密码错误");
                return PasswordResponse.error("原始密码输入错误,请重新输入");
            }
            
            //加密新密码
            String npassword = desEncrypt.encrypt(passwordVO.getNpassword());
            //修改用户密码
            user.setPassword(npassword);
            
            //修改密码
            newUserDao.updateByPrimaryKeySelective(user);
            logger.info("用户【"+user.getUsername()+"】,修改密码成功");
            return PasswordResponse.success("success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("调用修改用户登录密码业务异常:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return PasswordResponse.error("调用修改用户登录密码业务异常");
        }
        
    }

    /**
     * 修改用户取款密码
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public JSONObject updateQkPassword(PasswordVO passwordVO) {
        logger.info("调用修改用户取款密码业务开始=================START==================");
        try {
            
            //通过用户ID查询用户信息
            UserEntity user = newUserDao.selectByPrimaryKey(Integer.parseInt(passwordVO.getUid()));
            if(user == null){
                logger.info("查询用户信息失败,非法用户");
                return PasswordResponse.error("查询用户信息失败,非法用户");
            }
            
            //用户新的取款密码
            String npassword = desEncrypt.encrypt(passwordVO.getNpassword());
            //判断用户是否设置过取款密码
            if(StringUtils.isNotBlank(user.getQkPwd())){
              //判断用户旧的取款密码是否匹配
                String password = desEncrypt.encrypt(passwordVO.getPassword());
                if(!password.equals(user.getQkPwd())){
                    logger.info("用户【"+user.getUsername()+"】,输入的旧取款密码错误");
                    return PasswordResponse.error("输入的原始取款密码错误,请重新输入");
                }
            }else{
                logger.info("用户【"+user.getUsername()+"】,未设置过取款密码.");
            }
            //直接修改用户取款密码,密码加密
            user.setQkPwd(npassword);
            //修改操作
            newUserDao.updateByPrimaryKeySelective(user);
            //密码修改成功
            logger.info("用户【"+user.getUsername()+"】,修改取款密码成功");
            return PasswordResponse.success("success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用修改用户取款密码业务异常:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return PasswordResponse.error("调用修改用户取款密码业务异常");
        }
    }

    /**
     * 检查用户取款密码
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject checkQkpwd(String uid) {
        logger.info("调用检查用户取款密码业务开始===================START===================");
        try {
            
            //通过用户ID查询用户信息
            UserEntity user = newUserDao.selectByPrimaryKey(Integer.parseInt(uid));
            if(user == null){
                logger.info("查询用户信息失败,非法用户");
                return PasswordResponse.error("查询用户信息失败,非法用户");
            }
            
            if(StringUtils.isBlank(user.getQkPwd())){
                //未设置过取款密码
                logger.info("用户【"+user.getUsername()+"】,未设置过取款密码");
                return PasswordResponse.error("0");
            }
            
            logger.info("用户【"+user.getUsername()+"】,设置过取款密码");
            return PasswordResponse.success("1");
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用检查用户取款密码业务异常:{}",e.getMessage());
            return PasswordResponse.error("调用检查用户取款密码业务异常");
        }
    }

}
