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
import com.cn.tianxia.api.service.v2.TreasureRecordService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.TreasureRecordVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/1 10:49
 * @Description: 资金流水记录控制器
 */

@Controller
@RequestMapping("/User")
public class TreasureRecordController extends BaseController {

    @Autowired
    private TreasureRecordService treasureRecordService;
    
    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/queryByTreasurePage")
    @ResponseBody
    public JSONObject queryTreasureByPage(HttpServletRequest request, HttpServletResponse response, Integer type,
                                          @RequestParam String startTime,
                                          @RequestParam String endTime,
                                          @RequestParam(defaultValue = "10", required = false) int pageSize,
                                          @RequestParam(defaultValue = "1", required = false) int pageNo) {

        logger.info("查询用户资金流水记录开始==================START=======================");
        try {
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("查询用户资金流水记录异常:用户未登录");
                return BaseResponse.error(BaseResponse.ERROR_CODE, "查询用户资金流水记录异常:用户未登录");
            }
            String uid = data.get("uid");
            
            String query_type = 
                    type == null ? null : type == 1 ? "加款" : type == 2 ? "扣款" : type == 3 ? "彩金" : type == 4 ?"优惠" : 
                        type == 5 ? "提款" : type == 6 ? "反水" : null;

            TreasureRecordVO treasureRecordVO = new TreasureRecordVO();
            treasureRecordVO.setUid(uid);
            treasureRecordVO.setType(query_type);
            treasureRecordVO.setStartTime(startTime);
            treasureRecordVO.setEndTime(endTime);
            treasureRecordVO.setPageNo(pageNo);
            treasureRecordVO.setPageSize(pageSize);
            return treasureRecordService.findAllByPage(treasureRecordVO);
        } catch (Exception e) {
            logger.info("查询用户资金流水记录异常:{}", e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE, "查询用户资金流水记录异常:" + e.getMessage());
        }
    }
}
