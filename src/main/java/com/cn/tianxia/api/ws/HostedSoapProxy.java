package com.cn.tianxia.api.ws;

public class HostedSoapProxy implements HostedSoap {
  private String _endpoint = null;
  private HostedSoap hostedSoap = null;
  
  public HostedSoapProxy() {
    _initHostedSoapProxy();
  }
  
  public HostedSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initHostedSoapProxy();
  }
  
  private void _initHostedSoapProxy() {
    try {
      hostedSoap = (new HostedLocator()).getHostedSoap();
      if (hostedSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)hostedSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)hostedSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (hostedSoap != null)
      ((javax.xml.rpc.Stub)hostedSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public HostedSoap getHostedSoap() {
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap;
  }
  
  public CouponInfoDTO[] getBonusAvailablePlayer(BonusAvailablePlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getBonusAvailablePlayer(req);
  }
  
  public CouponResponseMessage applyBonusToPlayer(ApplyBonusToPlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.applyBonusToPlayer(req);
  }
  
  public BonusBalancesDTO[] getBonusBalancesForPlayer(BonusGenericPlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getBonusBalancesForPlayer(req);
  }
  
  public ToggleBonusBalanceResponse setPlayerBonusBalanceActive(SetBonusBalanceActiveRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.setPlayerBonusBalanceActive(req);
  }
  
  public ToggleBonusBalanceResponse deletePlayerBonusBalance(DeleteBonusBalanceRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.deletePlayerBonusBalance(req);
  }
  
  public CreateAndApplyBonusResponse createAndApplyBonus(CreateBonusAndApplyRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.createAndApplyBonus(req);
  }
  
  public GameTypeResponse getGameTypes(GameTypeRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getGameTypes(req);
  }
  
  public GameResponse getGames(GameRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getGames(req);
  }
  
  public GameResponse getGamesInMenuOnly(GameRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getGamesInMenuOnly(req);
  }
  
  public GameDisplayResponse getGameDisplay(GameDisplayRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getGameDisplay(req);
  }
  
  public JackpotInfoDTO[] getJackpots(JackpotInfoRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getJackpots(req);
  }
  
  public JackpotGameLinkInfoDTO[] getJackpotGameLink(JackpotInfoRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getJackpotGameLink(req);
  }
  
  public JackpotInfoDTO[] getAllJackpotsInAllBrands(JackpotInfoRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getAllJackpotsInAllBrands(req);
  }
  
  public JackpotGameLinkInfoDTO[] getJackpotGameLinkInAllBrands(JackpotInfoRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getJackpotGameLinkInAllBrands(req);
  }
  
  public PlayerStakePayoutDTO[] reportPlayerStakePayout(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportPlayerStakePayout(req);
  }
  
  public PlayerGameTransactionsDTO[] getPlayerGameTransactions(PlayerReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getPlayerGameTransactions(req);
  }
  
  public PlayerTransferTransactionsDTO[] getPlayerTransferTransactions(PlayerReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getPlayerTransferTransactions(req);
  }
  
  public PlayerTransferTransactionsDTO[] getBrandTransferTransactions(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getBrandTransferTransactions(req);
  }
  
  public PlayerTransferTransactionsDTO[] getGroupTransferTransactions(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getGroupTransferTransactions(req);
  }
  
  public PlayerGameResultsDTO[] getPlayerGameResults(PlayerReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getPlayerGameResults(req);
  }
  
  public PlayerGameResultsDTO[] getBrandGameResults(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getBrandGameResults(req);
  }
  
  public PlayerCompletedGamesDTO[] getBrandCompletedGameResults(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getBrandCompletedGameResults(req);
  }
  
  public PlayerCompletedGamesDTO[] getGroupCompletedGameResults(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getGroupCompletedGameResults(req);
  }
  
  public PlayerStakePayoutSummaryDTO getPlayerStakePayoutSummary(PlayerReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.getPlayerStakePayoutSummary(req);
  }
  
  public JackpotContributionRecord[] reportJackpotContribution(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportJackpotContribution(req);
  }
  
  public JackpotContributionPerGameRecord[] reportJackpotContributionPerGame(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportJackpotContributionPerGame(req);
  }
  
  public ReportDynamicResponseReportDynamicResult reportDynamic(DynamicReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportDynamic(req);
  }
  
  public GameOverviewRecord[] reportGameOverviewBrand(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportGameOverviewBrand(req);
  }
  
  public PlayerGameOverviewRecord[] reportGameOverviewPlayer(PlayerReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportGameOverviewPlayer(req);
  }
  
  public GameOverviewPerLocationRecord[] reportGameOverviewPerLocation(ReportRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.reportGameOverviewPerLocation(req);
  }
  
  public UpdatePlayerPasswordResponse updatePlayerPassword(UpdatePlayerPasswordRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.updatePlayerPassword(req);
  }
  
  public LoginUserResponse loginOrCreatePlayer(LoginOrCreatePlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.loginOrCreatePlayer(req);
  }
  
  public QueryTransferResponse queryTransfer(QueryTransferRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.queryTransfer(req);
  }
  
  public QueryPlayerResponse queryPlayer(QueryPlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.queryPlayer(req);
  }
  
  public LogoutPlayerResponse logoutPlayer(LogoutPlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.logoutPlayer(req);
  }
  
  public ThirdPartyPlayerLogoutResponse logoutThirdPartyPlayer(LogoutThirdPartyPlayerRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.logoutThirdPartyPlayer(req);
  }
  
  public MoneyResponse depositPlayerMoney(DepositPlayerMoneyRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.depositPlayerMoney(req);
  }
  
  public MoneyResponse withdrawPlayerMoney(WithdrawPlayerMoneyRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.withdrawPlayerMoney(req);
  }
  
  public LogoutAllPlayersInBrandResponse logoutAllPlayersInBrand(LogoutAllPlayersInBrandRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.logoutAllPlayersInBrand(req);
  }
  
  public MaintenanceModeResponse setMaintenanceMode(MaintenanceModeRequest req) throws java.rmi.RemoteException{
    if (hostedSoap == null)
      _initHostedSoapProxy();
    return hostedSoap.setMaintenanceMode(req);
  }
  
  
}