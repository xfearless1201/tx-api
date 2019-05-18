package com.cn.tianxia.api.error;

/**
 * @Auther: zed
 * @Date: 2019/3/3 09:53
 * @Description: 定义错误基类接口
 */
public interface CommonError {
    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
