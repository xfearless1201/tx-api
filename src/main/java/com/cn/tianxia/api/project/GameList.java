package com.cn.tianxia.api.project;

import java.io.Serializable;

/**
 * platform_games_list
 * @author 
 */
public class GameList implements Serializable {
    private Long id;

    /**
     * 游戏编码
     */
    private String game_id;

    /**
     * 游戏英文名称名称
     */
    private String game_name_en;

    /**
     * 游戏中文名称
     */
    private String game_name_cn;

    /**
     * 图片路径
     */
    private String src;

    /**
     * 游戏类型
     */
    private String game_type;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getGame_name_en() {
        return game_name_en;
    }

    public void setGame_name_en(String game_name_en) {
        this.game_name_en = game_name_en;
    }

    public String getGame_name_cn() {
        return game_name_cn;
    }

    public void setGame_name_cn(String game_name_cn) {
        this.game_name_cn = game_name_cn;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }
}