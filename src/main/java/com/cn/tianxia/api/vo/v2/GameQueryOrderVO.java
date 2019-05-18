package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @ClassName GameQueryOrderVO
 * @Description 游戏查询订单VO类
 * @author Hardy
 * @Date 2019年2月9日 下午4:46:59
 * @version 1.0.0
 */
public class GameQueryOrderVO implements Serializable {

    private static final long serialVersionUID = 872597294685111991L;

    private String uid;// 用户ID

    private String gamename;// 游戏登录账号

    private String password;// 游戏登录密码

    private String billno;// 订单号

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGamename() {
        return gamename;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

}
