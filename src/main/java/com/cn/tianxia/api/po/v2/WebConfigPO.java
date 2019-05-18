package com.cn.tianxia.api.po.v2;

import java.io.Serializable;

/**
 * @ClassName WebConfigPO
 * @Description 网站配置PO类
 * @author Hardy
 * @Date 2019年3月14日 下午5:40:40
 * @version 1.0.0
 */
public class WebConfigPO implements Serializable {

    private static final long serialVersionUID = 2444756754455434403L;

    private Integer id;

    private String type;

    private String name;

    private String title;

    private Integer weight;

    private String status;

    private String img1;

    private String src1;

    private String img2;

    private String rmk;

    private String updatetime;

    private String img3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getSrc1() {
        return src1;
    }

    public void setSrc1(String src1) {
        this.src1 = src1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    @Override
    public String toString() {
        return "WebConfigPO [id=" + id + ", type=" + type + ", name=" + name + ", title=" + title + ", weight=" + weight
                + ", status=" + status + ", img1=" + img1 + ", src1=" + src1 + ", img2=" + img2 + ", rmk=" + rmk
                + ", updatetime=" + updatetime + ", img3=" + img3 + "]";
    }

}
