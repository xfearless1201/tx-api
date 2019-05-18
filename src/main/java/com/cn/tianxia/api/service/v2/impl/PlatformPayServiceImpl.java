package com.cn.tianxia.api.service.v2.impl;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.common.PayConstant;
import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.common.v2.AmountUtil;
import com.cn.tianxia.api.common.v2.ScanCodeUtil;
import com.cn.tianxia.api.domain.txdata.v2.CagentYsepayDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.RechargeDao;
import com.cn.tianxia.api.enums.PayTypeEnum;
import com.cn.tianxia.api.pay.PayService;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.v2.CagentYsepayEntity;
import com.cn.tianxia.api.project.v2.RechargeEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.service.v2.PlatformPayService;
import com.cn.tianxia.api.vo.BankPayVO;
import com.cn.tianxia.api.vo.ScanPayVO;

import net.sf.json.JSONObject;

/**
 * @ClassName: PlatformPayServiceImpl
 * @Description: 支付服务实现类
 * @Author: Zed
 * @Date: 2019-01-02 14:02
 * @Version:1.0.0
 **/
@Service
public class PlatformPayServiceImpl implements PlatformPayService {
    private final Logger logger = LoggerFactory.getLogger(PlatformPayServiceImpl.class);

    @Autowired
    private NewUserDao userMapper;
    @Autowired
    private CagentYsepayDao cagentYsepayDao;
    @Autowired
    private RechargeDao rechargeDao;

    @Override
    public JSONObject bankPay(BankPayVO bankPayVO) {

        String uid = bankPayVO.getUid();
        //验证用户合法性
        UserEntity userEntity = userMapper.selectByPrimaryKey(Integer.valueOf(uid));
        if (null == userEntity) {
            logger.info("非法用户,查询用户信息失败,用户ID:{},用户不存在!", uid);
            return BaseResponse.error("1000", "非法用户,查询用户信息失败,用户ID:" + uid + ",用户不存在!");
        }
        //获取支付商配置
        CagentYsepayEntity cagentYsepayEntity = cagentYsepayDao.selectPaymentConfigByUidAndPayId(uid, bankPayVO.getPayId());
        if (null == cagentYsepayEntity) {
            logger.info("查询支付配置信息失败,支付商ID:{}", bankPayVO.getPayId());
            return BaseResponse.error("1000", "查询支付配置信息失败,支付商ID:" + bankPayVO.getPayId());
        }

        String cagent = userEntity.getCagent();     //平台商
        String paymentName = cagentYsepayEntity.getPaymentName();    //支付商

        JSONObject paymentConfig = JSONObject.fromObject(cagentYsepayEntity.getPaymentConfig());
        if (null == paymentConfig || paymentConfig.size() == 0) {
            return BaseResponse.error("1000","查询支付商配置信息失败：支付配置信息为空,支付商:{"+paymentName+"},平台商:{"+cagent+"}");
        }

        if (StringUtils.isBlank(cagentYsepayEntity.getPayUrl())) {
            logger.error("网银支付跳转URL不能为空!");
            return BaseResponse.error("1000", "网银支付跳转URL不能为空!");
        }


        double amount = bankPayVO.getAmount();

        if (amount <= 0 || amount < cagentYsepayEntity.getMinquota() || amount > cagentYsepayEntity.getMaxquota()) {
            logger.error("网银支付金额验证失败,请输入大于{},且小于{}之间的金额", cagentYsepayEntity.getMinquota(), cagentYsepayEntity.getMaxquota());
            return BaseResponse.error("1000","网银支付金额验证失败,请输入大于{"+cagentYsepayEntity.getMinquota()+"},且小于{"+ cagentYsepayEntity.getMaxquota()+"}之间的金额");
        }
        // 判断平台充值额度
        Double remainvalue = userMapper.selectAgentRechargeQuotaByUid(uid);
        if (remainvalue - amount < 0) {
            logger.info("平台商:{},剩余额度不足:{}",cagent,remainvalue);
            return BaseResponse.error("1000","平台商:{"+cagent+"},剩余额度不足:{"+remainvalue+"}");
        }


        //自定义修改金额
        Double finalAmount = AmountUtil.definedAmount(amount,cagent,paymentName, PayTypeEnum.wy.getType(),"");
        bankPayVO.setAmount(finalAmount);

        //生成订单号
        String orderNo = PayUtil.generatorPayOrderNo(paymentName, cagent);
        bankPayVO.setPay_url(cagentYsepayEntity.getPayUrl());
        bankPayVO.setUsername(userEntity.getUsername());
        bankPayVO.setOrderNo(orderNo);
        bankPayVO.setCid(String.valueOf(cagentYsepayEntity.getCid()));
        bankPayVO.setCagent(cagent);
        bankPayVO.setTopay(paymentName);
        bankPayVO.setPayType("1");

        PayEntity payEntity = sealBankPaymentBO(bankPayVO);

        return requestTopayImpl(paymentName,paymentConfig,payEntity,1);
    }

