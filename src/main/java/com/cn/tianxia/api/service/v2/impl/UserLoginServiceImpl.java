package com.cn.tianxia.api.service.v2.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.common.v2.DatePatternConstant;
import com.cn.tianxia.api.common.v2.DatePatternUtils;
import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.domain.txdata.v2.CagentDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentMsgDao;
import com.cn.tianxia.api.domain.txdata.v2.DissociateDao;
import com.cn.tianxia.api.domain.txdata.v2.LoginerrormapDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.RefererUrlDao;
import com.cn.tianxia.api.domain.txdata.v2.UserLoginDao;
import com.cn.tianxia.api.domain.txdata.v2.UserWalletDao;
import com.cn.tianxia.api.error.BusinessException;
import com.cn.tianxia.api.po.v2.LoginResponse;
import com.cn.tianxia.api.po.v2.ResultResponse;
import com.cn.tianxia.api.project.v2.CagentEntity;
import com.cn.tianxia.api.project.v2.CagentMsgEntity;
import com.cn.tianxia.api.project.v2.DissociateEntity;
import com.cn.tianxia.api.project.v2.LoginerrormapEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.project.v2.UserLoginEntity;
import com.cn.tianxia.api.service.v2.UserCapitalService;
import com.cn.tianxia.api.service.v2.UserLoginService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.vo.v2.MobileLoginVO;
import com.cn.tianxia.api.vo.v2.UserLoginVO;
import com.cn.tianxia.api.web.v2.UserLoginController;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName UserLoginServiceImpl
 * @Description 用户登录接口实现类
 * @author Hardy
 * @Date 2019年2月6日 下午2:59:23
 * @version 1.0.0
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);
    
    @Autowired
    private RefererUrlDao refererUrlDao;
    
    @Autowired
    private DissociateDao dissociateDao;
    
    @Autowired
    private NewUserDao newUserDao;
    
    @Autowired
    private LoginerrormapDao loginerrormapDao;
    
    @Autowired
    private UserLoginDao userLoginDao;

    @Autowired
    private UserWalletDao userWalletDao;
    
    @Autowired
    private CagentDao cagentDao;

    @Autowired
    private CagentMsgDao cagentMsgDao;
    
    @Autowired
    private UserCapitalService userCapitalService;
    
    /**
     * 登录
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public JSONObject login(UserLoginVO userLoginVO) {
        logger.info(userLoginVO.getUsername()+"调用用户登录业务开始==================START==================");
        try {
            Date date = new Date();
            //登录账号
            String username = userLoginVO.getUsername();
            //获取平台编码
            String cagent = username.substring(0, 3);
            //验证来源域名是否属于代该代理平台
            String referUrl = userLoginVO.getRefurl().split("/")[2];
            Integer count = refererUrlDao.selectByReferUrl(cagent, referUrl);
            if (null == count || count < 1) {
                return LoginResponse.faild("0", "域名不匹配");
            }

            //处理不活跃的用户
            int isPhoneNo = 0;//是否为电话号码 0 不是 1 是
//            String mobileUsername = username.substring(3, username.length());//手机号码
//            if(PatternUtils.isMatch(mobileUsername, PatternUtils.PHONENOREGEX)){
//                isPhoneNo = 1;//手机号码
//                username = mobileUsername;
//            }
//
            //查询用户信息
            UserEntity user = newUserDao.getUserInfoByUsername(username, cagent,isPhoneNo);
            if(user == null){
                //查询用户失败,再去游离表确认
                DissociateEntity dissociateEntity = dissociateDao.getDissociateInfoByUsername(username, cagent, isPhoneNo);
                if(dissociateEntity == null){
                    logger.info("用户登录名不正确--->>>tname:{}",userLoginVO.getUsername());
                    return LoginResponse.faild("0", "输入用户登录账号不正确");
                }else{
                    //把用户信息从游离表中写入到用户表
                    user = new UserEntity();
                    BeanUtils.copyProperties(dissociateEntity,user);
                    user.setIsStop("0");
                    user.setIsMobile(userLoginVO.getIsMobile());
                    user.setLoginIp(userLoginVO.getIp());
                    user.setUsername(dissociateEntity.getUsername());
                    user.setLoginTime(date);
                    user.setRegIp(userLoginVO.getIp());
                    newUserDao.insertSelective(user);
                    //删除游离表信息
                    dissociateDao.deleteByPrimaryKey(dissociateEntity.getUid());
                }
            }
            
            //查询用户登录错误日志
            LoginerrormapEntity loginerrormapEntity = loginerrormapDao.findAllByUsername(user.getUsername());
            if(loginerrormapEntity != null){
                //获取错误次数
                Integer errorTimes = loginerrormapEntity.getTimes();
                //获取最后一次登录时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(loginerrormapEntity.getLogintime());
                long loginTimes = calendar.getTimeInMillis();//最后登录的时间毫秒数
                //当前系统的毫秒数
                long nowTimes = System.currentTimeMillis();
                if(nowTimes - loginTimes > 60*60*24*1000){
                    //大于一天,清零所有的登录错误次数
                    loginerrormapEntity.setTimes(0);
                    loginerrormapEntity.setLogintime(date);
                    loginerrormapDao.updateByPrimaryKey(loginerrormapEntity);
                }else{
                    //小于一天
                    if(nowTimes - loginTimes < 300*1000 && errorTimes >= 5){
                        //小于五分钟,并且登录错误次数大于5
                        return LoginResponse.faild("0", "登录失败：【密码错误次数过多,账号已锁定5分钟】");
                    }
                    
                    if(errorTimes >= 10){
                        return LoginResponse.faild("0", "登录失败：【密码错误次数过多,账号已锁定一天】");
                    }
                }
            }
            
            //判断用户密码
            DESEncrypt desEncrypt = new DESEncrypt(KeyConstant.DESKEY);
            String password = desEncrypt.encrypt(userLoginVO.getPassword());
            if(!password.equals(user.getPassword())){
                logger.info("登录失败:【用户输入的登录密码不正确】");
                //写入一条错误日志
                if(loginerrormapEntity != null){
                    //更新错误次数
                    int newErrorTimes = loginerrormapEntity.getTimes()+1;
                    loginerrormapEntity.setTimes(newErrorTimes);
                    loginerrormapEntity.setLogintime(date);
                    //更新
                    loginerrormapDao.updateByPrimaryKeySelective(loginerrormapEntity);
                }else{
                    //写入一条
                    loginerrormapEntity = new LoginerrormapEntity();
                    loginerrormapEntity.setLogintime(date);
                    loginerrormapEntity.setTimes(1);
                    loginerrormapEntity.setUsername(user.getUsername());
                    loginerrormapDao.insertSelective(loginerrormapEntity);
                }
                
                return LoginResponse.faild("0", "登录失败:【输入登录密码错误】");
            }
            
            if("1".equals(user.getIsStop())){
                return LoginResponse.faild("0", "账户已被锁定,请联系客服");
            }
            
            //写入一条登录日志
            UserLoginEntity userLoginEntity = new UserLoginEntity();
            userLoginEntity.setUid(user.getUid());
            userLoginEntity.setIsMobile(userLoginVO.getIsMobile());
            userLoginEntity.setLoginIp(userLoginVO.getIp());
            userLoginEntity.setRefurl(referUrl);
            userLoginEntity.setAddress(userLoginVO.getAddress());
            userLoginEntity.setLoginTime(date);
            userLoginEntity.setIsLogin((byte)1);
            userLoginEntity.setLoginNum(1);
            userLoginEntity.setStatus("1");
            logger.info("用户【"+userLoginVO.getUsername()+"】 登录日志信息：{}",userLoginEntity.toString());
            //写入登录日志
            userLoginDao.insertSelective(userLoginEntity);
            //更新用户最后登录时间
            UserEntity updateEntity = new UserEntity();
            updateEntity.setUid(user.getUid());
            updateEntity.setLoginTime(date);
            newUserDao.updateByPrimaryKeySelective(updateEntity);
            //2.查询用户积分
            Double integralBalance = userWalletDao.getIntegralBalance(user.getUid());
            if(integralBalance == null){
                integralBalance = 0.00D;
            }
            //查询用户所属平台ID
            CagentEntity cagentEntity = cagentDao.selectByCagent(user.getCagent());
            //返回json对象
            //生成token
            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();
            //创建Map缓存
            Map<String,String> cacheJson = new HashMap<String,String>();
            cacheJson.put("uid", String.valueOf(user.getUid()));
            cacheJson.put("userName",user.getUsername());
            cacheJson.put("ag_username",user.getAgUsername());
            cacheJson.put("hg_username", user.getHgUsername());
            cacheJson.put("ag_password", user.getAgPassword());
            cacheJson.put("userkey",token);
            cacheJson.put("loginmobile",StringUtils.isBlank(user.getLoginmobile())?"":user.getLoginmobile());
            cacheJson.put("cagent", user.getCagent().toUpperCase());
            cacheJson.put("cid",String.valueOf(cagentEntity.getId()));
            cacheJson.put("typeid",String.valueOf(user.getTypeId()));//分层ID
            cacheJson.put("times",date.getTime()+"");
            cacheJson.put("login_time",DatePatternUtils.dateToStr(date, DatePatternConstant.NORM_DATETIME_PATTERN));
            cacheJson.put("balance", String.valueOf(user.getWallet()));
            cacheJson.put("integral", String.valueOf(integralBalance));
            JSONObject data = new JSONObject();
            data.put("status", "ok");
            data.put("userKey", token);
            data.put("userName",user.getUsername());
            data.put("balance", user.getWallet());
            data.put("integral",integralBalance);
            data.put("cacheJson", cacheJson);
            data.put("uid", String.valueOf(user.getUid()));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用用户登录业务异常:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return LoginResponse.faild("0", "调用用户登录业务异常");
        }
    }

    @Override
    public JSONObject mobileLogin(MobileLoginVO mobileLoginVO) {
        logger.info("调用用户登录业务开始==================START==================");
        logger.info("手机登录用户【"+mobileLoginVO.getMobileNo()+"】登入参数信息：{}",mobileLoginVO.toString());
        try {
            //手机号
            String mobileNo = mobileLoginVO.getMobileNo();
            //获取平台编码
            String cagent = mobileLoginVO.getCagent();
            //验证来源域名是否属于代该代理平台
            String referUrl = mobileLoginVO.getRefurl().split("/")[2];
            Integer urls = refererUrlDao.selectByReferUrl(cagent, referUrl);
            if (null == urls || urls < 1) {
                return LoginResponse.faild("0", "域名不匹配");
            }

            //验证短信验证码
            String msgCode = mobileLoginVO.getMsgCode();
            if (StringUtils.isBlank(msgCode)) {
                return LoginResponse.faild("0", "短信验证码为空，请输入短信验证码");
            }
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -5);
            Date now = cal.getTime();
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            // 验证短信验证码
            CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent, mobileNo, "3", nowTime);
            if (null == msgEntity) {
                return LoginResponse.faild("0", "短信验证码失效,请重新发送");
            } else {
                if (msgCode.equals(msgEntity.getMsg())) {
                    msgEntity.setStatus("1");
                    cagentMsgDao.updateByPrimaryKey(msgEntity);
                } else {
                    // 记录错误次数
                    if (UserLoginController.errorMap.containsKey(cagent + mobileNo)) {
                        int count = UserLoginController.errorMap.get(cagent + mobileNo);
                        // 如果大于5次错误则让验证码失效,必须重新发送
                        if (count < 5) {
                            UserLoginController.errorMap.put(cagent + mobileNo, count + 1);
                        } else {
                            msgEntity.setStatus("1");
                            cagentMsgDao.updateByPrimaryKey(msgEntity);
                            UserLoginController.errorMap.remove(cagent + mobileNo);
                        }
                    } else {
                        UserLoginController.errorMap.put(cagent + mobileNo, 1);
                    }
                    return LoginResponse.faild("0", "验证码错误");
                }
            }

            //处理不活跃的用户
            int isPhoneNo = 1;//是否为电话号码 0 不是 1 是

            //查询用户信息
            UserEntity user = newUserDao.getUserInfoByUsername(mobileNo, cagent,isPhoneNo);
            if(user == null){
                //查询用户失败,再去游离表确认
                DissociateEntity dissociateEntity = dissociateDao.getDissociateInfoByUsername(mobileNo, cagent, isPhoneNo);
                if(dissociateEntity == null){
                    logger.info("用户登录手机号不正确--->>>mobileNo:{}",mobileLoginVO.getMobileNo());
                    return LoginResponse.faild("0", "输入用户登录手机号不正确");
                }else{
                    //把用户信息从游离表中写入到用户表
                    user = new UserEntity();
                    BeanUtils.copyProperties(dissociateEntity, user);
                    user.setIsStop("0");
                    user.setIsMobile(mobileLoginVO.getIsMobile());
                    user.setLoginIp(mobileLoginVO.getIp());
                    user.setLoginTime(new Date());
                    newUserDao.insertSelective(user);
                    //删除游离表信息
                    dissociateDao.deleteByPrimaryKey(dissociateEntity.getUid());
                }
            }

            //查询用户登录错误日志
            LoginerrormapEntity loginerrormapEntity = loginerrormapDao.findAllByUsername(user.getUsername());
            if(loginerrormapEntity != null){
                //获取错误次数
                Integer errorTimes = loginerrormapEntity.getTimes();
                //获取最后一次登录时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(loginerrormapEntity.getLogintime());
                long loginTimes = calendar.getTimeInMillis();//最后登录的时间毫秒数
                //当前系统的毫秒数
                long nowTimes = System.currentTimeMillis();
                if(nowTimes - loginTimes > 60*60*24*1000){
                    //大于一天,清零所有的登录错误次数
                    loginerrormapEntity.setTimes(0);
                    loginerrormapEntity.setLogintime(new Date());
                    loginerrormapDao.updateByPrimaryKey(loginerrormapEntity);
                }else{
                    //小于一天
                    if(nowTimes - loginTimes < 300*1000 && errorTimes >= 5){
                        //小于五分钟,并且登录错误次数大于5
                        return LoginResponse.faild("0", "登录失败：【密码错误次数过多,账号已锁定5分钟】");
                    }

                    if(errorTimes >= 10){
                        return LoginResponse.faild("0", "登录失败：【密码错误次数过多,账号已锁定一天】");
                    }
                }
            }

            if("1".equals(user.getIsStop())){
                return LoginResponse.faild("0", "账户已被锁定,请联系客服");
            }

            //写入一条登录日志
            UserLoginEntity userLoginEntity = new UserLoginEntity();
            userLoginEntity.setUid(user.getUid());
            userLoginEntity.setIsMobile(mobileLoginVO.getIsMobile());
            userLoginEntity.setLoginIp(mobileLoginVO.getIp());
            userLoginEntity.setRefurl(referUrl);
            userLoginEntity.setAddress(mobileLoginVO.getAddress());
            userLoginEntity.setLoginTime(new Date());
            userLoginEntity.setIsLogin((byte)1);
            userLoginEntity.setLoginNum(1);
            userLoginEntity.setStatus("1");
            logger.info("手机号【"+mobileNo+"】登录信息：{}",userLoginEntity.toString());
            //写入登录日志
            userLoginDao.insertSelective(userLoginEntity);
            //更新用户最后登录时间
            UserEntity updateEntity = new UserEntity();
            updateEntity.setUid(user.getUid());
            updateEntity.setLoginTime(new Date());
            newUserDao.updateByPrimaryKeySelective(updateEntity);

            //查询需要存入缓存中的数据
            //1.查询所有平台的游戏配置信息,所有状态为1的已开启的游戏配置
//            List<PlatformConfigEntity> platformConfigs = platformConfigDao.findAll();
//            if(CollectionUtils.isEmpty(platformConfigs)){
//                platformConfigs = new ArrayList<>();
//            }
            //2.查询用户积分
            Double integralBalance = userWalletDao.getIntegralBalance(user.getUid());
            if(integralBalance == null){
                integralBalance = 0.00D;
            }
            //查询用户所属平台ID
            CagentEntity cagentEntity = cagentDao.selectByCagent(user.getCagent());
            //返回json对象
            //生成token
            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();
            JSONObject data = new JSONObject();
            data.put("status", "ok");
            data.put("userKey", token);
            data.put("userName",user.getUsername());
            data.put("balance", user.getWallet());
            data.put("integral",integralBalance);

            JSONObject cacheJson = new JSONObject();
            cacheJson.put("uid", String.valueOf(user.getUid()));
            cacheJson.put("userName",user.getUsername());
            cacheJson.put("ag_username",user.getAgUsername());
            cacheJson.put("hg_username", user.getHgUsername());
            cacheJson.put("ag_password", user.getAgPassword());
            cacheJson.put("userkey",token);
            cacheJson.put("loginmobile",user.getLoginmobile());
            cacheJson.put("cagent", user.getCagent());
            cacheJson.put("balance", String.valueOf(user.getWallet()));
            cacheJson.put("integral",String.valueOf(integralBalance));
            cacheJson.put("cid", String.valueOf(cagentEntity.getId()));
            cacheJson.put("typeid", String.valueOf(user.getTypeId()));//分层ID
            cacheJson.put("login_time",DatePatternUtils.dateToStr(user.getLoginTime(), DatePatternConstant.NORM_DATETIME_MINUTE_PATTERN));
            cacheJson.put("Transfer", "0");
            cacheJson.put("WithDraw", "0");

            data.put("cacheJson", cacheJson);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用用户登录业务异常:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return LoginResponse.faild("0", "调用用户登录业务异常");
        }
    }

    /**
     * 获取用户详情
     */
    @Override
    public Map<String, Object> getUserInfo(String uid) {
        logger.info("调用查询用户详情业务开始==================start=====================");
        Map<String,Object> data = new HashMap<>();
        try {
            //查询用户钱包余额
            Double balance = userLoginDao.getUserBalance(Integer.valueOf(uid));
            //查询用户积分余额
            Double integralBalance = userCapitalService.handleUserIntegralBalance(Integer.parseInt(uid),0.00,3);
            if(integralBalance == null){
                integralBalance = 0.00D;
            }
            data.put("balance", balance);
            data.put("integral", integralBalance);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户详情业务异常:{}",e.getMessage());
        }
        return data;
    }

    /**
     * 账号登录
     */
    @Override
    public ResultResponse accountLogin(String username,String password,String isMobile,
                    String refurl,String ip,String address,String sessionId) throws BusinessException{
        
        //判断用户账号类型
        String cagent = username.substring(0,3);
        //通过平台编码查询用户是否属于该代理平台的用户
        int count = refererUrlDao.selectByReferUrl(cagent,refurl.split("/")[2]);
        if(count == 0){
            return ResultResponse.faild("域名不匹配");
        }
        //通过用户名查询用户信息
        //TODO 待续...........
        return null;
    }
}
