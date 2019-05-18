package com.cn.tianxia.api.utils.ipseeker;

public class IPEntry {

    public String beginIp;
    public String endIp;
    public String country;
    public String area;

    public IPEntry() {
        this.beginIp = (this.endIp = this.country = this.area = "");
    }

    public String toString() {
        return this.area + "  " + this.country + "IP范围:" + this.beginIp + "-" + this.endIp;
    }
}
