package com.cn.tianxia.api.web;  
  
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import net.sf.json.JSONObject;
 
  
@Controller
@RequestMapping("LoginMap") 
@Scope("prototype")
public class LoginMapControl  extends BaseController  {
	
//	@RequestMapping("/getUserList.do")  
//	@ResponseBody
	public JSONObject getUserList(String key) {  
		 Properties pro = new Properties();
			InputStream in; 
			String acckey ="";
		try {
			in = this.getClass().getResourceAsStream("conf/file.properties");
			pro.load(in);
			acckey =pro.getProperty("key");   
		} catch (Exception e) { 
			
		} 
		if(key==null||"".equals(key)||!key.equals(acckey)){
			return null;
		}
		JSONObject jo=JSONObject.fromObject(onlineMap); 
		return jo;
	}
	
//	@RequestMapping("/shotOff.do")  
//	@ResponseBody
	public String shotOff(String uid,String key) {  
		 Properties pro = new Properties();
			InputStream in; 
			String acckey ="";
		try {
			in = this.getClass().getResourceAsStream("conf/file.properties");
			pro.load(in);
			acckey =pro.getProperty("key");   
		} catch (Exception e) { 
			return "faild";
		} 
		if(key==null||"".equals(key)||!key.equals(acckey)){
			return "faild";
		}
		if(onlineMap.containsKey(uid)){
		    Map<String, String> onlinemap = onlineMap.get(uid);
		    onlinemap.put("uid", uid);
		    onlinemap.put("sessionid", "FFFFFFFFFF");
		    if(loginmaps.containsKey(uid)){
		        Map<String, String> loginmap=loginmaps.get(uid);
		        loginmap.put("uid", uid);
		        loginmap.put("sessionid", "FFFFFFFFFF");
		        loginmaps.put("uid", loginmap);
		    }
	        onlineMap.put(uid, onlinemap); 
		}
		return "success";
	} 
	
	@RequestMapping("/update.do")  
	@ResponseBody 
	public String test(String uid,String key,String sid)  {   
		 Properties pro = new Properties();
			InputStream in; 
			String acckey ="";
		try {
			in = this.getClass().getResourceAsStream("conf/file.properties");
			pro.load(in);
			acckey =pro.getProperty("key");   
		} catch (Exception e) { 
			return "faild";
		} 
		if(key==null||"".equals(key)||!key.equals(acckey)){
			return "faild";
		}
		if(onlineMap.containsKey(uid)){
		    if(loginmaps.containsKey(uid)){
		        Map<String, String> loginmap=loginmaps.get(uid);
	            loginmap.put("uid", uid);
	            loginmap.put("sessionid", sid);
	            loginmaps.put(uid, loginmap);
		    }
			Map<String, String> onlinemap=onlineMap.get(uid);
			onlinemap.put("uid", uid);
			onlinemap.put("sessionid", sid);
			onlineMap.put(uid, onlinemap);
		} 
		return "success";
	} 
	 
}  