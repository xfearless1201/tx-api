/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.tx.platform.exception 
 *
 *    Filename:    ServiceException.java 
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
 *    Create at:   2019年01月14日 19:43 
 *
 *    Revision: 
 *
 *    2019/1/14 19:43 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.exception;

/**
 *  * @ClassName ServiceException
 *  * @Description TODO(这里用一句话描述这个类的作用)
 *  * @Author Horus
 *  * @Date 2019年01月14日 19:43
 *  * @Version 1.0.0
 *  
 **/
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
