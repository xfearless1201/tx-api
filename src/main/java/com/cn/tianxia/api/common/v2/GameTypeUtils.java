package com.cn.tianxia.api.common.v2;

/**
 * 
 * @ClassName GameTypeUtils
 * @Description 游戏类型工具类(修改此类时注意,内容只可增加不可减少,如果需要修改原有内容,需与相关人员沟通,并确保不影响其他功能的前提下)
 * @author Hardy
 * @Date 2019年2月18日 下午3:32:22
 * @version 1.0.0
 */
public class GameTypeUtils {
    
    /**
     * 
     * @Description 获取游戏类型,返回结果为大写游戏类型编码
     * @param type
     * @return
     */
    public static String formartGameType(String type){
        String result = type;
        if("IGLOTTO".equalsIgnoreCase(type) || "IGLOTTERY".equalsIgnoreCase(type)){
            result = "IG";
        }
        if("IGPJLOTTO".equalsIgnoreCase(type)||"IGPJLOTTERY".equalsIgnoreCase(type)){
            result = "IGPJ";
        }
        if("YOPLAY".equalsIgnoreCase(type) || "TASSPTA".equalsIgnoreCase(type) || "AGBY".equalsIgnoreCase(type)){
            result = "AGIN";
        }
        
        return result.toUpperCase();
    }

}
