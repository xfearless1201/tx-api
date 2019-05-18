package com.cn.tianxia.api.enums.v2;

/**
 * @ClassName: PayTypeEnum
 * @Description: 扫码支付类型枚举
 * @Author: Zed
 * @Date: 2019-01-02 16:39
 * @Version:1.0.0
 **/

public enum PayTypeEnum {
    wy(1,"wy","网银"),
    wx(2,"wx","微信"),
    ali(3,"ali","支付宝"),
    cft(4,"cft","财付通"),
    yl(5,"yl","银联"),
    jd(6,"jd","京东"),
    kj(7,"kj","快捷"),
    wxtm(8,"wxtm","微信条码"),
    alitm(9,"alitm","支付宝条码"),
    ;
    private int code;
    private String type;
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    PayTypeEnum(int code, String type, String desc) {
        this.code = code;
        this.type = type;
        this.desc = desc;
    }


    @Override
    public String toString() {
        return "PayTypeEnum{" +
                "code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
