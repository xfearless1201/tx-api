package com.cn.tianxia.api.vo.v2;

/**
 * @Auther: zed
 * @Date: 2019/1/25 15:00
 * @Description: 添加银行卡VO
 */
public class AddBankCardVO {
    private String uid;  //
    private String cardUserName; //开户人
    private String bankCode; //银行编码
    private String cardNum;  //卡号
    private String cardAddress;  //地址
    private String password;  //取款密码

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCardUserName() {
        return cardUserName;
    }

    public void setCardUserName(String cardUserName) {
        this.cardUserName = cardUserName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(String cardAddress) {
        this.cardAddress = cardAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
