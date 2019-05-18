package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName TransferVO
 * @Description 转账VO类
 * @author Hardy
 * @Date 2018年11月6日 下午10:06:21
 * @version 1.0.0
 */
public class TransferVO implements Serializable {

    private static final long serialVersionUID = 4342268087645226327L;

    private String uid;// 用户ID

    private String terminal;// pc端,mobile手机端

    private String cid;// 平台ID

    private Double money;// 订单金额

    private Double oldMoney;

    private Double newMoney;

    private String account;// 登录账号

    private String orderNo;// 订单号

    private int type;// 操作类型 0 创建用户 1查询余额 2上分 3 下分 4登录

    private String password;// 登录密码

    private int platGameStatus;// 游戏开关状态

    private int userGameStatus;// 用户游戏状态 0 为注册过游戏 1 已存在游戏账号

    private Map<String, String> platGameConfig;// 游戏配置信息

    private String platformMap;// 平台请求map参数

    private String gamePlat;// 游戏平台编码

    private String cagent;// 平台

    private String ip;// ip地址

    private String isGameType;

    private Integer enterChannel;// 进入渠道 0PC 1 移动端

    private String gameId;// 游戏ID

    private String handicap;// 平台游戏盘口

    private String inOrOut;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getOldMoney() {
        return oldMoney;
    }

    public void setOldMoney(Double oldMoney) {
        this.oldMoney = oldMoney;
    }

    public Double getNewMoney() {
        return newMoney;
    }

    public void setNewMoney(Double newMoney) {
        this.newMoney = newMoney;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPlatGameStatus() {
        return platGameStatus;
    }

    public void setPlatGameStatus(int platGameStatus) {
        this.platGameStatus = platGameStatus;
    }

    public int getUserGameStatus() {
        return userGameStatus;
    }

    public void setUserGameStatus(int userGameStatus) {
        this.userGameStatus = userGameStatus;
    }

    public Map<String, String> getPlatGameConfig() {
        return platGameConfig;
    }

    public void setPlatGameConfig(Map<String, String> platGameConfig) {
        this.platGameConfig = platGameConfig;
    }

    public String getPlatformMap() {
        return platformMap;
    }

    public void setPlatformMap(String platformMap) {
        this.platformMap = platformMap;
    }

    public String getGamePlat() {
        return gamePlat;
    }

    public void setGamePlat(String gamePlat) {
        this.gamePlat = gamePlat;
    }

    public String getCagent() {
        return cagent;
    }

    public void setCagent(String cagent) {
        this.cagent = cagent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIsGameType() {
        return isGameType;
    }

    public void setIsGameType(String isGameType) {
        this.isGameType = isGameType;
    }

    public Integer getEnterChannel() {
        return enterChannel;
    }

    public void setEnterChannel(Integer enterChannel) {
        this.enterChannel = enterChannel;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }

    @Override
    public String toString() {
        return "TransferVO{" +
                "uid='" + uid + '\'' +
                ", terminal='" + terminal + '\'' +
                ", cid='" + cid + '\'' +
                ", money=" + money +
                ", oldMoney=" + oldMoney +
                ", newMoney=" + newMoney +
                ", account='" + account + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", type=" + type +
                ", password='" + password + '\'' +
                ", platGameStatus=" + platGameStatus +
                ", userGameStatus=" + userGameStatus +
                ", platGameConfig=" + platGameConfig +
                ", platformMap='" + platformMap + '\'' +
                ", gamePlat='" + gamePlat + '\'' +
                ", cagent='" + cagent + '\'' +
                ", ip='" + ip + '\'' +
                ", isGameType='" + isGameType + '\'' +
                ", enterChannel=" + enterChannel +
                ", gameId='" + gameId + '\'' +
                ", handicap='" + handicap + '\'' +
                ", inOrOut='" + inOrOut + '\'' +
                '}';
    }
}
