package com.cn.tianxia.api.service.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.api.service.ESWAbstractService;
import com.cn.tianxia.api.utils.Encrypt;
import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.PlatFromConfig;
import com.cn.tianxia.api.vo.EswLoginVo;
import com.cn.tianxia.api.vo.EswTransferVo;

import net.sf.json.JSONObject;


public class ESWServiceImpl extends ESWAbstractService {

    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    private final static Logger logger = LoggerFactory.getLogger(ESWServiceImpl.class);

    private static String apiUrl;
    private static String desKey;
    private static String agentId;
    private static String md5Key;

    private static final String PLATFORM_KEY = "ESW";
    private static final String DATA = "data";
    private static final String CODE = "code";

    /**
     * 构造方法
     */
    public ESWServiceImpl(Map<String, String> pmap) {
        //txdata-db1.t_platform_config
        PlatFromConfig pf = new PlatFromConfig();
        pf.InitData(pmap, PLATFORM_KEY);
        JSONObject jo = JSONObject.fromObject(pf.getPlatform_config());
        apiUrl = jo.getString("apiUrl");
        desKey = jo.getString("desKey");
        agentId = jo.getString("agentId");
        md5Key = jo.getString("md5Key");
        /*apiUrl = "https://yshandle.dshyapi.com/index/agentHandle";
        desKey = "MzZkV2V6";
        agentId = "1317";
        md5Key = "jiQCQZFf5RRhl1oMggTZ5BsAEuc=";
        localHomeUrl = "http://www.myhomeurl.com";*/
    }

    @Override
    public String checkOrCreateGameAccout(EswLoginVo eswLoginVo) throws Exception {
        logger.info("(ESW)德胜棋牌检查或创建用户开始================START=============,参数{}",eswLoginVo);
        String action = "checkOrCreateGameAccout";
        //检查参数
        checkParam(eswLoginVo);
        //获取完整请求url
        String url = getCheckOrCreateGameAccoutFullUrl(eswLoginVo);
        String result = sendGet(url, action, PLATFORM_KEY);
        logger.info(PLATFORM_KEY+"-方法{}请求结果返回为{}",action,result);
        if (StringUtils.isBlank(result)) {
            logger.error("调用德胜棋牌登录接口未返回参数");
            throw new RuntimeException();
        }
        return checkResult(result,eswLoginVo.getUserCode(),url,action);
    }

