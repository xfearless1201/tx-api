package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.po.v2.JSONArrayResponse;
import com.cn.tianxia.api.service.v2.WithDrawService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.WithdrawRecordVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;

/**
 * 
 * @ClassName WithdrawRecordController
 * @Description 提现记录接口
 * @author Hardy
 * @Date 2019年1月31日 下午9:23:06
 * @version 1.0.0
 */
@Controller
@RequestMapping("/User")
public class WithdrawRecordController extends BaseController{
    
    @Autowired
    private WithDrawService withDrawService;
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 
     * @Description 查询用户提现记录
     * @param request
     * @param response
     * @param status
     * @param pageSize
     * @param pageNo
     * @param bdate
     * @param edate
     * @return
     */
    @RequestMapping("/getWithDrawInfo")
    @ResponseBody
    public JSONArray getWithDrawInfo(HttpServletRequest request, HttpServletResponse response, String status,
                                        @RequestParam(defaultValue="1",required=false)Integer pageNo,
                                        @RequestParam(defaultValue="10",required=false)Integer pageSize,
                                        @RequestParam String bdate,
                                        @RequestParam String edate) {
        logger.info("分页查询用户提现记录开始==================start=======================");
        try {
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("从缓存中获取用户ID为空,登录超时,请重新登录");
                return JSONArrayResponse.faild("登录超时,请重新登录");
            }
            String uid = data.get("uid");
            WithdrawRecordVO withdrawRecord = new WithdrawRecordVO();
            withdrawRecord.setUid(uid);
            withdrawRecord.setStatus(status);
            withdrawRecord.setBdate(bdate);
            withdrawRecord.setEdate(edate);
            withdrawRecord.setPageNo(pageNo);
            withdrawRecord.setPageSize(pageSize);
            return withDrawService.findAllByPage(withdrawRecord);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("分页查询用户提现记录异常:{}",e.getMessage());
            return JSONArrayResponse.faild("分页查询用户提现记录异常");
        }
    }
}
