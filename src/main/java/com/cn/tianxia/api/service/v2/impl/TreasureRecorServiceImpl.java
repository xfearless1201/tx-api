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
import com.cn.tianxia.api.domain.txdata.v2.UserTreasureDao;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.v2.UserTreasureEntity;
import com.cn.tianxia.api.service.v2.TreasureRecordService;
import com.cn.tianxia.api.vo.v2.TreasureRecordVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/1 10:58
 * @Description: 资金流水记录实现类
 */
@Service
public class TreasureRecorServiceImpl implements TreasureRecordService {

    private static final Logger logger = LoggerFactory.getLogger(DepositRecordServiceImpl.class);

    @Autowired
    private UserTreasureDao userTreasureDao;

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject findAllByPage(TreasureRecordVO treasureRecordVO) {
        logger.info("调用查询用户资金记录业务开始========================START===================");
        try {
            String uid = treasureRecordVO.getUid();
            String type = treasureRecordVO.getType();
            Integer pageNo = treasureRecordVO.getPageNo();
            Integer pageSize = treasureRecordVO.getPageSize();
            //格式化时间 
            String startTime = treasureRecordVO.getStartTime();
            String endTime = treasureRecordVO.getEndTime();
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
            
            //用户当前页小计
            Double subTotal = 0.00D;
            //分页查询用户资金流水
            JSONArray array = new JSONArray();
            List<UserTreasureEntity> userTreasures = userTreasureDao.findAllByPage(uid, type, bdate, edate, pageNo, pageSize);
            if(!CollectionUtils.isEmpty(userTreasures)){
                subTotal = userTreasures.stream().mapToDouble(UserTreasureEntity::getAmount).sum();
                
                userTreasures.stream().forEach(item ->{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("amount",item.getAmount());
                    jsonObject.put("new_money",item.getNewMoney());
                    jsonObject.put("t_type",item.gettType());
                    jsonObject.put("addtime",item.getAddtime());
                    jsonObject.put("rmk",item.getRmk());
                    array.add(jsonObject);
                });
            }
            //查询用户资金流水总条数
            long tatalPages = 0;//流水总条数
            Double totalAmount = 0.00D;//用户总金额
            Map<String,String> cntMap = userTreasureDao.countTotalPages(uid,type,bdate, edate);
            if(!CollectionUtils.isEmpty(cntMap)){
                if(cntMap.containsKey("cnt")){
                    tatalPages = Long.parseLong(String.valueOf(cntMap.get("cnt")));
                }
                
                if(cntMap.containsKey("total")){
                    totalAmount = Double.parseDouble(String.valueOf(cntMap.get("total")));
                }
            }
            
            JSONObject data = new JSONObject();
            data.put("cnt", tatalPages);
            data.put("total", totalAmount);
            data.put("subTotal", subTotal);
            data.put("data", array);
            data.put("status", "success");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户存款记录业务异常:{}",e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"调用查询用户存款记录业务异常:" + e.getMessage());
        }
    }
}
