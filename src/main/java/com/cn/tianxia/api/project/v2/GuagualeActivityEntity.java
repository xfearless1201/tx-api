package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * @ClassName GuagualeActivityEntity
 * @Description 刮刮乐活动表实体类
 * @author Hardy
 * @Date 2019年3月13日 上午11:46:42
 * @version 1.0.0
 */
public class GuagualeActivityEntity implements Serializable {

    private static final long serialVersionUID = 2065410558874477696L;

    private Long id;

    private Long activityId;

    private String title;

    private String description;

    private Long minquota;

    private Long maxquota;

    private Long userMoney;

    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Long getMinquota() {
        return minquota;
    }

    public void setMinquota(Long minquota) {
        this.minquota = minquota;
    }

    public Long getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(Long maxquota) {
        this.maxquota = maxquota;
    }

    public Long getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(Long userMoney) {
        this.userMoney = userMoney;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
