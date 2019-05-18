package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @ClassName WithdrawRecordVO
 * @Description 提现记录VO类
 * @author Hardy
 * @Date 2019年1月31日 下午10:18:51
 * @version 1.0.0
 */
public class WithdrawRecordVO implements Serializable {

    private static final long serialVersionUID = -1477315937732401555L;

    // 用户ID
    private String uid;
    private String status;
    // 分页页码
    private Integer pageNo;
    // 分页条数
    private Integer pageSize;
    // 开始时间
    private String bdate;
    // 结束时间
    private String edate;

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

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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

    @Override
    public String toString() {
        return "WithdrawRecordVO [uid=" + uid + ", status=" + status + ", pageNo=" + pageNo + ", pageSize=" + pageSize
                + ", bdate=" + bdate + ", edate=" + edate + "]";
    }

}
