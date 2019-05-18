package com.cn.tianxia.api.service.v2.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.common.v2.DatePatternConstant;
import com.cn.tianxia.api.common.v2.DatePatternUtils;
import com.cn.tianxia.api.domain.txdata.v2.CagentDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.UserCardDao;
import com.cn.tianxia.api.domain.txdata.v2.WithdrawDao;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.WithDrawResponse;
import com.cn.tianxia.api.project.v2.CagentEntity;
import com.cn.tianxia.api.project.v2.UserCardEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.project.v2.WithdrawEntity;
import com.cn.tianxia.api.service.v2.WithDrawService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.vo.CreateWithDrawOrderVO;
import com.cn.tianxia.api.vo.v2.WithdrawRecordVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName WithDrawServiceImpl
 * @Description 提现接口
 * @author Hardy
 * @Date 2019年1月29日 下午2:56:59
 * @version 1.0.0
 */
@Service
public class WithDrawServiceImpl implements WithDrawService {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(WithDrawServiceImpl.class);

    // 解密密码
    private final String deskey = "tianxia88";

    @Autowired
    private NewUserDao newUserDao;

    @Autowired
    private WithdrawDao withdrawDao;

    @Autowired
    private UserCardDao userCardDao;
    
    @Autowired
    private CagentDao cagentDao;

