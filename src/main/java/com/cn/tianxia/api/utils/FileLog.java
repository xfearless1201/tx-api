package com.cn.tianxia.api.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FileLog {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
	SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
	public void setLog(String type,Map<String,String> map){
		Properties pro = new Properties();
		InputStream in;
		try {
			in = this.getClass().getResourceAsStream("conf/file.properties");
			pro.load(in); 
			
			String savePath =pro.getProperty("savePath");
			File file = new File(savePath+type); 
			if (!file.exists() && !file.isDirectory()) {
	   			//System.out.println(file.getName() + "目录不存在，需要创建");
	   				file.mkdirs();
			}
			
			Date now = new Date();
			map.put("time",sdfnow.format(now));
			map.put("ErrorFrom",type);
			Calendar Cal=Calendar.getInstance();   
			Cal.setTime(now);   
			Cal.add(Calendar.HOUR_OF_DAY,-7);   
			String todayStr=sdf.format(Cal.getTime());     
			
			File f = new File(savePath+type+File.separator+todayStr+".txt");
			if(!f.exists()){ 
	     	   f.createNewFile();
	        }
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f, true),"UTF-8");
            BufferedWriter o = new BufferedWriter(osw);
            JSONObject jo = JSONObject.parseObject(JSON.toJSONString(map));
            o.newLine();
            o.write(jo.toString());
  	   		o.newLine();
  	   		o.close(); 
  	   		osw.close();
  	   		
		} catch (Exception e) { 
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileLog f=new FileLog(); 
		Map<String,String> map =new HashMap<>();
		map.put("ip", "192.168.0.5");
		map.put("username", "linbaba");
		f.setLog("HG", map);
	}

}
