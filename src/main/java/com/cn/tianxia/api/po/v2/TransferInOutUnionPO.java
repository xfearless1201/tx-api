/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.tx.platform.transfer.po 
 *
 *    Filename:    TransferInOutUnionPO.java 
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
 *    Create at:   2019年01月17日 9:40 
 *
 *    Revision: 
 *
 *    2019/1/17 9:40 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.po.v2;

import java.io.Serializable;

/**
 *  * @ClassName TransferInOutUnionPO
 *  * @Description 数据返回
 *  * @Author Horus
 *  * @Date 2019年01月17日 9:40
 *  * @Version 1.0.0
 *  
 **/
public class TransferInOutUnionPO implements Serializable{
    
    private static final long serialVersionUID = -8343534381340139145L;
    private String cagentId;
    private String cagent;
//    private String typeId;//用户类型
    private String handicap;//用户盘口
    private Double wallet;//用户余额

    public String getCagentId() {
        return cagentId;
    }

    public void setCagentId(String cagentId) {
        this.cagentId = cagentId;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return "TransferInOutUnionPO{" +
                "cagentId='" + cagentId + '\'' +
                ", cagent='" + cagent + '\'' +
                ", handicap='" + handicap + '\'' +
                ", wallet=" + wallet +
                '}';
    }
}
