package com.cn.tianxia.api.game.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.cn.tianxia.api.exception.HttpClientException;
import com.cn.tianxia.api.po.v2.ResultResponse;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.v2.TransferHttpClientUtils;
import com.cn.tianxia.api.utils.v2.TransferUtils;
import com.cn.tianxia.api.vo.v2.TransferVO;

import net.sf.json.JSONObject;

/**
 * @ClassName IBCServiceImpl
 * @Description TODO(这里用一句话描述这个类的作用)
 * @Author Wilson
 * @Date 2018年12月04日 21:10
 * @Version 1.0.0
 **/
public class IBCGameServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(IBCGameServiceImpl.class);

    private String url;
    private String SecurityToken;
    private String OpCode;     // 代理码
    private String OddsType;  // 赔率类型
    private String MaxTransfer;
    private String MinTransfer;

    public IBCGameServiceImpl(Map<String,String> data) {
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(data, "IBC");
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());

        if(jo != null && !jo.isEmpty()){
            if(jo.containsKey("url")){
                this.url = jo.getString("url");
            }
            if(jo.containsKey("SecurityToken")){
                this.SecurityToken = jo.getString("SecurityToken");
            }
            if(jo.containsKey("OpCode")){
                this.OpCode = jo.getString("OpCode");
            }
            if(jo.containsKey("OddsType")){
                this.OddsType = jo.getString("OddsType");
            }
            if(jo.containsKey("MaxTransfer")){
                this.MaxTransfer = jo.getString("MaxTransfer") == null ? "100000.00":jo.getString("MaxTransfer");
            }else{
                MaxTransfer = "100000.00";
            }
            if(jo.containsKey("MinTransfer")){
                this.MinTransfer = jo.getString("MinTransfer");
            }
        }
    }

    public ResultResponse loginGame(TransferVO transferVO) {
        try {
            Map<String, String> paramsMap =new HashMap<>();
            paramsMap.put("OpCode",OpCode);
            paramsMap.put("PlayerName",transferVO.getAccount());

            String concatStr = concatStr(paramsMap);
            String md5Str = MD5Utils.md5toUpCase_32Bit(SecurityToken+"/api/Login?"+concatStr);
            paramsMap.put("concatStr",concatStr);
            paramsMap.put("md5Str",md5Str);
            logger.info("沙巴体育登录请求参数："+paramsMap.toString());

            String res = com.cn.tianxia.api.utils.tx.HttpClientUtil.doGet(url+"/api/Login?"+concatStr+"&SecurityToken="+md5Str);
            logger.info("沙巴体育登录响应："+res);
            JSONObject jsonObject = JSONObject.fromObject(res);
            if ("0".equalsIgnoreCase(jsonObject.getString("error_code"))){

                String sessionToken = jsonObject.getString("sessionToken");
                String url = "https://mkt.gsoft-ib.com/Deposit_ProcessLogin.aspx?lang=cs&g="+sessionToken;
                if ("mobile".equalsIgnoreCase(transferVO.getTerminal())){
                    url = "http://ismart.ib.gsoft88.net/Deposit_ProcessLogin.aspx?lang=cs&st="+sessionToken;
                }
                return ResultResponse.success("沙巴体育登录成功!", url);
            }
            return ResultResponse.faild("沙巴体育登录失败!", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultResponse.faild("沙巴体育登录失败!", "");
        }
    }

    public ResultResponse CheckOrCreateGameAccout(TransferVO transferVO){
        try {
            Map<String, String> paramsMap =new HashMap<>();
            paramsMap.put("OpCode",OpCode);
            paramsMap.put("PlayerName",transferVO.getAccount());
            paramsMap.put("OddsType",OddsType);
            paramsMap.put("MaxTransfer",MaxTransfer);
            paramsMap.put("MinTransfer",MinTransfer);
            String concatStr = concatStr(paramsMap);
            String md5Str = MD5Utils.md5toUpCase_32Bit(SecurityToken+"/api/CreateMember?"+concatStr);
            paramsMap.put("concatStr",concatStr);
            paramsMap.put("md5Str",md5Str);
            logger.info("沙巴体育创建用户请求参数："+paramsMap.toString());
            String res = com.cn.tianxia.api.utils.tx.HttpClientUtil.doGet(url+"/api/CreateMember?"+concatStr+"&SecurityToken="+md5Str);
            logger.info("沙巴体育创建用户响应："+res);
            JSONObject jsonObject = JSONObject.fromObject(res);
            if ("0".equalsIgnoreCase(jsonObject.getString("error_code"))){
                return  ResultResponse.success("沙巴体育创建用户ok",jsonObject);
            }else if("22005".equalsIgnoreCase(jsonObject.getString("error_code"))){
                return  ResultResponse.success("沙巴体育创建用户ok",jsonObject);
            }
            return  ResultResponse.faild("沙巴体育创建用户失败",jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultResponse.faild("沙巴体育创建用户失败","");
        }
    }

    private String concatStr(Map<String,? extends Object> paramsMap) {
        StringBuffer sb = new StringBuffer();

        for (Map.Entry m : paramsMap.entrySet()){
            sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
        }
        String substring = sb.substring(0, sb.length() - 1);
        return substring;

    }

    public ResultResponse getBalance(String playerName) throws Exception {
        Map<String, Object> paramsMap =new HashMap<>();
        paramsMap.put("OpCode",OpCode);
        paramsMap.put("PlayerName",playerName);

        String concatStr = concatStr(paramsMap);
        String md5Str = MD5Utils.md5toUpCase_32Bit(SecurityToken+"/api/CheckUserBalance?"+concatStr);
        paramsMap.put("concatStr",concatStr);
        paramsMap.put("md5Str",md5Str);
        logger.info("沙巴体育查询余额请求参数："+paramsMap);
        String fullUrl = url+"/api/CheckUserBalance?"+concatStr+"&SecurityToken="+md5Str;
        return getResultResponse(playerName,null,true,TransferUtils.GET_BALANCE,fullUrl);
    }

    /**
     * 转入(游戏上分)
     */
    public String transferIn(TransferVO vo) throws Exception {
        return transfer(vo,TransferUtils.TRANSFER_IN,"1");
    }

    private String transfer(TransferVO vo,String action,String direction) throws Exception {
        logger.info("IBC 用户【"+ vo.getAccount() +"】,上下分发起HTTP请求开始,发起金额【"+ vo.getMoney()+"】");
        logger.info("transfer(TransferVO vo,String action,String direction = {}",vo);
        Map<String, Object> paramsMap =new HashMap<>();
        paramsMap.put("OpCode",OpCode);
        paramsMap.put("PlayerName",vo.getAccount());

        paramsMap.put("OpTransId",vo.getOrderNo());
        paramsMap.put("amount",vo.getMoney());
        // 0 提款 ,1 存款
        paramsMap.put("Direction",direction);

        String concatStr = concatStr(paramsMap);
        String md5Str = MD5Utils.md5toUpCase_32Bit(SecurityToken+"/api/FundTransfer?"+concatStr).toLowerCase();
        paramsMap.put("concatStr",concatStr);
        paramsMap.put("md5Str",md5Str);
        logger.info("沙巴体育转账请求参数："+paramsMap);
        String fullUrl = url+"/api/FundTransfer?"+concatStr+"&SecurityToken="+md5Str;
        ResultResponse resultResponse = getResultResponse(vo.getAccount(),vo.getOrderNo(),false,action,fullUrl);
        //上分特殊处理
        if(direction.equals("1")){
            if(StringUtils.isEmpty(resultResponse.getData().toString())){
                logger.info("IBC 上下分请求响应报文为空！");
                //处理中  响应无结果
                return "process";
            }
            JSONObject jsonObject = JSONObject.fromObject(resultResponse.getData());
            logger.info("IBC 上下分请求响应部分报文："  + resultResponse.getData() );
            if(jsonObject.containsKey("status")&& jsonObject.get("status").toString().equals("0")){
                logger.info("IBC 上分请求成功  响应状态码:"+ jsonObject.get("status").toString());
                return "success";
            }else if(jsonObject.get("status").toString().equals("1")){
                logger.info("IBC 上分请求成功,报文响应结果为失败  响应状态码:"+ jsonObject.get("status").toString());
                return  "faild";
            }else{
                // 挂起  上下分处理中
                return "process";
            }
        }else{
            //下分只有两种状态， success/faild
            if(StringUtils.isEmpty(resultResponse.getData().toString())){
                logger.info("IBC 下分请求响应报文为空！");
                return  "faild";
            }
            JSONObject jsonObject = JSONObject.fromObject(resultResponse.getData());
            if (jsonObject.containsKey("status")&& jsonObject.get("status").toString().equals("0")){
                logger.info("IBC 下分请求成功  响应状态码:"+ jsonObject.get("status").toString());
                return "success";
            }else{
                logger.info("IBC 下分失败  响应状态码:"+ jsonObject.get("status").toString());
                return  "faild";
            }
        }

    }

    /**
     * 转出(游戏下分)
     */
    public String transferOut(TransferVO vo) throws Exception {

        return transfer(vo,TransferUtils.TRANSFER_OUT,"0");
    }

    private static final String PLATFORM_KEY = "IBC";
    private static final int SUCCESS_CODE = 0;

    private ResultResponse getResultResponse(String account,String orderNo, boolean isGetBalance, String action, String url) throws Exception {
        String result = null;
        try {
            result = TransferHttpClientUtils.doGet(url,null);
            logger.info(PLATFORM_KEY+"-方法{}-Http请求结果返回为{}",action,result);
        } catch (HttpClientException e) {//处理三种超时
            TransferUtils.setFileLog(PLATFORM_KEY,account,url,action,result);
            return ResultResponse.error("["+PLATFORM_KEY+"]"+action+"请求ERROR!",null,orderNo,result);
        }
        if(StringUtils.isNotEmpty(result)){
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
            if(jsonObject != null && jsonObject.containsKey("error_code")){
                int errorCode = jsonObject.getInteger("error_code");
                String message = jsonObject.getString("message");
                if(SUCCESS_CODE == errorCode){
                    String balance = null;
                    if(isGetBalance){
                        JSONArray data = jsonObject.getJSONArray("Data");
                        com.alibaba.fastjson.JSONObject st = (com.alibaba.fastjson.JSONObject)data.get(0);
                        balance = st.getString("balance");
                    }
                    if(TransferUtils.TRANSFER_IN.equals(action) || TransferUtils.TRANSFER_OUT.equals(action)){
//                        balance = jsonObject.getJSONObject("Data").getString("after_amount");
                        String data = jsonObject.get("Data").toString();
                        return  ResultResponse.success("[IBC]请求成功",data);
                    }
                    return ResultResponse.success("[IBC]请求成功",balance, orderNo,result);
                }else {
                    if(TransferUtils.CHECK_OR_CREATE_GAME_ACCOUT.equals(action) && 22005 == errorCode){
                        return ResultResponse.success(message,null, null,result);
                    }
                    return ResultResponse.faild(message,null, orderNo,result);
                }
            }else{
                if(TransferUtils.TRANSFER_IN.equals(action) || TransferUtils.TRANSFER_OUT.equals(action)){
                    //游戏上下分  如果响应结果为null则返回null，不作失败处理，由上层处理
                    return  ResultResponse.success("[IBC]请求成功",null);
                }
            }
        }
        return ResultResponse.faild("["+PLATFORM_KEY+"]"+action+"请求结果为异常结果!",null, orderNo,result);
    }

    /**
     * 查询用户订单状态
     */
    public String  checkFundTransfer(TransferVO transferVO) throws  Exception{
        logger.info("查询用户订单状态开始 checkFundTransfer(TransferVO transferVO = {}",transferVO);
        if (StringUtils.isEmpty(transferVO.getAccount())|| StringUtils.isEmpty(transferVO.getOrderNo())){
            logger.info("IBC 查询订单状态请求参数不足！");
            throw  new Exception("IBC 查询订单状态请求参数不足！ 用户名或订单编号不能为空！");
        }
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("OpCode",OpCode);
        paramsMap.put("PlayerName",transferVO.getAccount());
        paramsMap.put("OpTransId",transferVO.getOrderNo());
        try{
            String concatStr = concatStr(paramsMap);
            String md5Str = MD5Utils.md5toUpCase_32Bit(SecurityToken+"/api/CheckFundTransfer?"+concatStr).toLowerCase();
            String fullUrl = url+"/api/CheckFundTransfer?"+concatStr+"&SecurityToken="+md5Str;

            logger.info("IBC 发起Http请求报文, URL:{} ，params:{}",fullUrl,paramsMap);
            String result = TransferHttpClientUtils.doGet(fullUrl,null);

            if (StringUtils.isEmpty(result)){
                logger.info("IBC 发起HTTP请求查询订单状态响应无结果！");
                return "process";
            }
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
            com.alibaba.fastjson.JSONObject data = null;
            if (jsonObject.containsKey("error_code")&& jsonObject.get("error_code").toString().equals(SUCCESS_CODE)){

                logger.info("IBC 发起HTTP请求查询订单状态响应成功！ 响应数据：{}",jsonObject.toString());
                if (jsonObject.containsKey("Data")){
                    data = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.get("Data").toString());
                    logger.info("IBC 发起HTTP请求查询订单状态响应成功！ 部分报文响应数据:"+ data.toString());

                    if (StringUtils.isEmpty(data.toString())) return "process";
                    return  data.get("status").toString().equals("0") ? "success":"faild";
                }else{

                    //HTTP响应结果异常
                    logger.info("IBC 发起HTTP请求查询订单响应异常！ Data参数为空");
                    return "process";
                }

            }else if(jsonObject.get("error_code").toString().equals("2")){
                logger.info("IBC 发起Http请求响应返回交易记录不存在");
                return  "faild";
            }
            //查询失败  挂起状态，继续处理
            return  "process";
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询用户订单信息错误",e);
            throw  e;
        }
    }

    public String updateMember(TransferVO transferVO) throws  Exception{
        logger.info("IBC 发起修改用户信息HTTP请求开始：{}",transferVO);
        if(StringUtils.isEmpty(transferVO.getAccount())){
            logger.info("IBC 发起修改用户信息请求，请求用户名为空！");
            throw  new  Exception("IBC 发起修改用户信息请求，请求用户名为空！");
        }
        /**
         */
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("OpCode",OpCode);
        paramsMap.put("PlayerName",transferVO.getAccount());
        paramsMap.put("MaxTransfer","100000.00");
        String concatStr = concatStr(paramsMap);
        String md5Str = MD5Utils.md5toUpCase_32Bit(SecurityToken+"/api/UpdateMember?"+concatStr).toLowerCase();
        String fullUrl = url+"/api/UpdateMember?"+concatStr+"&SecurityToken="+md5Str;
        String result = TransferHttpClientUtils.doGet(fullUrl,null);
        logger.info("IBC 用户【"+ transferVO.getAccount()+"】 发起HTTP请求修改用户信息响应结果:{}",result);
        if(StringUtils.isEmpty(result)){
            logger.info("IBC 用户【"+ transferVO.getAccount()+"】 修改IBC用户信息返为null");
            return  "error";
        }
        logger.info("IBC 用户【"+ transferVO.getAccount()+"】 修改IBC用户信息返回结果：{}",result);
        //{"error_code":0,"message":"Successfully executed"}
        JSONObject jsonObject = JSONObject.fromObject(result);
        if(jsonObject.get("error_code").toString().equals("0")){
            return  "success";
        }
        return  "error";

    }

}