/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.exception 
 *
 *    Filename:    HttpClientException.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Horus 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年01月20日 14:00 
 *
 *    Revision: 
 *
 *    2019/1/20 14:00 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.exception;

/**
 *  * @ClassName HttpClientException
 *  * @Description 自定义发起HttpClient抛出的异常
 *  * @Author Horus
 *  * @Date 2019年01月20日 14:00
 *  * @Version 1.0.0
 *  
 **/
public class HttpClientException extends RuntimeException{

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
