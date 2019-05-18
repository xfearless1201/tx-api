package com.cn.tianxia.api.utils;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap; 
  
/** 
 * JDBC 的工具类 
 *  
 * 其中包含: 获取数据库连接, 关闭数据库资源等方法. 
 */  
public class JDBCTools {   
	
    public static Connection getConnection() throws Exception {  
        Properties properties = new Properties();  
        InputStream inStream = JDBCTools.class.getClassLoader()  
                .getResourceAsStream("jdbc.properties");  
        properties.load(inStream);   
        // 1. 准备获取连接的 4 个字符串: user, password, url, jdbcDriver  
        String user = properties.getProperty("username");  
        String password = properties.getProperty("password");  
        String url= properties.getProperty("url");  
        String jdbcDriver= properties.getProperty("driver");  
  
        // 2. 加载驱动: Class.forName(driverClass)  
        Class.forName(jdbcDriver);  
  
        // 3.获取数据库连接  
        Connection connection = DriverManager.getConnection(url, user,  
                password);  
        return connection;  
    }  
  
    public static void releaseDB(ResultSet resultSet, PreparedStatement statement,  PreparedStatement statement1,  
            Connection connection) {  
  
        if (resultSet != null) {  
            try {  
                resultSet.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
  
        if (statement != null) {  
            try {  
                statement.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
        
        if (statement1 != null) {  
            try {  
                statement1.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
  
        if (connection != null) {  
            try {  
                connection.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    public static List<Map<String, Object>> convertList(ResultSet rs) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
        }
    }
        return list;
    }
    
    public static Map<String, Object> convertMap(ResultSet rs){
	        Map<String, Object> map = new TreeMap<String, Object>();
	        try{
	            ResultSetMetaData md = rs.getMetaData();
	            int columnCount = md.getColumnCount();
	            while (rs.next()) {
	                for (int i = 1; i <= columnCount; i++) {
	                    map.put(md.getColumnName(i), rs.getObject(i));
	                }
	            }
	        } catch (SQLException e){
	            e.printStackTrace();
	        } finally {
	            try {
	                if (rs != null)
	                rs.close();
	                rs = null;
	            } catch (SQLException e) {
	                e.printStackTrace();
	        }
	        return map;
	    }
    }

  
}  