package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @ClassName PasswordVO
 * @Description 密码VO类
 * @author Hardy
 * @Date 2019年2月1日 下午6:15:41
 * @version 1.0.0
 */
public class PasswordVO implements Serializable {

    private static final long serialVersionUID = -3165443052617910843L;

    private String uid;// 用户ID

    private String password;// 原始密码

    private String npassword;// 新密码

    private String renpassword;// 确认密码

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNpassword() {
        return npassword;
    }

    public void setNpassword(String npassword) {
        this.npassword = npassword;
    }

    public String getRenpassword() {
        return renpassword;
    }

    public void setRenpassword(String renpassword) {
        this.renpassword = renpassword;
    }

    @Override
    public String toString() {
        return "PasswordVO [uid=" + uid + ", password=" + password + ", npassword=" + npassword + ", renpassword="
                + renpassword + "]";
    }

}
