package com.cn.tianxia.api.service.v2.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.domain.txdata.v2.AmountRecordDao;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.v2.DepositRecordEntity;
import com.cn.tianxia.api.service.v2.DepositRecordService;
import com.cn.tianxia.api.vo.v2.DepositRecordVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName DepositRecordServiceImpl
 * @Description 存款记录接口实现类
 * @author Hardy
 * @Date 2019年1月31日 下午9:36:59
 * @version 1.0.0
 */
@Service
public class DepositRecordServiceImpl implements DepositRecordService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(DepositRecordServiceImpl.class);

    @Autowired
    private AmountRecordDao amountRecordDao;

    /**
     * 查询用户存款记录
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONArray findAllByPage(DepositRecordVO depositRecordVO) {
        logger.info("调用查询用户存款记录业务开始========================START===================");
        try {
            Map<String,Object> queryParams = new HashMap<>();
            String uid = depositRecordVO.getUid();
            queryParams.put("uid",uid);

            String Type = depositRecordVO.getType();
            if (StringUtils.isNotBlank(Type)) {
                queryParams.put("type",Type);
            }

            String status = depositRecordVO.getStatus();
            if (StringUtils.isNotBlank(status)) {
                queryParams.put("status",status);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String bdate = depositRecordVO.getBdate();
            sdf.parse(bdate);  //格式化开始时间
            String edate = depositRecordVO.getEdate();
            sdf.parse(edate);  //格式化结束时间

            queryParams.put("bdate",bdate);
            queryParams.put("edate",edate);

            //条件查出所有符合条件条目，除分页外
            List<DepositRecordEntity> depositRecordList = amountRecordDao.selectDepositRecordLimit(queryParams);

            int count = depositRecordList.size();
            Double total = depositRecordList.stream().mapToDouble(DepositRecordEntity::getOrderAmount).sum();

            Map<String,Object> statisNode = new HashMap<>();

            statisNode.put("cnt",count);
            statisNode.put("total",total);

            //分页数据
            int pageNo = depositRecordVO.getPageNo();
            int pageSize = depositRecordVO.getPageSize();
            int startIndex = (pageNo - 1) * pageSize;

            List<DepositRecordEntity> currentPageList = new ArrayList<>();
            depositRecordList.stream().skip(startIndex).limit(pageSize).forEach(currentPageList::add);   //从所有条目中分页选出分页数据

            Double subTotal = currentPageList.stream().mapToDouble(DepositRecordEntity::getOrderAmount).sum();   //统计分页数据总金额

            statisNode.put("subTotal",subTotal);

            JSONArray result = new JSONArray();

            result.add(statisNode);

            for (DepositRecordEntity entity:currentPageList) {
                JSONObject entityData = new JSONObject();
                entityData.put("uid",entity.getUid());
                entityData.put("order_time",entity.getOrderTime());
                entityData.put("pay_type",entity.getPayType());
                entityData.put("order_amount",entity.getOrderAmount());
                entityData.put("trade_status",entity.getTradeStatus());
                entityData.put("type",entity.getType());
                entityData.put("rmk",entity.getRmk());

                result.add(entityData);

            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户存款记录业务异常:{}",e.getMessage());
            return JSONArray.fromObject(BaseResponse.error(BaseResponse.ERROR_CODE,"调用查询用户存款记录业务异常:" + e.getMessage()));
        }
    }

}
