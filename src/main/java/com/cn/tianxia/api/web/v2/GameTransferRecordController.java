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
import com.cn.tianxia.api.service.v2.TransferRecordService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.TransferRecordVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;

/**
 * @ClassName GameTransferRecordController
 * @Description 游戏转账记录接口
 * @author Hardy
 * @Date 2019年1月31日 下午9:22:39
 * @version 1.0.0
 */
@Controller
@RequestMapping("/User")
public class GameTransferRecordController extends BaseController {

    @Autowired
    private TransferRecordService transferRecordService;
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     * @Description 查询用户的转账记录
     * @param request
     * @param response
     * @param Type
     * @param Ttype
     * @param pageSize
     * @param pageNo
     * @param bdate
     * @param edate
     * @return
     */
    @RequestMapping("/getTransferInfo")
    @ResponseBody
    public JSONArray getTransferInfo(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String Type,
            @RequestParam String Ttype, 
            @RequestParam(required = true) String bdate, @RequestParam(required = true) String edate,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNo) {

        logger.info("分页查询用户转账记录列表开始================START======================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,用户登录超时");
                return JSONArrayResponse.faild("获取用户ID失败,用户登录超时");
            }
            String uid = map.get("uid");

            TransferRecordVO transferRecordVO = new TransferRecordVO();
            transferRecordVO.setUid(uid);
            transferRecordVO.setEdate(edate);
            transferRecordVO.setBdate(bdate);
            transferRecordVO.setPageNo(pageNo);
            transferRecordVO.setPageSize(pageSize);
            transferRecordVO.setTtype(Ttype);
            transferRecordVO.setType(Type);

            return transferRecordService.getTransferInfo(transferRecordVO);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("分页查询用户转账记录列表异常:{}", e.getMessage());
            return JSONArrayResponse.faild("分页查询用户转账记录异常");
        }
    }
}
