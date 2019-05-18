package com.cn.tianxia.api.service.v2.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.common.v2.PatternUtils;
import com.cn.tianxia.api.common.v2.ResultResponse;
import com.cn.tianxia.api.domain.txdata.v2.ActivityDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentMsgDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentStoredvalueDao;
import com.cn.tianxia.api.domain.txdata.v2.DissociateDao;
import com.cn.tianxia.api.domain.txdata.v2.GuagualeActivityDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.UserAcitivityRecordDao;
import com.cn.tianxia.api.po.v2.GGLActivityPO;
import com.cn.tianxia.api.project.v2.ActivityEntity;
import com.cn.tianxia.api.project.v2.CagentMsgEntity;
import com.cn.tianxia.api.project.v2.DissociateEntity;
import com.cn.tianxia.api.project.v2.GuagualeActivityEntity;
import com.cn.tianxia.api.project.v2.UserAcitivityRecordEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.service.v2.ActivityService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.utils.v2.CalcUtils;

/**
 * @ClassName ActivityServiceImpl
 * @Description 活动接口实现类
 * @author Hardy
 * @Date 2019年3月12日 上午11:35:11
 * @version 1.0.0
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private GuagualeActivityDao guagualeActivityDao;

    @Autowired
    private CagentStoredvalueDao cagentStoredvalueDao;

    @Autowired
    private CagentMsgDao cagentMsgDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private NewUserDao newUserDao;

    @Autowired
    private DissociateDao dissociateDao;
    
    @Autowired
    private UserAcitivityRecordDao userAcitivityRecordDao;

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public ResultResponse guagualeAcitivity(Map<String,String> userMap, String agentCode,String type) throws Exception {
        logger.info("调用刮刮乐活动接口业务开始==================START=================");
        // 通过平台编码查询刮刮乐活动
        ActivityEntity activity = activityDao.findOneByCagent(agentCode,type);
        if (activity == null) {
            logger.info("查询平台活动失败");
            return ResultResponse.faild("根据平台编码【" + agentCode + "】,查询活动类型【" + type + "】失败");
        }

        long activityId = activity.getId();// 活动ID
        // 通过活动ID查询刮刮乐活动
        GuagualeActivityEntity guagualeActivityEntity = guagualeActivityDao.findOneByActivityId(activityId);
        if (guagualeActivityEntity == null) {
            logger.info("查询刮刮乐活动失败");
            return ResultResponse.faild("根据平台编码【" + agentCode + "】,查询活动类型【" + 3 + "】失败");
        }

        //查询用户是否抽过奖
        UserAcitivityRecordEntity recordEntity = userAcitivityRecordDao.selectRecordByAidAndUid(activityId,Integer.parseInt(userMap.get("uid")));
        if (recordEntity != null) {
            return ResultResponse.faild("用户已参与过此次活动");
        }
        //查询用户是否验证过注册手机号
        int vaildPhone = 0;
        UserEntity user = newUserDao.selectByPrimaryKey(Integer.parseInt(userMap.get("uid")));
        if (StringUtils.isNotBlank(user.getLoginmobile())) {
            vaildPhone = 1;
        }

        // 查询平台额度
        Double cagentremainvalue = cagentStoredvalueDao.getCagentRemainvalue(activity.getCid());
        if (cagentremainvalue == null) {
            logger.info("通过平台编码【" + agentCode + "】查询平台剩余额度失败,查询结果为:{}", cagentremainvalue);
            cagentremainvalue = 0.00D;
        }
        // 获取活动最小金额
        Double minquota = CalcUtils.divLong(guagualeActivityEntity.getMinquota(), 100);
        // 获取活动最大金额
        Double maxquota = CalcUtils.divLong(guagualeActivityEntity.getMaxquota(), 100);
        // 生成最小金额与最大金额区间的随机金额
        Double usermoney = CalcUtils.generatorRandomDouble(minquota, maxquota);
        // 判断剩余额度是否够抽奖
        if (cagentremainvalue < minquota || cagentremainvalue < usermoney) {
            logger.info("平台【" + agentCode + "】剩余额度不足,告诉平台商充值");
            return ResultResponse.faild("平台【" + agentCode + "】剩余额度不足");
        }
        GGLActivityPO ggl = new GGLActivityPO();
        ggl.setActivityId(activityId);
        ggl.setStatus(activity.getStatus().intValue());
        ggl.setType(activity.getType().intValue());
        ggl.setUsermoney(usermoney);
        ggl.setVerifyPhone(vaildPhone);
        return ResultResponse.success("查询成功", ggl);
    }

    /**
     * 领取刮刮乐奖金
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public ResultResponse receiveGGLBonus(Map<String,String> userMap, String activityAmount, String activityId, String code,
            String phoneNo) throws Exception {
        try {
            logger.info("调用领取刮刮乐奖金业务开始==================START======================");
            // 是否需要修改用户的手机号码
            boolean isUpdateMobile = false;// 默认为false,不用修改(因为有手机注册)
            // 从缓存中获取用户的平台编码
            String cagent = userMap.get("cagent");
            // 会员分层ID
            Integer typeId = Integer.parseInt(userMap.get("typeid"));
            //用户名称
            String username = userMap.get("userName");
            Integer cid = Integer.parseInt(userMap.get("cid"));
            String uid = userMap.get("uid");
            logger.info("用户:{}查询是否参与过抽奖开始=================start==================",phoneNo);
            //查询用户是否抽过奖
            UserAcitivityRecordEntity recordEntity = userAcitivityRecordDao.selectRecordByAidAndUid(Long.valueOf(activityId),Integer.parseInt(userMap.get("uid")));
            if (recordEntity != null) {
                return ResultResponse.faild("用户已参与过此次活动");
            }
            // 校验用户手机号码
            if (StringUtils.isNotBlank(phoneNo)) {
                ResultResponse verifyResult = verifyPhoneNo(uid, code, phoneNo, cagent);
                if (!"0".equals(verifyResult.getCode())) {
                    return verifyResult;
                }

                // 验证用户手机号码是否一致
                String regMobile = userMap.get("loginmobile");
                if (!phoneNo.equals(regMobile)) {
                    // 判断用户手机号码是否已存在数据库,通过手机号码查询用户的信息
                    UserEntity user = newUserDao.getUserInfoByUsername(phoneNo, cagent, 1);
                    if (user == null) {
                        // 查询用户失败,再去游离表确认
                        DissociateEntity dissociateEntity = dissociateDao.getDissociateInfoByUsername(phoneNo, cagent, 1);
                        if (dissociateEntity == null) {
                            isUpdateMobile = true;
                        }
                    }
                }
            } else {
                phoneNo = userMap.get("loginmobile");
            }
            
            //通过活动ID查询活动信息
            ActivityEntity activity = activityDao.selectByPrimaryKey(Long.parseLong(activityId));
            if (activity == null) {
                logger.info("查询平台活动失败");
                return ResultResponse.faild("非法活动ID,查询活动信息失败");
            }
            long bounsmoney = CalcUtils.formatDouble(Double.parseDouble(activityAmount)*100);
            //时间戳
            long times = System.currentTimeMillis();
            
            //生成一条用户抽奖记录信息
            UserAcitivityRecordEntity userAcitivityRecordEntity = new UserAcitivityRecordEntity();
            userAcitivityRecordEntity.setActivityAmount(bounsmoney);
            userAcitivityRecordEntity.setActivityId(Long.parseLong(activityId));
            userAcitivityRecordEntity.setActivityName(activity.getName());
            userAcitivityRecordEntity.setActivityType(activity.getType());
            userAcitivityRecordEntity.setCagent(activity.getCagent());
            userAcitivityRecordEntity.setCareteTime(times);
            userAcitivityRecordEntity.setCid(activity.getCid());
            userAcitivityRecordEntity.setMobile(phoneNo);
            userAcitivityRecordEntity.setTypeId(typeId);
            userAcitivityRecordEntity.setUid(Integer.parseInt(uid));
            userAcitivityRecordEntity.setUsername(username);
            userAcitivityRecordEntity.setActivityNumber("GGL" + System.currentTimeMillis());
            logger.info("用户:{}生成一条抽奖记录写入到抽奖记录表中开始=================start==================",phoneNo);
            //插入用户记录
            userAcitivityRecordDao.insertSelective(userAcitivityRecordEntity);

            //是否修改用户的电话号码
            if(isUpdateMobile){
            	logger.info("用户:{}是否需要修改原有的电话号码=================start==================",phoneNo);
                newUserDao.updateUserPhoneNo(uid, phoneNo);
                //如果修改用户的手机号码的话,需要重新更新缓存
                Map<String,String> data = redisUtils.get(uid,Map.class);
                data.put("loginmobile",phoneNo);
                redisUtils.hmset(uid, data);
            }
            
            //修改活动使用金额
            guagualeActivityDao.subtractActicityUserMoney(bounsmoney,Long.parseLong(activityId));
            logger.info("用户:{}参与抽奖完,修改用户钱包余额结束=================end==================",phoneNo);
            //查询平台额度,并且扣除平台额度
//            cagentStoredvalueDao.updateCagentRemainvalue(cid, Double.parseDouble(activityAmount));
            //增加用户钱包余额
//            newUserDao.subtractUserBalance(Integer.parseInt(uid), Double.parseDouble(activityAmount));
            return ResultResponse.success("领取成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户:["+phoneNo+"]调用领取刮刮了奖金业务异常:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultResponse.faild("用户:["+phoneNo+"]领取失败");
        }
    }

    /**
     * @Description 验证用户手机号码
     * @param uid
     * @param code
     * @param phoneNo
     * @return
     * @throws Exception
     */
    private ResultResponse verifyPhoneNo(String uid, String code, String phoneNo, String cagent) throws Exception {
        // 校验用户手机号码
        // 校验手机格式
        if (!PatternUtils.isMatch(phoneNo, PatternUtils.PHONENOREGEX)) {
            return ResultResponse.faild("请求参数错误:非法手机号码【" + phoneNo + "】");
        }
        // 校验手机验证码
        if (StringUtils.isBlank(code)) {
            return ResultResponse.faild("请求参数错误:手机验证码不能为空");
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5);
        Date now = cal.getTime();
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        // 验证短信验证码
        CagentMsgEntity msgEntity = cagentMsgDao.selectMsgLog(cagent, phoneNo, "4", nowTime);
        if (null == msgEntity) {
            return ResultResponse.faild("短信验证码失效,请重新发送");
        } else {
            if (code.equals(msgEntity.getMsg())) {
                msgEntity.setStatus("1");
                cagentMsgDao.updateByPrimaryKey(msgEntity);
            } else {
                return ResultResponse.faild("验证码错误");
            }
        }
        return ResultResponse.success("验证成功", null);
    }
}
