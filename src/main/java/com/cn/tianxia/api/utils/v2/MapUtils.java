package com.cn.tianxia.api.utils.v2;

import java.util.Map;

/**
 * 
 * @ClassName MapUtils
 * @Description map工具类
 * @author Hardy
 * @Date 2018年11月27日 下午6:48:06
 * @version 1.0.0
 */
public class MapUtils {
    
    public static boolean isEmptyOrNull(Map<String,String> data) {
        if(data == null || data.isEmpty()){
            return false;
        }
        return true;
    }

    
	/**
	 * 
	 * @Title: mapToString   
	 * @Description: TODO(map转换链接)   
	 * @param: @param params
	 * @param: @return      
	 * @return: String      
	 * @throws
	 */
	public static  String mapToString (Map<String, String> params){

		StringBuffer sb =new StringBuffer();
		String result ="";

		if (params == null || params.size() <= 0) {
			return "";
		}
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null || value.equals("")) {
				continue;
			}
			sb.append(key+"="+value+"&");
		}

		result=sb.toString().substring(0,sb.length()-1);

		return result;
	}
}
