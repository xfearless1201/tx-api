package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName WithdrawEntity
 * @Description 提款表实体类
 * @author Hardy
 * @Date 2019年1月29日 下午3:28:33
 * @version 1.0.0
 */
public class WithdrawEntity implements Serializable{

    private static final long serialVersionUID = 5751256977272175525L;

    private Integer id;

    private Integer cid;

    private Integer uid;

    private String billno;

    private Float amount;

    private String status;

    private Date addTime;

    private String username;

    private String bankname;

    private String cardno;

    private Integer totaltimes;

    private Integer todaytimes;

    private Float poundage;

    private Float administrativeFee;

    private Float amountPaid;

    private Integer vuid;

    private Date vtime;

    private String rmk;

    private String remark;

    private Double markingQuantity;

    private Double userQuantity;

    private String issuedName;

    private Date issuedTime;

    private String issuedPayerId;

    private Integer issuedStatus;

    private String payerResult;

    private String issuedIp;

    private String cardAddress;

    private String cagent;

    private String phoneno;

    private String zjno;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno == null ? null : billno.trim();
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname == null ? null : bankname.trim();
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno == null ? null : cardno.trim();
    }

    public Integer getTotaltimes() {
        return totaltimes;
    }

    public void setTotaltimes(Integer totaltimes) {
        this.totaltimes = totaltimes;
    }

    public Integer getTodaytimes() {
        return todaytimes;
    }

    public void setTodaytimes(Integer todaytimes) {
        this.todaytimes = todaytimes;
    }

    public Float getPoundage() {
        return poundage;
    }

    public void setPoundage(Float poundage) {
        this.poundage = poundage;
    }

    public Float getAdministrativeFee() {
        return administrativeFee;
    }

    public void setAdministrativeFee(Float administrativeFee) {
        this.administrativeFee = administrativeFee;
    }

    public Float getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Float amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getVuid() {
        return vuid;
    }

    public void setVuid(Integer vuid) {
        this.vuid = vuid;
    }

    public Date getVtime() {
        return vtime;
    }

    public void setVtime(Date vtime) {
        this.vtime = vtime;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Double getMarkingQuantity() {
        return markingQuantity;
    }

    public void setMarkingQuantity(Double markingQuantity) {
        this.markingQuantity = markingQuantity;
    }

    public Double getUserQuantity() {
        return userQuantity;
    }

    public void setUserQuantity(Double userQuantity) {
        this.userQuantity = userQuantity;
    }

    public String getIssuedName() {
        return issuedName;
    }

    public void setIssuedName(String issuedName) {
        this.issuedName = issuedName == null ? null : issuedName.trim();
    }

    public Date getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(Date issuedTime) {
        this.issuedTime = issuedTime;
    }

    public String getIssuedPayerId() {
        return issuedPayerId;
    }

    public void setIssuedPayerId(String issuedPayerId) {
        this.issuedPayerId = issuedPayerId == null ? null : issuedPayerId.trim();
    }

    public Integer getIssuedStatus() {
        return issuedStatus;
    }

    public void setIssuedStatus(Integer issuedStatus) {
        this.issuedStatus = issuedStatus;
    }

    public String getPayerResult() {
        return payerResult;
    }

    public void setPayerResult(String payerResult) {
        this.payerResult = payerResult == null ? null : payerResult.trim();
    }

    public String getIssuedIp() {
        return issuedIp;
    }

    public void setIssuedIp(String issuedIp) {
        this.issuedIp = issuedIp == null ? null : issuedIp.trim();
    }

    public String getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(String cardAddress) {
        this.cardAddress = cardAddress;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent == null ? null : cagent.trim();
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno == null ? null : phoneno.trim();
    }

    public String getZjno() {
        return zjno;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WithdrawEntity{");
        sb.append("id=").append(id);
        sb.append(", cid=").append(cid);
        sb.append(", uid=").append(uid);
        sb.append(", billno='").append(billno).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", status='").append(status).append('\'');
        sb.append(", addTime=").append(addTime);
        sb.append(", username='").append(username).append('\'');
        sb.append(", bankname='").append(bankname).append('\'');
        sb.append(", cardno='").append(cardno).append('\'');
        sb.append(", totaltimes=").append(totaltimes);
        sb.append(", todaytimes=").append(todaytimes);
        sb.append(", poundage=").append(poundage);
        sb.append(", administrativeFee=").append(administrativeFee);
        sb.append(", amountPaid=").append(amountPaid);
        sb.append(", vuid=").append(vuid);
        sb.append(", vtime=").append(vtime);
        sb.append(", rmk='").append(rmk).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", markingQuantity=").append(markingQuantity);
        sb.append(", userQuantity=").append(userQuantity);
        sb.append(", issuedName='").append(issuedName).append('\'');
        sb.append(", issuedTime=").append(issuedTime);
        sb.append(", issuedPayerId='").append(issuedPayerId).append('\'');
        sb.append(", issuedStatus=").append(issuedStatus);
        sb.append(", payerResult='").append(payerResult).append('\'');
        sb.append(", issuedIp='").append(issuedIp).append('\'');
        sb.append(", cardAddress='").append(cardAddress).append('\'');
        sb.append(", cagent='").append(cagent).append('\'');
        sb.append(", phoneno='").append(phoneno).append('\'');
        sb.append(", zjno='").append(zjno).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public void setZjno(String zjno) {
        this.zjno = zjno == null ? null : zjno.trim();
    }


}
