package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * 
 * @ClassName PlatformStatusEntity
 * @Description 平台游戏开关配置表实体类
 * @author Hardy
 * @Date 2019年2月7日 下午3:09:48
 * @version 1.0.0
 */
public class PlatformStatusEntity implements Serializable{
    
    private static final long serialVersionUID = 323200296033824773L;

    private Integer id;

    private Integer cid;

    private String domain;

    private Byte ag;

    private Byte aghsr;

    private Byte bbin;

    private Byte bbingame;

    private Byte cg;

    private Byte ds;

    private Byte ggby;

    private Byte haba;

    private Byte hg;

    private Byte ig;

    private Byte mggame;

    private Byte ob;

    private Byte og;

    private Byte pt;

    private Byte shenbo;

    private Byte shenbogame;

    private Byte shenboMd;

    private Byte shenbogameMd;

    private Byte bgvideo;

    private Byte bglottery;

    private Byte igpj;

    private Byte vr;

    private Byte jf;

    private Byte yoplay;

    private Byte kyqp;

    private Byte tasspta;

    private Byte vgqp;

    private Byte vg;

    private Byte gy;

    private Byte ps;

    private Byte nb;

    private Byte lyqp;

    private Byte jdb;

    private Byte sw;

    private Byte ibc;

    private Byte esw;

    private Byte cqj;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain == null ? null : domain.trim();
    }

    public Byte getAg() {
        return ag;
    }

    public void setAg(Byte ag) {
        this.ag = ag;
    }

    public Byte getAghsr() {
        return aghsr;
    }

    public void setAghsr(Byte aghsr) {
        this.aghsr = aghsr;
    }

    public Byte getBbin() {
        return bbin;
    }

    public void setBbin(Byte bbin) {
        this.bbin = bbin;
    }

    public Byte getBbingame() {
        return bbingame;
    }

    public void setBbingame(Byte bbingame) {
        this.bbingame = bbingame;
    }

    public Byte getCg() {
        return cg;
    }

    public void setCg(Byte cg) {
        this.cg = cg;
    }

    public Byte getDs() {
        return ds;
    }

    public void setDs(Byte ds) {
        this.ds = ds;
    }

    public Byte getGgby() {
        return ggby;
    }

    public void setGgby(Byte ggby) {
        this.ggby = ggby;
    }

    public Byte getHaba() {
        return haba;
    }

    public void setHaba(Byte haba) {
        this.haba = haba;
    }

    public Byte getHg() {
        return hg;
    }

    public void setHg(Byte hg) {
        this.hg = hg;
    }

    public Byte getIg() {
        return ig;
    }

    public void setIg(Byte ig) {
        this.ig = ig;
    }

    public Byte getMggame() {
        return mggame;
    }

    public void setMggame(Byte mggame) {
        this.mggame = mggame;
    }

    public Byte getOb() {
        return ob;
    }

    public void setOb(Byte ob) {
        this.ob = ob;
    }

    public Byte getOg() {
        return og;
    }

    public void setOg(Byte og) {
        this.og = og;
    }

    public Byte getPt() {
        return pt;
    }

    public void setPt(Byte pt) {
        this.pt = pt;
    }

    public Byte getShenbo() {
        return shenbo;
    }

    public void setShenbo(Byte shenbo) {
        this.shenbo = shenbo;
    }

    public Byte getShenbogame() {
        return shenbogame;
    }

    public void setShenbogame(Byte shenbogame) {
        this.shenbogame = shenbogame;
    }

    public Byte getShenboMd() {
        return shenboMd;
    }

    public void setShenboMd(Byte shenboMd) {
        this.shenboMd = shenboMd;
    }

    public Byte getShenbogameMd() {
        return shenbogameMd;
    }

    public void setShenbogameMd(Byte shenbogameMd) {
        this.shenbogameMd = shenbogameMd;
    }

    public Byte getBgvideo() {
        return bgvideo;
    }

    public void setBgvideo(Byte bgvideo) {
        this.bgvideo = bgvideo;
    }

    public Byte getBglottery() {
        return bglottery;
    }

    public void setBglottery(Byte bglottery) {
        this.bglottery = bglottery;
    }

    public Byte getIgpj() {
        return igpj;
    }

    public void setIgpj(Byte igpj) {
        this.igpj = igpj;
    }

    public Byte getVr() {
        return vr;
    }

    public void setVr(Byte vr) {
        this.vr = vr;
    }

    public Byte getJf() {
        return jf;
    }

    public void setJf(Byte jf) {
        this.jf = jf;
    }

    public Byte getYoplay() {
        return yoplay;
    }

    public void setYoplay(Byte yoplay) {
        this.yoplay = yoplay;
    }

    public Byte getKyqp() {
        return kyqp;
    }

    public void setKyqp(Byte kyqp) {
        this.kyqp = kyqp;
    }

    public Byte getTasspta() {
        return tasspta;
    }

    public void setTasspta(Byte tasspta) {
        this.tasspta = tasspta;
    }

    public Byte getVgqp() {
        return vgqp;
    }

    public void setVgqp(Byte vgqp) {
        this.vgqp = vgqp;
    }

    public Byte getVg() {
        return vg;
    }

    public void setVg(Byte vg) {
        this.vg = vg;
    }

    public Byte getGy() {
        return gy;
    }

    public void setGy(Byte gy) {
        this.gy = gy;
    }

    public Byte getPs() {
        return ps;
    }

    public void setPs(Byte ps) {
        this.ps = ps;
    }

    public Byte getNb() {
        return nb;
    }

    public void setNb(Byte nb) {
        this.nb = nb;
    }

    public Byte getLyqp() {
        return lyqp;
    }

    public void setLyqp(Byte lyqp) {
        this.lyqp = lyqp;
    }

    public Byte getJdb() {
        return jdb;
    }

    public void setJdb(Byte jdb) {
        this.jdb = jdb;
    }

    public Byte getSw() {
        return sw;
    }

    public void setSw(Byte sw) {
        this.sw = sw;
    }

    public Byte getIbc() {
        return ibc;
    }

    public void setIbc(Byte ibc) {
        this.ibc = ibc;
    }

    public Byte getEsw() {
        return esw;
    }

    public void setEsw(Byte esw) {
        this.esw = esw;
    }

    public Byte getCqj() {
        return cqj;
    }

    public void setCqj(Byte cqj) {
        this.cqj = cqj;
    }
}