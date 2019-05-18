package com.cn.tianxia.api.vo.v2;

/**
 * @Auther: zed
 * @Date: 2019/2/25 15:21
 * @Description: 一级代理用户VO
 */
public class ProxyUserVO {

    private Integer dUserType;

    private Long pid;

    public Integer getdUserType() {
        return dUserType;
    }

    public void setdUserType(Integer dUserType) {
        this.dUserType = dUserType;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}
