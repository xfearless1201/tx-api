package com.cn.tianxia.api.service.v2.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.common.v2.PatternUtils;
import com.cn.tianxia.api.domain.txdata.v2.CagentMsgDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentMsgconfigDao;
import com.cn.tianxia.api.domain.txdata.v2.MobileLogDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.RefererUrlDao;
import com.cn.tianxia.api.dx.service.CRShortMessageServiceImpl;
import com.cn.tianxia.api.dx.service.DDYShortMessageServiceImpl;
import com.cn.tianxia.api.dx.service.HYWXShortMessageServiceImpl;
import com.cn.tianxia.api.dx.service.YDXShortMessageServiceImpl;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.v2.CagentMsgEntity;
import com.cn.tianxia.api.project.v2.CagentMsgconfigEntity;
import com.cn.tianxia.api.project.v2.MobileLogEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.service.v2.ShortMessageService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.vo.v2.ChangeMobileVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/7 20:04
 * @Description: 手机短信服务实现类
 */
@Service
public class ShortMessageServiceImpl implements ShortMessageService {

    private final static Logger logger = LoggerFactory.getLogger(ShortMessageServiceImpl.class);

    @Autowired
    private CagentMsgconfigDao cagentMsgconfigDao;
    @Autowired
    private CagentMsgDao cagentMsgDao;
    @Autowired
    private RefererUrlDao refererUrlDao;
    @Autowired
    private NewUserDao newUserDao;
    @Autowired
    private MobileLogDao mobileLogDao;

    @Override
    public JSONObject sendRegisterSuccess(String cagent, String mobileNo, String passWord, String url) {
        try {
            return sendMessage(cagent,mobileNo,passWord,url,0);
        } catch (Exception e) {
            logger.error("发送注册成功短信失败:{}" + e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"发送注册成功短信失败:" + e.getMessage());

        }
    }

