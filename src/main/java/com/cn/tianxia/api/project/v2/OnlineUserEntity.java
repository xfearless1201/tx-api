package com.cn.tianxia.api.project.v2;

public class OnlineUserEntity {

    private Long id;

    private Long uid;

    private String cagent;

    private Integer cid;

    private String address;

    private String ip;

    private Long loginTime;

    private String token;

    private String refurl;

    private Byte isMobile;

    private Long logoutTime;

    private Byte offStatus;

    private Byte isOff;

    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefurl() {
        return refurl;
    }

    public void setRefurl(String refurl) {
        this.refurl = refurl;
    }

    public Byte getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(Byte isMobile) {
        this.isMobile = isMobile;
    }

    public Long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Byte getOffStatus() {
        return offStatus;
    }

    public void setOffStatus(Byte offStatus) {
        this.offStatus = offStatus;
    }

    public Byte getIsOff() {
        return isOff;
    }

    public void setIsOff(Byte isOff) {
        this.isOff = isOff;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "OnlineUserEntity [id=" + id + ", uid=" + uid + ", cagent=" + cagent + ", cid=" + cid + ", address="
                + address + ", ip=" + ip + ", loginTime=" + loginTime + ", token=" + token + ", refurl=" + refurl
                + ", isMobile=" + isMobile + ", logoutTime=" + logoutTime + ", offStatus=" + offStatus + ", isOff="
                + isOff + ", count=" + count + "]";
    }

}
