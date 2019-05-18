package com.cn.tianxia.api.common.v2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @ClassName DatePatternUtils
 * @Description 时间工具类
 * @author Hardy
 * @Date 2019年2月1日 下午10:06:35
 * @version 1.0.0
 */
public class DatePatternUtils {
    
    /**
     * 
     * @Description 字符串日期转Date日期
     * @param date
     * @return
     * @throws ParseException 
     */
    public static Date strToDate(String date,String formatStr) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.parse(date);
    }
    
    /**
     * 
     * @Description Date日期转字符串日期
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date,String formatStr){
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    
    /**
     * 
     * @Description 新增或扣除天数
     * @param month 天数
     * @return
     */
    public static Date addOrMinusDay(Date date,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,day);
        return calendar.getTime();
    }
}
