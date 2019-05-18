package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.DepositRecordVO;

import net.sf.json.JSONArray;

/**
 * 
 * @ClassName DepositRecordService
 * @Description 存款接口
 * @author Hardy
 * @Date 2019年1月31日 下午9:37:14
 * @version 1.0.0
 */
public interface DepositRecordService {

    JSONArray findAllByPage(DepositRecordVO depositRecordVO);
}
