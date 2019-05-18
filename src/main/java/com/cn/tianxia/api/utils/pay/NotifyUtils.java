package com.cn.tianxia.api.utils.pay;

import java.util.HashMap;
import java.util.Map;

import com.cn.tianxia.api.utils.FileLog;
import com.cn.tianxia.api.utils.JSONUtils;

/**
 * @ClassName: NotifyUtils
 * @Description: 回调工具方法类
 * @Author: Zed
 * @Date: 2019-01-11 19:45
 * @Version:1.0.0
 **/

public class NotifyUtils {


    /**
     * 保存文件
     *
     * @param fileName
     * @param request1
     * @param ip
     */
    public static void savePayFile(String fileName, Map<String, String> request1, String ip) {
        // 文件记录
        FileLog f = new FileLog();
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("requestIp", ip);
        fileMap.put("requestParams", JSONUtils.toJSONString(request1));
        f.setLog(fileName, fileMap);
    }

}
