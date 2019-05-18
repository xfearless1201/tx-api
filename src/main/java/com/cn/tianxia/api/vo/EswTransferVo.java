/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.service.vo 
 *
 *    Filename:    EswTransferVo.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Administrator 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年01月07日 17:14 
 *
 *    Revision: 
 *
 *    2019/1/7 17:14 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.vo;

/**
 *  * @ClassName EswTransferVo
 *  * @Description 上分下分VO
 *  * @Author Administrator
 *  * @Date 2019年01月07日 17:14
 *  * @Version 1.0.0
 *  
 **/
public class EswTransferVo {

    //用户
    private String userCode;
    //上/下分数
    private String money;
    //订单号 生成规则：agentId+yyyyMMddHHmmssSSS+userCode
    private String orderId;
    //上分时：乙方信用网平台用户的上级编号，用户在乙方平台跳转到甲方游戏平台时所携带
    //的字段，信用网封盘时对该代理商下属于该 parentCode 的用户进行重置分数时使
    //用（注：信用网必须传此参数）
    private String parentCode;

    public EswTransferVo() {}

    public EswTransferVo(String userCode, String money) {
        this.userCode = userCode;
        this.money = money;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}
