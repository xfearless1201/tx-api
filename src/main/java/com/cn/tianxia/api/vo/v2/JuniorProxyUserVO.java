package com.cn.tianxia.api.vo.v2;

/**
 * @Auther: zed
 * @Date: 2019/2/25 15:46
 * @Description: 二级代理用户VO
 */
public class JuniorProxyUserVO {

    private String upId;

    private Long pid;

    private Integer dUserType;

    public String getUpId() {
        return upId;
    }

    public void setUpId(String upId) {
        this.upId = upId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getdUserType() {
        return dUserType;
    }

    public void setdUserType(Integer dUserType) {
        this.dUserType = dUserType;
    }
}
