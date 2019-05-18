package com.cn.tianxia.api.utils.jfk;

import java.io.Serializable;

/**
 * 
 * @project  ob-util
 * @description http请求处理结果
 * @author xiuhong.wang
 * @create time 2012-3-7 下午2:32:43
 * @modify time 2012-3-7 下午2:32:43
 * @modify comment 
 * @version
 */
public class HttpSendResult implements Serializable{
	  
	/**  
	*  
	* @since Ver 1.1  
	*/  
	
	private static final long serialVersionUID = 1L;
	private int status;
	private String responseBody;

	public HttpSendResult() {
		status = -1;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

}
