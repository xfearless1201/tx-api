/**
 * HostedSoapStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cn.tianxia.api.ws;

public class HostedSoapStub extends org.apache.axis.client.Stub implements HostedSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[40];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBonusAvailablePlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusAvailablePlayerRequest"), BonusAvailablePlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfCouponInfoDTO"));
        oper.setReturnClass(CouponInfoDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBonusAvailablePlayerResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "CouponInfoDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ApplyBonusToPlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ApplyBonusToPlayerRequest"), ApplyBonusToPlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "CouponResponseMessage"));
        oper.setReturnClass(CouponResponseMessage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "couponresponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBonusBalancesForPlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusGenericPlayerRequest"), BonusGenericPlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfBonusBalancesDTO"));
        oper.setReturnClass(BonusBalancesDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBonusBalancesForPlayerResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusBalancesDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetPlayerBonusBalanceActive");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "SetBonusBalanceActiveRequest"), SetBonusBalanceActiveRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ToggleBonusBalanceResponse"));
        oper.setReturnClass(ToggleBonusBalanceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "SetPlayerBonusBalanceActiveResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeletePlayerBonusBalance");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "DeleteBonusBalanceRequest"), DeleteBonusBalanceRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ToggleBonusBalanceResponse"));
        oper.setReturnClass(ToggleBonusBalanceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "DeletePlayerBonusBalanceResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CreateAndApplyBonus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "CreateBonusAndApplyRequest"), CreateBonusAndApplyRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "CreateAndApplyBonusResponse"));
        oper.setReturnClass(CreateAndApplyBonusResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "CreateAndApplyBonusResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGameTypes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeRequest"), GameTypeRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeResponse"));
        oper.setReturnClass(GameTypeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGameTypesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameRequest"), GameRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameResponse"));
        oper.setReturnClass(GameResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGamesInMenuOnly");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameRequest"), GameRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameResponse"));
        oper.setReturnClass(GameResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGamesInMenuOnlyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGameDisplay");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameDisplayRequest"), GameDisplayRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameDisplayResponse"));
        oper.setReturnClass(GameDisplayResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGameDisplayResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetJackpots");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoRequest"), JackpotInfoRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotInfoDTO"));
        oper.setReturnClass(JackpotInfoDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetJackpotsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetJackpotGameLink");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoRequest"), JackpotInfoRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotGameLinkInfoDTO"));
        oper.setReturnClass(JackpotGameLinkInfoDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetJackpotGameLinkResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotGameLinkInfoDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllJackpotsInAllBrands");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoRequest"), JackpotInfoRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotInfoDTO"));
        oper.setReturnClass(JackpotInfoDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetAllJackpotsInAllBrandsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetJackpotGameLinkInAllBrands");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoRequest"), JackpotInfoRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotGameLinkInfoDTO"));
        oper.setReturnClass(JackpotGameLinkInfoDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetJackpotGameLinkInAllBrandsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotGameLinkInfoDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportPlayerStakePayout");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerStakePayoutDTO"));
        oper.setReturnClass(PlayerStakePayoutDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportPlayerStakePayoutResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerStakePayoutDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPlayerGameTransactions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerReportRequest"), PlayerReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameTransactionsDTO"));
        oper.setReturnClass(PlayerGameTransactionsDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerGameTransactionsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameTransactionsDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPlayerTransferTransactions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerReportRequest"), PlayerReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerTransferTransactionsDTO"));
        oper.setReturnClass(PlayerTransferTransactionsDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerTransferTransactionsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerTransferTransactionsDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBrandTransferTransactions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerTransferTransactionsDTO"));
        oper.setReturnClass(PlayerTransferTransactionsDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBrandTransferTransactionsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerTransferTransactionsDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGroupTransferTransactions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerTransferTransactionsDTO"));
        oper.setReturnClass(PlayerTransferTransactionsDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGroupTransferTransactionsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerTransferTransactionsDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPlayerGameResults");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerReportRequest"), PlayerReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameResultsDTO"));
        oper.setReturnClass(PlayerGameResultsDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerGameResultsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameResultsDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBrandGameResults");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameResultsDTO"));
        oper.setReturnClass(PlayerGameResultsDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBrandGameResultsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameResultsDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBrandCompletedGameResults");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerCompletedGamesDTO"));
        oper.setReturnClass(PlayerCompletedGamesDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBrandCompletedGameResultsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerCompletedGamesDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGroupCompletedGameResults");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerCompletedGamesDTO"));
        oper.setReturnClass(PlayerCompletedGamesDTO[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGroupCompletedGameResultsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerCompletedGamesDTO"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPlayerStakePayoutSummary");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerReportRequest"), PlayerReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerStakePayoutSummaryDTO"));
        oper.setReturnClass(PlayerStakePayoutSummaryDTO.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerStakePayoutSummaryResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportJackpotContribution");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotContributionRecord"));
        oper.setReturnClass(JackpotContributionRecord[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportJackpotContributionResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionRecord"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportJackpotContributionPerGame");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotContributionPerGameRecord"));
        oper.setReturnClass(JackpotContributionPerGameRecord[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportJackpotContributionPerGameResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionPerGameRecord"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportDynamic");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "DynamicReportRequest"), DynamicReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", ">>ReportDynamicResponse>ReportDynamicResult"));
        oper.setReturnClass(ReportDynamicResponseReportDynamicResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportDynamicResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportGameOverviewBrand");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameOverviewRecord"));
        oper.setReturnClass(GameOverviewRecord[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportGameOverviewBrandResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewRecord"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportGameOverviewPlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerReportRequest"), PlayerReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameOverviewRecord"));
        oper.setReturnClass(PlayerGameOverviewRecord[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportGameOverviewPlayerResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameOverviewRecord"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReportGameOverviewPerLocation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest"), ReportRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameOverviewPerLocationRecord"));
        oper.setReturnClass(GameOverviewPerLocationRecord[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportGameOverviewPerLocationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewPerLocationRecord"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdatePlayerPassword");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordRequest"), UpdatePlayerPasswordRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordResponse"));
        oper.setReturnClass(UpdatePlayerPasswordResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LoginOrCreatePlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "LoginOrCreatePlayerRequest"), LoginOrCreatePlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LoginUserResponse"));
        oper.setReturnClass(LoginUserResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LoginOrCreatePlayerResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryTransfer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryTransferRequest"), QueryTransferRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryTransferResponse"));
        oper.setReturnClass(QueryTransferResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryTransferResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryPlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryPlayerRequest"), QueryPlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryPlayerResponse"));
        oper.setReturnClass(QueryPlayerResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryPlayerResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LogoutPlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerRequest"), LogoutPlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerResponse"));
        oper.setReturnClass(LogoutPlayerResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LogoutThirdPartyPlayer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutThirdPartyPlayerRequest"), LogoutThirdPartyPlayerRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ThirdPartyPlayerLogoutResponse"));
        oper.setReturnClass(ThirdPartyPlayerLogoutResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutThirdPartyPlayerResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DepositPlayerMoney");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "DepositPlayerMoneyRequest"), DepositPlayerMoneyRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "MoneyResponse"));
        oper.setReturnClass(MoneyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "DepositPlayerMoneyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("WithdrawPlayerMoney");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "WithdrawPlayerMoneyRequest"), WithdrawPlayerMoneyRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "MoneyResponse"));
        oper.setReturnClass(MoneyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "WithdrawPlayerMoneyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LogoutAllPlayersInBrand");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutAllPlayersInBrandRequest"), LogoutAllPlayersInBrandRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutAllPlayersInBrandResponse"));
        oper.setReturnClass(LogoutAllPlayersInBrandResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutAllPlayersInBrandResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetMaintenanceMode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ws.oxypite.com/", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ws.oxypite.com/", "MaintenanceModeRequest"), MaintenanceModeRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "MaintenanceModeResponse"));
        oper.setReturnClass(MaintenanceModeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "SetMaintenanceModeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[39] = oper;

    }

    public HostedSoapStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public HostedSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public HostedSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
    }

    private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://microsoft.com/wsdl/types/", "guid");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">>ReportDynamicResponse>ReportDynamicResult");
            cachedSerQNames.add(qName);
            cls = ReportDynamicResponseReportDynamicResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">DepositPlayerMoney");
            cachedSerQNames.add(qName);
            cls = DepositPlayerMoney.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">DepositPlayerMoneyResponse");
            cachedSerQNames.add(qName);
            cls = DepositPlayerMoneyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LoginOrCreatePlayer");
            cachedSerQNames.add(qName);
            cls = LoginOrCreatePlayer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LoginOrCreatePlayerResponse");
            cachedSerQNames.add(qName);
            cls = LoginOrCreatePlayerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutAllPlayersInBrand");
            cachedSerQNames.add(qName);
            cls = LogoutAllPlayersInBrand.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutAllPlayersInBrandResponse");
            cachedSerQNames.add(qName);
            cls = LogoutAllPlayersInBrandResponseType3.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutPlayer");
            cachedSerQNames.add(qName);
            cls = LogoutPlayer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutPlayerResponse");
            cachedSerQNames.add(qName);
            cls = LogoutPlayerResponseType0.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutThirdPartyPlayer");
            cachedSerQNames.add(qName);
            cls = LogoutThirdPartyPlayer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutThirdPartyPlayerResponse");
            cachedSerQNames.add(qName);
            cls = LogoutThirdPartyPlayerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">QueryPlayer");
            cachedSerQNames.add(qName);
            cls = QueryPlayer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">QueryPlayerResponse");
            cachedSerQNames.add(qName);
            cls = QueryPlayerResponseType5.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">QueryTransfer");
            cachedSerQNames.add(qName);
            cls = QueryTransfer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">QueryTransferResponse");
            cachedSerQNames.add(qName);
            cls = QueryTransferResponseType2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">ReportGameOverviewBrand");
            cachedSerQNames.add(qName);
            cls = ReportGameOverviewBrand.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">ReportGameOverviewBrandResponse");
            cachedSerQNames.add(qName);
            cls = ReportGameOverviewBrandResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">ReportGameOverviewPerLocation");
            cachedSerQNames.add(qName);
            cls = ReportGameOverviewPerLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">ReportGameOverviewPerLocationResponse");
            cachedSerQNames.add(qName);
            cls = ReportGameOverviewPerLocationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">ReportGameOverviewPlayer");
            cachedSerQNames.add(qName);
            cls = ReportGameOverviewPlayer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">ReportGameOverviewPlayerResponse");
            cachedSerQNames.add(qName);
            cls = ReportGameOverviewPlayerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">SetMaintenanceMode");
            cachedSerQNames.add(qName);
            cls = SetMaintenanceMode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">SetMaintenanceModeResponse");
            cachedSerQNames.add(qName);
            cls = SetMaintenanceModeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">UpdatePlayerPassword");
            cachedSerQNames.add(qName);
            cls = UpdatePlayerPassword.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">UpdatePlayerPasswordResponse");
            cachedSerQNames.add(qName);
            cls = UpdatePlayerPasswordResponseType1.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">WithdrawPlayerMoney");
            cachedSerQNames.add(qName);
            cls = WithdrawPlayerMoney.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", ">WithdrawPlayerMoneyResponse");
            cachedSerQNames.add(qName);
            cls = WithdrawPlayerMoneyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ApplyBonusToPlayerRequest");
            cachedSerQNames.add(qName);
            cls = ApplyBonusToPlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfBonusBalancesDTO");
            cachedSerQNames.add(qName);
            cls = BonusBalancesDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusBalancesDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusBalancesDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfComplexDepositCouponInfo");
            cachedSerQNames.add(qName);
            cls = ComplexDepositCouponInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ComplexDepositCouponInfo");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ComplexDepositCouponInfo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfCouponInfoDTO");
            cachedSerQNames.add(qName);
            cls = CouponInfoDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "CouponInfoDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "CouponInfoDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfDisplayNode");
            cachedSerQNames.add(qName);
            cls = Game[][].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "DisplayNode");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "DisplayNode");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameClientDbDTO");
            cachedSerQNames.add(qName);
            cls = GameClientDbDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameClientDbDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameClientDbDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameOverviewPerLocationRecord");
            cachedSerQNames.add(qName);
            cls = GameOverviewPerLocationRecord[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewPerLocationRecord");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewPerLocationRecord");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameOverviewRecord");
            cachedSerQNames.add(qName);
            cls = GameOverviewRecord[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewRecord");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewRecord");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameTranslationDTO");
            cachedSerQNames.add(qName);
            cls = GameTranslationDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTranslationDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTranslationDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGameTypeClientDbDTO");
            cachedSerQNames.add(qName);
            cls = GameTypeClientDbDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeClientDbDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeClientDbDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfGuid");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://microsoft.com/wsdl/types/", "guid");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "guid");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotContributionPerGameRecord");
            cachedSerQNames.add(qName);
            cls = JackpotContributionPerGameRecord[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionPerGameRecord");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionPerGameRecord");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotContributionRecord");
            cachedSerQNames.add(qName);
            cls = JackpotContributionRecord[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionRecord");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionRecord");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotGameLinkInfoDTO");
            cachedSerQNames.add(qName);
            cls = JackpotGameLinkInfoDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotGameLinkInfoDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotGameLinkInfoDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotInfoDTO");
            cachedSerQNames.add(qName);
            cls = JackpotInfoDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfJackpotValueDTO");
            cachedSerQNames.add(qName);
            cls = JackpotValueDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotValueDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotValueDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerCompletedGamesDTO");
            cachedSerQNames.add(qName);
            cls = PlayerCompletedGamesDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerCompletedGamesDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerCompletedGamesDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameOverviewRecord");
            cachedSerQNames.add(qName);
            cls = PlayerGameOverviewRecord[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameOverviewRecord");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameOverviewRecord");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameResultsDTO");
            cachedSerQNames.add(qName);
            cls = PlayerGameResultsDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameResultsDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameResultsDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerGameTransactionsDTO");
            cachedSerQNames.add(qName);
            cls = PlayerGameTransactionsDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameTransactionsDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameTransactionsDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerStakePayoutDTO");
            cachedSerQNames.add(qName);
            cls = PlayerStakePayoutDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerStakePayoutDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerStakePayoutDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfPlayerTransferTransactionsDTO");
            cachedSerQNames.add(qName);
            cls = PlayerTransferTransactionsDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerTransferTransactionsDTO");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerTransferTransactionsDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "string");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BaseRequest");
            cachedSerQNames.add(qName);
            cls = BaseRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BaseResponse");
            cachedSerQNames.add(qName);
            cls = BaseResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusAvailablePlayerRequest");
            cachedSerQNames.add(qName);
            cls = BonusAvailablePlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusBalancesDTO");
            cachedSerQNames.add(qName);
            cls = BonusBalancesDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "BonusGenericPlayerRequest");
            cachedSerQNames.add(qName);
            cls = BonusGenericPlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ComplexDepositCouponInfo");
            cachedSerQNames.add(qName);
            cls = ComplexDepositCouponInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "CouponInfoDTO");
            cachedSerQNames.add(qName);
            cls = CouponInfoDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "CouponResponseMessage");
            cachedSerQNames.add(qName);
            cls = CouponResponseMessage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "CreateAndApplyBonusResponse");
            cachedSerQNames.add(qName);
            cls = CreateAndApplyBonusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "CreateBonusAndApplyRequest");
            cachedSerQNames.add(qName);
            cls = CreateBonusAndApplyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "DeleteBonusBalanceRequest");
            cachedSerQNames.add(qName);
            cls = DeleteBonusBalanceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "DepositPlayerMoneyRequest");
            cachedSerQNames.add(qName);
            cls = DepositPlayerMoneyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "DisplayNode");
            cachedSerQNames.add(qName);
            cls = Game[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "Game");
            qName2 = new javax.xml.namespace.QName("http://ws.oxypite.com/", "Game");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "DynamicReportRequest");
            cachedSerQNames.add(qName);
            cls = DynamicReportRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "Game");
            cachedSerQNames.add(qName);
            cls = Game.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameClientDbDTO");
            cachedSerQNames.add(qName);
            cls = GameClientDbDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameDisplayRequest");
            cachedSerQNames.add(qName);
            cls = GameDisplayRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameDisplayResponse");
            cachedSerQNames.add(qName);
            cls = GameDisplayResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewPerLocationRecord");
            cachedSerQNames.add(qName);
            cls = GameOverviewPerLocationRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameOverviewRecord");
            cachedSerQNames.add(qName);
            cls = GameOverviewRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameRequest");
            cachedSerQNames.add(qName);
            cls = GameRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameResponse");
            cachedSerQNames.add(qName);
            cls = GameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTranslationDTO");
            cachedSerQNames.add(qName);
            cls = GameTranslationDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeClientDbDTO");
            cachedSerQNames.add(qName);
            cls = GameTypeClientDbDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeRequest");
            cachedSerQNames.add(qName);
            cls = GameTypeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "GameTypeResponse");
            cachedSerQNames.add(qName);
            cls = GameTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "InfoMessage");
            cachedSerQNames.add(qName);
            cls = InfoMessage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionPerGameRecord");
            cachedSerQNames.add(qName);
            cls = JackpotContributionPerGameRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotContributionRecord");
            cachedSerQNames.add(qName);
            cls = JackpotContributionRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotGameLinkInfoDTO");
            cachedSerQNames.add(qName);
            cls = JackpotGameLinkInfoDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoDTO");
            cachedSerQNames.add(qName);
            cls = JackpotInfoDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotInfoRequest");
            cachedSerQNames.add(qName);
            cls = JackpotInfoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "JackpotValueDTO");
            cachedSerQNames.add(qName);
            cls = JackpotValueDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LoginOrCreatePlayerRequest");
            cachedSerQNames.add(qName);
            cls = LoginOrCreatePlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LoginUserResponse");
            cachedSerQNames.add(qName);
            cls = LoginUserResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutAllPlayersInBrandRequest");
            cachedSerQNames.add(qName);
            cls = LogoutAllPlayersInBrandRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutAllPlayersInBrandResponse");
            cachedSerQNames.add(qName);
            cls = LogoutAllPlayersInBrandResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerRequest");
            cachedSerQNames.add(qName);
            cls = LogoutPlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerResponse");
            cachedSerQNames.add(qName);
            cls = LogoutPlayerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutThirdPartyPlayerRequest");
            cachedSerQNames.add(qName);
            cls = LogoutThirdPartyPlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "MaintenanceModeRequest");
            cachedSerQNames.add(qName);
            cls = MaintenanceModeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "MaintenanceModeResponse");
            cachedSerQNames.add(qName);
            cls = MaintenanceModeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "MoneyResponse");
            cachedSerQNames.add(qName);
            cls = MoneyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerCompletedGamesDTO");
            cachedSerQNames.add(qName);
            cls = PlayerCompletedGamesDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameOverviewRecord");
            cachedSerQNames.add(qName);
            cls = PlayerGameOverviewRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameResultsDTO");
            cachedSerQNames.add(qName);
            cls = PlayerGameResultsDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerGameTransactionsDTO");
            cachedSerQNames.add(qName);
            cls = PlayerGameTransactionsDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerReportRequest");
            cachedSerQNames.add(qName);
            cls = PlayerReportRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerStakePayoutDTO");
            cachedSerQNames.add(qName);
            cls = PlayerStakePayoutDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerStakePayoutSummaryDTO");
            cachedSerQNames.add(qName);
            cls = PlayerStakePayoutSummaryDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "PlayerTransferTransactionsDTO");
            cachedSerQNames.add(qName);
            cls = PlayerTransferTransactionsDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryPlayerRequest");
            cachedSerQNames.add(qName);
            cls = QueryPlayerRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryPlayerResponse");
            cachedSerQNames.add(qName);
            cls = QueryPlayerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryTransferRequest");
            cachedSerQNames.add(qName);
            cls = QueryTransferRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryTransferResponse");
            cachedSerQNames.add(qName);
            cls = QueryTransferResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportRequest");
            cachedSerQNames.add(qName);
            cls = ReportRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "SetBonusBalanceActiveRequest");
            cachedSerQNames.add(qName);
            cls = SetBonusBalanceActiveRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ThirdPartyPlayerLogoutResponse");
            cachedSerQNames.add(qName);
            cls = ThirdPartyPlayerLogoutResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "ToggleBonusBalanceResponse");
            cachedSerQNames.add(qName);
            cls = ToggleBonusBalanceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordRequest");
            cachedSerQNames.add(qName);
            cls = UpdatePlayerPasswordRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordResponse");
            cachedSerQNames.add(qName);
            cls = UpdatePlayerPasswordResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ws.oxypite.com/", "WithdrawPlayerMoneyRequest");
            cachedSerQNames.add(qName);
            cls = WithdrawPlayerMoneyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public CouponInfoDTO[] getBonusAvailablePlayer(BonusAvailablePlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetBonusAvailablePlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBonusAvailablePlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (CouponInfoDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (CouponInfoDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, CouponInfoDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public CouponResponseMessage applyBonusToPlayer(ApplyBonusToPlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ApplyBonusToPlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ApplyBonusToPlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (CouponResponseMessage) _resp;
            } catch (java.lang.Exception _exception) {
                return (CouponResponseMessage) org.apache.axis.utils.JavaUtils.convert(_resp, CouponResponseMessage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public BonusBalancesDTO[] getBonusBalancesForPlayer(BonusGenericPlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetBonusBalancesForPlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBonusBalancesForPlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (BonusBalancesDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (BonusBalancesDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, BonusBalancesDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ToggleBonusBalanceResponse setPlayerBonusBalanceActive(SetBonusBalanceActiveRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/SetPlayerBonusBalanceActive");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "SetPlayerBonusBalanceActive"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ToggleBonusBalanceResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ToggleBonusBalanceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ToggleBonusBalanceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ToggleBonusBalanceResponse deletePlayerBonusBalance(DeleteBonusBalanceRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/DeletePlayerBonusBalance");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "DeletePlayerBonusBalance"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ToggleBonusBalanceResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ToggleBonusBalanceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ToggleBonusBalanceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public CreateAndApplyBonusResponse createAndApplyBonus(CreateBonusAndApplyRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/CreateAndApplyBonus");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "CreateAndApplyBonus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (CreateAndApplyBonusResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (CreateAndApplyBonusResponse) org.apache.axis.utils.JavaUtils.convert(_resp, CreateAndApplyBonusResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public GameTypeResponse getGameTypes(GameTypeRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetGameTypes");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGameTypes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (GameTypeResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (GameTypeResponse) org.apache.axis.utils.JavaUtils.convert(_resp, GameTypeResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public GameResponse getGames(GameRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetGames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (GameResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (GameResponse) org.apache.axis.utils.JavaUtils.convert(_resp, GameResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public GameResponse getGamesInMenuOnly(GameRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetGamesInMenuOnly");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGamesInMenuOnly"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (GameResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (GameResponse) org.apache.axis.utils.JavaUtils.convert(_resp, GameResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public GameDisplayResponse getGameDisplay(GameDisplayRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetGameDisplay");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGameDisplay"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (GameDisplayResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (GameDisplayResponse) org.apache.axis.utils.JavaUtils.convert(_resp, GameDisplayResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public JackpotInfoDTO[] getJackpots(JackpotInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetJackpots");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetJackpots"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (JackpotInfoDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (JackpotInfoDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, JackpotInfoDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public JackpotGameLinkInfoDTO[] getJackpotGameLink(JackpotInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetJackpotGameLink");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetJackpotGameLink"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (JackpotGameLinkInfoDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (JackpotGameLinkInfoDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, JackpotGameLinkInfoDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public JackpotInfoDTO[] getAllJackpotsInAllBrands(JackpotInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetAllJackpotsInAllBrands");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetAllJackpotsInAllBrands"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (JackpotInfoDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (JackpotInfoDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, JackpotInfoDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public JackpotGameLinkInfoDTO[] getJackpotGameLinkInAllBrands(JackpotInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetJackpotGameLinkInAllBrands");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetJackpotGameLinkInAllBrands"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (JackpotGameLinkInfoDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (JackpotGameLinkInfoDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, JackpotGameLinkInfoDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerStakePayoutDTO[] reportPlayerStakePayout(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportPlayerStakePayout");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportPlayerStakePayout"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerStakePayoutDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerStakePayoutDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerStakePayoutDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerGameTransactionsDTO[] getPlayerGameTransactions(PlayerReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetPlayerGameTransactions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerGameTransactions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerGameTransactionsDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerGameTransactionsDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerGameTransactionsDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerTransferTransactionsDTO[] getPlayerTransferTransactions(PlayerReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetPlayerTransferTransactions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerTransferTransactions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerTransferTransactionsDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerTransferTransactionsDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerTransferTransactionsDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerTransferTransactionsDTO[] getBrandTransferTransactions(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetBrandTransferTransactions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBrandTransferTransactions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerTransferTransactionsDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerTransferTransactionsDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerTransferTransactionsDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerTransferTransactionsDTO[] getGroupTransferTransactions(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetGroupTransferTransactions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGroupTransferTransactions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerTransferTransactionsDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerTransferTransactionsDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerTransferTransactionsDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerGameResultsDTO[] getPlayerGameResults(PlayerReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetPlayerGameResults");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerGameResults"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerGameResultsDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerGameResultsDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerGameResultsDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerGameResultsDTO[] getBrandGameResults(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetBrandGameResults");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBrandGameResults"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerGameResultsDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerGameResultsDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerGameResultsDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerCompletedGamesDTO[] getBrandCompletedGameResults(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetBrandCompletedGameResults");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetBrandCompletedGameResults"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerCompletedGamesDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerCompletedGamesDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerCompletedGamesDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerCompletedGamesDTO[] getGroupCompletedGameResults(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetGroupCompletedGameResults");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetGroupCompletedGameResults"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerCompletedGamesDTO[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerCompletedGamesDTO[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerCompletedGamesDTO[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerStakePayoutSummaryDTO getPlayerStakePayoutSummary(PlayerReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/GetPlayerStakePayoutSummary");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "GetPlayerStakePayoutSummary"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerStakePayoutSummaryDTO) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerStakePayoutSummaryDTO) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerStakePayoutSummaryDTO.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public JackpotContributionRecord[] reportJackpotContribution(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportJackpotContribution");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportJackpotContribution"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (JackpotContributionRecord[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (JackpotContributionRecord[]) org.apache.axis.utils.JavaUtils.convert(_resp, JackpotContributionRecord[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public JackpotContributionPerGameRecord[] reportJackpotContributionPerGame(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportJackpotContributionPerGame");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportJackpotContributionPerGame"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (JackpotContributionPerGameRecord[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (JackpotContributionPerGameRecord[]) org.apache.axis.utils.JavaUtils.convert(_resp, JackpotContributionPerGameRecord[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ReportDynamicResponseReportDynamicResult reportDynamic(DynamicReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportDynamic");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportDynamic"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ReportDynamicResponseReportDynamicResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (ReportDynamicResponseReportDynamicResult) org.apache.axis.utils.JavaUtils.convert(_resp, ReportDynamicResponseReportDynamicResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public GameOverviewRecord[] reportGameOverviewBrand(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportGameOverviewBrand");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportGameOverviewBrand"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (GameOverviewRecord[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (GameOverviewRecord[]) org.apache.axis.utils.JavaUtils.convert(_resp, GameOverviewRecord[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public PlayerGameOverviewRecord[] reportGameOverviewPlayer(PlayerReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportGameOverviewPlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportGameOverviewPlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (PlayerGameOverviewRecord[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (PlayerGameOverviewRecord[]) org.apache.axis.utils.JavaUtils.convert(_resp, PlayerGameOverviewRecord[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public GameOverviewPerLocationRecord[] reportGameOverviewPerLocation(ReportRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/ReportGameOverviewPerLocation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "ReportGameOverviewPerLocation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (GameOverviewPerLocationRecord[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (GameOverviewPerLocationRecord[]) org.apache.axis.utils.JavaUtils.convert(_resp, GameOverviewPerLocationRecord[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public UpdatePlayerPasswordResponse updatePlayerPassword(UpdatePlayerPasswordRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/UpdatePlayerPassword");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPassword"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (UpdatePlayerPasswordResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (UpdatePlayerPasswordResponse) org.apache.axis.utils.JavaUtils.convert(_resp, UpdatePlayerPasswordResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public LoginUserResponse loginOrCreatePlayer(LoginOrCreatePlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/LoginOrCreatePlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LoginOrCreatePlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (LoginUserResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (LoginUserResponse) org.apache.axis.utils.JavaUtils.convert(_resp, LoginUserResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public QueryTransferResponse queryTransfer(QueryTransferRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/QueryTransfer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryTransfer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (QueryTransferResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (QueryTransferResponse) org.apache.axis.utils.JavaUtils.convert(_resp, QueryTransferResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public QueryPlayerResponse queryPlayer(QueryPlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/QueryPlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "QueryPlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (QueryPlayerResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (QueryPlayerResponse) org.apache.axis.utils.JavaUtils.convert(_resp, QueryPlayerResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public LogoutPlayerResponse logoutPlayer(LogoutPlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/LogoutPlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (LogoutPlayerResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (LogoutPlayerResponse) org.apache.axis.utils.JavaUtils.convert(_resp, LogoutPlayerResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ThirdPartyPlayerLogoutResponse logoutThirdPartyPlayer(LogoutThirdPartyPlayerRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/LogoutThirdPartyPlayer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutThirdPartyPlayer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ThirdPartyPlayerLogoutResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ThirdPartyPlayerLogoutResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ThirdPartyPlayerLogoutResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public MoneyResponse depositPlayerMoney(DepositPlayerMoneyRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/DepositPlayerMoney");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "DepositPlayerMoney"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (MoneyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (MoneyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, MoneyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public MoneyResponse withdrawPlayerMoney(WithdrawPlayerMoneyRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/WithdrawPlayerMoney");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "WithdrawPlayerMoney"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (MoneyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (MoneyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, MoneyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public LogoutAllPlayersInBrandResponse logoutAllPlayersInBrand(LogoutAllPlayersInBrandRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/LogoutAllPlayersInBrand");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutAllPlayersInBrand"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (LogoutAllPlayersInBrandResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (LogoutAllPlayersInBrandResponse) org.apache.axis.utils.JavaUtils.convert(_resp, LogoutAllPlayersInBrandResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public MaintenanceModeResponse setMaintenanceMode(MaintenanceModeRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ws.oxypite.com/SetMaintenanceMode");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "SetMaintenanceMode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (MaintenanceModeResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (MaintenanceModeResponse) org.apache.axis.utils.JavaUtils.convert(_resp, MaintenanceModeResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