    @Override
    public JSONObject scanPay(ScanPayVO scanPayVO) {
        String uid = scanPayVO.getUid();

        //验证用户合法性
        UserEntity userEntity = userMapper.selectByPrimaryKey(Integer.valueOf(uid));
        if (null == userEntity) {
            logger.info("非法用户,查询用户信息失败,用户ID:{},用户不存在!", uid);
            return BaseResponse.error("1000", "非法用户,查询用户信息失败,用户ID:" + uid + ",用户不存在!");
        }
        //获取支付商配置
        CagentYsepayEntity cagentYsepayEntity = cagentYsepayDao.selectPaymentConfigByUidAndPayId(uid, scanPayVO.getPayId());
        if (null == cagentYsepayEntity) {
            logger.info("查询支付商信息失败,支付商ID:{}", scanPayVO.getPayId());
            return BaseResponse.error("1000", "查询支付配置信息失败,支付商ID:" + scanPayVO.getPayId());
        }

        String cagent = userEntity.getCagent(); //平台商
        String paymentName = cagentYsepayEntity.getPaymentName(); //支付商

        JSONObject paymentConfig = JSONObject.fromObject(cagentYsepayEntity.getPaymentConfig());
        if (null == paymentConfig || paymentConfig.size() == 0) {
            return BaseResponse.error("1000","查询支付商配置信息失败：支付配置信息为空,支付商:{"+paymentName+"},平台商:{"+cagent+"}");
        }


        //校验限额
        double amount = scanPayVO.getAmount();
        JSONObject validAmount = validAmount(amount,cagentYsepayEntity,scanPayVO.getScancode());
        if (null != validAmount) {   //validAmount不为null，说明校验支付商限额失败
            logger.info("校验支付商限额失败，金额：{}，响应信息：{}", amount, validAmount);
            return validAmount;
        }
        // 判断平台充值额度
        Double remainvalue = userMapper.selectAgentRechargeQuotaByUid(uid);
        if (remainvalue - amount < 0) {
            logger.info("平台商:{},剩余额度不足:{}",cagent,remainvalue);
            return BaseResponse.error("1000","平台商:{"+cagent+"},剩余额度不足:{"+remainvalue+"}");
        }


        String scanCode = scanPayVO.getScancode();  //扫码支付类型
        PayTypeEnum scanType;
        try {
            scanType = PayTypeEnum.valueOf(scanCode);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.error("支付类型匹配错误:{}",e.getMessage());
            return BaseResponse.error("1000","扫码类型匹配错误，请检查scancode参数！");
        }

        scanPayVO.setOriMobile(scanPayVO.getMobile());//设置原始mobile参数
        String mobile = getRequestChannel(scanType,cagentYsepayEntity,scanPayVO.getMobile());
        //从配置文件中读取payCode
        String payCode = getPayCodeFormProperty(paymentName, scanType, mobile,paymentConfig);
        if (StringUtils.isBlank(payCode)){
            return BaseResponse.error("1000","从配置properties文件中获取支付渠道异常");
        }
        //自定义修改金额
        Double finalAmount = AmountUtil.definedAmount(amount,cagent,paymentName,scanType.getType(),mobile);
        scanPayVO.setAmount(finalAmount);
        //生成订单号
        String orderNo = PayUtil.generatorPayOrderNo(paymentName, cagent);
        scanPayVO.setUserName(userEntity.getUsername());
        scanPayVO.setMobile(mobile);
        scanPayVO.setOrderNo(orderNo);
        scanPayVO.setPayCode(payCode);
        scanPayVO.setPayType(String.valueOf(scanType.getCode()));
        scanPayVO.setCid(String.valueOf(cagentYsepayEntity.getCid()));
        scanPayVO.setCagent(cagent);
        scanPayVO.setTopay(paymentName);
        PayEntity payEntity = sealScanPaymentBO(scanPayVO);
        return requestTopayImpl(paymentName,paymentConfig,payEntity,2);
    }

