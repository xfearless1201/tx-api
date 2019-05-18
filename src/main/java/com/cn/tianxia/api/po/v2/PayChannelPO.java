package com.cn.tianxia.api.po.v2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PayChannelPO
 * @Description 支付渠道PO类
 * @author Hardy
 * @Date 2019年3月12日 下午12:24:43
 * @version 1.0.0
 */
public class PayChannelPO implements Serializable {

    private static final long serialVersionUID = -5689827975744703239L;

    private List<String> PCchannel = new ArrayList<>();

    private List<String> MBchannel = new ArrayList<>();

    public List<String> getPCchannel() {
        return PCchannel;
    }

    public void setPCchannel(List<String> pCchannel) {
        PCchannel = pCchannel;
    }

    public List<String> getMBchannel() {
        return MBchannel;
    }

    public void setMBchannel(List<String> mBchannel) {
        MBchannel = mBchannel;
    }

    @Override
    public String toString() {
        return "PayChannelPO [PCchannel=" + PCchannel + ", MBchannel=" + MBchannel + "]";
    }

}
