package com.cn.tianxia.api.common.v2;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.cn.tianxia.api.error.BusinessErrorEnum;
import com.cn.tianxia.api.error.BusinessException;
import com.cn.tianxia.api.error.OriginalError;

/**
 * @Auther: zed
 * @Date: 2019/3/3 16:14
 * @Description: 参数验证工具类
 */
public class ParamValidate {

    /**
     * 验证不能为空的参数
     */
    public static <T> void notNull(T object, String message) throws BusinessException {
        if (!ObjectUtils.allNotNull(object)) {
            throw new BusinessException(OriginalError.getError(BusinessErrorEnum.PARAMMETER_VALIDATION_ERROR, message));
        }
        if (object instanceof String && StringUtils.isBlank((String) object)) {
            throw new BusinessException(OriginalError.getError(BusinessErrorEnum.PARAMMETER_VALIDATION_ERROR, message));
        }
    }

}