    /**
     * 创建提现订单
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public JSONObject createWithDrawOrder(CreateWithDrawOrderVO createWithDrawOrderVO) {
        logger.info("会员调用提现业务开始===================START=================");
        logger.info(" createWithDrawOrder(CreateWithDrawOrderVO createWithDrawOrderVO = {} -start",createWithDrawOrderVO);
        try {
            // 通过用户ID查询用户信息
            UserEntity user = newUserDao.selectByPrimaryKey(Integer.parseInt(createWithDrawOrderVO.getUid()));
            if (user == null) {
                return WithDrawResponse.faild("查询会员信息失败,会员【" + createWithDrawOrderVO.getUid() + "】非法用户");
        }
            //查询用户平台信息
            CagentEntity cagentEntity = cagentDao.selectByCagent(user.getCagent());
            if(cagentEntity == null){
                return WithDrawResponse.faild("查询会员信息失败,会员【" + createWithDrawOrderVO.getUid() + "】非法用户");
            }
            // 判断用户的提现密码
            String withDrawPassword = user.getQkPwd();
            if (StringUtils.isBlank(withDrawPassword)) {
                return WithDrawResponse.faild("会员【" + user.getUid() + "】,未设置过提款密码,请前去设置提现密码");
            }
            // 查询会员的提现记录
            Integer unaudit = withdrawDao.getUserUnwithDrawCounts(user.getUid());
            if (unaudit > 0) {
                return WithDrawResponse.faild("您存在未审核提款，待管理员审核通过继续提款操作");
            }
            // 解密密码
            DESEncrypt d = new DESEncrypt(deskey);
            String password = d.encrypt(createWithDrawOrderVO.getPassword());// 加密提现密码
            // 验证用户取款面
            if (!password.equals(withDrawPassword)) {
                return WithDrawResponse.faild("输入提现密码错误,请重新输入");
            }
            // 查询用户银行卡信息
            UserCardEntity userCard = userCardDao.selectUserCard(Integer.parseInt(createWithDrawOrderVO.getCardid()),
                    user.getUid());
            if (userCard == null) {
                return WithDrawResponse.faild("你还未绑定提现银行卡,请先绑定提现银行卡");
            }
            // 查询用户打码量
            Double marking_quantity = 0d;
            Double user_quantity = 0d;
            Map<String, String> mapQuantity = withdrawDao.selectUserQuantityByid(user.getUid());
            if (!CollectionUtils.isEmpty(mapQuantity)) {
                if (mapQuantity.containsKey("marking_quantity") && 
                        StringUtils.isNotBlank(String.valueOf(mapQuantity.get("marking_quantity")))) {
                    marking_quantity = Double.parseDouble(String.valueOf(mapQuantity.get("marking_quantity")));
                }
                if (mapQuantity.containsKey("user_quantity") && 
                        StringUtils.isNotBlank(String.valueOf(mapQuantity.get("user_quantity")))) {
                    user_quantity = Double.parseDouble(String.valueOf(mapQuantity.get("user_quantity")));
                }
            }
            
            if (user_quantity > marking_quantity) {
                user_quantity = marking_quantity;
            }
            // 查询用户的提现记录次数
            int totaltimes = 0;// 总提现次数
            int todaytimes = 0;// 当天提现次数
            Map<String, String> withdrawMap = withdrawDao.selectWithDrawTotaltimes(user.getUid());
            if (!CollectionUtils.isEmpty(withdrawMap)) {
                if (withdrawMap.containsKey("totalCounts") && 
                        StringUtils.isNotBlank(String.valueOf(withdrawMap.get("totalCounts")))) {
                    totaltimes = Integer.parseInt(String.valueOf(withdrawMap.get("totalCounts")));
                }
                if (withdrawMap.containsKey("todayCounts") && 
                        StringUtils.isNotBlank(String.valueOf(withdrawMap.get("todayCounts")))) {
                    todaytimes = Integer.parseInt(String.valueOf(withdrawMap.get("todayCounts")));
                }
            }

            // 查询用户当前余额
            Double balance = newUserDao.queryUserBalance(user.getUid());
            //订单金额
            Double amount = (double) createWithDrawOrderVO.getCredit();
            // 判断用户余额是否足够提现
            if (balance < createWithDrawOrderVO.getCredit()) {
                return WithDrawResponse.faild("提现订单创建失败,余额不足");
            }

            // 生成订单号 3位平台编码+16位日期
            String orderNo = user.getCagent() + System.currentTimeMillis();
            // 创建提现订单
            WithdrawEntity withdrawEntity = new WithdrawEntity();
            withdrawEntity.setUid(user.getUid());
            withdrawEntity.setCid(cagentEntity.getId());
            withdrawEntity.setCagent(user.getCagent());// 平台编码
            withdrawEntity.setCardAddress(StringUtils.isBlank(userCard.getCardAddress())?"":userCard.getCardAddress());
            withdrawEntity.setAmount((float) createWithDrawOrderVO.getCredit());// 订单金额
            withdrawEntity.setStatus("0");
            withdrawEntity.setUsername(userCard.getCardUsername());// 开户名
            withdrawEntity.setBankname(userCard.getBankName());
            withdrawEntity.setCardno(userCard.getCardNum());
            withdrawEntity.setPoundage(0f);// 手续费
            withdrawEntity.setAdministrativeFee(0f);// 行政费
            withdrawEntity.setAmountPaid(0f);
            withdrawEntity.setVuid(0);
            withdrawEntity.setMarkingQuantity(marking_quantity);
            withdrawEntity.setUserQuantity(user_quantity);
            withdrawEntity.setTodaytimes(todaytimes + 1);
            withdrawEntity.setTotaltimes(totaltimes + 1);
            withdrawEntity.setAddTime(new Date());
            withdrawEntity.setBillno(orderNo);
            withdrawEntity.setRmk("创建提现订单");
            withdrawEntity.setRemark("创建提现订单");
            withdrawEntity.setVtime(new Date());
            logger.info("用户【"+user.getAgUsername()+"】 提现订单:{}",withdrawEntity);
            // 创建提现订单
            withdrawDao.insertSelective(withdrawEntity);
            //扣减用户钱包金额
            newUserDao.subtractUserBalance(user.getUid(),amount);
            logger.info("用户【"+user.getAgUsername()+"】 提现操作成功！");
            return WithDrawResponse.success("提现成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户创建提现订单异常:{}", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return WithDrawResponse.faild("会员提现异常");
        }
    }

    
    
    
    /**
     * 查询用户提现记录
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONArray findAllByPage(WithdrawRecordVO withdrawRecordVO) {
        logger.info("调用分页查询用户提现记录业务开始==================START==================");
        JSONArray data = new JSONArray();
        try {
            //格式化时间 
            String startTime = withdrawRecordVO.getBdate()+" 00:00:00";
            String endTime = withdrawRecordVO.getEdate()+" 23:59:59";
            //格式化时间
            if(StringUtils.isBlank(startTime)){
                startTime = DatePatternUtils.dateToStr(
                        DatePatternUtils.addOrMinusDay(new Date(), -29),DatePatternConstant.NORM_DATE_PATTERN)+" 00:00:00";
            }
            
            if(StringUtils.isBlank(endTime)){
                endTime = DatePatternUtils.dateToStr(new Date(),DatePatternConstant.NORM_DATE_PATTERN) + " 23:59:59";
            }
            
            Date bdate = DatePatternUtils.strToDate(startTime, DatePatternConstant.NORM_DATETIME_PATTERN);
            Date edate = DatePatternUtils.strToDate(endTime, DatePatternConstant.NORM_DATETIME_PATTERN);
            
            //查询参数
            String uid = withdrawRecordVO.getUid();
            String status = withdrawRecordVO.getStatus();
            Integer pageNo = withdrawRecordVO.getPageNo();
            Integer pageSize = withdrawRecordVO.getPageSize();
            DecimalFormat df = new DecimalFormat("0.00");
            //查询用户提现订单记录
            List<WithdrawEntity> withdraws = withdrawDao.findAllByPage(uid, status, bdate, edate, pageNo, pageSize);
            //查询总页数
            Map<String,String> totalCounts = withdrawDao.selectWithDrawCount(uid, status, bdate, edate);
            JSONObject totalJson = new JSONObject();
            JSONArray array = new JSONArray();
            if(!CollectionUtils.isEmpty(withdraws)){
                //统计当前总金额
                Double totalAmount = withdraws.stream().mapToDouble(WithdrawEntity::getAmount).sum();
                //必须按照此顺序排列，否则前段不显示
                totalJson.put("total",totalCounts.get("total"));
                totalJson.put("cnt",totalCounts.get("cnt"));
                totalJson.put("subTotal",totalAmount);
                array.add(totalJson);
                
                for (WithdrawEntity item : withdraws) {
                    JSONObject jsonObject = new JSONObject();
                    String cardno = item.getCardno();
                    String fisrtStr = cardno.substring(0,4);
                    String endStr = cardno.substring(cardno.length()-4,cardno.length());
                    cardno = fisrtStr + "********" + endStr;
                    jsonObject.put("add_time",item.getAddTime());
                    jsonObject.put("card_num",cardno);
                    jsonObject.put("amount",item.getAmount());
                    jsonObject.put("remark", item.getRemark());
                    jsonObject.put("poundage", df.format(item.getPoundage()));
                    jsonObject.put("administrative_fee", df.format(item.getAdministrativeFee()));
                    jsonObject.put("amount_paid", df.format(item.getAmountPaid()));
                    jsonObject.put("rmk",item.getRmk());
                    if("0".equals(item.getStatus())){
                        jsonObject.put("status","处理中");
                    }else if("1".equals(item.getStatus())){
                        jsonObject.put("status","已通过");
                    }else{
                        jsonObject.put("status","未通过");
                    }
                    array.add(jsonObject);
                }
            }else{
                totalJson.put("total",0);//默认值
                totalJson.put("cnt",0);
                totalJson.put("subTotal",0);
                array.add(totalJson);
            }
            data.addAll(array);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用分页查询用户提现记录业务异常:{}",e.getMessage());
        }
        return data;
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject selectWithdrawConfig(String uid) {
        logger.info("查询用户打码量总游戏金额强制提款手续费业务-----------------开始---------------");
        try {
            JSONObject result = new JSONObject();
            Map<String, Object> resultMap = withdrawDao.selectWithdrawConfig(uid);
            if (MapUtils.isEmpty(resultMap)) {
                result.put("marking_quantity", 0);
                result.put("user_quantity", 0);
                result.put("winAmount", 0);
                result.put("user_winAmount", 0);
                result.put("withdrawConfig", 0);
                result.put("withdraw_fee", 0);// 取款手续费
                result.put("withdraw_manage_fee", 0);// 取款行政费
            } else {
                result = JSONObject.fromObject(resultMap);
                // //超过当日免费取款次数则收取取款手续费
                // // 当日取款次数 todaytimes 免费取款次数free_withdraw_time
                if (result.getInt("todaytimes") >= result.getInt("free_withdraw_time")) {
                    result.put("withdraw_fee", result.getDouble("withdraw_fee"));
                    result.put("withdraw_manage_fee", result.getDouble("withdraw_manage_fee"));
                } else {
                    result.put("withdraw_fee", 0);
                    result.put("withdraw_manage_fee", 0);
                }

                result.remove("todaytimes");
                result.remove("free_withdraw_time");
            }
            result.put("status", "success");
            return result;
        } catch (Exception e) {
            logger.error("查询用户打码量总游戏金额强制提款手续费业务异常：{}" + e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE , "查询用户打码量总游戏金额强制提款手续费业务异常:" + e.getMessage());
        }
    }

}
