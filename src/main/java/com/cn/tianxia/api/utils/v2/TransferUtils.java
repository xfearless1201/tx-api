package com.cn.tianxia.api.utils.v2;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cn.tianxia.api.utils.FileLog;

/**
 * 
 * @ClassName TransferUtils
 * @Description 转账工具类
 * @author Hardy
 * @Date 2018年11月6日 下午8:51:01
 * @version 1.0.0
 */
public class TransferUtils {

    public final static String CHECK_OR_CREATE_GAME_ACCOUT = "checkOrCreateGameAccout";
    public final static String LOGIN_GAME = "loginGame";
    public final static String GET_BALANCE = "getBalance";
    public final static String TRANSFER_IN = "transferIn";
    public final static String TRANSFER_OUT = "transferOut";
    public final static String QUERY_ORDER = "queryOrder";
    public final static String FIND_ORDER = "findOrder";

    //默认 username+currTime
    private final static String[] ZERO = {"IG","IGPJ"};

    private final static String[] ONE = {"DS","OG","SB","MG","PT","HABA","CG","HG","VR","SW"};
    private final static String[] TWO = {"BBIN","OB","GGBY","BG"};
    private final static String[] THREE = {"VG","PS","GY","NB","JDB","AGIN","AG"};
    private final static String[] FOUR = {"JF"};

    public static String generatorTransferOrderNo(String platCode,String username){
        String key = platCode.toUpperCase();
        String currTime = String.valueOf(System.currentTimeMillis());//时间戳字符串 
        if(Arrays.asList(ONE).contains(key)){
             //订单号 = "TX"+currTime组合字符串
            return "TX"+currTime;
         }
         if(Arrays.asList(TWO).contains(key)){
             return currTime;
         }
         if(Arrays.asList(THREE).contains(key)){
             //订单号 = cagent + currTime组合字符串
             return key+currTime;
         }
        if(Arrays.asList(FOUR).contains(key)){
            return "00"+currTime;
        }
        return username+currTime;
     }

    //棋牌类订单号
    private final static String[] FIVE = {"KYQP","LYQP","ESW"};
    //key为代理Id,如1317
    public static String cardTransferOrderNo(String key,String username){
        return key + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + username;
    }

    public static void setFileLog(String platCode,String userCode, String url, String action,String result) {
        FileLog f = new FileLog();
        Map<String, String> param = new HashMap<>();
        param.put("platCode", platCode);
        if(userCode != null){
            param.put("userCode", userCode);
        }
        param.put("result", result);
        param.put("function", action);
        param.put("url", url);
        f.setLog(platCode, param);
    }



}
