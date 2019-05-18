package com.cn.tianxia.api.service.v2.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import com.cn.tianxia.api.common.v2.ResultResponse;
import com.cn.tianxia.api.domain.txdata.v2.CagentYsepayDao;
import com.cn.tianxia.api.domain.txdata.v2.UserChannelDao;
import com.cn.tianxia.api.domain.txdata.v2.UserTypeDao;
import com.cn.tianxia.api.po.PaymentChannelPO;
import com.cn.tianxia.api.po.PaymentListPO;
import com.cn.tianxia.api.po.v2.PayChannelPO;
import com.cn.tianxia.api.po.v2.PayYsepayPO;
import com.cn.tianxia.api.project.v2.CagentYsepayEntity;
import com.cn.tianxia.api.project.v2.UserChannelEntity;
import com.cn.tianxia.api.service.v2.PlatPaymentService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @ClassName PlatPaymentServiceImpl
 * @Description 平台支付商接口实现
 * @author Hardy
 * @Date 2018年12月31日 下午3:46:44
 * @version 1.0.0
 */
@Service
public class PlatPaymentServiceImpl implements PlatPaymentService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(PlatPaymentServiceImpl.class);

    @Autowired
    private UserTypeDao userTypeDao;

    @Autowired
    private CagentYsepayDao cagentYsepayDao;
    
    @Autowired
    private UserChannelDao userChannelDao;

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject getPaymentChannel(Integer uid,Integer cid,Integer typeId) throws Exception{
        logger.info("调用获取用户【"+uid+"】,具备的支付渠道列表业务开始===================START================");
        //查询用户可用渠道列表
        String paymentChannel = userTypeDao.getPaychannelByTypeId(typeId);
        if(StringUtils.isBlank(paymentChannel)){
            logger.info("查询会员【"+uid+"】,平台【"+cid+"】分层【"+typeId+"】无可用支付渠道");
            return PaymentChannelPO.error("0", "查询会员【"+uid+"】,平台【"+cid+"】分层【"+typeId+"】无可用支付渠道");
        }
        
        //先切割字符串为数组,在把数组转换为集合
        List<Integer> channels = new ArrayList<>();
        String[] arr = paymentChannel.split(",");
        for (String channel : arr) {
            channels.add(Integer.parseInt(channel));
        }
        //判断集合是否存在
        if(CollectionUtils.isEmpty(channels)){
            logger.info("切割渠道列表字符串为数组,在把数组转换成集合为空");
            return PaymentChannelPO.error("0", "查询会员【"+uid+"】,平台【"+cid+"】分层【"+typeId+"】无可用支付渠道");
        }
        //分组
        List<Integer> PCchannel = channels.stream().filter(e -> e < 20).collect(Collectors.toList());
        List<Integer> MBchannel = channels.stream().filter(e -> e >= 20).collect(Collectors.toList());
        return PaymentChannelPO.success(JSONArray.fromObject(PCchannel),JSONArray.fromObject(MBchannel),"支付渠道数据获取成功！");
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject getPaymentList(Integer uid,Integer cid,Integer typeId,String type) throws Exception{
        logger.info("调用获取用户【"+uid+"】,具备的支付商列表业务开始===================START================");
        
        //通过用户分层ID拿到用户分层可用的支付商信息
        String onlinepayId = userTypeDao.getOnlinepayIdByTypeId(typeId);
        if(StringUtils.isBlank(onlinepayId)){
            logger.info("查询会员【"+uid+"】分层【"+typeId+"】可用支付商信息失败");
            return PaymentChannelPO.error("0", "查询会员【"+uid+"】分层【"+typeId+"】可用支付商信息失败");
        }
        List<String> payIds = Arrays.asList(onlinepayId.split(","));
        //通过会员支付商去查询会员的固额
        List<UserChannelEntity> channels = userChannelDao.findAllByType(payIds,cid, typeId, type);
        logger.info("支付渠道信息：{}", channels);
        if(CollectionUtils.isEmpty(channels)){
            logger.info("查询用户分层可用支付商列表为空");
            return PaymentChannelPO.error("0", "查询用户分层可用支付商列表为空");
        }
        
        //获取支付商ID集合
        List<Integer> payments = channels.stream().map(UserChannelEntity::getPaymentId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(payments)){
            logger.info("获取用户可用支付商ID列表为空");
            return PaymentChannelPO.error("0", "查询用户分层可用支付商列表失败");
        }
        
        //查询支付商信息
        List<CagentYsepayEntity> cagentYsepays = cagentYsepayDao.findAllByIds(payments);
        if(CollectionUtils.isEmpty(cagentYsepays)){
            return PaymentChannelPO.error("0", "查询用户分层可用支付商列表失败");
        }
        
        List<PayYsepayPO> payYsepayPOs = new ArrayList<>();
        cagentYsepays.stream().forEach(item -> {
            PayYsepayPO payYsepayPO = new PayYsepayPO();
            payYsepayPO.setId(String.valueOf(item.getId()));
            Double minquota = 0.00D;
            Double maxquota = 0.00D;
            switch (type) {                                     //限额设置
                case "1": case "5":
                    minquota = item.getMinquota();
                    maxquota = item.getMaxquota();
                    break;
                case "2": case "6":
                    minquota = item.getAliMinquota();
                    maxquota = item.getAliMaxquota();
                    break;
                case "3": case "7":
                    minquota = item.getWxMinquota();
                    maxquota = item.getWxMaxquota();
                    break;
                case "4": case "8":
                    minquota = item.getQrminquota();
                    maxquota = item.getQrmaxquota();
                    break;
                case "9": case "10":
                    minquota = item.getYlMinquota();
                    maxquota = item.getYlMaxquota();
                    break;
                case "11": case "12":   // 11pc京东扫码，12手机端京东扫码
                    minquota = item.getJdMinquota();
                    maxquota = item.getJdMaxquota();
                    break;
                case "13": case "14":   // 13pc端快捷，14手机端快捷
                    minquota = item.getKjMinquota();
                    maxquota = item.getKjMaxquota();
                    break;
                case "15": case "16":   // 15 PC微信条码 16 手机微信条码
                    minquota = item.getWxtmMinquota();
                    maxquota = item.getWxtmMaxquota();
                    break;
                case "17": case "18":   // 17 PC支付宝条码 18 手机支付宝条码
                    minquota = item.getAlitmMinquota();
                    maxquota = item.getAlitmMaxquota();
                    break;
            }
            payYsepayPO.setMinquota(minquota);
            payYsepayPO.setMaxquota(maxquota);
            payYsepayPO.setPaymentName(item.getPaymentName());
            
            payYsepayPOs.add(payYsepayPO);
        });
        
        for (PayYsepayPO payYsepayPO : payYsepayPOs) {
            for (UserChannelEntity channel : channels) {
                if(payYsepayPO.getId().equals(String.valueOf(channel.getPaymentId()))){
                    payYsepayPO.setIsSolid(channel.getSolidStatus());
                    List<String> solids = new ArrayList<>();
                    if(StringUtils.isNotBlank(channel.getSolidAmount()) && !"0".equals(channel.getSolidAmount())){
                        solids = Arrays.asList(channel.getSolidAmount().split(","));
                    }
                    payYsepayPO.setSolidAmouns(solids);
                }
            }
        }

        return PaymentListPO.success(JSONArray.fromObject(payYsepayPOs),"接口获取成功！");
    }

    /**
     * 根据用户ID获取用户具备的支付渠道列表
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public ResultResponse queryPaymentChannel(Integer uid,Integer cid,Integer typeId) throws Exception{
        logger.info("调用获取用户【"+uid+"】,具备的支付渠道列表业务开始===================START================");
        //查询用户可用渠道列表
        String paymentChannel = userTypeDao.getPaychannelByTypeId(typeId);
        if(StringUtils.isBlank(paymentChannel)){
            logger.info("查询会员【"+uid+"】,平台【"+cid+"】分层【"+typeId+"】无可用支付渠道");
            return ResultResponse.faild("查询会员【"+uid+"】,平台【"+cid+"】分层【"+typeId+"】无可用支付渠道");
        }
        
        //先切割字符串为数组,在把数组转换为集合
        List<String> channels = Arrays.asList(paymentChannel.split(","));
        //判断集合是否存在
        if(CollectionUtils.isEmpty(channels)){
            logger.info("切割渠道列表字符串为数组,在把数组转换成集合为空");
            return ResultResponse.faild("查询会员【"+uid+"】,平台【"+cid+"】分层【"+typeId+"】无可用支付渠道");
        }
        //分组
        List<String> PCchannel = channels.stream().filter(e -> new Integer(e) < 20).collect(Collectors.toList());
        List<String> MBchannel = channels.stream().filter(e -> new Integer(e) >= 20).collect(Collectors.toList());
        
        PayChannelPO payChannelPO = new PayChannelPO();
        payChannelPO.setPCchannel(PCchannel);
        payChannelPO.setMBchannel(MBchannel);
        
        return ResultResponse.success("查询成功", payChannelPO);
    }

    /**
     * 根据渠道类型获取用户具备的支付商信息列表
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public ResultResponse queryPaymentList(Integer uid,Integer cid,Integer typeId,String type) throws Exception{
        logger.info("调用获取用户【"+uid+"】,具备的支付商列表业务开始===================START================");
        
        //通过用户分层ID拿到用户分层可用的支付商信息
        String onlinepayId = userTypeDao.getOnlinepayIdByTypeId(typeId);
        if(StringUtils.isBlank(onlinepayId)){
            logger.info("查询会员【"+uid+"】分层【"+typeId+"】可用支付商信息失败");
            return ResultResponse.faild("查询会员【"+uid+"】分层【"+typeId+"】可用支付商信息失败");
        }
        List<String> payIds = Arrays.asList(onlinepayId.split(","));
        //通过会员支付商去查询会员的固额
        List<UserChannelEntity> channels = userChannelDao.findAllByType(payIds,cid, typeId, type);
        if(CollectionUtils.isEmpty(channels)){
            logger.info("查询用户分层可用支付商列表为空");
            return ResultResponse.faild("查询用户分层可用支付商列表为空");
        }
        
        //获取支付商ID集合
        List<Integer> payments = channels.stream().map(UserChannelEntity::getPaymentId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(payments)){
            logger.info("获取用户可用支付商ID列表为空");
            return ResultResponse.faild("查询用户分层可用支付商列表失败");
        }
        
        //查询支付商信息
        List<CagentYsepayEntity> cagentYsepays = cagentYsepayDao.findAllByIds(payments);
        if(CollectionUtils.isEmpty(cagentYsepays)){
            return ResultResponse.faild("查询用户分层可用支付商列表失败");
        }
        
        List<PayYsepayPO> payYsepayPOs = new ArrayList<>();
        cagentYsepays.stream().forEach(item -> {
            PayYsepayPO payYsepayPO = new PayYsepayPO();
            payYsepayPO.setId(String.valueOf(item.getId()));
            Double minquota = 0.00D;
            Double maxquota = 0.00D;
            switch (type) {                                     //限额设置
                case "1": case "5":
                    minquota = item.getMinquota();
                    maxquota = item.getMaxquota();
                    break;
                case "2": case "6":
                    minquota = item.getAliMinquota();
                    maxquota = item.getAliMaxquota();
                    break;
                case "3": case "7":
                    minquota = item.getWxMinquota();
                    maxquota = item.getWxMaxquota();
                    break;
                case "4": case "8":
                    minquota = item.getQrminquota();
                    maxquota = item.getQrmaxquota();
                    break;
                case "9": case "10":
                    minquota = item.getYlMinquota();
                    maxquota = item.getYlMaxquota();
                    break;
                case "11": case "12":   // 11pc京东扫码，12手机端京东扫码
                    minquota = item.getJdMinquota();
                    maxquota = item.getJdMaxquota();
                    break;
                case "13": case "14":   // 13pc端快捷，14手机端快捷
                    minquota = item.getKjMinquota();
                    maxquota = item.getKjMaxquota();
                    break;
                case "15": case "16":   // 15 PC微信条码 16 手机微信条码
                    minquota = item.getWxtmMinquota();
                    maxquota = item.getWxtmMaxquota();
                    break;
                case "17": case "18":   // 17 PC支付宝条码 18 手机支付宝条码
                    minquota = item.getAlitmMinquota();
                    maxquota = item.getAlitmMaxquota();
                    break;
            }
            payYsepayPO.setMinquota(minquota);
            payYsepayPO.setMaxquota(maxquota);
            payYsepayPO.setPaymentName(item.getPaymentName());
            
            payYsepayPOs.add(payYsepayPO);
        });
        
        for (PayYsepayPO payYsepayPO : payYsepayPOs) {
            for (UserChannelEntity channel : channels) {
                if(payYsepayPO.getId().equals(String.valueOf(channel.getPaymentId()))){
                    payYsepayPO.setIsSolid(channel.getSolidStatus());
                    List<String> solids = new ArrayList<>();
                    if(StringUtils.isNotBlank(channel.getSolidAmount()) && !"0".equals(channel.getSolidAmount())){
                        solids = Arrays.asList(channel.getSolidAmount().split(","));
                    }
                    payYsepayPO.setSolidAmouns(solids);
                }
            }
        }
        return ResultResponse.success("查询成功", payYsepayPOs);
    }
}
