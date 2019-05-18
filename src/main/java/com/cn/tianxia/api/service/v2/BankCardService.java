package com.cn.tianxia.api.service.v2;

import com.cn.tianxia.api.vo.v2.AddBankCardVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/1/25 15:17
 * @Description:
 */
public interface BankCardService {
    JSONObject addUserCard(AddBankCardVO addBankCardVO);
    JSONObject delUserCard(String uid, String cardId, String password);
    JSONArray getUserCard(String uid);
}
