package com.cn.tianxia.api.common.v2;

import com.cn.tianxia.api.common.PayProperties;

/**
 * @ClassName: ScanCodeUtil
 * @Description: 获取payCode配置文件
 * @Author: Zed
 * @Date: 2019-01-08 17:50
 * @Version:1.0.0
 **/

public class ScanCodeUtil {
    public static String getMobileScanPayCode(String topay,int index) {
        try {
            if (PayProperties.scanMobileTypeMap == null || PayProperties.scanMobileTypeMap.size() == 0) {
                PayProperties tb = new PayProperties();
                PayProperties.scanMobileTypeMap = tb.readMobileProperties();
            }
            if (PayProperties.scanMobileTypeMap.containsKey(topay)) {
                String sptStr = PayProperties.scanMobileTypeMap.get(topay);
                String[] sptNumber = sptStr.split(",");
                return sptNumber[index];
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPcScanPayCode(String topay,int index) {
        try {
            // pc端
            if (PayProperties.scanTypeMap == null || PayProperties.scanTypeMap.size() == 0) {
                PayProperties tb = new PayProperties();
                PayProperties.scanTypeMap = tb.readProperties();
            }
            if (PayProperties.scanTypeMap.containsKey(topay)) {
                String sptStr = PayProperties.scanTypeMap.get(topay);
                String[] sptNumber = sptStr.split(",");
                return sptNumber[index];
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
