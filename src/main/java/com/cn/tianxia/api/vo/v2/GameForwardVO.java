package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName GameForwardVO
 * @Description 游戏跳转VO类
 * @author Hardy
 * @Date 2019年2月9日 下午4:43:58
 * @version 1.0.0
 */
public class GameForwardVO implements Serializable {

    private static final long serialVersionUID = -8884651862672782138L;

    private String uid;// 用户名

    private String username;// 用户名

    private String ag_username;// 游戏登录账号

    private String hg_username;// 皇冠账号

    private String password;// 游戏登录密码

    private String gameId;// 游戏ID

    private String ip;// ip地址

    private String handicap;// 盘口

    private String type;// 游戏类型

    private String sid;// 账号

    private String model;// 是否为手机端

    private JSONObject config;

    private String refererUrl;//来源域名

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "GameForwardVO{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", ag_username='" + ag_username + '\'' +
                ", hg_username='" + hg_username + '\'' +
                ", password='" + password + '\'' +
                ", gameId='" + gameId + '\'' +
                ", ip='" + ip + '\'' +
                ", handicap='" + handicap + '\'' +
                ", type='" + type + '\'' +
                ", sid='" + sid + '\'' +
                ", model='" + model + '\'' +
                ", refererUrl='" + refererUrl + '\'' +
                '}';
    }
}
