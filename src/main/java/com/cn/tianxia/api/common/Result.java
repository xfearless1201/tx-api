package com.cn.tianxia.api.common;

import java.io.Serializable;

/**
 * @description：操作结果集
 * @author：
 * @date：2015/10/1 14:51
 */
public class Result implements Serializable {

    public static final int SUCCESS = 1;
    public static final int FAILURE = -1;

    private static final long serialVersionUID = 5576237395711742681L;
 
    private String status="error";
    
    private String msg = "";

    private Object obj = null; 

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
