package com.cn.tianxia.api.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理
 * @author hb
 * @date 2018-05-17
 */
public class DateUtils {

	//日期
	private static DateFormat fmt_day = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getToday() {
		return fmt_day.format(new Date());
	}
	
	public static String getMonthAgo() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -29);
		return fmt_day.format(c.getTime());
	}
	
	public static void main(String[] args) {
		System.out.println(getToday());
		System.out.println(getMonthAgo());
	}
}
