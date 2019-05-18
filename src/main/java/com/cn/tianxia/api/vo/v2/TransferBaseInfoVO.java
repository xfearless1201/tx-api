package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @Auther: zed
 * @Date: 2019/2/26 20:14
 * @Description: 转账信息基础VO类
 */
public class TransferBaseInfoVO implements Serializable {

    private static final long serialVersionUID = 5642135487645221237L;

    /**
     * 用户id
     */
    private String uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 平台id cid
     */
    private String cid;
    /**
     * 平台号
     */
    private String cagent;
    /**
     * 游戏登录账号
     */
    private String ag_username;
    /**
     * 皇冠体育
     */
    private String hg_username;
    /**
     * 游戏登录密码
     */
    private String ag_password;
    /**
     * 转账金额
     */
    private String credit;
    /**
     * 游戏平台编码
     */
    private String gameType;
    /**
     * ip
     */
    private String ip;
    /**
     * 转账类型
     */
    private int transferType;

    /**
     * 游戏id
     */
    private String gameId;

    /**
     * model
     * @return
     */
    private String model;

    /**
     * 来源url
     */
    private String refererUrl;

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAg_username() {
        return ag_username;
    }

    public void setAg_username(String ag_username) {
        this.ag_username = ag_username;
    }

    public String getHg_username() {
        return hg_username;
    }

    public void setHg_username(String hg_username) {
        this.hg_username = hg_username;
    }

    public String getAg_password() {
        return ag_password;
    }

    public void setAg_password(String ag_password) {
        this.ag_password = ag_password;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "TransferBaseInfoVO{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", cid='" + cid + '\'' +
                ", cagent='" + cagent + '\'' +
                ", ag_username='" + ag_username + '\'' +
                ", hg_username='" + hg_username + '\'' +
                ", ag_password='" + ag_password + '\'' +
                ", credit='" + credit + '\'' +
                ", gameType='" + gameType + '\'' +
                ", ip='" + ip + '\'' +
                ", transferType=" + transferType +
                ", gameId='" + gameId + '\'' +
                ", model='" + model + '\'' +
                ", refererUrl='" + refererUrl + '\'' +
                '}';
    }
}
