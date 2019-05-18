package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.PatternUtils;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.po.ResponsePO;
import com.cn.tianxia.api.service.v2.IntegralMallService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.PluCateVO;
import com.cn.tianxia.api.vo.PluInfoVO;
import com.cn.tianxia.api.vo.PluOrderVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: IntegralMallController
 * @Description: 积分商城控制类
 * @Author: Bing
 * @Date: 2019-02-05
 * @Version:1.2.0
 **/
@Controller
@RequestMapping("integral")
public class IntegralMallController extends BaseController {

	@Resource
	private IntegralMallService integralMallService;

	@Autowired
	private RedisUtils redisUtils;
	
	/**
	 * 根据平台商名称获取商品类型
	 * @param cagentName
	 * @return
	 */
	@RequestMapping("/goodsType")
	@ResponseBody
	public JSONArray getTypeByCagentName(String cagentName){
		try {
			JSONArray resJsonObj = integralMallService.getTypeByCagentName(cagentName);
			return resJsonObj;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("根据平台商名称获取商品类型异常："+e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获取所有的商品列表
	 * @param pluInfoVO
	 * @return
	 */
	@RequestMapping("/goodsList")
	@ResponseBody
	public JSONObject getGoodsList(PluInfoVO pluInfoVO){
		try {
			JSONObject resJsonObj = integralMallService.getGoodsList(pluInfoVO);
			return resJsonObj;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("积分商城获取商品列表异常"+e.getMessage());
			return ResponsePO.error("获取商品列表失败");
		}
	}
	
	/**
	 * 获取商品类别
	 * @param pluCateVO
	 * @return
	 */
	@RequestMapping("/typeSearch")
	@ResponseBody
	public JSONObject getGoodsType(PluCateVO pluCateVO){
		try {
			return integralMallService.getGoodsType(pluCateVO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("积分商城获取商品类别异常"+e.getMessage());
			return ResponsePO.error("获取商品类别失败");
		}
	}
	
	/**
	 * 获取单个商品信息
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("/goodsDetails")
	@ResponseBody
	public JSONObject getGoodsInfo(PluInfoVO pluInfoVO){
		try {
			return integralMallService.getGoodsInfo(pluInfoVO.getId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("获取单个商品信息异常："+e.getMessage());
			return ResponsePO.error("获取单个商品信息失败");
		}
	}
	
    /**
     * 创建订单
     * @param request
     * @param pluOrderVO
     * @return
     */
	@RequestMapping("/generateOrder")
	@ResponseBody
	public JSONObject createOrder(HttpServletRequest request,PluOrderVO pluOrderVO){
		logger.info("调用创建积分订单接口开始===============START===============");
	    try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,用户登录超时");
                return BaseResponse.faild("0","登录已失效，请重新登录");
            }
            String uid = map.get("uid");
            
            //通过用户ID获取缓存信息
            String cid = map.get("cid");
            //校验请求参数
            JSONObject checkParams = checkParamsByCreateOrder(pluOrderVO);
            if(!"success".equalsIgnoreCase(checkParams.getString("status"))){
               //参数校验失败
                return checkParams;
            }
			pluOrderVO.setUid(uid);
			pluOrderVO.setCid(cid);
			JSONObject createOrder = integralMallService.createOrder(pluOrderVO);
			return createOrder;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("生成订单信息异常："+e.getMessage());
			return ResponsePO.error("生成订单失败");
		}
	}
	
	/**
	 * 获取历史订单
	 * @param paramMap
	 * @param uid
	 * @return
	 */
	@RequestMapping("/orderHistory")
	@ResponseBody
	public JSONObject getOrderHistory(HttpServletRequest request,HttpServletResponse response,String startTime,String endTime,
	                                             @RequestParam(defaultValue="1",required=false) Integer pageNo,
	                                             @RequestParam(defaultValue="10",required=false) Integer pageSize){
		logger.info("调用查询用户历史订单接口开始==============START===================");
	    try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,用户登录超时");
                return BaseResponse.faild("0","登录已失效，请重新登录");
            }
            String uid = map.get("uid");
			
			return  integralMallService.getOrderHistory(uid, startTime, endTime, pageNo, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("调用查询用户历史订单接口异常:{}",e.getMessage());
			return ResponsePO.error("获取历史订单记录失败");
		}
	}
	
	/**
	 * 获取兑换排行榜
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("/rankingList")
	@ResponseBody
	public JSONArray getExchangeRankList(PluInfoVO pluInfoVO){
		try {
			JSONArray rankList = integralMallService.getExchangeRankList(pluInfoVO);
			return rankList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("查询兑换排行榜异常："+e.getMessage());
			return null;
		}
	}
	
	/**
	 * 
	 * @Description 检验订单请求参数
	 * @param pluOrderVO
	 * @return
	 */
	private JSONObject checkParamsByCreateOrder(PluOrderVO pluOrderVO){
	    logger.info("检验创建积分订单请求参数开始==============START==============");
	    if(pluOrderVO == null){
	        return BaseResponse.faild("0","请求参数不能为空");
	    }
	    
	    if(pluOrderVO.getId() == null){
	        return BaseResponse.faild("0","请求参数异常:商品ID不能为空");
	    }
	    
	    if(pluOrderVO.getNum() == null){
	        return BaseResponse.faild("0","请求参数异常:商品数量不能为空");
	    }
	    
	    if(pluOrderVO.getNum() < 1){
	        return BaseResponse.faild("0","请求参数异常:商品数量不能小于1");
	    }
	    
	    if(StringUtils.isBlank(pluOrderVO.getDeliverName())){
	        return BaseResponse.faild("0","请求参数异常:收获人名称不能为空");
	    }
	    
	    if(StringUtils.isBlank(pluOrderVO.getDeliverAddress())){
	        return BaseResponse.faild("0","请求参数异常:收获人地址不能为空");
	    }
	    
	    if(StringUtils.isBlank(pluOrderVO.getDeliverPhone())){
	        return BaseResponse.faild("0","请求参数异常:收货人手机号码不能为空");
	    }
	    
	    if(!PatternUtils.isMatch(pluOrderVO.getDeliverPhone(), PatternUtils.PHONENOREGEX)){
	        return BaseResponse.faild("0","请求参数异常:收货人手机号码格式不对");
	    }
	    
	    return BaseResponse.success("参数校验成功");
	}
}
