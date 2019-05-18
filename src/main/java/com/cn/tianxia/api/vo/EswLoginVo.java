/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下科技 
 *    http://www.d-telemedia.com/ 
 *
 *    Package:     com.cn.tianxia.service.vo 
 *
 *    Filename:    EswLoginVo.java
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下科技 
 *
 *    @author:     Administrator 
 *
 *    @version:    1.0.0 
 *
 *    Create at:   2019年01月07日 9:13 
 *
 *    Revision: 
 *
 *    2019/1/7 9:13 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.vo;

/**
 *  * @ClassName EswLoginVo
 *  * @Description 德胜棋牌登录VO
 *  * @Author Administrator
 *  * @Date 2019年01月07日 9:13
 *  * @Version 1.0.0
 *  
 **/
public class EswLoginVo {

    //必传参数
    private String userCode;//用户
    private String ip;//客户端请求 IP
    private Integer gameId;//游戏 ID
    private Integer ac = 1;//

    private String nickName;//
    private String money;//
    private String orderId;//
    private String lang = "zh-CN";//游戏语言，默认值（zh-CN） String 否
    private Integer isLandscape;//闪屏界面及大厅界面是否横屏显示 1 为横屏，0 为竖屏，默认为 1
    private Integer isFullScreen;//是否全屏显示游戏界面 1 为全屏，0 为不全屏，默认是 0
    private Integer loadType;//游戏加载方式，分为预加载及后加载两种，预加载会先把资源下载完成后，才进入游戏；后加载则边进游戏边加载资源。 1 为预加载，0 为后加载，默认是 1
    private String homeUrl;//主页地址
    private Integer isShowLobby;//是否需要大厅界面 1 为显示，0 为不显示，默认 1
    private String parentCode;//乙方信用网平台用户的上级编号
    private Integer popGameLst;//是否需要显示游戏列表弹窗，1 为显示，0 为不显示，默认显示；isShowLobby 为0 时，配置才生效；isShowLobby 为 1 时，不显示游戏列表弹窗

    public EswLoginVo(String userCode, String ip, Integer gameId) {
        this.userCode = userCode;
        this.ip = ip;
        this.gameId = gameId;
    }

    public EswLoginVo() {
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getAc() {
        return ac;
    }

    public void setAc(Integer ac) {
        this.ac = ac;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getIsLandscape() {
        return isLandscape;
    }

    public void setIsLandscape(Integer isLandscape) {
        this.isLandscape = isLandscape;
    }

    public Integer getIsFullScreen() {
        return isFullScreen;
    }

    public void setIsFullScreen(Integer isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public Integer getLoadType() {
        return loadType;
    }

    public void setLoadType(Integer loadType) {
        this.loadType = loadType;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public Integer getIsShowLobby() {
        return isShowLobby;
    }

    public void setIsShowLobby(Integer isShowLobby) {
        this.isShowLobby = isShowLobby;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getPopGameLst() {
        return popGameLst;
    }

    public void setPopGameLst(Integer popGameLst) {
        this.popGameLst = popGameLst;
    }
}
