package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.BankPayVO;
import com.cn.tianxia.api.vo.ScanPayVO;

import net.sf.json.JSONObject;

/**
 * @ClassName: PlatformPayService
 * @Description: 支付服务接口
 * @Author: Zed
 * @Date: 2019-01-02 13:59
 * @Version:1.0.0
 **/

public interface PlatformPayService {

    JSONObject bankPay(BankPayVO bankPayVO);

    JSONObject scanPay(ScanPayVO scanPayVO);
}
