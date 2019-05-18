package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName PlatformConfigEntity
 * @Description 游戏平台配置表实体类
 * @author Hardy
 * @Date 2019年2月6日 上午11:46:07
 * @version 1.0.0
 */
public class PlatformConfigEntity implements Serializable{
    
    private static final long serialVersionUID = 3502061005811109278L;

    private Integer id;

    private String platformKey;

    private String platformName;

    private String platformConfig;

    private String platformStatus;

    private String tipName;

    private Date addTime;

    private Date updateTime;

    private Integer updateUid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlatformKey() {
        return platformKey;
    }

    public void setPlatformKey(String platformKey) {
        this.platformKey = platformKey == null ? null : platformKey.trim();
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName == null ? null : platformName.trim();
    }

    public String getPlatformConfig() {
        return platformConfig;
    }

    public void setPlatformConfig(String platformConfig) {
        this.platformConfig = platformConfig == null ? null : platformConfig.trim();
    }

    public String getPlatformStatus() {
        return platformStatus;
    }

    public void setPlatformStatus(String platformStatus) {
        this.platformStatus = platformStatus == null ? null : platformStatus.trim();
    }

    public String getTipName() {
        return tipName;
    }

    public void setTipName(String tipName) {
        this.tipName = tipName == null ? null : tipName.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUid() {
        return updateUid;
    }

    public void setUpdateUid(Integer updateUid) {
        this.updateUid = updateUid;
    }
}