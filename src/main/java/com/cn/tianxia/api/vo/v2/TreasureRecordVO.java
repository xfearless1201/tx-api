package com.cn.tianxia.api.vo.v2;

import java.io.Serializable;

/**
 * @Auther: zed
 * @Date: 2019/2/1 10:53
 * @Description: 资金流水记录VO
 */
public class TreasureRecordVO implements Serializable {

    private static final long serialVersionUID = 8587231933101887041L;

    private String uid;
    private String type;
    private String startTime;
    private String endTime;
    private Integer pageSize;
    private Integer pageNo;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
