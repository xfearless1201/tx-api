package com.cn.tianxia.api.utils.v2;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * 
 * @ClassName CalcUtils
 * @Description 计算工具类
 * @author Hardy
 * @Date 2019年3月13日 下午2:54:43
 * @version 1.0.0
 */
public class CalcUtils {

    static final DecimalFormat pointdf = new DecimalFormat("0.00");//待两位小数点的
    
    static final DecimalFormat unpointdf = new DecimalFormat("##");//不带小数点的
    
    static final Random random = new Random();
    
    /**
     * 
     * @Description 两个double类型数字相除获取double类型数字(b1/b2)
     * @param b1
     * @param b2
     * @return
     */
    public static Double divDuoble(double b1,double b2){
        BigDecimal db1 = new BigDecimal(b1);
        BigDecimal db2 = new BigDecimal(b2);
        BigDecimal result = db1.divide(db2);
        return Double.parseDouble(pointdf.format(result.doubleValue()));
    }
    
    /**
     * 
     * @Description 两个long类型数字相除获取double类型数字(log1/log2)
     * @param log1
     * @param log2
     * @return
     */
    public static Double divLong(long log1,long log2){
        BigDecimal db1 = new BigDecimal(log1);
        BigDecimal db2 = new BigDecimal(log2);
        BigDecimal result = db1.divide(db2);
        return Double.parseDouble(pointdf.format(result.doubleValue()));
    }
    
    
    /**
     * 
     * @Description 生成一个min和max区间的随机的double类型的数字,
     * @param min
     * @param max
     * @return
     */
    public static Double generatorRandomDouble(double min,double max){
        Double result = random.doubles(min, max).findFirst().getAsDouble();
        return Double.parseDouble(pointdf.format(result));
    }
    
    /**
     * 
     * @Description double类型转long类型
     * @param b
     * @return
     */
    public static long formatDouble(Double b){
        String result = unpointdf.format(b);
        return Long.parseLong(result);
    }
}
