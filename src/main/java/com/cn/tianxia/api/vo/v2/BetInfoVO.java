package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @ClassName BetInfoVO
 * @Description 注单请求参数VO类
 * @author Hardy
 * @Date 2019年1月30日 下午8:42:09
 * @version 1.0.0
 */
public class BetInfoVO implements Serializable {

    private static final long serialVersionUID = -7951639169272049116L;

    private String uid;//用户ID
    
    private String type;// 平台编码
    
    private String bdate;// 起始时间

    private String edate;// 结束时间

    private int pageSize = 30;// 每页条数,默认为30条

    private int pageNo = 0;// 页数,默认为0

    
    public String getUid() {
        return uid;
    }

    
    public void setUid(String uid) {
        this.uid = uid;
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

    
    public int getPageSize() {
        return pageSize;
    }

    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    
    public int getPageNo() {
        return pageNo;
    }

    
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }


    @Override
    public String toString() {
        return "BetInfoVO [uid=" + uid + ", type=" + type + ", bdate=" + bdate + ", edate=" + edate + ", pageSize="
                + pageSize + ", pageNo=" + pageNo + "]";
    }

    

}
