package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @ClassName TransferRecordVO
 * @Description 转账记录VO
 * @author Hardy
 * @Date 2019年2月1日 上午10:22:10
 * @version 1.0.0
 */
public class TransferRecordVO implements Serializable {

    private static final long serialVersionUID = 1294136112533399849L;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 游戏类型编码
     */
    private String Type;

    /**
     * 转入or转出(转账类型)
     */
    private String Ttype;
    /**
     * 分页条数
     */
    private Integer pageSize;
    /**
     * 分页页码
     */
    private Integer pageNo;
    /**
     * 起始时间
     */
    private String bdate;
    /**
     * 结束时间
     */
    private String edate;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTtype() {
        return Ttype;
    }

    public void setTtype(String ttype) {
        Ttype = ttype;
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
        return "TransferRecordVO [uid=" + uid + ", Type=" + Type + ", Ttype=" + Ttype + ", pageSize=" + pageSize
                + ", pageNo=" + pageNo + ", bdate=" + bdate + ", edate=" + edate + "]";
    }

}
