package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.CacheKeyConstants;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.WithDrawResponse;
import com.cn.tianxia.api.service.v2.WithDrawService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.CreateWithDrawOrderVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * @author Hardy
 * @version 1.0.0
 * @ClassName WithDrawController
 * @Description 会员提现
 * @Date 2019年1月29日 下午2:44:47
 */
@RequestMapping("/User")
@Controller
public class WithDrawController extends BaseController {

    @Autowired
    private WithDrawService withDrawService;
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     * @param session
     * @param request
     * @param response
     * @param credit
     * @param cardid
     * @param password
     * @param poundage
     * @param administrative_fee
     * @param withdrawConfig
     * @return
     * @throws Exception
     * @Description 提现接口
     */
    @RequestMapping("/WithDraw")
    @ResponseBody
    public JSONObject WithDraw(HttpSession session, HttpServletRequest request,
                               HttpServletResponse response, String credit, String cardid, String password, Double poundage,
                               Double administrative_fee, Double withdrawConfig) {
        logger.info("会员提现接口开始===============START======================");
        Map<String,String> data = getUserInfoMap(redisUtils,request);
        if(CollectionUtils.isEmpty(data)){
            logger.info("从缓存中获取用户ID失败,登录过期,请重新登录");
            return WithDrawResponse.faild("获取用户ID失败,请重新登录");
        }
        String uid = data.get("uid");
        logger.info("用户【"+data.get("userName")+"】 开始提现操作！");

        if(!StringUtils.isNumeric(credit)){
            return WithDrawResponse.faild("提现金额输入错误："+ credit);
        }

        int creditNumber = Integer.valueOf(credit);

        //key缓存
        String key = CacheKeyConstants.WITHDRAW_KEY_UID + uid;
        if(redisUtils.hasKey(key)){
            return WithDrawResponse.faild("提现订单正在处理中,请稍等....");
        }
        redisUtils.set(key, uid,30);
        try {
            //校验请求参数
            JSONObject params = checkRequestParams(creditNumber, cardid, password, withdrawConfig);
            if (params.containsKey("code") && "0".equals(params.getString("code"))) {
                return params;
            }
            synchronized (uid.intern()) {
                CreateWithDrawOrderVO createWithDrawOrderVO = new CreateWithDrawOrderVO();
                createWithDrawOrderVO.setCardid(cardid);
                createWithDrawOrderVO.setCredit(creditNumber);
                createWithDrawOrderVO.setPassword(password);
                createWithDrawOrderVO.setPoundage(poundage);
                createWithDrawOrderVO.setUid(uid);
                createWithDrawOrderVO.setWithdrawConfig(withdrawConfig);
                createWithDrawOrderVO.setAdministrative_fee(administrative_fee);
                //发起提现请求
                return withDrawService.createWithDrawOrder(createWithDrawOrderVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("会员创建提现订单异常:{}", e.getMessage());
            return WithDrawResponse.faild("会员创建提现订单异常");
        } finally {
            if(redisUtils.hasKey(key)){
                logger.info("删除缓存key:{}", key);
                redisUtils.delete(key);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @Description 获取用户打码量，游戏流水，强制提款手续费
     */
    @RequestMapping("/selectWithdrawConfig")
    @ResponseBody
    public JSONObject selectWithdrawConfig(HttpServletRequest request, HttpServletResponse response) {
        logger.info("获取用户打码量、游戏流水、强制提款金额---------------------开始-----------------------");
        try {
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("从缓存中获取用户ID失败,登录过期,请重新登录");
                return BaseResponse.error( BaseResponse.ERROR_CODE , "用户未登录");
            }
            String uid = data.get("uid");
            return withDrawService.selectWithdrawConfig(uid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("获取用户打码量、游戏流水、强制提款手续费接口异常:{}" + e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE, "获取用户打码量、游戏流水、强制提款手续费接口异常:" + e.getMessage());
        }
    }


    /**
     * @param credit
     * @param cardid
     * @param password
     * @param withdrawConfig
     * @return
     * @Description 校验请求参数
     */
    private JSONObject checkRequestParams(int credit, String cardid, String password, Double withdrawConfig) {

        if (credit < 100 || credit > 500000) {
            return WithDrawResponse.faild("提款金额请输入大于100小于500000之间的金额");
        }

        if (StringUtils.isBlank(cardid)) {
            return WithDrawResponse.faild("提现银行卡号不能为空");
        }

        if (StringUtils.isBlank(password)) {
            return WithDrawResponse.faild("提现密码不能为空");
        }

        return WithDrawResponse.success("参数校验成功");
    }
}
