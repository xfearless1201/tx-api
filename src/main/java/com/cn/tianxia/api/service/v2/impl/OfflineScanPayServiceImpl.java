package com.cn.tianxia.api.service.v2.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.domain.txdata.v2.AmountRecordDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentQrcodepayDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.UserTypeDao;
import com.cn.tianxia.api.po.OfflineScanReponse;
import com.cn.tianxia.api.project.v2.AmountRecordEntity;
import com.cn.tianxia.api.project.v2.CagentQrcodepayEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.project.v2.UserTypeEntity;
import com.cn.tianxia.api.service.v2.OfflineScanPayService;
import com.cn.tianxia.api.vo.OfflineScanQrCodeVO;

import net.sf.json.JSONObject;

/**
 * @ClassName OfflineScanPayServiceImpl
 * @Description 线下扫码接口实现类
 * @author Hardy
 * @Date 2019年1月11日 下午2:54:39
 * @version 1.0.0
 */
@Service
public class OfflineScanPayServiceImpl implements OfflineScanPayService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(OfflineScanPayServiceImpl.class);

    // 平台商二维码配置
    @Autowired
    private CagentQrcodepayDao cagentQrcodepayDao;
    @Autowired
    private AmountRecordDao amountRecordDao;
    @Autowired
    private UserTypeDao userTypeDao;
    @Autowired
    private NewUserDao userMapper;
    

    /**
     * 获取线下扫码二维码
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject getOfflineScanQrCode(String uid, String type) {
        logger.info("获取平台商线下二维码支付配置信息开始================start=================");
        try {
            // 二维码图片ID集合
            String qrcodeIds = null;
            // 根据用户id查询用户所在平台分层中的配置信息
            UserTypeEntity userTypeEntity = userTypeDao.getOfflineQrCodeByUser(uid);
            if (userTypeEntity == null) {
                return OfflineScanReponse.success("查询会员【" + uid + "】,平台分层扫码图片失败");
            }

            // 当前支持类型:1 支付宝 2 微信 3 财付通
            if ("1".equals(type)) {
                // 支付宝
                qrcodeIds = userTypeEntity.getAlipayId();
            } else if ("3".equals(type)) {
                qrcodeIds = userTypeEntity.getTenpayId();
            } else {
                qrcodeIds = userTypeEntity.getWechatId();
            }

            if (StringUtils.isBlank(qrcodeIds)) {
                String message = "1".equals(type) ? "支付宝" : "3".equals(type) ? "财付通" : "2".equals(type) ? "微信" : "";
                message = "平台商没配置" + message + "扫码二维码图片";
                return OfflineScanReponse.success(message);
            }
            // 查询配置的图片信息
            List<String> ids = Arrays.asList(qrcodeIds.split(","));
            if (CollectionUtils.isEmpty(ids)) {
                logger.info("平台会员:{},分层无配置扫码二维码图片信息", uid);
                return OfflineScanReponse.success("查询平台商配置扫码二维码图片失败,配置表ID为空");
            }
            // 批量查询
            List<CagentQrcodepayEntity> cagentQrcodepays = cagentQrcodepayDao.findAllByIds(ids);
            if (CollectionUtils.isEmpty(cagentQrcodepays)) {
                logger.info("平台会员:{},分层配置扫码图片ID:{},查询无结果", uid, ids.toString());
                return OfflineScanReponse.success("查询平台商配置扫码二维码图片失败");
            }
            // 随机获取一张二维码图片
            int randIdx = new Random().nextInt(cagentQrcodepays.size());
            return OfflineScanReponse.success("查询成功", cagentQrcodepays.get(randIdx));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("获取平台商线下二维码支付配置信息异常:{}", e.getMessage());
            return OfflineScanReponse.success("获取平台商线下二维码支付配置信息异常");
        }
    }

    /**
     * 生成线下扫码订单
     */
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    @Override
    public JSONObject addOfflineQrCodeOrderRecord(OfflineScanQrCodeVO offlineScanQrCodeVO) {
        logger.info("线下扫码二维码支付创建订单执行业务开始================START=================");
        try {
            
            UserEntity userEntity = userMapper.selectByPrimaryKey(Integer.parseInt(offlineScanQrCodeVO.getUid()));
            
            if(userEntity == null){
                return OfflineScanReponse.error("非法用户,查询用户:【"+offlineScanQrCodeVO.getUid()+"】,信息失败"); 
            }
            
            // 获取支付配置
            CagentQrcodepayEntity cagentQrcodepayEntity = cagentQrcodepayDao.selectByPrimaryKey(Integer.parseInt(offlineScanQrCodeVO.getId()));
            if (null == cagentQrcodepayEntity) {
                return OfflineScanReponse.error("数据库不存在该配置信息，请核实后再重新操作");
            }
            
            //判断订单金额
            String regex = "^([1-9]\\d{0,9}|0)([.]?|(\\.\\d{1,2})?)$";
            if(!offlineScanQrCodeVO.getAmount().matches(regex)){
                return OfflineScanReponse.error("请输入合法的订单金额,当前金额:"+offlineScanQrCodeVO.getAmount());
            }
            double amount = Double.parseDouble(offlineScanQrCodeVO.getAmount());
            if(amount < cagentQrcodepayEntity.getMinquota() || amount > cagentQrcodepayEntity.getMaxquota()){
                return OfflineScanReponse.error("请输入大于:【"+cagentQrcodepayEntity.getMinquota()+"】,且小于:【"+cagentQrcodepayEntity.getMaxquota()+"】区间的金额,当前金额:【"+amount+"】");
            }
            
            // 获取用户类型信息
            UserTypeEntity userTypeEntity = userTypeDao.getOfflineQrCodeByUser(offlineScanQrCodeVO.getUid());
            if(userTypeEntity == null){
                return OfflineScanReponse.error("查询会员分层信息失败,会员ID:【"+offlineScanQrCodeVO.getUid()+"】");
            }
            //判断支付类型
            if("1".equals(offlineScanQrCodeVO.getType())){
                //支付宝
                if(StringUtils.isBlank(userTypeEntity.getAlipayId()) 
                        || !userTypeEntity.getAlipayId().contains(offlineScanQrCodeVO.getId())){
                    return OfflineScanReponse.error("会员【"+offlineScanQrCodeVO.getUid()+"】,分层无支付宝支付渠道二维码配置信息,二维码配置ID:【"+offlineScanQrCodeVO.getId()+"】");
                }
            }else if("3".equals(offlineScanQrCodeVO.getType())){
                //财富通
                if(StringUtils.isBlank(userTypeEntity.getTenpayId()) 
                        || !userTypeEntity.getTenpayId().contains(offlineScanQrCodeVO.getId())){
                    return OfflineScanReponse.error("会员【"+offlineScanQrCodeVO.getUid()+"】,分层无财付通支付渠道二维码配置信息,二维码配置ID:【"+offlineScanQrCodeVO.getId()+"】");
                }
            }else {
                //微信
                if(StringUtils.isBlank(userTypeEntity.getWechatId()) 
                        || !userTypeEntity.getWechatId().contains(offlineScanQrCodeVO.getId())){
                    return OfflineScanReponse.error("会员【"+offlineScanQrCodeVO.getUid()+"】,分层无微信支付渠道二维码配置信息,二维码配置ID:【"+offlineScanQrCodeVO.getId()+"】");
                }
            }
            //查询用户的未审核订单
            int uneditOrder = amountRecordDao.sumUnauditRemittance(offlineScanQrCodeVO.getUid());
            if(uneditOrder > 4){
                return OfflineScanReponse.error("该用户【"+offlineScanQrCodeVO.getUid()+"】存在超过五笔未审核单据");
            }
            // 获取存款次数
            int times = amountRecordDao.sumUserRemittanceTimes(offlineScanQrCodeVO.getUid());
            //写入订单信息
            AmountRecordEntity amountRecordEntity = new AmountRecordEntity();
            amountRecordEntity.setCid(cagentQrcodepayEntity.getCid());// 平台id
            amountRecordEntity.setUid(Integer.parseInt(offlineScanQrCodeVO.getUid()));
            amountRecordEntity.setRefId("CK" + System.currentTimeMillis());// 关联Id
            amountRecordEntity.setUsername(userEntity.getUsername());// 用户名
            amountRecordEntity.setUsercode(offlineScanQrCodeVO.getOrderNum());// 订单Id
            amountRecordEntity.setAmount((float)amount);
            amountRecordEntity.setTimes(times+1);// 存款次数
            amountRecordEntity.setBankname("1".equals(offlineScanQrCodeVO.getType())?"支付宝":"3".equals(offlineScanQrCodeVO.getType())?"财付通":"微信");
            amountRecordEntity.setBankcode(cagentQrcodepayEntity.getAccountcode());// 银行卡号
            amountRecordEntity.setBankusername(cagentQrcodepayEntity.getAccountname());// 收款人姓名
            amountRecordEntity.setDiscount(0f);// 优惠金额
            amountRecordEntity.setHandsel(0f);// 彩金 = 彩金倍率 * 订单金额
            amountRecordEntity.setQuantity(0f);// 打码量= (充值金额 + 彩金) * 打码量倍率
            amountRecordEntity.setAddtime(new Date());// 添加时间
            amountRecordEntity.setTransfertime(new Date());// 交易时间
            amountRecordEntity.setStatus("0");// 交易状态
            amountRecordEntity.setVuid(0);// 审核人Id
            amountRecordEntity.setVtime(new Date());// 审核时间
            amountRecordEntity.setRmk("线下扫码支付");// 审核备注
            amountRecordEntity.setCagent(userEntity.getCagent());// 平台编码
            amountRecordEntity.setType(offlineScanQrCodeVO.getType());
            amountRecordDao.insertSelective(amountRecordEntity);
            return OfflineScanReponse.success("存款成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("线下扫码二维码支付创建订单异常:{}", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return OfflineScanReponse.error("存款失败");
        }
    }
}

