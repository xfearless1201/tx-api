package com.cn.tianxia.api.common.v2;

/**
 * 
 * @ClassName CacheKeyConstants
 * @Description 缓存KEY
 * @author Hardy
 * @Date 2019年1月5日 下午2:43:16
 * @version 1.0.0
 */
public class CacheKeyConstants {
    
    public static final String TT_TOKEN = "TOKEN";
    
    public static final String LOGIN_USER_JWT_KEY="LOGIN:USER:JWT:";

    //过期时间
    public static final long EXPIRE_TIME = 7200;
    
    /**
     * 用户提交银行汇款key
     */
    public static final String BANK_REMITTANCE_KEY_UID="BANK:REMITTANCE:";
    
    /**
     * 在线支付key
     */
    public static final String ONLINE_PAY_KEY_UID="ONLINE:PAY:UID:";
    
    /**
     * 在线用户key
     */
    public static final String ONLINE_USER_KEY_UID="ONLINE:USER:UID:";
    
    
    /**
     * 登录会员key
     */
    public static final String LOGIN_USER_KEY_TOKEN="LOGIN:USER:TOKEN:";
    
    /**
     * 手机号验证码
     */
    public static final String SEND_PHONE_KEY_CODE="SEND:PHONE:CODE:";
    
    /**
     * 线下扫码key
     */
    public static final String UNONLINE_SCAN_KEY_PAY_UID="UNONLINE:SCAN:PAY:UID:";
    
    /**
     * 提现key
     */
    public static final String WITHDRAW_KEY_UID="WITHDRAW:UID:";
    
    /**
     * 活动红包key
     */
    public static final String ACTIVITY_LUCKDRAW_KEY_UID="ACTIVITY:LUCKDRAW:UID:";
    
    /**
     * 游戏转账key
     */
    public static final String GAME_TRANSFER_KEY_UID="GAME:TRANSFER:UID:";
    
    /**
     * 注册key
     */
    public static final String REGIST_USER_KEY_UID="REGIST:USER:UID:";
    
    /**
     * 踢掉用户下线
     */
    public static final String KICK_USER_KEY_UID="KICK:USER:UID:";

    /**
     * 验证码
     */
    public static final String VALIDATE_CODE_SESSION = "VALIDATE:CODE:SESSION:";

    /**
     * 刮刮乐用户刮到的金额
     */
    public static final String CAGENT_GGL_UID = "CAGENT:GGL:UID:";

    /**
     * 网站设置
     */
    public static final String WEB_CONFIG_CAGENT = "WEB:CONFIG:CAGENT:";

    /**
     * 手机网站设置
     */
    public static final String MOBILE_WEB_CONFIG_CAGENT = "WEB:CONFIG:MOB:CAGENT:";

    /**
     * 公告
     */
    public static final String WEB_GONGGAO_CAGENT = "WEB:GONGGAO:CAGENT:";


}
