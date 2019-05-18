package com.cn.tianxia.api.service.v2.impl;

import java.util.Arrays;
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
import com.cn.tianxia.api.domain.txdata.v2.InternalMessageDao;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.v2.JSONArrayResponse;
import com.cn.tianxia.api.project.v2.InternalMessageEntity;
import com.cn.tianxia.api.service.v2.InstationService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName InstationServiceImpl
 * @Description 站内信接口实现类
 * @author Hardy
 * @Date 2019年2月1日 下午8:41:30
 * @version 1.0.0
 */
@Service
public class InstationServiceImpl implements InstationService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(InstationServiceImpl.class);
    
    @Autowired
    private InternalMessageDao internalMessageDao;
    
    /**
     * 获取站内信数量
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject getMessageNum(String uid, String bdate, String edate) {
        logger.info("调用查询用户站内信总条数业务开始==================START================");
        JSONObject data = new JSONObject();
        try {
            //格式化时间
            if(StringUtils.isBlank(bdate)){
                bdate = DatePatternUtils.dateToStr(DatePatternUtils.addOrMinusDay(new Date(), -29),DatePatternConstant.NORM_DATE_PATTERN);
            }
            
            if(StringUtils.isBlank(edate)){
                edate = DatePatternUtils.dateToStr(new Date(),DatePatternConstant.NORM_DATE_PATTERN);
            }
            
            Map<String,String> map = internalMessageDao.sumInternalMessagesByUid(uid,bdate,edate);
            if(!CollectionUtils.isEmpty(map)){
                data.put("isread",map.get("isread"));
                data.put("noread",map.get("noread"));
                data.put("code", "1");
                data.put("status", "success");
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户站内信总条数业务异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用查询用户站内信总条数异常");
        }
        
    }
    
    
    /**
     * 获取站内信列表
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONArray getMessageList(String uid,String status, String bdate, String edate) {
        logger.info("调用查询用户站内信列表业务开始===================START==================");
        JSONArray data = new JSONArray();
        try {
            //格式化时间
            if(StringUtils.isBlank(bdate)){
                bdate = DatePatternUtils.dateToStr(DatePatternUtils.addOrMinusDay(new Date(), -29),DatePatternConstant.NORM_DATE_PATTERN);
            }
            
            if(StringUtils.isBlank(edate)){
                edate = DatePatternUtils.dateToStr(new Date(),DatePatternConstant.NORM_DATE_PATTERN);
            }
            
            if(StringUtils.isBlank(status)){
                status = null;
            }
            
            //查询用户站内信列表
            List<InternalMessageEntity> messages = internalMessageDao.findAllByUid(uid, status, bdate,edate);
            if(!CollectionUtils.isEmpty(messages)){
                for (InternalMessageEntity message : messages) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", message.getId());
                    jsonObject.put("message", message.getMessage());
                    jsonObject.put("status", message.getStatus());
                    jsonObject.put("addtime", message.getAddtime());
                    
                    data.add(jsonObject);
                }
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户站内信列表业务异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询用户站内信列表业务异常");
        }
    }

    /**
     * 获取站内信息详情
     */
    @Override
    public JSONObject getMessageInfo(String id) {
        logger.info("调用查询用户站内信详情业务开始=============START=================");
        try {
            
            InternalMessageEntity message = internalMessageDao.selectByPrimaryKey(Integer.parseInt(id));
            if(message == null){
                return BaseResponse.error("0", "查询用户站内信详情失败,错误ID");
            }
            
            JSONObject data = new JSONObject();
            data.put("id", message.getId());
            data.put("message", message.getMessage());
            data.put("status", message.getStatus());
            
            //修改该条信息为已读
            message.setStatus("1");
            internalMessageDao.updateByPrimaryKeySelective(message);
            
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户站内信详情业务异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用查询用户站内信详情业务异常");
        }
    }

    /**
     * 删除站内信
     */
    @Override
    public JSONObject deleteMessage(String id) {
        logger.info("调用删除用户站内信业务开始=================START================");
        try {
            //解析请求参数
            String[] idArr = id.split(",");
            //数组转集合
            List<String> ids = Arrays.asList(idArr); 
            //批量删除站内信
            internalMessageDao.batchDelInternalMessage(ids);
            return BaseResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用删除用户站内信业务异常:{}",e.getMessage());
            return BaseResponse.faild("0", "删除失败");
        }
    }

}
