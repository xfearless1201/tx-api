package com.cn.tianxia.api.project.v2;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName CagnetYsepayEntity
 * @Description `t_cagent_ysepay` 在线支付配置实体类
 * @author Zed
 */
public class CagnetYsepayEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    private Integer id;

    private Integer cid;

    private String pathParterPkcs12;

    private String passwordParterPkcs12;

    private String pathYsepayPublicCert;

    private String rsaAlgorithm;

    private String signAlgorithm;

    private String defaultCharset;

    private String partnerId;

    private String sellerId;

    private String sellerName;

    private String version;

    private String ysepayGetwayUrl;

    private String ysepayGetwayUrlDf;

    private String notifyUrl;

    private String retrunUrl;

    private Date updatetime;

    private Integer uid;

    private String type;

    private Double minquota;

    private Double maxquota;

    private Double qrmaxquota;

    private Double qrminquota;

    private Double dayquota;

    private Float dividendRate;

    private Float codingRate;

    private Boolean status;

    private String rmk;

    private String channel;

    private String payUrl;

    private String paymentName;

    private String paymentConfig;

    private Double aliMinquota;

    private Double aliMaxquota;

    private Double wxMinquota;

    private Double wxMaxquota;

    private Integer caIndex;

    private Integer isIssued;

    private Double ylMaxquota;

    private Double ylMinquota;

    private Double jdMaxquota;

    private Double jdMinquota;

    private Double kjMaxquota;

    private Double kjMinquota;

    private Integer ish5Ali;

    private Integer ish5Wx;

    private Integer ish5Cft;

    private Integer ish5Jd;

    private Integer ish5Yl;

    private Double wxtmMinquota;

    private Double wxtmMaxquota;

    private Double alitmMinquota;

    private Double alitmMaxquota;

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

    public String getPathParterPkcs12() {
        return pathParterPkcs12;
    }

    public void setPathParterPkcs12(String pathParterPkcs12) {
        this.pathParterPkcs12 = pathParterPkcs12 == null ? null : pathParterPkcs12.trim();
    }

    public String getPasswordParterPkcs12() {
        return passwordParterPkcs12;
    }

    public void setPasswordParterPkcs12(String passwordParterPkcs12) {
        this.passwordParterPkcs12 = passwordParterPkcs12 == null ? null : passwordParterPkcs12.trim();
    }

    public String getPathYsepayPublicCert() {
        return pathYsepayPublicCert;
    }

    public void setPathYsepayPublicCert(String pathYsepayPublicCert) {
        this.pathYsepayPublicCert = pathYsepayPublicCert == null ? null : pathYsepayPublicCert.trim();
    }

    public String getRsaAlgorithm() {
        return rsaAlgorithm;
    }

    public void setRsaAlgorithm(String rsaAlgorithm) {
        this.rsaAlgorithm = rsaAlgorithm == null ? null : rsaAlgorithm.trim();
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm == null ? null : signAlgorithm.trim();
    }

    public String getDefaultCharset() {
        return defaultCharset;
    }

    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset == null ? null : defaultCharset.trim();
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId == null ? null : partnerId.trim();
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName == null ? null : sellerName.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getYsepayGetwayUrl() {
        return ysepayGetwayUrl;
    }

    public void setYsepayGetwayUrl(String ysepayGetwayUrl) {
        this.ysepayGetwayUrl = ysepayGetwayUrl == null ? null : ysepayGetwayUrl.trim();
    }

    public String getYsepayGetwayUrlDf() {
        return ysepayGetwayUrlDf;
    }

    public void setYsepayGetwayUrlDf(String ysepayGetwayUrlDf) {
        this.ysepayGetwayUrlDf = ysepayGetwayUrlDf == null ? null : ysepayGetwayUrlDf.trim();
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl == null ? null : notifyUrl.trim();
    }

    public String getRetrunUrl() {
        return retrunUrl;
    }

    public void setRetrunUrl(String retrunUrl) {
        this.retrunUrl = retrunUrl == null ? null : retrunUrl.trim();
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Double getMinquota() {
        return minquota;
    }

    public void setMinquota(Double minquota) {
        this.minquota = minquota;
    }

    public Double getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(Double maxquota) {
        this.maxquota = maxquota;
    }

    public Double getQrmaxquota() {
        return qrmaxquota;
    }

    public void setQrmaxquota(Double qrmaxquota) {
        this.qrmaxquota = qrmaxquota;
    }

    public Double getQrminquota() {
        return qrminquota;
    }

    public void setQrminquota(Double qrminquota) {
        this.qrminquota = qrminquota;
    }

    public Double getDayquota() {
        return dayquota;
    }

    public void setDayquota(Double dayquota) {
        this.dayquota = dayquota;
    }

    public Float getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(Float dividendRate) {
        this.dividendRate = dividendRate;
    }

    public Float getCodingRate() {
        return codingRate;
    }

    public void setCodingRate(Float codingRate) {
        this.codingRate = codingRate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl == null ? null : payUrl.trim();
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName == null ? null : paymentName.trim();
    }

    public String getPaymentConfig() {
        return paymentConfig;
    }

    public void setPaymentConfig(String paymentConfig) {
        this.paymentConfig = paymentConfig == null ? null : paymentConfig.trim();
    }

    public Double getAliMinquota() {
        return aliMinquota;
    }

    public void setAliMinquota(Double aliMinquota) {
        this.aliMinquota = aliMinquota;
    }

    public Double getAliMaxquota() {
        return aliMaxquota;
    }

    public void setAliMaxquota(Double aliMaxquota) {
        this.aliMaxquota = aliMaxquota;
    }

    public Double getWxMinquota() {
        return wxMinquota;
    }

    public void setWxMinquota(Double wxMinquota) {
        this.wxMinquota = wxMinquota;
    }

    public Double getWxMaxquota() {
        return wxMaxquota;
    }

    public void setWxMaxquota(Double wxMaxquota) {
        this.wxMaxquota = wxMaxquota;
    }

    public Integer getCaIndex() {
        return caIndex;
    }

    public void setCaIndex(Integer caIndex) {
        this.caIndex = caIndex;
    }

    public Integer getIsIssued() {
        return isIssued;
    }

    public void setIsIssued(Integer isIssued) {
        this.isIssued = isIssued;
    }

    public Double getYlMaxquota() {
        return ylMaxquota;
    }

    public void setYlMaxquota(Double ylMaxquota) {
        this.ylMaxquota = ylMaxquota;
    }

    public Double getYlMinquota() {
        return ylMinquota;
    }

    public void setYlMinquota(Double ylMinquota) {
        this.ylMinquota = ylMinquota;
    }

    public Double getJdMaxquota() {
        return jdMaxquota;
    }

    public void setJdMaxquota(Double jdMaxquota) {
        this.jdMaxquota = jdMaxquota;
    }

    public Double getJdMinquota() {
        return jdMinquota;
    }

    public void setJdMinquota(Double jdMinquota) {
        this.jdMinquota = jdMinquota;
    }

    public Double getKjMaxquota() {
        return kjMaxquota;
    }

    public void setKjMaxquota(Double kjMaxquota) {
        this.kjMaxquota = kjMaxquota;
    }

    public Double getKjMinquota() {
        return kjMinquota;
    }

    public void setKjMinquota(Double kjMinquota) {
        this.kjMinquota = kjMinquota;
    }

    public Integer getIsh5Ali() {
        return ish5Ali;
    }

    public void setIsh5Ali(Integer ish5Ali) {
        this.ish5Ali = ish5Ali;
    }

    public Integer getIsh5Wx() {
        return ish5Wx;
    }

    public void setIsh5Wx(Integer ish5Wx) {
        this.ish5Wx = ish5Wx;
    }

    public Integer getIsh5Cft() {
        return ish5Cft;
    }

    public void setIsh5Cft(Integer ish5Cft) {
        this.ish5Cft = ish5Cft;
    }

    public Integer getIsh5Jd() {
        return ish5Jd;
    }

    public void setIsh5Jd(Integer ish5Jd) {
        this.ish5Jd = ish5Jd;
    }

    public Integer getIsh5Yl() {
        return ish5Yl;
    }

    public void setIsh5Yl(Integer ish5Yl) {
        this.ish5Yl = ish5Yl;
    }

    public Double getWxtmMinquota() {
        return wxtmMinquota;
    }

    public void setWxtmMinquota(Double wxtmMinquota) {
        this.wxtmMinquota = wxtmMinquota;
    }

    public Double getWxtmMaxquota() {
        return wxtmMaxquota;
    }

    public void setWxtmMaxquota(Double wxtmMaxquota) {
        this.wxtmMaxquota = wxtmMaxquota;
    }

    public Double getAlitmMinquota() {
        return alitmMinquota;
    }

    public void setAlitmMinquota(Double alitmMinquota) {
        this.alitmMinquota = alitmMinquota;
    }

    public Double getAlitmMaxquota() {
        return alitmMaxquota;
    }

    public void setAlitmMaxquota(Double alitmMaxquota) {
        this.alitmMaxquota = alitmMaxquota;
    }
}