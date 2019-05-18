package com.cn.tianxia.api.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.base.annotation.LogApi;
import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.service.v2.PlatformPayService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.ScanPayVO;

import net.sf.json.JSONObject;

/**
 * @Description:TODO
 * @author:zouwei
 * @time:2017年7月9日 下午3:24:04
 */
@RequestMapping("TestPay")
@Controller 
@Scope("prototype")
public class TestPayController extends BaseController {

    @Autowired
    private PlatformPayService platformPayService;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 扫描支付 // 参数:scancode 扫描支付code amount 支付金额 topay 支付商code
     *
     * @param request
     * @param response
     * @return
     */
    @LogApi("扫码支付接口")
    @RequestMapping("/scanPay")
    @ResponseBody
    public JSONObject testScanPay(String acounmt, String userId) {
            String key = CacheKeyConstants.ONLINE_PAY_KEY_UID+userId;
            if(redisUtils.hasKey(key)){
                logger.info("{}该用户已存在支付提交记录", userId);
            }
            redisUtils.set(key, userId,5);
            ScanPayVO scanPayVO = new ScanPayVO();
            scanPayVO.setUid(userId);
            scanPayVO.setPayId("1207");
            scanPayVO.setAmount(Double.valueOf(acounmt));
            scanPayVO.setScancode("ali");
            scanPayVO.setRefererUrl("");
            scanPayVO.setIp("127.0.0.1");
            scanPayVO.setMobile("");
            return platformPayService.scanPay(scanPayVO);
    }

}
