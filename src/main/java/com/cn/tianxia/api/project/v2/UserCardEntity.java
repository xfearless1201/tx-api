package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName UserCardEntity
 * @Description 会员银行卡信息表实体类
 * @author Hardy
 * @Date 2019年1月29日 下午3:45:33
 * @version 1.0.0
 */
public class UserCardEntity implements Serializable{
   
    private static final long serialVersionUID = -8844294881491236955L;

    private Integer id;

    private Integer uid;

    private String cardUsername;

    private Integer bankId;

    private String cardNum;

    private String cardAddress;
    
    private String bankName;

    private Date addTime;

    private String isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getCardUsername() {
        return cardUsername;
    }

    public void setCardUsername(String cardUsername) {
        this.cardUsername = cardUsername == null ? null : cardUsername.trim();
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum == null ? null : cardNum.trim();
    }

    public String getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(String cardAddress) {
        this.cardAddress = cardAddress == null ? null : cardAddress.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }

    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
}