package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.error.BusinessException;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.v2.JSONArrayResponse;
import com.cn.tianxia.api.service.v2.BankCardService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.AddBankCardVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName BankController
 * @Description 银行卡接口
 * @author Hardy
 * @Date 2019年1月30日 下午8:30:57
 * @version 1.0.0
 */
@Controller
@RequestMapping("/User")
public class BankCardController extends BaseController{

    @Autowired
    private BankCardService bankCardService;
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 添加银行卡
     *
     * @param request
     * @return
     */
    @RequestMapping("/addUserCard")
    @ResponseBody
    public JSONObject addUserCard(HttpServletRequest request, AddBankCardVO addBankCardVO) throws BusinessException {
        logger.info("用户添加银行卡开始================start=================");
        //从缓存中获取用户ID
        Map<String,String> data = getUserInfoMap(redisUtils,request);
        if(CollectionUtils.isEmpty(data)){
            logger.info("请求未登录,请登录");
            return BaseResponse.faild("faild", "用户未登录,请重新登录");
        }
        String uid = data.get("uid");
        addBankCardVO.setUid(uid);
        return bankCardService.addUserCard(addBankCardVO);
    }

    /**
     * 删除银行卡
     *
     * @param request
     * @param cardId   银行卡ID
     * @param password 取款密码
     * @return
     */
    @RequestMapping("/delUserCard")
    @ResponseBody
    public JSONObject delUserCard(HttpServletRequest request, String cardId, String password) throws BusinessException{
        logger.info("用户删除银行卡开始================start=================");
        //从缓存中获取用户ID
        Map<String,String> data = getUserInfoMap(redisUtils,request);
        if(CollectionUtils.isEmpty(data)){
            logger.info("请求未登录,请登录");
            return BaseResponse.faild("faild", "用户未登录,请重新登录");
        }
        String uid = data.get("uid");
        return bankCardService.delUserCard(uid,cardId,password);
    }

    /**
     * 获取银行卡信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/getUserCard")
    @ResponseBody
    public JSONArray getUserCard(HttpServletRequest request) {
        logger.info("用户获取银行卡列表开始================start=================");
        try {
            //从缓存中获取用户ID
            Map<String,String> data = getUserInfoMap(redisUtils,request);
            if(CollectionUtils.isEmpty(data)){
                logger.info("请求未登录,请登录");
                return JSONArrayResponse.faild("获取用户ID失败,用户登录超时");
            }
            String uid = data.get("uid");
            return bankCardService.getUserCard(uid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户获取银行卡列表异常:{}",e.getMessage());
            return JSONArrayResponse.faild("用户获取银行卡列表异常");
        }
    }
}
