package com.cn.tianxia.api.project.v2;

import java.io.Serializable;

/**
 * @ClassName GameBenInfoEntity
 * @Description 游戏注单记录实体类
 * @author Hardy
 * @Date 2019年1月31日 上午10:10:47
 * @version 1.0.0
 */
public class GameBetInfoEntity implements Serializable {

    private static final long serialVersionUID = 3469458743692348374L;

    private Integer id;
    private String bettime;
    private String type;
    private Double betAmount;// 投注金额
    private Double validBetAmount;// 有效投注额度

    private Double payout;// 派彩金额

    private Double netAmount;// 玩家输赢额度

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBettime() {
        return bettime;
    }

    public void setBettime(String bettime) {
        this.bettime = bettime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Double betAmount) {
        this.betAmount = betAmount;
    }

    public Double getValidBetAmount() {
        return validBetAmount;
    }

    public void setValidBetAmount(Double validBetAmount) {
        this.validBetAmount = validBetAmount;
    }

    public Double getPayout() {
        return payout;
    }

    public void setPayout(Double payout) {
        this.payout = payout;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    @Override
    public String toString() {
        return "GameBetInfoEntity [id=" + id + ", bettime=" + bettime + ", type=" + type + ", betAmount=" + betAmount
                + ", validBetAmount=" + validBetAmount + ", payout=" + payout + ", netAmount=" + netAmount + "]";
    }

}
