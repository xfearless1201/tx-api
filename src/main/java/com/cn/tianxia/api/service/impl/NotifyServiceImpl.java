package com.cn.tianxia.api.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import com.cn.tianxia.api.domain.txdata.NotifyDao;
import com.cn.tianxia.api.po.ResultResponse;
import com.cn.tianxia.api.project.RechargeVO;
import com.cn.tianxia.api.service.NotifyService;
import com.cn.tianxia.api.service.v2.UserCapitalService;
import com.cn.tianxia.api.vo.CagentYespayVO;
import com.cn.tianxia.api.vo.CjOrDmlRateVO;
import com.cn.tianxia.api.vo.RechargeOrderVO;

@Service
@Transactional
public class NotifyServiceImpl implements NotifyService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);
    
    @Autowired
    public NotifyDao notifyDao;
    
    @Autowired
    private UserCapitalService userCapitalService;
    
    
    
    
    // 回调主方法
    @SuppressWarnings("unused")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    @Override
    public int saveProcess(Map<String, Object> paramsMap) throws Exception {
        try {
            int res = 0;
            int res4 = 0;
            int res11 = 0;
            int res12 = 0;
            if (Double.parseDouble(paramsMap.get("cj").toString()) > 0) {
                res = notifyDao.insertUserTreasure1(paramsMap);
                res4 = notifyDao.insertTCagentStoredvalueLog1(paramsMap);
            }
            int res1 = notifyDao.insertUserTreasure(paramsMap);
            int res2 = notifyDao.updateTrecharge(paramsMap);
            int res3 = notifyDao.insertTUserQuantity(paramsMap);
            int res10 = notifyDao.insertTCagentStoredvalueLog(paramsMap);
//            Double res5 = notifyDao.queryTuser(paramsMap);
//            Double res6 = notifyDao.querytuserwallet(paramsMap);
//            paramsMap.put("old_integral", res6);
            double newamt = 0.0;
  /*          if (res6 == null) {
                newamt = Double.parseDouble(paramsMap.get("amt").toString()) * (res5.doubleValue() / 100.0);
                paramsMap.put("jfb", newamt);
                res11 = notifyDao.insertTuserwallet(paramsMap);
                res6 = 0.0;
            } else {
                newamt = Double.parseDouble(paramsMap.get("amt").toString()) * (res5.doubleValue() / 100.0) + res6.doubleValue();
                paramsMap.put("jfb", newamt);
                res12 = notifyDao.updateTuserwallet(paramsMap);
            }
            int res7 = notifyDao.insertTuserwalletlog(paramsMap);*/
            int res8 = notifyDao.updateTCagentStoredvalue(paramsMap);
            int res9 = notifyDao.updateUserMoney(paramsMap);
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public String saveRecharge(String orderNo,String tradeStatus,String tradeNo,String successStatus,String ip,String params) {
        logger.info("回调写入订单信息、打码量、资金流水开始==============START=======================");
        int result = 0;
        try {
            RechargeVO recharge = notifyDao.findRechargeByOrderNo(orderNo);
            if(recharge == null){
                logger.error("非法订单号,查询订单信息失败!");
                return "success";
            }
            //判断订单状态
            if(tradeStatus.equals(successStatus)){//订单支付成功
                //计算用户充值获取的彩金:彩金  = 充值金额  X 彩金倍率
                BigDecimal cj = new BigDecimal("0.00");//彩金倍率
                if(recharge.getDividendRate() != null && recharge.getDividendRate() > 0){
                    cj = new BigDecimal(recharge.getDividendRate()).multiply(new BigDecimal(recharge.getOrderAmount()));
                }
                
                //计算用户的打码量:打码量 = (充值金额  + 彩金) * 打码量倍率
                BigDecimal dml = new BigDecimal("0.00");//打码量倍率
                if(recharge.getCodingRade() != null && recharge.getCodingRade() > 0){
                    dml = new BigDecimal(recharge.getCodingRade()).multiply(new BigDecimal(recharge.getOrderAmount()).add(cj));
                }
                
                String cjAmount = new DecimalFormat("0.00").format(cj);
                String dmlAmount = new DecimalFormat("0.00").format(dml);
                logger.info("用户获取的彩金[CJ="+cjAmount+"]");
                logger.info("用户获取的打码量:[dml="+dmlAmount+"]");
                logger.info("用户充值订单金额:[orderAmount="+recharge.getOrderAmount()+"]");
                recharge.setCj(Double.parseDouble(cjAmount));
                recharge.setDml(Double.parseDouble(dmlAmount));
                recharge.setIp(ip);
                recharge.setTradeNo(tradeNo);
                recharge.setParams(params);
                logger.info("回调请求参数:[orderNo:"+recharge.getOrderNo()+",tradeNo:"+recharge.getTradeNo()+",tradeStatus:"+tradeStatus+"]");
                
                //查询平台剩余金额
                Double remainvalue = notifyDao.selectCagentQuota(recharge.getCid());
                if(remainvalue == null){
                    remainvalue = 0.00;
                }
                Double withholdAmount = cj.add(new BigDecimal(recharge.getOrderAmount())).doubleValue();//平台预扣金额
                if(remainvalue > withholdAmount){
                    remainvalue = withholdAmount;//避免出现负数
                }else {
                    remainvalue = 0.00;
                }
                recharge.setRemainvalue(remainvalue);
                
                int userTreasures = notifyDao.findUserTreasureByOrderNo(recharge.getUid(),recharge.getOrderNo(),recharge.getCagent());
                if(userTreasures > 0){
                    logger.info("用户资金流水已存在,资金流水订单号:{}",recharge.getOrderNo());
                    return "success";
                }
                //更新订单 和 钱包余额
                result = notifyDao.updateRechargeAndBalance(recharge);
                //在写入用户资金流水
                if(result > 0){
                    result = notifyDao.saveUserTreasureAndQuantity(recharge);
                }
            }else{
              //订单支付失败,更新订单状态为失败
                result = notifyDao.updateRechargeStatus(orderNo, String.valueOf(recharge.getUid()), tradeNo, tradeStatus);
            }
            if(result > 0){
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("回调写入订单信息、打码量、资金流水异常:"+e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return "error";
    }
    
    
    
    
    /**
     * 根据订单号查询订单信息
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public RechargeOrderVO findNotifyOrderByOrderNo(String orderNo) throws Exception{
        
        return notifyDao.findNotifyOrderByOrderNo(orderNo);
    }

    
    /**
     * 通过支付商ID查询支付商新
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public CagentYespayVO getCagentYespayByPayId(Integer payId) throws Exception{
        
        return notifyDao.getCagentYespayByPayId(payId);
    }

    /**
     * 处理订单回调业务
     */
    @SuppressWarnings("unused")
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public synchronized String processNotifyOrder(RechargeOrderVO rechargeOrderVO) throws Exception{
        logger.info("回调订单号:{},回调订单流水号:{},处理回调订单业务开始=====================START===================",rechargeOrderVO.getOrderNo(),rechargeOrderVO.getTradeNo());
        try {
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            Integer cid = rechargeOrderVO.getCid();//平台ID
            Integer uid = rechargeOrderVO.getUid();//用户ID
            if(cid == null || cid == 0){
                //查询用户平台
                cid = notifyDao.getCangetIdByCagent(uid);
            }
            String type = getTypes(String.valueOf(rechargeOrderVO.getPayType()));
            logger.info("支付渠道："+type+" 支付类型："+rechargeOrderVO.getPayType());
            //查询彩金比例
            CjOrDmlRateVO cjOrDmlRateVO = notifyDao.getCjOrDmlRate(uid,cid,payId,type);
            if(cjOrDmlRateVO == null){
                logger.info("查询平台彩金倍率和打码量倍率为空!");
                return ResultResponse.ERROR_CODE;
            }
            logger.info("平台彩金倍率："+cjOrDmlRateVO.getDividendRate()+"打码量倍率:"+cjOrDmlRateVO.getCodingRate());
            //彩金    = 彩金倍率   * 订单金额
            BigDecimal cj = new BigDecimal(cjOrDmlRateVO.getDividendRate()).multiply(new BigDecimal(rechargeOrderVO.getOrderAmount()));//彩金倍率
            //计算用户的打码量:打码量 = (充值金额  + 彩金) * 打码量倍率
            BigDecimal dml = new BigDecimal(cjOrDmlRateVO.getCodingRate()).multiply(new BigDecimal(rechargeOrderVO.getOrderAmount()).add(cj));//打码量倍率
            String cjAmount = new DecimalFormat("0.00").format(cj);
            String dmlAmount = new DecimalFormat("0.00").format(dml);
            logger.info("用户获取的彩金[CJ="+cjAmount+"]");
            logger.info("用户获取的打码量:[dml="+dmlAmount+"]");
            logger.info("用户充值订单金额:[orderAmount="+rechargeOrderVO.getOrderAmount()+"]");
            
            //查询平台用户积分倍率
            double integralRatio = notifyDao.getCagentIntegralRatio(uid);
            logger.info("用户积分倍率："+integralRatio);
            //查询用户钱包积分
            Double integralBalance = userCapitalService.handleUserIntegralBalance(uid, 0.00,3);
            logger.info("用户当前钱包积分："+integralBalance);
            if(integralBalance == null){
                integralBalance = 0.0;
            }
            //计算充值积分
            BigDecimal integralAmount = new BigDecimal(integralRatio/100).multiply(new BigDecimal(rechargeOrderVO.getOrderAmount()));
            logger.info("用户充值积分："+integralAmount);
            
            rechargeOrderVO.setIntegralAmount(integralAmount.doubleValue());
            rechargeOrderVO.setIntegralBalance(integralBalance==null?0:integralBalance);//积分余额
            
            rechargeOrderVO.setCj(Double.parseDouble(cjAmount));
            rechargeOrderVO.setDml(Double.parseDouble(dmlAmount));
            //获取用户当前余额
            Double walletBalance = userCapitalService.handleUserWalletBalance(uid, 0.00, 3);
            logger.info("用户【"+uid+"】回调验签获取钱包余额:{}",walletBalance);
            rechargeOrderVO.setWalletBalance(walletBalance);
            rechargeOrderVO.setType(0);
            //查询平台剩余金额
            Double remainvalue = notifyDao.selectCagentQuota(cid);
            logger.info("用户当前剩余金额："+remainvalue);
            Double withholdAmount = cj.add(new BigDecimal(rechargeOrderVO.getOrderAmount())).doubleValue();//平台预扣金额
            if(remainvalue > withholdAmount){
                remainvalue = withholdAmount;//避免出现负数
            }else {
                remainvalue = 0.00;
            }
            logger.info("用户扣减金额："+remainvalue);
            rechargeOrderVO.setRemainvalue(remainvalue);
            //判断回调订单状态
            String tradeStatus = rechargeOrderVO.getTradeStatus();//第三方回调通知订单支付状态
            String successStatus = rechargeOrderVO.getSuccessStatus();//第三方回调通知支付成功时的状态
            logger.info("回调请求参数:[orderNo:"+rechargeOrderVO.getOrderNo()+",tradeNo:"+rechargeOrderVO.getTradeNo()+",tradeStatus:"+tradeStatus+"]");
            if(tradeStatus.equalsIgnoreCase(successStatus)){
                //如果回调通知的状态 和 已知的回调订单支付状态相同，则支付成功,
                rechargeOrderVO.setOrderStatus(tradeStatus);//订单支付状态
                rechargeOrderVO.setDescription("The top-up order is pay success.");
                rechargeOrderVO.setFinishTime(new Date());//订单修改时间
                rechargeOrderVO.setTradeStatus("success");
                int userTreasures = notifyDao.findUserTreasureByOrderNo(uid,rechargeOrderVO.getOrderNo(),rechargeOrderVO.getCagent());
                if(userTreasures > 0){
                    logger.info("用户资金流水已存在,资金流水订单号:{}",rechargeOrderVO.getOrderNo());
                    return ResultResponse.ERROR_CODE;
                }
                
                //批量修改订单业务
                notifyDao.batchUpdateNotifyOrderProcess(rechargeOrderVO);
                //写入用户资金流水、平台储值日志、回调日志
                if(rechargeOrderVO.getCj() > 0){
                    rechargeOrderVO.setType(1);
                }
                notifyDao.batchSaveNotifyProcess(rechargeOrderVO);
                logger.info("回调订单号:{},回调订单流水号:{},处理回调订单业务结束=====================END===================",rechargeOrderVO.getOrderNo(),rechargeOrderVO.getTradeNo());
                return ResultResponse.SUCCESS_CODE;
            }else{
                rechargeOrderVO.setOrderStatus(tradeStatus);//订单回调支付状态 与 约定回调支付成功状态不符,属于处理中
                rechargeOrderVO.setDescription("The top-up order is notify faild,because the third pay status is different plat success status,Please contact customer service.");
                rechargeOrderVO.setFinishTime(new Date());//订单修改时间
                rechargeOrderVO.setTradeStatus("process");//处理中
                notifyDao.updateNotifyOrderStatus(rechargeOrderVO);
                logger.info("回调订单号:{},回调订单流水号:{},处理回调订单业务结束=====================END===================",rechargeOrderVO.getOrderNo(),rechargeOrderVO.getTradeNo());
                return ResultResponse.ERROR_CODE;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("回调订单号:{},处理回调订单业务异常:{}",rechargeOrderVO.getOrderNo(),e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultResponse.ERROR_CODE;
    }

    /**
     * 修改回调订单的描述信息
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public void updateNotifyOrderDescription(RechargeOrderVO rechargeOrderVO)throws Exception{
        logger.info("回调订单号:{},回调订单流水号:{},处理回调订单业务开始=====================START===================",rechargeOrderVO.getOrderNo(),rechargeOrderVO.getTradeNo());
        try {
            Integer payId = rechargeOrderVO.getPayId();//支付商ID
            Integer cid = rechargeOrderVO.getCid();//平台ID
            Integer uid = rechargeOrderVO.getUid();//用户ID
            String type = getTypes(String.valueOf(rechargeOrderVO.getPayType()));
            //查询彩金比例
            CjOrDmlRateVO cjOrDmlRateVO = notifyDao.getCjOrDmlRate(uid,cid,payId,type);
            if(cjOrDmlRateVO != null){
                //彩金    = 彩金倍率   * 订单金额
                BigDecimal cj = new BigDecimal(cjOrDmlRateVO.getDividendRate()).multiply(new BigDecimal(rechargeOrderVO.getOrderAmount()));//彩金倍率
                //计算用户的打码量:打码量 = (充值金额  + 彩金) * 打码量倍率
                BigDecimal dml = new BigDecimal(cjOrDmlRateVO.getCodingRate()).multiply(new BigDecimal(rechargeOrderVO.getOrderAmount()).add(cj));//打码量倍率
                String cjAmount = new DecimalFormat("0.00").format(cj);
                String dmlAmount = new DecimalFormat("0.00").format(dml);
                logger.info("用户获取的彩金[CJ="+cjAmount+"]");
                logger.info("用户获取的打码量:[dml="+dmlAmount+"]");
                logger.info("用户充值订单金额:[orderAmount="+rechargeOrderVO.getOrderAmount()+"]");
                
                //查询平台用户积分倍率
                double integralRatio = notifyDao.getCagentIntegralRatio(uid);
              //查询用户钱包积分
                Double integralBalance = notifyDao.getCagentIntegralBalance(uid);
                if(integralBalance == null){
                    integralBalance = 0.0;
                }
                //计算充值积分
                BigDecimal integralAmount = new BigDecimal(integralRatio/100).multiply(new BigDecimal(rechargeOrderVO.getOrderAmount()));
                logger.info("用户订单充值积分:[orderAmount=:{}]",integralAmount);
                
                rechargeOrderVO.setIntegralAmount(integralAmount.doubleValue());
                rechargeOrderVO.setIntegralBalance(integralBalance);//积分余额
                
                rechargeOrderVO.setCj(Double.parseDouble(cjAmount));
                rechargeOrderVO.setDml(Double.parseDouble(dmlAmount));
                rechargeOrderVO.setOrderStatus(rechargeOrderVO.getTradeStatus());//订单回调支付状态 与 约定回调支付成功状态不符,属于处理中
                rechargeOrderVO.setTradeStatus("process");//订单处理中
                rechargeOrderVO.setFinishTime(new Date());//订单修改时间
                if (StringUtils.isBlank(rechargeOrderVO.getDescription()) || "Create top-up order is success".equals(rechargeOrderVO.getDescription())) {
                    rechargeOrderVO.setDescription("The top-up order is notify faild,because notify verify sign is faild,Please contact customer service.");
                }
                notifyDao.updateNotifyOrderStatus(rechargeOrderVO);
                logger.info("回调订单号:{},回调订单流水号:{},处理回调订单业务结束=====================END===================",rechargeOrderVO.getOrderNo(),rechargeOrderVO.getTradeNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("回调订单号:{},处理回调订单业务异常:{}",rechargeOrderVO.getOrderNo(),e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public CagentYespayVO getCagentYsepayByCagentAndPayment(String cagent, String payment) {
        return notifyDao.getCagentYsepayByCagentAndPayment(cagent,payment);
    }
    
    private String getTypes(String type) {
        //手机端
        if(Integer.valueOf(type)>20) {
            switch (type){
                case "21"://网银
                    type="5";
                    break;
                case "24"://支付宝
                    type="6";
                    break;
                case "23"://微信
                    type="7";
                    break;
                case "25"://财付通
                    type="8";
                    break;
                case "27"://银联扫码
                    type="10";
                    break;
                case "26"://京东扫码
                    type="12";
                    break;
                case "29"://快捷支付
                    type="14";
                    break;
                case "30"://微信条码
                    type="16";
                    break;
                case "31"://支付宝条码
                    type="18";
                    break;
                case "32"://云闪付
                    type="20";
                    break;
            }
        }else {
            //PC端
            switch (type){
                case "1"://网银
                    type="1";;
                    break;
                case "4"://支付宝
                    type="2";
                    break;
                case "3"://微信
                    type="3";
                    break;
                case "5"://财付通
                    type="4";
                    break;
                case "7"://银联扫码
                    type="9";
                    break;
                case "6"://京东扫码
                    type="11";
                    break;
                case "9"://快捷支付
                    type="13";
                    break;
                case "10"://微信条码
                    type="15";
                    break;
                case "11"://支付宝条码
                    type="17";
                    break;
                case "12"://云闪付
                    type="19";
                    break;
             }
        }
        return type;
    }
}
