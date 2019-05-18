package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.TreasureRecordVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/2/1 10:57
 * @Description: 资金流水记录查询Service
 */
public interface TreasureRecordService {
    JSONObject findAllByPage(TreasureRecordVO treasureRecordVO);
}
