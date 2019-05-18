package com.cn.tianxia.api.common.v2;

import java.security.NoSuchAlgorithmException;

import com.cn.tianxia.api.utils.v2.MD5Utils;

/**
 * @ClassName TokenUtils
 * @Description token生成工具
 * @author Hardy
 * @Date 2019年5月10日 下午1:40:48
 * @version 1.0.0
 */
public class TokenUtils {
    
    //秘钥串
    public static final String SECRET_KEY = "UFGFKzspbzWav2ERqXJ7R3ZjvbBeMsq0";
    
    //生成一个加密串
    public static String generatorToken(String sessionId) throws NoSuchAlgorithmException{
        //加密串的由 秘钥+用户名
        StringBuffer sb = new StringBuffer();
        sb.append(SECRET_KEY).append(":");
        sb.append(sessionId);
        //生成待加密串
        String signStr = sb.toString();
        //进行MD5加密
        String token = MD5Utils.md5toUpCase_16Bit(signStr);
        return token;
    }
}
