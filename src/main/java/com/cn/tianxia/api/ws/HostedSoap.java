/**
 * HostedSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cn.tianxia.api.ws;

public interface HostedSoap extends java.rmi.Remote {

    /**
     * Get a list of Coupons/Bonuses available to a player for redemption
     */
    public CouponInfoDTO[] getBonusAvailablePlayer(BonusAvailablePlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Apply a bonus coupon to a player
     */
    public CouponResponseMessage applyBonusToPlayer(ApplyBonusToPlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of the players bonus balances (these are coupons
     * which have been activated on players account and are ready to use)
     */
    public BonusBalancesDTO[] getBonusBalancesForPlayer(BonusGenericPlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Toggle the provided BonusBalanceId active status.
     */
    public ToggleBonusBalanceResponse setPlayerBonusBalanceActive(SetBonusBalanceActiveRequest req) throws java.rmi.RemoteException;

    /**
     * Deletes a Bonus Balance
     */
    public ToggleBonusBalanceResponse deletePlayerBonusBalance(DeleteBonusBalanceRequest req) throws java.rmi.RemoteException;

    /**
     * Create a new Coupon and optionally redeeme it for specified
     * username
     */
    public CreateAndApplyBonusResponse createAndApplyBonus(CreateBonusAndApplyRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of Game Types
     */
    public GameTypeResponse getGameTypes(GameTypeRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of all available Games for the Brand.
     */
    public GameResponse getGames(GameRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of all available Games for the Brand but only if
     * they appear in the Backoffice Game Menu Display [same format as GetGames()]
     */
    public GameResponse getGamesInMenuOnly(GameRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of all Games for the Brand in hierarchical menu
     * format structure as per Backoffice Game Menu Display
     */
    public GameDisplayResponse getGameDisplay(GameDisplayRequest req) throws java.rmi.RemoteException;

    /**
     * Get a detailed list of all Jackpots in the Brand
     */
    public JackpotInfoDTO[] getJackpots(JackpotInfoRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of all Jackpots id's in the Brand with a list of
     * linked BrandGameIds. This shows you which Jackpots a specific Game
     * is using.
     */
    public JackpotGameLinkInfoDTO[] getJackpotGameLink(JackpotInfoRequest req) throws java.rmi.RemoteException;

    /**
     * Get a detailed list of all Jackpots in all the Brands in same
     * Group]
     */
    public JackpotInfoDTO[] getAllJackpotsInAllBrands(JackpotInfoRequest req) throws java.rmi.RemoteException;

    /**
     * Get a list of all Jackpots id's for all Brands in a Group with
     * a list of linked BrandGameIds
     */
    public JackpotGameLinkInfoDTO[] getJackpotGameLinkInAllBrands(JackpotInfoRequest req) throws java.rmi.RemoteException;

    /**
     * Get the summarised Total stakes and payouts per player for
     * a Brand during the date range. Winners/Losers report. ONLY completed
     * games. Hours granularity
     */
    public PlayerStakePayoutDTO[] reportPlayerStakePayout(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get individual debit and credit transactions per game for a
     * player in a date range. Seconds granularity.
     */
    public PlayerGameTransactionsDTO[] getPlayerGameTransactions(PlayerReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get individual money transfers in and out for a player in a
     * date range. Same result format as GetBrandTransferTransactions().
     * Seconds granularity.
     */
    public PlayerTransferTransactionsDTO[] getPlayerTransferTransactions(PlayerReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get all individual money transfers in and out for the brand
     * in a date range. Same result format as GetPlayerTransferTransactions().
     * Seconds granularity.
     */
    public PlayerTransferTransactionsDTO[] getBrandTransferTransactions(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get all individual money transfers in and out for the GROUP
     * in a date range. Same result format as GetPlayerTransferTransactions().
     * Seconds granularity.
     */
    public PlayerTransferTransactionsDTO[] getGroupTransferTransactions(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get individual game instance results for a player in a date
     * range. INCLUDES incomplete games. Seconds granularity
     */
    public PlayerGameResultsDTO[] getPlayerGameResults(PlayerReportRequest req) throws java.rmi.RemoteException;

    /**
     * NOTE: Please Use GetBrandCompletedGameResults(). Get up to
     * 10000 records, up to 7 days ago, of individual game instance results
     * for players in a brand in the date range. Includes incomplete games.
     * Seconds granularity
     */
    public PlayerGameResultsDTO[] getBrandGameResults(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get completed game instance results for players in a brand
     * where the Completed Date of the game is in the date range. Seconds
     * granularity
     */
    public PlayerCompletedGamesDTO[] getBrandCompletedGameResults(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get completed game instance results for players in GROUP Wide
     * where the Completed Date of the game is in the date range. The Group
     * will be determined by the brandId requested. Seconds granularity
     */
    public PlayerCompletedGamesDTO[] getGroupCompletedGameResults(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get a single players summed Stake, Payout, Jackpot win (portion
     * of the Payout), Jackpot Contributions in 1 row. INCLUDES incomplete
     * games. Seconds granularity
     */
    public PlayerStakePayoutSummaryDTO getPlayerStakePayoutSummary(PlayerReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get overall contributions report for each Jackpot in a Brand
     * in the individual funding currency. Hours granularity
     */
    public JackpotContributionRecord[] reportJackpotContribution(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get overall contributions per Game for each Jackpot in a Brand.
     * Hours granularity
     */
    public JackpotContributionPerGameRecord[] reportJackpotContributionPerGame(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get the results from a Dynamic Report configured in the Back
     * Office
     */
    public ReportDynamicResponseReportDynamicResult reportDynamic(DynamicReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get game overview/summary report for each Game in a Brand.
     * ONLY completed games. Hour granularity.
     */
    public GameOverviewRecord[] reportGameOverviewBrand(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get game overview/summary report for games played by Player
     * in date range. ONLY completed games. Hour granularity.
     */
    public PlayerGameOverviewRecord[] reportGameOverviewPlayer(PlayerReportRequest req) throws java.rmi.RemoteException;

    /**
     * Get game overview report for each Game in a POS/Kiosk Location
     * in a Brand [For Terminals/Kiosk usage only], Hour granularity.
     */
    public GameOverviewPerLocationRecord[] reportGameOverviewPerLocation(ReportRequest req) throws java.rmi.RemoteException;

    /**
     * Update player password.
     */
    public UpdatePlayerPasswordResponse updatePlayerPassword(UpdatePlayerPasswordRequest req) throws java.rmi.RemoteException;

    /**
     * Logs in player and creates/updates Player using provided details.
     * On creation the wallet is created using the currency code provided.
     * This cannot be changed. Returns the Session Token used to launch game.
     */
    public LoginUserResponse loginOrCreatePlayer(LoginOrCreatePlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Query a Deposit or Withdraw RequestId to get the status
     */
    public QueryTransferResponse queryTransfer(QueryTransferRequest req) throws java.rmi.RemoteException;

    /**
     * Query player record for current balance
     */
    public QueryPlayerResponse queryPlayer(QueryPlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Logout a Habanero Wallet Player (Note this will not work for
     * Single/Seamless wallet)
     */
    public LogoutPlayerResponse logoutPlayer(LogoutPlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Logout a Player using external token for single wallet.
     */
    public ThirdPartyPlayerLogoutResponse logoutThirdPartyPlayer(LogoutThirdPartyPlayerRequest req) throws java.rmi.RemoteException;

    /**
     * Deposit money into player wallet. If player exists the currency
     * code must match. Otherwise a new player will be created.
     */
    public MoneyResponse depositPlayerMoney(DepositPlayerMoneyRequest req) throws java.rmi.RemoteException;

    /**
     * Withdraw money from player wallet.
     */
    public MoneyResponse withdrawPlayerMoney(WithdrawPlayerMoneyRequest req) throws java.rmi.RemoteException;

    /**
     * Logouts out all Thirdparty players in a Brand.
     */
    public LogoutAllPlayersInBrandResponse logoutAllPlayersInBrand(LogoutAllPlayersInBrandRequest req) throws java.rmi.RemoteException;

    /**
     * Set Maintenance mode ON or OFF for a Group.
     */
    public MaintenanceModeResponse setMaintenanceMode(MaintenanceModeRequest req) throws java.rmi.RemoteException;
}
