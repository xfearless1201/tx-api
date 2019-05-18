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

import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.service.v2.DepositRecordService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.DepositRecordVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName DepositRecordController
 * @Description 存款记录接口
 * @Date 2019年1月31日 下午9:25:02
 */
@Controller
@RequestMapping("/User")
public class DepositRecordController extends BaseController {

    @Autowired
    private DepositRecordService depositRecordService;
    
    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/getReChargeInfo")
    @ResponseBody
    public JSONArray getReChargeInfo(HttpServletRequest request, HttpServletResponse response,
                                     String status, String Type,
                                     @RequestParam() String bdate,
                                     @RequestParam() String edate,
                                     @RequestParam(defaultValue = "10", required = false) int pageSize,
                                     @RequestParam(defaultValue = "1", required = false) int pageNo) {

        logger.info("查询用户存款记录开始==================START=======================");

        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("查询用户存款记录异常:用户未登录");
                return JSONArray.fromObject(BaseResponse.error(BaseResponse.ERROR_CODE,"查询用户存款记录异常:用户未登录"));
            }
            String uid = map.get("uid");
            DepositRecordVO depositRecordVO = new DepositRecordVO();
            depositRecordVO.setUid(uid);
            depositRecordVO.setType(Type);
            depositRecordVO.setBdate(bdate);
            depositRecordVO.setEdate(edate);
            depositRecordVO.setStatus(status);
            depositRecordVO.setPageNo(pageNo);
            depositRecordVO.setPageSize(pageSize);

            return depositRecordService.findAllByPage(depositRecordVO);

        } catch (Exception e) {
            logger.info("查询用户存款记录异常:{}", e.getMessage());
            return JSONArray.fromObject(BaseResponse.error(BaseResponse.ERROR_CODE,"查询用户存款记录异常:" + e.getMessage()));
        }
    }
}