    /**
     * 实例化第三方支付实现类，调用支付方法
     * @param paymentName 支付商编号
     * @param paymentConfig  支付商配置信息
     * @param payEntity  请求支付实体类参数
     * @param type 1 网银 2 扫码
     * @return
     */
    private JSONObject requestTopayImpl(String paymentName,JSONObject paymentConfig,PayEntity payEntity,int type) {
        try {

            //创建充值入库实体
            RechargeEntity rechargeEntity = getRechargeEntity(payEntity);
            //支付商配置，用于实例化支付实现类对象

            /**根据支付类型获取配置,有的支付配置需要传入类型**/
            String serviceType = "";
            if (type == 1) {
                // 通财支付 秒卡通 国盛通
                if (paymentConfig.containsKey("isMore")) {
                    serviceType = "bank";
                }
            } else {
                String scanCode = payEntity.getPayType();
                // 通财支付 国盛通 秒卡通
                if (PayConstant.CONSTANT_TCP.equals(paymentName)) {
                    serviceType = "scan";
                } else if (paymentConfig.containsKey("isMore")) {
                    serviceType = PayTypeEnum.getPayCode(scanCode);
                }
            }

            PayService payService = getPayService(paymentName, paymentConfig,serviceType);

            //创建待支付订单
            logger.info("发起第三方订单支付请求开始=====================START==========================");
            JSONObject payResult;
            if (type == 1) {
                payResult = payService.wyPay(payEntity);
            } else {
                payResult = payService.smPay(payEntity);
            }

            if (payResult.containsKey("status") && payResult.getString("status").equalsIgnoreCase("success")) {
                //创建成功订单
                logger.info("发起第三方支付请求成功:{}", payResult.toString());
                rechargeEntity.setDescription("Create top-up order is success");
            } else {
                //支付失败订单
                logger.info("发起第三方支付请求失败:{}", payResult.toString());
                rechargeEntity.setDescription("Create top-up order is faild,Please contact customer service.");
            }

            rechargeDao.insertSelective(rechargeEntity);

            return payResult;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发起第三方支付请求失败:"+e.getMessage());
            return BaseResponse.error("1000","发起第三方支付请求失败:"+e.getMessage());
        }
    }

    /**
     * 扫码支付生成支付BO
     *
     * @param scanPayVO
     * @return
     */
    private PayEntity sealScanPaymentBO(ScanPayVO scanPayVO) {
        PayEntity bo = new PayEntity();
        bo.setUsername(scanPayVO.getUserName());
        bo.setuId(scanPayVO.getUid());
        bo.setIp(scanPayVO.getIp());
        bo.setRefererUrl(scanPayVO.getRefererUrl());
        bo.setAmount(scanPayVO.getAmount());
        bo.setTopay(scanPayVO.getTopay());
        bo.setOrderNo(scanPayVO.getOrderNo());
        bo.setPayUrl(scanPayVO.getPayUrl());
        bo.setMobile(scanPayVO.getMobile());
        bo.setOriMobile(scanPayVO.getOriMobile());
        bo.setCid(scanPayVO.getCid());
        bo.setCagent(scanPayVO.getCagent());
        bo.setPayId(scanPayVO.getPayId());
        bo.setPayCode(scanPayVO.getPayCode());
        bo.setPayType(scanPayVO.getPayType());

        return bo;
    }


