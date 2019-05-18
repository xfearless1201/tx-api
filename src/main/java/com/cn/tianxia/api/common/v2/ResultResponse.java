package com.cn.tianxia.api.common.v2;

/**
 * @ClassName ResultResponse
 * @Description 封装返回结果
 * @author Hardy
 * @Date 2019年3月11日 下午9:52:05
 * @version 1.0.0
 */
public class ResultResponse {

    private String code;// 接口响应状态值 0 成功 1 失败 2 异常 3 处理中

    private String message;// 接口响应描述信息

    private Object data;// 接口响应结果集

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResultResponse(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ResultResponse(String code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }
 
    public static ResultResponse success(String message,Object data){
        return new ResultResponse(ResponseCode.SUCCESS_CODE, message, data);
    }

    public static ResultResponse faild(String message){
        return new ResultResponse(ResponseCode.FAILD_CODE, message);
    }
    
    public static ResultResponse error(String message){
        return new ResultResponse(ResponseCode.ERROR_CODE, message);
    }
    
    public static ResultResponse process(String message){
        return new ResultResponse(ResponseCode.PROCESS_CODE, message);
    }
}
