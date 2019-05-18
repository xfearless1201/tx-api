package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.OfflineScanQrCodeVO;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName OfflineScanPayService
 * @Description 线下扫码支付接口
 * @author Hardy
 * @Date 2019年1月11日 下午2:54:17
 * @version 1.0.0
 */
public interface OfflineScanPayService {

    /**
     * 
     * @Description 获取线下扫码二维码
     * @param uid 用户ID
     * @param type 扫码支付类型:1 支付宝 2 微信 3 财付通
     * @return
     * @throws Exception
     */
    JSONObject getOfflineScanQrCode(String uid,String type);
    
    
    /**
     * 
     * @Description 创建线下扫码二维码订单
     * @param offlineScanQrCodeVO
     * @return
     */
	JSONObject addOfflineQrCodeOrderRecord(OfflineScanQrCodeVO offlineScanQrCodeVO);
}
