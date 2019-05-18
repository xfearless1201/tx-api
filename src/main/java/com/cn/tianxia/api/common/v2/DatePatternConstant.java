package com.cn.tianxia.api.common.v2;

/**
 * 
 * @ClassName DateFormatConstant
 * @Description 时间格式化类型
 * @author Hardy
 * @Date 2019年2月1日 下午9:58:07
 * @version 1.0.0
 */
public class DatePatternConstant {
    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";
    public static final String PURE_DATE_PATTERN = "yyyyMMdd";
    public static final String PURE_TIME_PATTERN = "HHmmss";
    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
    public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
}