    /**
     * 网银支付生成支付BO
     *
     * @param bankPayVO
     * @return
     */
    private PayEntity sealBankPaymentBO(BankPayVO bankPayVO) {
        PayEntity bo = new PayEntity();
        bo.setUsername(bankPayVO.getUsername());
        bo.setuId(bankPayVO.getUid());
        bo.setIp(bankPayVO.getIp());
        bo.setRefererUrl(bankPayVO.getReturn_url());
        bo.setAmount(bankPayVO.getAmount());
        bo.setTopay(bankPayVO.getTopay());
        bo.setOrderNo(bankPayVO.getOrderNo());
        bo.setPayUrl(bankPayVO.getPay_url());
        bo.setCid(bankPayVO.getCid());
        bo.setCagent(bankPayVO.getCagent());
        bo.setPayType(bankPayVO.getPayType());
        bo.setPayCode(bankPayVO.getBankcode());
        bo.setPayId(bankPayVO.getPayId());
        return bo;
    }


    /**
     * 构造充值单实体
     *
     * @param paymentBO
     * @return
     */
    private RechargeEntity getRechargeEntity(PayEntity paymentBO) {
        RechargeEntity entity = new RechargeEntity();
        entity.setUid(Integer.parseInt(paymentBO.getuId()));
        entity.setBankCode(paymentBO.getPayCode());
        entity.setCid(Integer.parseInt(paymentBO.getCid()));
        entity.setCagent(paymentBO.getCagent());
        entity.setOrderNo(paymentBO.getOrderNo());
        entity.setPayAmount(paymentBO.getAmount());
        entity.setOrderAmount(paymentBO.getAmount());
        entity.setOrderTime(new Date());
        entity.setMerchant(paymentBO.getTopay());
        entity.setTradeStatus("paying");
        entity.setTradeNo("");
        entity.setIp(paymentBO.getIp()); //ip
        entity.setIntegral(0.00);
        entity.setPayId(Integer.parseInt(paymentBO.getPayId()));
        byte payType = 0;
        if (StringUtils.isBlank(paymentBO.getOriMobile())) {
            switch (paymentBO.getPayType()){
                case "1":
                    payType = 1;
                    break;
                case PayConstant.CHANEL_WX:
                    payType = 3;
                    break;
                case PayConstant.CHANEL_ALI:
                    payType = 4;
                    break;
                case PayConstant.CHANEL_CFT:
                    payType = 5;
                    break;
                case PayConstant.CHANEL_YL:
                    payType = 7;
                    break;
                case PayConstant.CHANEL_JD:
                    payType = 6;
                    break;
                case PayConstant.CHANEL_KJ:
                    payType = 9;
                    break;
                case PayConstant.CHANEL_WXTM:
                    payType = 10;
                    break;
                case PayConstant.CHANEL_ALITM:
                    payType = 11;
                    break;
            }
        } else {
            switch (paymentBO.getPayType()){
                case "1":
                    payType = 21;
                    break;
                case PayConstant.CHANEL_WX:
                    payType = 23;
                    break;
                case PayConstant.CHANEL_ALI:
                    payType = 24;
                    break;
                case PayConstant.CHANEL_CFT:
                    payType = 25;
                    break;
                case PayConstant.CHANEL_YL:
                    payType = 27;
                    break;
                case PayConstant.CHANEL_JD:
                    payType = 26;
                    break;
                case PayConstant.CHANEL_KJ:
                    payType = 29;
                    break;
                case PayConstant.CHANEL_WXTM:
                    payType = 30;
                    break;
                case PayConstant.CHANEL_ALITM:
                    payType = 31;
                    break;
            }
        }
        entity.setPayType(payType);
        return entity;
    }

