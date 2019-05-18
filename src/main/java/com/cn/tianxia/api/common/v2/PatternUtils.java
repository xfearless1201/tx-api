package com.cn.tianxia.api.common.v2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: zed
 * @Date: 2019/1/24 15:45
 * @Description: 正则工具类
 */
public class PatternUtils {

    public static final String COMMONREGEX = "<([^<>]*)>|[a-zA-z]+://|<+|>+|//|\\?";
    //手机号正则
    public static final String PHONENOREGEX = "^1[2-9][0-9]{9}$";
    //代理平台正则
    public static final String CAGENTREGEX = "[a-zA-Z][a-zA-Z0-9]{2}";
    //用户名正则
    public static final String USERNAMEREGEX = "[a-zA-Z0-9]{5,15}";
    //注册用户真实姓名正则
    public static final String REALNAMEREGEX = "^[\\u4e00-\\u9fa5a-zA-Z.·]{2,10}$";
    //ip正则
    public static final String IPREGEX = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    //域名正则
    public static final String DOMAINREGEX = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co|vip|top|name|wang|shop|xin|tv|site|info|hk|ltd|xyz|red|club|wang|ink|pro|edu|group|link|mobi|ren|kim|idv|asia|ceo|one|space|work|app|me|club|life|co\\.in|in|online|biz|tv|net\\.in|org\\.in|mobi|io|gen\\.in|ind\\.in|world)\\b()*";
    //银行卡号
    public static final String CARDNUMBERREGEX = "^([1-9]{1})(\\d{13}|\\d{14}|\\d{15}|\\d{16}|\\d{17}|\\d{18}|\\d{19})$";
    //开户地址
    public static final String CARDADDRREGEX = "^([\\u4e00-\\u9fa5])([\\u4e00-\\u9fa5]|[0-9])+";
    //密码正则
    public static final String PASSWORDREGEX = "[a-zA-Z0-9]{5,20}";
    //取款密码正则表达式
    public static final String QKPASSWORDREGEX = "[a-zA-Z0-9]{4,6}";

    public static Boolean isMatch(String str,String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
