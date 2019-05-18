package com.cn.tianxia.api.enums.v2;

/**
 * @Auther: zed
 * @Date: 2019/3/4 15:58
 * @Description: 游戏编码枚举
 */
public enum GameTypeEnum {

    AG("AG","ag"),
    AGIN("AGIN","ag"),
    BBIN("BBIN","bbin"),
    BBIN1("BBIN","bbin"),
    BBIN2("BBIN","bbinGame"),
    CG("CG","cg"),
    DS("DS","ds"),
    GGBY("GGBY","ggby"),
    HABA("HABA","haba"),
    HG("HG","hg"),
    IG("IG","ig"),
    IGLOTTERY("IG","ig"),
    IGLOTTO("IG","ig"),
    IGPJ("IGPJ","igpj"),
    IGPJLOTTERY("IGPJ","igpj"),
    IGPJLOTTO("IGPJ","igpj"),
    MG("MG","mgGame"),
    AGBY("AGIN","aghsr"),
    OB("OB","ob"),
    OG("OG","og"),
    PT("PT","pt"),
    SB1("SB","shenbo"),
    SB2("SB","shenboGame"),
    SB("SB","IF((shenbo=0 )OR(shenboGame=0) ,\"0\",\"1\")"),
    BG("BG","bgVideo"),
    VR("VR","vr"),
    YOPLAY("AGIN","yoplay"),
    TASSPTA("AGIN","tasspta"),
    VG("VG","vgqp"),
    GY("GY","gy"),
    NB("NB","nb"),
    PS("PS","ps"),
    KYQP("KYQP","kyqp"),
    LYQP("LYQP","lyqp"),
    JDB("JDB","jdb"),
    SW("SW","sw"),
    IBC("IBC","ibc"),
    ESW("ESW","esw"),
    CQJ("CQJ","cqj"),
    ;

    private String gameType;
    private String gameCode;

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    GameTypeEnum(String gameType, String gameCode) {
        this.gameType = gameType;
        this.gameCode = gameCode;
    }
}
