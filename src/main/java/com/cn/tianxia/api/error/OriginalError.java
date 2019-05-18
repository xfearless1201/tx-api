package com.cn.tianxia.api.error;

/**
 * @Auther: zed
 * @Date: 2019/3/13 21:30
 * @Description: 封装老代码错误接口
 */
public class OriginalError implements CommonError{

    private CommonError commonError;

    private String code;
    private String msg;
    private String oriErrMsg;
    private String status;

    public OriginalError(CommonError commonError) {
        this.commonError = commonError;
    }

    public OriginalError(CommonError commonError, String code, String msg) {
        this.commonError = commonError;
        this.code = code;
        this.msg = msg;
        this.oriErrMsg = commonError.getErrMsg();
        this.status = "error";
    }

    public OriginalError(CommonError commonError, String code, String msg, String oriErrMsg, String status) {
        this.commonError = commonError;
        this.code = code;
        this.msg = msg;
        this.oriErrMsg = oriErrMsg;
        this.status = status;
    }

    public static OriginalError getError(CommonError commonError) {
        OriginalError error = new OriginalError(commonError);
        error.status = "error";
        error.code = "1001";
        error.msg = commonError.getErrMsg();
        error.oriErrMsg = commonError.getErrMsg();
        return error;
    }

    public static OriginalError getError(CommonError commonError, String msg) {
        OriginalError error = getError(commonError);
        error.msg = msg;
        return error;
    }

    public static OriginalError getError(CommonError commonError,String code,String msg) {
        OriginalError error = getError(commonError);
        error.code = code;
        error.msg = msg;
        return error;
    }

    public static OriginalError getError(CommonError commonError,String code, String msg, String oriErrMsg) {
        OriginalError error = getError(commonError);
        error.code = code;
        error.msg = msg;
        error.oriErrMsg = oriErrMsg;
        return error;
    }

    public static OriginalError getFailed(CommonError commonError, String code, String msg) {
        OriginalError error = getError(commonError);
        error.status = "faild";
        error.code = code;
        error.msg = msg;
        return error;
    }


    public CommonError getCommonError() {
        return commonError;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getOriErrMsg() {
        return oriErrMsg;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int getErrCode() {
        return 0;
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg() ;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        return null;
    }
}
