package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;

public class UserTypeHandicapEntity implements Serializable {

    private static final long serialVersionUID = 4655153543569756775L;

    private Integer id;

    private Integer typeId;

    private String game;

    private String handicap;

    private String odds;

    private Date utime;

    private String upsn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game == null ? null : game.trim();
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap == null ? null : handicap.trim();
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds == null ? null : odds.trim();
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    public String getUpsn() {
        return upsn;
    }

    public void setUpsn(String upsn) {
        this.upsn = upsn == null ? null : upsn.trim();
    }
}