    private String getCheckOrCreateGameAccoutFullUrl(EswLoginVo eswLoginVo) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for(Field field : EswLoginVo.class.getDeclaredFields()){
            field.setAccessible(true);
            Object obj = field.get(eswLoginVo);
            if(obj != null && obj.toString() != ""){
                stringBuilder.append("&").append(field.getName()).append("=").append(obj);
            }
        }
        String param = stringBuilder.toString().substring(1);
        return urlAppendParams(param);
    }

    private void checkParam(EswLoginVo eswLoginVo) {
        if (eswLoginVo.getGameId() == null) {
            eswLoginVo.setGameId(0);
        }
        if (StringUtils.isBlank(eswLoginVo.getUserCode())) {
            throw new RuntimeException("userCode未传");
        }
        if (StringUtils.isBlank(eswLoginVo.getIp())) {
            eswLoginVo.setIp("127.0.0.1");
        }
    }

    @Override
    public String getBalance(String userCode) throws Exception {
        logger.info("(ESW)德胜棋牌查询用户余额开始================START=============,参数{}",userCode);
        String data = queryUserInfo(userCode);
        JSONObject jsonObject = JSONObject.fromObject(data);
        if(jsonObject.getInt(CODE) == 0){
            return jsonObject.getString("money");
        }
        return ERROR;
    }

    @Override
    public String queryUserInfo(String userCode) throws Exception {
        logger.info("(ESW)德胜棋牌查询用户开始================START=============,参数{}",userCode);
        String action = "queryUserInfo";
        if (StringUtils.isBlank(userCode)) {
            throw new RuntimeException("userCode未传");
        }
        //获取完整请求url
        String url = getQueryUserInfoFullUrl(userCode);
        String result = sendGet(url, action, PLATFORM_KEY);
        logger.info(PLATFORM_KEY+"-方法{}请求结果返回为{}",action,result);
        return checkResult(result,userCode,url,action);
    }

    private String getQueryUserInfoFullUrl(String userCode) throws Exception {
        StringBuilder param = new StringBuilder();
        param.append("ac=").append("2").append("&userCode=").append(userCode);
        return urlAppendParams(param.toString());
    }

    @Override
    public String transferIn(String userCode,String money,String orderId) throws Exception {
        logger.info("(ESW)德胜棋牌上分开始================START=============,参数{},{}",userCode,money);
        EswTransferVo eswTransferVo  = new EswTransferVo();
        eswTransferVo.setUserCode(userCode);
        eswTransferVo.setMoney(money);
        eswTransferVo.setOrderId(orderId);

        String data = deposit(eswTransferVo);
        JSONObject jsonObject = JSONObject.fromObject(data);
        if(jsonObject.getInt(CODE) == 0){
            return SUCCESS;
        }
        return ERROR;
    }

    //parentCode的参数为处理
    private String deposit(EswTransferVo eswTransferVo) throws Exception {
        logger.info("(ESW)德胜棋牌上分开始================START=============,参数{}",eswTransferVo);
        String action = "deposit";
        return depositOrWithdraw(action,eswTransferVo,"3");
    }

    private String depositOrWithdraw(String action,EswTransferVo eswTransferVo,String ac) throws Exception {
        if(StringUtils.isBlank(eswTransferVo.getUserCode()) || StringUtils.isBlank(eswTransferVo.getMoney())){
            throw new RuntimeException("userCode或者Money未传");
        }
        //获取完整请求url：先拼接param，在拼接url后面
        String url = getTransferFullUrl(eswTransferVo,ac);
        String result = sendGet(url, action, PLATFORM_KEY);
        logger.info(PLATFORM_KEY+"-方法{}请求结果返回为{}",action,result);
        return checkResult(result,eswTransferVo.getUserCode(),url,action);
    }

    private String getTransferFullUrl(EswTransferVo eswTransferVo,String ac) throws Exception {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        String orderId = agentId + sdf.format(new Date()) + eswTransferVo.getUserCode();
        String orderId = eswTransferVo.getOrderId();
        //生成的订单号保存下来
        eswTransferVo.setOrderId(orderId);
        StringBuilder param = new StringBuilder();
        param.append("ac=").append(ac).append("&userCode=").append(eswTransferVo.getUserCode())
                .append("&money=").append(eswTransferVo.getMoney()).append("&orderId=").append(orderId);
        return urlAppendParams(param.toString());
    }

    @Override
    public String transferOut(String userCode,String money,String orderId) throws Exception {
        EswTransferVo eswTransferVo  = new EswTransferVo();
        eswTransferVo.setUserCode(userCode);
        eswTransferVo.setMoney(money);
        eswTransferVo.setOrderId(orderId);

        String data = withdraw(eswTransferVo);
        JSONObject jsonObject = JSONObject.fromObject(data);
        if(jsonObject.getInt(CODE) == 0){
            return SUCCESS;
        }
        return ERROR;
    }

    private String withdraw(EswTransferVo eswTransferVo) throws Exception {
        logger.info("(ESW)德胜棋牌下分开始================START=============,参数{}",eswTransferVo);
        String action = "withdraw";
        return depositOrWithdraw(action,eswTransferVo,"4");
    }

    @Override
    public String queryOrderStatus(EswTransferVo eswTransferVo) throws Exception {
        String action = "queryOrderStatus";
        if(StringUtils.isBlank(eswTransferVo.getUserCode()) || StringUtils.isBlank(eswTransferVo.getOrderId())){
            throw new RuntimeException("userCode或者OrderId未传");
        }
        //获取完整请求url：先拼接param，在拼接url后面
        String url = getQueryOrderStatusFullUrl(eswTransferVo);
        String result = sendGet(url, action, PLATFORM_KEY);
        logger.info(PLATFORM_KEY+"-方法{}请求结果返回为{}",action,result);
        return checkResult(result,eswTransferVo.getUserCode(),url,action);
    }

    private String getQueryOrderStatusFullUrl(EswTransferVo eswTransferVo) throws Exception {
        StringBuilder param = new StringBuilder();
        param.append("ac=").append("5").append("&userCode=").append(eswTransferVo.getUserCode())
                .append("&orderId=").append(eswTransferVo.getOrderId());
        return urlAppendParams(param.toString());
    }

    /**
     * 功能描述: 查询所有游戏状态
     *
     * @Author: Horus
     * @Date: 2019/1/7 19:41
     * @param
     * @return: java.lang.String
     **/
    @Override
    public String queryAllGameStatus() throws Exception {
        String action = "queryAllGameStatus";
        StringBuilder param = new StringBuilder();
        param.append("ac=").append("8");
        String url = urlAppendParams(param.toString());
        String result = sendGet(url, action, PLATFORM_KEY);
        logger.info(PLATFORM_KEY+"-方法{}请求结果返回为{}",action,result);
        return checkResult(result,null,url,action);
    }


    /********************************** 公共方法 **********************************************/
    private String urlAppendParams(String param) throws Exception {
        logger.info("ESW请求URL参数部分报文{}",param);
        System.out.println(param);
        Long timestamp = System.currentTimeMillis();
        StringBuilder url = new StringBuilder(apiUrl);
        url.append("?agentId=").append(agentId);
        url.append("&timestamp=").append(timestamp);
        url.append("&param=").append(Encrypt.DESEncrypt(param, desKey));
        url.append("&sign=").append(Encrypt.MD5(agentId + timestamp + md5Key));
        logger.info("ESW请求完整URL报文{}",url);
        System.out.println(url);
        return url.toString();
    }

    private String checkResult(String result, String userCode, String url, String action) {
        JSONObject json = JSONObject.fromObject(result);
        JSONObject data = JSONObject.fromObject(json.getString(DATA));
        int resultCode = data.getInt(CODE);
        if (resultCode != 0) {
            setFileLog(userCode,url,action,result);
        }
        return data.toString();
    }

    private void setFileLog(String userCode, String url, String action,String result) {
        FileLog f = new FileLog();
        Map<String, String> param = new HashMap<>();
        param.put("platCode", PLATFORM_KEY);
        if(userCode != null){
            param.put("userCode", userCode);
        }
        param.put("url", url);
        param.put("result", result);
        param.put("function", action);
        f.setLog(PLATFORM_KEY, param);
    }

    private <T> Map<String,String> getInitMap(T t) throws IllegalAccessException {
        Map<String,String> map = new HashMap<>();
        Class<EswLoginVo> clz = EswLoginVo.class;
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object obj = field.get(t);
            if(obj != null && obj.toString() != ""){
                map.put(field.getName(),obj.toString());
            }
        }
        return map;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> pmap = new HashMap<>();
        pmap.put("ESW","{\"apiUrl\":\"https://yshandle.dshyapi.com/index/agentHandle\",\"desKey\":\"MzZkV2V6\",\"agentId\":\"1317\",\"md5Key\":\"jiQCQZFf5RRhl1oMggTZ5BsAEuc=\",\"localHomeUrl\":\"http://www.myhomeurl.com\"}");
        ESWServiceImpl service = new ESWServiceImpl(pmap);

        //登录
       /* EswLoginVo vo = new EswLoginVo();
        vo.setUserCode("bl1james123");
        vo.setNickName("james123");
        vo.setGameId(1001);
        vo.setMoney("200000");
        vo.setIp("192.168.79.1");
        String result = service.checkOrCreateGameAccout(vo);
        System.out.println(result);*/

        //上分下分
//        EswTransferVo eswTransferVo = new EswTransferVo();
//        eswTransferVo.setUserCode("bl1wilson");
//        eswTransferVo.setMoney("8");
//        System.out.println(service.deposit(eswTransferVo));
//        System.out.println(service.withdraw(eswTransferVo));

        //上分下分
//        System.out.println(service.TransferIn("bl1wilson","8"));
//        System.out.println(service.TransferOut("bl1wilson0","10"));

        //查询用户
        System.out.println(service.queryUserInfo("bl1wilson"));
        System.out.println(service.getBalance("bl1wilson"));

        //查询订单状态
        //131720190107175517516bl1wilson 131720190107175517516bl1wilson
        /*EswTransferVo eswTransferVo = new EswTransferVo();
        eswTransferVo.setUserCode("bl1wilson");
        eswTransferVo.setOrderId("131720190107175517516bl1wilson");
        System.out.println(service.queryOrderStatus(eswTransferVo));*/


        //查询游戏状态
        /*System.out.println(service.queryAllGameStatus());*/
    }
}
