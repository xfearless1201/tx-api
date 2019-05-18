package com.cn.tianxia.api.service.v2.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.common.v2.DatePatternConstant;
import com.cn.tianxia.api.common.v2.DatePatternUtils;
import com.cn.tianxia.api.domain.txdata.TransferDao;
import com.cn.tianxia.api.project.v2.TransferEntity;
import com.cn.tianxia.api.service.v2.TransferRecordService;
import com.cn.tianxia.api.vo.v2.TransferRecordVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName TransferRecordServiceImpl
 * @Description 转账记录接口实现类
 * @author Hardy
 * @Date 2019年2月1日 上午10:28:16
 * @version 1.0.0
 */
@Service
public class TransferRecordServiceImpl implements TransferRecordService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(TransferRecordServiceImpl.class);

    @Autowired
    private TransferDao transferDao;
    
    /**
     * 转账接口实现类
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONArray getTransferInfo(TransferRecordVO transferRecordVO) {
        logger.info("调用分页查询用户转账记录业务开始====================START===================");
        JSONArray data = new JSONArray();
        try {
            //格式化时间参数
            String startTime = transferRecordVO.getBdate()+" 00:00:00";
            String endTime = transferRecordVO.getEdate()+" 23:59:59";
            //格式化时间
            if(StringUtils.isBlank(startTime)){
                startTime = DatePatternUtils.dateToStr(
                        DatePatternUtils.addOrMinusDay(new Date(), -29),DatePatternConstant.NORM_DATE_PATTERN)+" 00:00:00";
            }
            if(StringUtils.isBlank(endTime)){
                endTime = DatePatternUtils.dateToStr(new Date(),DatePatternConstant.NORM_DATE_PATTERN)+" 23:59:59";
            }
            Date bdate = DatePatternUtils.strToDate(startTime, DatePatternConstant.NORM_DATETIME_PATTERN);
            Date edate = DatePatternUtils.strToDate(endTime, DatePatternConstant.NORM_DATETIME_PATTERN);
            //获取查询参数
            Integer uid = Integer.parseInt(transferRecordVO.getUid());
            String tType = transferRecordVO.getTtype();//转账类型,OUT/IN
            if(StringUtils.isBlank(tType)){
                tType = null;
            }
            String type = transferRecordVO.getType();//游戏类型编码
            if(StringUtils.isBlank(type)){
                type = null;
            }
            Integer pageNo = transferRecordVO.getPageNo();//分页页码
            Integer pageSize = transferRecordVO.getPageSize();//分页条数
            
            //查询用户转账记录列表
            List<TransferEntity> transfers = 
                    transferDao.findAllByPage(uid, tType, type, bdate, edate, pageNo, pageSize);
            //查询用户总条数
            Map<String,String> transferTotals = transferDao.sumTransferTotalCounts(uid, tType, type, bdate, edate);
            JSONObject totalJson = new JSONObject();
            JSONArray array = new JSONArray();
            
            //当前转入总金额
            Double inTotalAmount = 0D;
            //当前转出总金额
            Double outTotalAmount = 0D;
            Double totalAmount = 0D;
            if(!CollectionUtils.isEmpty(transfers)){
                //必须按照此顺序排列，否则前段不显示
                totalJson.put("total",transferTotals.get("total"));
                totalJson.put("cnt",transferTotals.get("cnt"));
                for (TransferEntity transfer : transfers) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("t_time",transfer.gettTime());
                    if("IN".equalsIgnoreCase(transfer.gettType())){
                        jsonObject.put("t_type","转入平台");
                        inTotalAmount += transfer.gettMoney();
                    }else{
                        jsonObject.put("t_type","转入游戏");
                        outTotalAmount -= transfer.gettMoney();
                    }
                    jsonObject.put("type",transfer.getType().toUpperCase());
                    jsonObject.put("old_money",transfer.getOldMoney());
                    jsonObject.put("t_money",transfer.gettMoney());
                    jsonObject.put("new_money",transfer.getNewMoney());
                    
                    array.add(jsonObject);
                }
                totalAmount = inTotalAmount + outTotalAmount;
                totalJson.put("subTotal", totalAmount);
            }

            data.add(totalJson);
            data.addAll(array);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用分页查询用户转账记录业务异常:{}",e.getMessage());
        }
        return data;
    }

}
