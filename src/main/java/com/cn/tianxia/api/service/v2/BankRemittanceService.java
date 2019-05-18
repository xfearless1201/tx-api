package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.BankRemittanceVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName BankRemittanceServince
 * @Description 银行汇款接口
 * @author Hardy
 * @Date 2019年1月5日 上午10:48:39
 * @version 1.0.0
 */
public interface BankRemittanceService {

    /**
     * 
     * @Description 创建汇款订单
     * @return
     * @throws Exception
     */
    public JSONObject createRemittanceOrder(BankRemittanceVO bankRemittanceVO) throws Exception;
    
    public JSONArray getRemittanceBankInfo(String uid) throws Exception;
}
