package com.cn.tianxia.api.service.v2.impl;

import java.util.Date;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.domain.txdata.v2.AmountRecordDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentBankcardDao;
import com.cn.tianxia.api.po.BankRemittanceResponse;
import com.cn.tianxia.api.po.v2.JSONArrayResponse;
import com.cn.tianxia.api.project.v2.AmountRecordEntity;
import com.cn.tianxia.api.project.v2.CagentBankcardEntity;
import com.cn.tianxia.api.service.v2.BankRemittanceService;
import com.cn.tianxia.api.vo.BankRemittanceVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName BankRemittanceServiceImpl
 * @Description 银行汇款接口实现类
 * @author Hardy
 * @Date 2019年1月5日 上午10:49:24
 * @version 1.0.0
 */
@Service
public class BankRemittanceServiceImpl implements BankRemittanceService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(BankRemittanceServiceImpl.class);
    
    @Autowired
    private AmountRecordDao amountRecordDao;
    
    @Autowired
    private CagentBankcardDao cagentBankcardDao;
    
    /**
     * 创建汇款订单
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    public JSONObject createRemittanceOrder(BankRemittanceVO bankRemittanceVO) throws Exception {
        logger.info("创建银行汇款订单开始=================START==============");
        logger.info("createRemittanceOrder(BankRemittanceVO bankRemittanceVO = {}",bankRemittanceVO.toString());
        try {
            
            //1.判断存款金额是否满足银行卡限额
            CagentBankcardEntity cagentBankcardEntity = 
                    cagentBankcardDao.selectUserBankRemittanceInfo(bankRemittanceVO.getUid());
            if(cagentBankcardEntity == null){
                //查询银行信息失败
                return BankRemittanceResponse.error("查询平台银行卡信息失败");
            }
            
            //订单金额
            double amount = bankRemittanceVO.getAmount();
            //最小额度
            double minquota = cagentBankcardEntity.getMinquota().doubleValue();
            //最大额度
            double maxquota = cagentBankcardEntity.getMaxquota().doubleValue();
            
            if(amount < minquota || amount > maxquota){
                return BankRemittanceResponse.error("请输入大于【"+minquota+"】,且小于【"+maxquota+"】的汇款金额,当前金额:【"+amount+"】");
            }
            //2.判断存款次数
            int unAuditRemittances = amountRecordDao.sumUnauditRemittance(bankRemittanceVO.getUid());
            if(unAuditRemittances > 4){
                return BankRemittanceResponse.error("该用户存在超过五笔未审核单据");
            }
            //写入汇款订单 
            bankRemittanceVO.setCid(cagentBankcardEntity.getCid());
            bankRemittanceVO.setCagent(cagentBankcardEntity.getCagent());
            bankRemittanceVO.setTimes(unAuditRemittances);
            AmountRecordEntity record = sealAmountRecordEntity(bankRemittanceVO);
            amountRecordDao.insertSelective(record);
            return BankRemittanceResponse.success("汇款订单提交成功",record);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("创建银行汇款订单异常:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return BankRemittanceResponse.error("汇款订单提交异常");
        }
    }
    
    /**
     * 获取汇款银行信息
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONArray getRemittanceBankInfo(String uid) throws Exception {
        logger.info("获取汇款银行卡信息开始==============START==============");
        try {
            JSONArray data = new JSONArray();
            //通过会员ID查询银行卡信息
            CagentBankcardEntity cagentBankcardEntity = cagentBankcardDao.selectUserBankRemittanceInfo(uid);
            if(cagentBankcardEntity == null){
                return JSONArrayResponse.faild("查询用户平台绑卡信息失败,请用户先去绑卡");
            }
            JSONObject jsonObject = JSONObject.fromObject(cagentBankcardEntity);
            jsonObject.put("status", "success");
            jsonObject.put("msg", "获取汇款银行卡信息成功");
            data.add(jsonObject);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("获取汇款银行卡信息异常:{}",e.getMessage());
            return JSONArrayResponse.faild("获取汇款银行卡信息异常");
            
        }
    }
    
    
    /**
     * 
     * @Description 组装汇款订单
     * @return
     */
    @DataSource(Database.TXDATA_DB_SLAVE)
    private AmountRecordEntity sealAmountRecordEntity(BankRemittanceVO bankRemittanceVO){
        logger.info("sealAmountRecordEntity(BankRemittanceVO bankRemittanceVO = {}",bankRemittanceVO.toString());
        AmountRecordEntity amountRecordEntity = new AmountRecordEntity();
        amountRecordEntity.setUid(Integer.parseInt(bankRemittanceVO.getUid()));//用户ID
        amountRecordEntity.setCid(bankRemittanceVO.getCid());//平台ID
        amountRecordEntity.setCagent(bankRemittanceVO.getCagent());//平台编码
        amountRecordEntity.setRefId("CK"+System.currentTimeMillis());//订单号
        amountRecordEntity.setUsername(bankRemittanceVO.getName());//用户名
        amountRecordEntity.setUsercode(bankRemittanceVO.getAccount());//卡号
        amountRecordEntity.setAmount(bankRemittanceVO.getAmount().floatValue());
        amountRecordEntity.setType(bankRemittanceVO.getType());//转账类型
        //查询一共转账次数
        int times = amountRecordDao.sumUserRemittanceTimes(bankRemittanceVO.getUid());
        amountRecordEntity.setTimes(times+1);
        amountRecordEntity.setDiscount(0f);
        amountRecordEntity.setHandsel(0f);
        amountRecordEntity.setQuantity(0f);
        amountRecordEntity.setAddtime(new Date());
        amountRecordEntity.setTransfertime(bankRemittanceVO.getRemittanceDate());
        amountRecordEntity.setStatus("0");
        amountRecordEntity.setVuid(0);
        amountRecordEntity.setVtime(new Date());
        amountRecordEntity.setRmk("0".equals(bankRemittanceVO.getCaijin())?"申请彩金":"不申请彩金");
        amountRecordEntity.setBankcode("");
        amountRecordEntity.setBankname("");
        amountRecordEntity.setBankusername("");
        logger.info("sealAmountRecordEntity(AmountRecordEntity amountRecordEntity = {}",amountRecordEntity.toString());
        return amountRecordEntity;
    }

}