    private PayService getPayService(String provider, Map<String, String> pmapsconfig,String type) throws Exception {
        logger.info("获取支付反射接口开始====================START========================");
        try {
            // 组装游戏实现类路径
            StringBuffer sb = new StringBuffer();
            sb.append("com.cn.tianxia.api.pay.impl").append(".");// 包名
            sb.append(provider).append("PayServiceImpl");
            logger.info("反射接口包名:{}", sb.toString());
            // 创建构造器
            PayService payService;
            if (StringUtils.isBlank(type)) {
                Constructor<?> constructor = Class.forName(sb.toString()).getConstructor(Map.class);
                payService = (PayService) constructor.newInstance(pmapsconfig);
            } else {
                Constructor<?> constructor = Class.forName(sb.toString()).getConstructor(Map.class,String.class);
                payService = (PayService) constructor.newInstance(pmapsconfig,type);
            }
            return payService;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("获取支付反射接口异常:{}", e.getMessage());
            throw new Exception("获取支付反射接口异常");
        }
    }


    /**
     * 获取请求的设备是手机H5还是pc,
     * @param typeEnum
     * @param config
     * @param mobile
     * @return
     */
    private String getRequestChannel(PayTypeEnum typeEnum, CagentYsepayEntity config, String mobile) {
        if (StringUtils.isBlank(mobile)) {  //如果mobile参数为空，直接返回，表示前端设备是PC端
            return "";
        }
        int mbish5 = 0;
        switch (typeEnum) {    //手机端是否配置的扫码，如果是直接走pc扫码，否则走手机h5
            case wx:
                mbish5 = config.getIsh5Wx();
                break;
            case ali:
                    mbish5 = config.getIsh5Ali();
                break;
            case cft:
                    mbish5 = config.getIsh5Cft();
                break;
            case yl:
                    mbish5 = config.getIsh5Yl();
                break;
            case jd:
                    mbish5 = config.getIsh5Jd();
                break;
        }
        if (mbish5 == 0) {
            return "mobile";
        }
        return "";
    }

    /**
     * 通过支付商配置和配置文件,获取对应支付类型的支付商payCode
     *
     * @param provider     支付商编号
     * @param payType      支付类型枚举，详见PayTypeEnum
     * @param mobile       mobile 手机端H5  pc pc扫码
     * @return payCode 支付商对应支付类型Code
     */
    private String getPayCodeFormProperty(String provider, PayTypeEnum payType, String mobile,JSONObject paymentConfig) {
        String payCode=null;
        if (StringUtils.isNotBlank(mobile)) {
            // 手机端
            if(paymentConfig.containsKey("isRead") && "1".equals(paymentConfig.getString("isRead"))){
                //从配置文件中获取支付渠道
                payCode = paymentConfig.getString("mb");
                if(StringUtils.isNotBlank(payCode)){
                    payCode = payCode.split(",")[payType.getCode()-2];
                }
            }else{
                payCode = ScanCodeUtil.getMobileScanPayCode(provider,payType.getCode() - 2);
//            payCode = mobileScanCodeConfig.getPayCode(provider,  payType.getCode() - 2);   //获取配置下标从0开始,所以用payType - 2
            }
        } else {
            // pc扫码
            if(paymentConfig.containsKey("isRead") && "1".equals(paymentConfig.getString("isRead"))){
                //从配置文件中获取支付渠道
                payCode = paymentConfig.getString("pc");
                if(StringUtils.isNotBlank(payCode)){
                    payCode = payCode.split(",")[payType.getCode()-2];
                }
            }else{
                payCode = ScanCodeUtil.getPcScanPayCode(provider,payType.getCode() - 2);
//            payCode = pcScanCodeConfig.getPayCode(provider,  payType.getCode() - 2);
            }
        }
        return payCode;
    }