    @Override
    public JSONObject sendRegisterCode(String cagent, String mobileNo, String refererUrl) {
        logger.info("发送手机注册短信业务-------------------------------开始-----------------------");
        try {

            refererUrl = refererUrl.split("/")[2];

            JSONObject verify = verifyParam(cagent, mobileNo, refererUrl);
            if (null != verify) {
                return verify;
            }
            // 检测手机号是否已绑定
            UserEntity userEntity1 = newUserDao.selectUserByMobile(cagent, mobileNo);
            if (null != userEntity1) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "手机号已存在");
            }
            // 检测该用户1分钟以内是否发送过注册短信,是则返回提示(cagent+mobileNo)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            Date now = cal.getTime();
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            // 验证短信验证码
            CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent, mobileNo, "1", nowTime);
            if (msgEntity != null) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "操作频繁,请稍后再试");
            }
            // 生成随机验证码
            String message = String.valueOf(RandomUtils.nextInt(1000, 9999));

            return sendMessage(cagent, mobileNo, message, refererUrl,1);
        } catch (Exception e) {
            logger.error("发送手机注册验证码业务异常：{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE, "发送手机注册验证码业务异常:" + e.getMessage());
        }
    }

    @Override
    public JSONObject sendLoginCode(String cagent, String mobileNo, String refererUrl) {
        logger.info("发送用户手机登录验证码业务--------------------------------开始-----------------------");
        try {

            refererUrl = refererUrl.split("/")[2];

            JSONObject verify = verifyParam(cagent, mobileNo, refererUrl);
            if (null != verify) {
                return verify;
            }
            // 检测手机号是否已绑定
            UserEntity userEntity1 = newUserDao.selectUserByMobile(cagent, mobileNo);
            if (null == userEntity1) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "用户不存在");
            }
            // 检测该用户1分钟以内是否发送过注册短信,是则返回提示(cagent+mobileNo)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            Date now = cal.getTime();
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            // 验证短信验证码
            CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent, mobileNo, "3", nowTime);
            if (msgEntity != null) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "操作频繁,请稍后再试");
            }
            // 生成随机验证码
            String message = String.valueOf(RandomUtils.nextInt(1000, 9999));

            return sendMessage(cagent, mobileNo, message, refererUrl,3);
        } catch (Exception e) {
            logger.error("发送手机登录验证码业务异常：{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE, "发送手机登录验证码业务异常:" + e.getMessage());
        }
    }

    @Override
    public JSONObject sendChangeCode(String cagent, String mobileNo, String refererUrl) {
        logger.info("发送用户绑定手机验证码业务--------------------------------开始----------- mobileNo:" + mobileNo);
        try {

            refererUrl = refererUrl.split("/")[2];

            JSONObject verify = verifyParam(cagent, mobileNo, refererUrl);
            if (null != verify) {
                return verify;
            }
            // 检测手机号是否已绑定
            UserEntity userEntity1 = newUserDao.selectUserByMobile(cagent, mobileNo);
            if (null != userEntity1) {
                 logger.info("手机号码： "+ mobileNo+"已存在！");
                return BaseResponse.error(BaseResponse.ERROR_CODE, "该手机号已存在");
            }
            // 检测该用户1分钟以内是否发送过注册短信,是则返回提示(cagent+mobileNo)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            Date now = cal.getTime();
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            // 验证短信验证码
            CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent, mobileNo, "4", nowTime);
            if (msgEntity != null) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "操作频繁,请稍后再试");
            }
            // 生成随机验证码
            String message = String.valueOf(RandomUtils.nextInt(1000, 9999));

            logger.info("修改手机号码短信验证码："+ message);

            JSONObject jsonObject = sendMessage(cagent, mobileNo, message, refererUrl,4);
            
            logger.info("手机号码："+ mobileNo+ "调用发送短信验证码返回信息：{}",jsonObject.toString());
            return jsonObject;
        } catch (Exception e) {
            logger.error("发送用户绑定手机验证码业务异常：{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE, "发送用户绑定手机验证码业务异常:" + e.getMessage());
        }
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public JSONObject changeMobile(ChangeMobileVO changeMobileVO) {
        logger.info("用户修改绑定手机业务-----------------------------开始-----------------------");
        try {
            String uid = changeMobileVO.getUid();
            UserEntity localUser = newUserDao.selectByPrimaryKey(Integer.parseInt(uid));
            if (null == localUser) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "用户异常");
            }
            // 检测手机号是否已绑定
            String cagent = changeMobileVO.getCagent();
            String mobileNo = changeMobileVO.getMobileNo();
            UserEntity userEntity1 = newUserDao.selectUserByMobile(cagent, mobileNo);
            if (null != userEntity1) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "该手机号已绑定");
            }

            if (StringUtils.isBlank(mobileNo)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE,"手机号不能为空");
            }

            if (!mobileNo.matches(PatternUtils.PHONENOREGEX)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "手机号格式不正确");
            }

            String msgCode = changeMobileVO.getMsgCode();
            if (StringUtils.isBlank(msgCode)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "验证码不能为空");
            }

            String password = changeMobileVO.getPassword();
            if (StringUtils.isBlank(password)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "密码不能为空");
            }

            List<MobileLogEntity> mobileLogEntityList = mobileLogDao.selectMobileLogByUid(changeMobileVO.getUid());

            if (mobileLogEntityList.size() > 0) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "当日只能更换一次绑定操作");
            }
            DESEncrypt desEncrypt = new DESEncrypt(KeyConstant.DESKEY);
            password = desEncrypt.encrypt(password);
            if (!password.equals(localUser.getPassword())) {
                return BaseResponse.error(BaseResponse.ERROR_CODE,"密码错误");
            }

            //验证短信验证码
            JSONObject verifyMsgCode = verifyMsgCode(cagent,mobileNo,msgCode,"4",-5);
            if (null != verifyMsgCode) {
                return verifyMsgCode;
            }
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.MINUTE, -5);
//            Date now = cal.getTime();
//            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
//            // 验证短信验证码
//            CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent,mobileNo,"4",nowTime);
//            if (null == msgEntity) {
//                return BaseResponse.error(BaseResponse.ERROR_CODE,"短信验证码失效,请重新发送");
//            } else {
//                if (msgCode.equals(msgEntity.getMsg())) {
//                    msgEntity.setStatus("1");
//                    cagentMsgDao.updateByPrimaryKey(msgEntity);
//                } else {
//                    return BaseResponse.error(BaseResponse.ERROR_CODE, "验证码错误");
//                }
//            }

            localUser.setMobile(mobileNo);
            localUser.setLoginmobile(mobileNo);

            newUserDao.updateByPrimaryKeySelective(localUser);

            MobileLogEntity entity = new MobileLogEntity();
            entity.setCagent(cagent);
            entity.setIp(changeMobileVO.getIp());
            entity.setUid(Integer.parseInt(uid));
            entity.setNewMobile(mobileNo);
            entity.setOldMobile(changeMobileVO.getLoginMobile());
            entity.setUpdateTime(new Date());

            mobileLogDao.insertSelective(entity);

            return BaseResponse.success("success");
        } catch (Exception e) {
            logger.error("用户修改绑定手机业务异常：{}", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return BaseResponse.error(BaseResponse.ERROR_CODE, "用户修改绑定手机业务异常:" + e.getMessage());
        }
    }

    @Override
    public JSONObject verifyMsgCode(String cagent, String mobileNo, String msgCode, String type, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minutes);
        Date now = cal.getTime();
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        // 验证短信验证码
        CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent,mobileNo,type,nowTime);
        if (null == msgEntity) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"短信验证码失效,请重新发送");
        } else {
            if (msgCode.equals(msgEntity.getMsg())) {
                msgEntity.setStatus("1");
                cagentMsgDao.updateByPrimaryKey(msgEntity);
            } else {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "验证码错误");
            }
        }
        return null;
    }

    private JSONObject verifyParam(String cagent, String mobileNo, String refererUrl) {
        if (StringUtils.isBlank(cagent) || !cagent.matches(PatternUtils.CAGENTREGEX)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE, "代理商编号格式错误");
        }

        if (StringUtils.isBlank(mobileNo)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE, "手机号不能为空");
        }

        if (!mobileNo.matches(PatternUtils.PHONENOREGEX)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE, "手机号格式错误");
        }

        if (StringUtils.isBlank(refererUrl)) {
            logger.error("来源域名为空！");
            return BaseResponse.error(BaseResponse.ERROR_CODE, "来源域名为空");
        }
        boolean isWhiteDomain = false;
        List<String> domainList = refererUrlDao.findAllByCagent(cagent);
        if(!CollectionUtils.isEmpty(domainList)){
            String refurls = domainList.toString();
            if(refurls.indexOf(refererUrl) > 0){
                isWhiteDomain = true;
            }
        }
        if (!isWhiteDomain) {
            logger.error("来源域名与代理平台不匹配！");
            return BaseResponse.error(BaseResponse.ERROR_CODE, "域名不匹配");
        }
        return null;
    }

    private JSONObject sendMessage(String cagent,String mobileNo,String message,String url,int type) throws Exception{
        CagentMsgconfigEntity cagentMsgconfigEntity = cagentMsgconfigDao.selectByCagent(cagent);
        if (cagentMsgconfigEntity == null) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"未配置短信渠道");
        }
        String PaymentConfig = cagentMsgconfigEntity.getConfig();
        // 短信平台商配置信息
        Map<String, String> pmapsconfig = JSONObject.fromObject(PaymentConfig);
        String shortLetter = cagentMsgconfigEntity.getMname();
        String sign = cagentMsgconfigEntity.getSign();
        String returnStr = "";
        if (type == 0) {
            if ("cr".equals(shortLetter)) {
                CRShortMessageServiceImpl cRShortMessageServiceImpl = new CRShortMessageServiceImpl(pmapsconfig);
                returnStr = cRShortMessageServiceImpl.SendRegOut(mobileNo, message, "", sign);
            } else if ("ddy".equals(shortLetter)) {
                DDYShortMessageServiceImpl dDYShortMessageServiceImpl = new DDYShortMessageServiceImpl(pmapsconfig);
                returnStr = dDYShortMessageServiceImpl.SendRegOut(mobileNo, message, sign);
            } else if ("ihuyi".equals(shortLetter)) {
                HYWXShortMessageServiceImpl hYWXShortMessageServiceImpl = new HYWXShortMessageServiceImpl(
                        pmapsconfig);
                returnStr = hYWXShortMessageServiceImpl.SendRegOut(mobileNo, message);
            } else if ("dxw".equals(shortLetter)) {
                YDXShortMessageServiceImpl yDXShortMessageServiceImpl = new YDXShortMessageServiceImpl(pmapsconfig);
                returnStr = yDXShortMessageServiceImpl.SendRegOut(mobileNo, message, sign);
            } else {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "系统错误:未知的短信渠道");
            }
        } else {
            if ("cr".equals(shortLetter)) {
                CRShortMessageServiceImpl cRShortMessageServiceImpl = new CRShortMessageServiceImpl(pmapsconfig);
                returnStr = cRShortMessageServiceImpl.SendOut(mobileNo, message, "", sign);
            } else if ("ddy".equals(shortLetter)) {
                DDYShortMessageServiceImpl dDYShortMessageServiceImpl = new DDYShortMessageServiceImpl(pmapsconfig);
                returnStr = dDYShortMessageServiceImpl.SendOut(mobileNo, message, sign);
            } else if ("ihuyi".equals(shortLetter)) {
                HYWXShortMessageServiceImpl hYWXShortMessageServiceImpl = new HYWXShortMessageServiceImpl(
                        pmapsconfig);
                returnStr = hYWXShortMessageServiceImpl.SendOut(mobileNo, message);
            } else if ("dxw".equals(shortLetter)) {
                YDXShortMessageServiceImpl yDXShortMessageServiceImpl = new YDXShortMessageServiceImpl(pmapsconfig);
                returnStr = yDXShortMessageServiceImpl.SendOut(mobileNo, message, sign);
            } else {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "系统错误:未知的短信渠道");
            }
        }
        if ("success".equals(returnStr)) {
            CagentMsgEntity newMsg = new CagentMsgEntity();
            newMsg.setCagent(cagent);
            newMsg.setMname(shortLetter);
            newMsg.setMobileno(mobileNo);
            newMsg.setMsg(message);
            newMsg.setSendtime(new Date());
            newMsg.setStatus("0");
            newMsg.setType(String.valueOf(type));
            newMsg.setDomain(url);
            cagentMsgDao.insertSelective(newMsg);
            return BaseResponse.success("success");
        } else {
            return BaseResponse.error(BaseResponse.ERROR_CODE,returnStr);
        }

    }
}
