package com.cn.tianxia.api.utils.pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @ClassName MapUtils
 * @Description Map工具类写
 * @author Hardy
 * @Date 2018年10月18日 上午10:39:52
 * @version 1.0.0
 */
public class MapUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(MapUtils.class);

    /**
     * 
     * @Description 根据key排序
     * @param data
     * @return
     */
    public static Map<String,String> sortByKeys(Map<String,String> data) throws Exception{
        try {
            if(data == null || data.isEmpty()) {
                throw new Exception("待排序map不能为空!");
            }
            
            Map<String,String> treemap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            treemap.putAll(data);
            return treemap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("map排序异常:"+e.getMessage());
            throw new Exception("map排序异常!");
        }
    }
    
    /**
     * 
     * @Description 排序
     * @param data
     * @return
     * @throws Exception
     */
    public static Map<String,Object> sortMapByKeys(Map<String,Object> data) throws Exception{
        try {
            if(data == null || data.isEmpty()) {
                throw new Exception("待排序map不能为空!");
            }
            Map<String,Object> treemap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            treemap.putAll(data);
            return treemap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("map排序异常:"+e.getMessage());
            throw new Exception("map排序异常!");
        }
    }
    
    /**
     * 
     * @Description 判断map不为空
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean isNotEmpty(Map<String,String> data){
        if(data == null || data.isEmpty()){
            return false;
        }
        return true;
    }
}
