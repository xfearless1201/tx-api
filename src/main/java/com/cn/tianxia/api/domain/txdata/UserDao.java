package com.cn.tianxia.api.domain.txdata;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Scope;

import com.cn.tianxia.api.common.PayEntity;
import com.cn.tianxia.api.project.User;

/**
 * 功能概要：User的DAO类
 * 
 */
public interface UserDao {

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Scope("prototype")
	Map<String, Object> selectUserById(Map<String, Object> map);

	@Scope("prototype")
	Map<String, Object> selectUserInfo(Map<String, Object> map);

	/**
	 * 
	 * @param userName
	 * @return
	 */
	@Scope("prototype")
	List<Map<String, Object>> selectUserByUserName(@Param("userName") String userName,
			@Param("passWord") String passWord);

	@Scope("prototype")
	List<Map<String, Object>> selectDisUserByUserName(@Param("userName") String userName,
												   @Param("passWord") String passWord);

	@Scope("prototype")
	List<Map<String, Object>> selectReserveAccount(@Param("userName") String userName, @Param("cagent") String cagent);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Scope("prototype")
	List<User> selectlogByUserId(@Param("userId") String userId);

	@Scope("prototype")
	List<Map<String, String>> checkkpwd(Map<String, Object> map);

	@Scope("prototype")
	void insertUser(Map<String, Object> map);

	@Scope("prototype")
	void insertLogin(Map<String, Object> map);

	@Scope("prototype")
	void insertTransfer(Map<String, Object> map);

	@Scope("prototype")
	void updateTransferPro(Map<String, Object> map);

	void insertRechange(Map<String, Object> map);
	
	void insertErrorRechange(Map<String, Object> map);

	@Scope("prototype")
	void updateMoney(Map<String, Object> map);

	void updateMgUserName(Map<String, Object> map);

