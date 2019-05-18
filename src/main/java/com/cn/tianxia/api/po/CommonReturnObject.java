package com.cn.tianxia.api.po;

import com.cn.tianxia.api.error.OriginalError;

/**
 * @Auther: zed
 * @Date: 2019/3/3 10:25
 * @Description: 通用的返回结果
 */
public class CommonReturnObject {
    private Integer code;

    private String message;

    private Object data;

    private String status;

    private String msg;

    private String errmsg;

    //定义一个通用的创建方法
    public static CommonReturnObject success() {
        CommonReturnObject object = new CommonReturnObject();
        object.setCode(0);
        object.setMessage("success");
        return object;
    }


    public static CommonReturnObject success(Object data) {
        CommonReturnObject object = new CommonReturnObject();
        object.setCode(0);
        object.setData(data);
        object.setMessage("success");
        return object;
    }

    public static CommonReturnObject error(Integer code, String message) {
        CommonReturnObject object = new CommonReturnObject();
        object.setCode(code);
        object.setMsg("error");
        object.setErrmsg("未知异常,请联系客服");
        object.setStatus("error");
        object.setMessage(message);
        return object;
    }

    public static CommonReturnObject error(OriginalError error) {
        CommonReturnObject object = new CommonReturnObject();
        object.setCode(Integer.valueOf(error.getCode()));
        object.setMessage(error.getErrMsg());
        object.setStatus(error.getStatus());
        object.setMsg(error.getMsg());
        object.setErrmsg(error.getOriErrMsg());
        return object;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
