package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * 
 * @ClassName DepositRecordVO
 * @Description 存款记录VO类
 * @author Hardy
 * @Date 2019年1月31日 下午9:38:29
 * @version 1.0.0
 */
public class DepositRecordVO implements Serializable{

    private static final long serialVersionUID = 8587231933101887041L;

    private String uid;
    private String status;
    private String type;
    private String bdate;
    private String edate;
    private Integer pageSize;
    private Integer pageNo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