	@Scope("prototype")
	void updateGame(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectTransferCount(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectTransferInfo(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectReChargeCount(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectReChargeInfo(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectWithDrawCount(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectWithDrawInfo(Map<String, Object> map);

	@Scope("prototype")
	void insertUserCard(Map<String, Object> map);

	@Scope("prototype")
	void deleteUserCard(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectUserCard(Map<String, Object> map);

	@Scope("prototype")
	void UpdateRechange(Map<String, Object> map);

	@Scope("prototype")
	void insertUserTreasure(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectPlatFromInfo(@Param("KEY") String KEY);

	@Scope("prototype")
	List<Map<String, String>> selectWebCom(@Param("type") String type, @Param("cagent") String cagent);

	@Scope("prototype")
	List<Map<String, String>> selectRefererUrl(@Param("domain") String domain, @Param("cagent") String cagent);

	@Scope("prototype")
	void insertLoginMap(Map<String, String> map);

	@Scope("prototype")
	List<Map<String, String>> selectLoginMap(Map<String, String> map);

	@Scope("prototype")
	void deleteLoginMap(@Param("sessionid") String sessionid);

	@Scope("prototype")
	void insertLoginErrorMap(@Param("username") String username);

	@Scope("prototype")
	List<Map<String, Object>> selectLoginErrorMap(@Param("username") String username);

	@Scope("prototype")
	void deleteLoginErrorMap(@Param("username") String username);

	@Scope("prototype")
	List<Map<String, Object>> selectMessageRead(@Param("uid") String uid, @Param("bdate") String bdate,
			@Param("edate") String edate);

	@Scope("prototype")
	List<Map<String, Object>> selectMessageByStatus(@Param("uid") String uid, @Param("status") String status,
			@Param("bdate") String bdate, @Param("edate") String edate);

	@Scope("prototype")
	List<Map<String, Object>> selectMessageInfo(@Param("uid") String uid, @Param("id") String id);

	@Scope("prototype")
	void updateMessageInfo(@Param("uid") String uid, @Param("id") String id);

	@Scope("prototype")
	void deleteMessage(@Param("uid") String uid, @Param("id") String id);

	@Scope("prototype")
	List<Map<String, Object>> getProxyUser(@Param("proxyname") String proxyname, @Param("cagent") String cagent);

	@Scope("prototype")
	List<Map<String, Object>> getJuniorProxyUser(@Param("proxyname") String proxyname, @Param("cagent") String cagent);

	@Scope("prototype")
	List<Map<String, String>> getYsepayConfig(@Param("username") String username);

	@Scope("prototype")
	List<Map<String, String>> getBankPayConfig(@Param("uid") String uid, @Param("bid") String bid);

	// 用户在线存款
	@Scope("prototype")
	void userOnlineDeposit(Map<String, Object> map);

	// 扣储值额度
	@Scope("prototype")
	void updateCagentStoredvalue(Map<String, Object> map);

	// 扣储值额度记录
	@Scope("prototype")
	void insertStoredvalueLog(Map<String, Object> map);

	// 用户提款
	@Scope("prototype")
	void insertWithdraw(Map<String, Object> map);

	// 查询用户卡号
	@Scope("prototype")
	Map<String, String> selectUserCardNum(Integer uid);

	// 查询用户提款次数
	@Scope("prototype")
	Map<String, String> queryTotaltimes(Integer uid);

	// 查询用户存款次数
	@Scope("prototype")
	Integer queryDeposittimes(@Param("uid") Integer uid);

	// 查询平台彩金优惠打码比例
	@Scope("prototype")
	Map<String, Object> queryWithdrawConfig(@Param("cagent") String cagent);

	// 汇总平台一天总存款
	@Scope("prototype")
	String sumTodayWithdraw(Map<String, Object> map);

	// 新增或者修改当前打码量
	@Scope("prototype")
	Integer insertORUpdate(Map<String, Object> map);

	// 查询会员打码量总游戏金额强制提款手续费
	@Scope("prototype")
	Map<String, Object> selectWithdrawConfig(Integer uid);

	// 查询网站配置
	@Scope("prototype")
	List<Map<String, String>> selectWebComConfig(Map<String, String> map);

	// 查询网站文档配置
	@Scope("prototype")
	Map<String, String> selectWebTexttMap(Map<String, String> map);

	@Scope("prototype")
	List<Map<String, String>> selectUserGameStatus(Map<String, Object> map);

	@Scope("prototype")
	void insertUserGameStatus(Map<String, Object> map);

	// 分页查询用户资金流水
	@Scope("prototype")
	List<Map<String, String>> queryByTreasurePage(Map<String, Object> map);

	// 用户资金流水分页总条数
	@Scope("prototype")
	Double queryByTreasurePageCount(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectReChargeStatus(Map<String, Object> map);

	@Scope("prototype")
	List<Map<String, String>> selectDemoAccount(Map<String, Object> map);

	@Scope("prototype")
	void insertDemoAccount(Map<String, Object> map);

	// bg_record
	@Scope("prototype")
	List<Map<String, String>> selectBGRecord(Map<String, Object> map);

	@Scope("prototype")
	Integer InserterBGRecord(Map<String, Object> map);

	@Scope("prototype")
	Map<String, Object> selectUserByAgUserName(Map<String, Object> map);

	// 查询单据是否存在
	@Scope("prototype")
	List<Map<String, String>> selectChickReCharge(Map<String, Object> map);

	// 保存回调记录
	void InsertCallbacklog(Map<String, Object> map);

	// 支付回调
	void ysePayCallBack(Map<String, Object> map);

	// 转账错误记录
	void insertTransferFaild(@Param("uid") String uid, @Param("billno") String billno,
			@Param("username") String username, @Param("t_type") String t_type, @Param("t_money") String t_money,
			@Param("type") String type, @Param("ip") String ip, @Param("result") String result);
	// 更新
	void updateTransferFaild(@Param("uid") String uid, @Param("billno") String billno,
							 @Param("username") String username, @Param("t_type") String t_type, @Param("t_money") String t_money,
							 @Param("type") String type, @Param("ip") String ip, @Param("result") String result);

	// 获取在线会员配置地址
	List<Map<String, String>> selectCagentOnlineMem(@Param("cagent") String cagent);

	// 获取代理平台游戏游戏开关
	List<Map<String, String>> selectPlatformCagent(String uid);

	// 根据用户获取代理平台
	List<Map<String, String>> selectCagentByUid(@Param("uid") String uid);

	// 查询用户分层
	List<Map<String, String>> selectUserTypeById(Map<String, Object> map);

	// 查询用户分层支付列表
	List<Map<String, String>> selectUserPaymentList(Map<String, Object> map);

	// 根据id查询支付商
	List<Map<String, String>> selectYsepaybyId(@Param("pid") String pid,@Param("uid") String iid);

	// <!--查询t_proxy_user user_name-->
	List<Map<String, String>> selectProxyByName(Map<String, Object> map);

	// <!--查询selectUserQuantityByid-->
	Map<String, String> selectUserQuantityByid(@Param("uid") String uid);

	List<Map<String, String>> selectTcagentYsepay(String paymentName);

	int updatePersonalInformation(Map<String, Object> map);

	@Scope("prototype")
	int selectWithDrawStatusCount(@Param("uid") int uid);

	// 查询用户分层盘口设置
	@Scope("prototype")
	Map<String, Object> selectUserTypeHandicap(@Param("game") String game, @Param("uid") String uid);

	// 查询短信平台配置
	List<Map<String, String>> selectMsgconfig(@Param("cagent") String cagent);

	// 保存短信发送记录
	void InsertMsgLog(@Param("cagent") String cagent, @Param("mname") String mname, @Param("mobileno") String mobileno,
			@Param("msg") String msg, @Param("type") String type, @Param("domain") String domain);

	// 查询短信发送记录
	List<Map<String, String>> selectMsgLog(@Param("cagent") String cagent, @Param("mobileno") String mobileno,
			@Param("type") String type, @Param("sendtime") String sendtime);

	// 更新短信状态
	void updateMsgValue(@Param("id") String id, @Param("status") String status);

	// 检测手机号是否存在
	List<Map<String, Object>> selectUserByMobileNo(@Param("cagent") String cagent, @Param("mobileNo") String mobileNo,
			@Param("password") String password);

    // 检测游离表中的手机号是否存在
    List<Map<String, Object>> selectDisUserByMobileNo(@Param("cagent") String cagent, @Param("mobileNo") String mobileNo,
                                                   @Param("password") String password);

	// 手机绑定记录
	int insertMobileLog(Map<String, Object> map);

	// 当日只能更改一次绑定
	List<Map<String, Object>> selectMobileLog(@Param("uid") String uid);

	// 检查取款密码
	List<Map<String, Object>> selectQkpwdCheck(@Param("uid") String uid);
	////获取当前用户未审批订单数
	int getOrderCount(Map<String, Object> userMap);
	
	//修改订单金额
	int UpdateRechangeMoney(@Param("orderNo") String orderNo,@Param("amount") String amount);
	
	//用户可用支付渠道
	Map<String, String> selectUserPaychannel(Map<String, Object> map);
	
	//平台可用额度
	Map<String, String> selectCagentQuota(@Param("cagent") String cagent);
	
	//保存pstaoken
	int insertPSToken(@Param("auth") String auth,@Param("step") int step,@Param("uid") String uid);
	
	//PS游戏查询token
	Map<String, String> selectPSByauth(@Param("auth") String auth);
	
	//PS游戏token状态
	int UpdatePSToken(@Param("auth") String auth,@Param("step") int step);
	
	//查询订单状态
	int selectOrderNoStatus(Map<String, Object> map);

	Map<String,String> getContactInfo(Map<String, Object> map);

    List<Map<String,Object>> getProxyUserByrefererCode(String referralCode);

	List<Map<String,Object>> getJuniorProxyUserByrefererCode(String referralCode);
	
	/**
	 * 
	 * @Description 查询用户的基本信息、所属平台信息、平台剩余额度等信息
	 * @param uid
	 * @return
	 */
	Map<String,Object> findUserInfoByUid(@Param("uid") Integer uid);
	
	/**
	 * 
	 * @Description 创建充值订单
	 * @param payEntity
	 * @return
	 */
	int createTopUpRecharge(PayEntity payEntity);
	/**
	 * 修改用户钱包余额
	 * @param uid
	 * @param amount
	 * @return
	 */
	int transferToGame(@Param("uid")Integer uid,@Param("amount")BigDecimal amount);
	
	/**
	 * 退还或给与用户钱包
	 * @param uid
	 * @param amount
	 * @return
	 */
	public int transferReturnOrGiving(@Param("uid")Integer uid,@Param("amount")BigDecimal amount);
	/**
	 * 获取会员信息
	 * @param uid
	 * @return
	 */
	public double findUserById(@Param("uid")Integer uid);
	
}
