package com.cn.tianxia.api.utils;

import java.util.Map;

public class PlatFromConfig {
	
	private String platform_key;
	private String platform_name;
	private String platform_config;
	private String platform_status;
	
	public void InitData(Map<String, String> pmap,String KEY){   
		if(KEY==null||KEY==""){
			this.platform_status="0";
			return;
		}else if("IGLOTTO".equals(KEY)||"IGLOTTERY".equals(KEY)){
			KEY="IG";
		}else if("AGBY".equals(KEY)){
			KEY="AGIN";
		}else if("IGPJLOTTO".equals(KEY)||"IGPJLOTTERY".equals(KEY)){
			KEY="IGPJ";
		}else if("YOPLAY".equals(KEY)){
			KEY="AGIN";
		}else if("TASSPTA".equals(KEY)){
			KEY="AGIN";
		}
		if(!pmap.containsKey(KEY)){
			this.platform_status="0";
			return;
		}else{ 
			this.platform_key=KEY;
			this.platform_config=pmap.get(KEY);
			this.platform_status="1";
		} 
	}

	public String getPlatform_key() {
		return platform_key;
	}

	public void setPlatform_key(String platform_key) {
		this.platform_key = platform_key;
	}

	public String getPlatform_name() {
		return platform_name;
	}

	public void setPlatform_name(String platform_name) {
		this.platform_name = platform_name;
	}

	public String getPlatform_config() {
		return platform_config;
	}

	public void setPlatform_config(String platform_config) {
		this.platform_config = platform_config;
	}

	public String getPlatform_status() {
		return platform_status;
	}

	public void setPlatform_status(String platform_status) {
		this.platform_status = platform_status;
	}   

}
