package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.TransferRecordVO;

import net.sf.json.JSONArray;

/**
 * 
 * @ClassName TransferRecordService
 * @Description 转账记录接口
 * @author Hardy
 * @Date 2019年2月1日 上午10:27:32
 * @version 1.0.0
 */
public interface TransferRecordService {

    public JSONArray getTransferInfo(TransferRecordVO transferRecordVO);
}