    private JSONObject validAmount(double amount, CagentYsepayEntity yseConfig, String scancode) {
        logger.info("验证请求金额开始===========================START================================");
        PayTypeEnum scanType = PayTypeEnum.valueOf(scancode);
        switch (scanType) {
            case wx:
                if (amount <= 0 || amount < yseConfig.getWxMinquota()
                        || amount > yseConfig.getWxMaxquota()) {
                    logger.info("微信支付金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getWxMinquota(), yseConfig.getWxMaxquota());
                    return BaseResponse.error("1000","微信支付金额验证失败,请输入大于"+yseConfig.getWxMinquota()+",且小于"+yseConfig.getWxMaxquota()+"之间的金额");
                }
                break;
            case ali:
                if (amount <= 0 || amount < yseConfig.getAliMinquota()
                        || amount > yseConfig.getAliMaxquota()) {
                    logger.info("支付宝支付金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getAliMinquota(), yseConfig.getAliMaxquota());
                    return BaseResponse.error("1000","支付宝支付金额验证失败,请输入大于"+yseConfig.getAliMinquota()+",且小于"+yseConfig.getAliMaxquota()+"之间的金额");
                }
                break;
            case cft:
                if (amount <= 0 || amount < yseConfig.getQrminquota()
                        || amount > yseConfig.getQrmaxquota()) {
                    logger.info("财付通支付金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getQrminquota(), yseConfig.getQrmaxquota());
                    return BaseResponse.error("1000","财付通支付金额验证失败,请输入大于"+yseConfig.getQrminquota()+",且小于"+yseConfig.getQrmaxquota()+"之间的金额");
                }
                break;
            case yl:
                if (amount <= 0 || amount < yseConfig.getYlMinquota()
                        || amount > yseConfig.getYlMaxquota()) {
                    logger.info("银联支付金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getYlMinquota(), yseConfig.getYlMaxquota());
                    return BaseResponse.error("1000","银联支付金额验证失败,请输入大于"+yseConfig.getYlMinquota()+",且小于"+yseConfig.getYlMaxquota()+"之间的金额");
                }
                break;
            case jd:
                if (amount <= 0 || amount < yseConfig.getJdMinquota()
                        || amount > yseConfig.getJdMaxquota()) {
                    logger.info("京东支付金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getJdMinquota(), yseConfig.getJdMaxquota());
                    return BaseResponse.error("1000","京东支付金额验证失败,请输入大于"+yseConfig.getJdMinquota()+",且小于"+yseConfig.getJdMaxquota()+"之间的金额");
                }
                break;
            case kj:
                if (amount <= 0 || amount < yseConfig.getKjMinquota()
                        || amount > yseConfig.getKjMaxquota()) {
                    logger.info("快捷支付金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getKjMinquota(), yseConfig.getJdMaxquota());
                    return BaseResponse.error("1000","快捷支付金额验证失败,请输入大于"+yseConfig.getKjMinquota()+",且小于"+yseConfig.getKjMaxquota()+"之间的金额");
                }
                break;
            case wxtm:
                if (amount <= 0 || amount < yseConfig.getWxtmMinquota()
                        || amount > yseConfig.getWxtmMaxquota()) {
                    logger.info("微信条码金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getWxtmMinquota(), yseConfig.getWxtmMinquota());
                    return BaseResponse.error("1000","微信条码金额验证失败,请输入大于"+yseConfig.getWxtmMinquota()+",且小于"+yseConfig.getWxtmMaxquota()+"之间的金额");
                }
                break;
            case alitm:
                if (amount <= 0 || amount < yseConfig.getAlitmMinquota()
                        || amount > yseConfig.getAlitmMaxquota()) {
                    logger.info("支付宝条码金额验证失败,请输入大于{},且小于{}之间的金额", yseConfig.getAlitmMinquota(), yseConfig.getAlitmMaxquota());
                    return BaseResponse.error("1000","支付宝条码金额验证失败,请输入大于"+yseConfig.getAlitmMinquota()+",且小于"+yseConfig.getAlitmMaxquota()+"之间的金额");
                }
                break;
        }
        return null;
    }
}

