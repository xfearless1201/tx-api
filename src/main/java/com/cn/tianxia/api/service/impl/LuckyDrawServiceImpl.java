package com.cn.tianxia.api.service.impl;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.tianxia.api.common.v2.DatePatternConstant;
import com.cn.tianxia.api.common.v2.DatePatternUtils;
import com.cn.tianxia.api.domain.ftpdata.GameBetInfoDao;
import com.cn.tianxia.api.domain.txdata.LuckyDrawDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentLuckyDrawDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentLuckyDrawDetailDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.UserLuckrdrawLogDao;
import com.cn.tianxia.api.domain.txdata.v2.UserTreasureDao;
import com.cn.tianxia.api.project.v2.CagentEntity;
import com.cn.tianxia.api.project.v2.CagentLuckyDrawDetailEntity;
import com.cn.tianxia.api.project.v2.CagentLuckyDrawEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.project.v2.UserLuckrdrawLogEntity;
import com.cn.tianxia.api.service.LuckyDrawService;
import com.cn.tianxia.api.utils.v2.DateUtils;

import net.sf.json.JSONObject;

/**
 * 功能概要：UserService实现类
 * 
 */
@Service
public class LuckyDrawServiceImpl implements LuckyDrawService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(LuckyDrawServiceImpl.class);
	
    @Resource
	private LuckyDrawDao luckyDrawDao;
	@Autowired
	private NewUserDao newUserDao;
	@Autowired
	private CagentDao cagentDao;
	@Autowired
	private CagentLuckyDrawDao cagentLuckyDrawDao;
	@Autowired
	private CagentLuckyDrawDetailDao cagentLuckyDrawDetailDao;
	@Autowired
	private UserLuckrdrawLogDao userLuckrdrawLogDao;
	@Autowired
	private UserTreasureDao userTreasureDao;
	
	@Autowired
	private GameBetInfoDao gameBetInfoDao;
	

	@Override
	public List<Map<String, Object>> selectLuckyDrawStatus(String domain) { 
		return luckyDrawDao.selectLuckyDrawStatus(domain);
	}

	private int updateLuckydraw(Map<String, String> storedvalueMap) {
		// TODO Auto-generated method stub
		return luckyDrawDao.updateLuckydraw(storedvalueMap);
	}

	/**
	 * 抽取红包
	 */
	@Transactional(rollbackFor = Exception.class)
	public synchronized JSONObject luckyDraw(String userName,String refurl)throws Exception{
	    logger.info("用户:【"+userName+"LuckyDraw】调用红包抽奖业务开始==============START=====================");
	    try {
	        JSONObject jo = new JSONObject();
	        // 获取来源域名
	        if (StringUtils.isBlank(userName)) {
	            logger.info("用户:【"+userName+"LuckyDraw】用户名为空");
	            jo.put("status", "faild");
	            jo.put("msg", "请传入用户名");
	            return jo;
	        }

			logger.info("用户:【"+userName+"LuckyDraw】,通过用户名查询用户信息-----------开始------------");

			UserEntity user = newUserDao.selectByUsername(userName);
			if (null == user) {
				logger.info("用户:【"+userName+"LuckyDraw】,通过用户名查询用户信息,查询失败");
				jo.put("status", "faild");
				jo.put("msg", "未查询到会员信息");
				return jo;
			}

			logger.info("用户:【"+userName+"LuckyDraw】,通过用户名查询用户信息:{}",user.toString());

			logger.info("用户:【"+userName+"LuckyDraw】,通过网站活动域名:【"+refurl+"】,查询平台网站活动-------------开始---------------");
			// 通过域名查询代理商信息
			CagentEntity cagentEntity = cagentDao.selectByRefererUrl(refurl);
			if (cagentEntity == null) {
				jo.put("status", "faild");
				jo.put("msg", "来源域名不正确");
				return jo;
			}
			//查询平台活动
			CagentLuckyDrawEntity luckyDrawEntity = cagentLuckyDrawDao.selectByCid(cagentEntity.getId());
			if (luckyDrawEntity == null) {
				logger.info("用户:【"+userName+"LuckyDraw】,通过网站活动域名:【"+refurl+"】,查询平台网站活动失败");
				jo.put("status", "faild");
				jo.put("msg", "暂无活动");
				return jo;
			}
			//活动详情
			List<CagentLuckyDrawDetailEntity> details = cagentLuckyDrawDetailDao.selectByLid(luckyDrawEntity.getId());
			if (details == null || details.size() <= 0) {
				logger.info("用户:【"+userName+"LuckyDraw】,通过网站活动域名:【"+refurl+"】,查询平台网站活动失败");
				jo.put("status", "faild");
				jo.put("msg", "活动异常");
				return jo;
			}
			logger.info("用户:【"+userName+"LuckyDraw】,查询到的活动详情:{}",Arrays.toString(details.toArray()));

			String bTime = DatePatternUtils.dateToStr(luckyDrawEntity.getBegintime(),DatePatternConstant.NORM_TIME_PATTERN);
			String eTime = DatePatternUtils.dateToStr(luckyDrawEntity.getEndtime(),DatePatternConstant.NORM_TIME_PATTERN);

			Date beginTime = DatePatternUtils.strToDate(DatePatternUtils.dateToStr(new Date(),DatePatternConstant.NORM_DATE_PATTERN) + " " +  bTime, DatePatternConstant.NORM_DATETIME_PATTERN);
			Date endTime = DatePatternUtils.strToDate(DatePatternUtils.dateToStr(new Date(),DatePatternConstant.NORM_DATE_PATTERN) + " " +  eTime, DatePatternConstant.NORM_DATETIME_PATTERN);

			Date now = new Date();
			//未到抽奖时间
			if (now.before(beginTime)) {
				logger.info("用户:【"+userName+"LuckyDraw】,在此时间段,起始时间:【"+beginTime+"】,结束时间:【"+endTime+"】查询抽奖失败");
				jo.put("status", "waiting");
				jo.put("now", now);
				jo.put("begintime", beginTime);
				jo.put("endtime", endTime);
				jo.put("diff", - now.getTime() - beginTime.getTime() / 1000);
				jo.put("msg", "未到活动时间");
				return jo;
			}
			//超过活动时间
			if (now.after(endTime)) {
				logger.info("用户:【"+userName+"LuckyDraw】,在此时间段,起始时间:【"+beginTime+"】,结束时间:【"+beginTime+"】查询抽奖已结束");
				jo.put("status", "end");
				jo.put("now", now);
				jo.put("begintime", beginTime);
				jo.put("endtime", endTime);
				jo.put("msg", "今日活动已结束");
				return jo;
			}
			//验证会员是否来自该平台
			if (!user.getCagent().equalsIgnoreCase(cagentEntity.getCagent())) {
				logger.info("用户:【"+userName+"LuckyDraw】,不属于此平台:【"+cagentEntity.getCagent()+"】的用户");
				jo.put("status", "faild");
				jo.put("msg", "会员帐号错误");
				return jo;
			}
			//计算抽奖金额
			logger.info("单次抽奖金额,最小金额:{},最大金额{}",luckyDrawEntity.getMinamount(),luckyDrawEntity.getMaxamount());
			float min = luckyDrawEntity.getMinamount();
			float max = luckyDrawEntity.getMaxamount();
	        if (min >= max) {
	            luckyDrawDao.updateStatusByAmount(luckyDrawEntity.getId(), "1");
	            jo.put("status", "faild");
	            jo.put("msg", "活动已结束");
	            return jo;
	        }
	        float result = Float.valueOf(new DecimalFormat("##0.00").format(min + Math.random() * (max - min)));//随机金额

	        logger.info("用户:【"+userName+"LuckyDraw】,随机到的金额:{}",result);

	        //金额计算方式，0 当日金额  1 昨日金额
	        String type = luckyDrawEntity.getType();
			logger.info("抽奖次数计算方式，type:{}",type.equals("1")?"昨日金额":"当日金额");
			Date begin;
			Date end;

	        if(type.equals("1")){ //昨日的开始和结束时间
	            begin = DateUtils.getDayBeginOfYesterday();
	            end = DateUtils.getDayEndOfYesterDay();
	        } else {
	        	begin = DateUtils.getDayBegin();
	        	end = DateUtils.getDayEnd();
			}

			String typesOf = luckyDrawEntity.getTypesof(); //红包抽奖类型
			logger.info("用户:【"+userName+"LuckyDraw】,设置该活动抽奖类型:typesOf:{}",typesOf.equals("1")?"存款金额":"注单总额");

			//查询用户上一次活动抽奖记录,从今天开始到本次活动开始时间
			List<UserLuckrdrawLogEntity> userLuckyDrawLogs = userLuckrdrawLogDao.selectByUidAndLid(user.getUid(),luckyDrawEntity.getId(),DateUtils.getDayBegin(),beginTime,typesOf);
			Double usedLimitAmount = 0D;  //用户已用额度
			if (userLuckyDrawLogs != null && userLuckyDrawLogs.size() > 0) {
				Optional<UserLuckrdrawLogEntity> maxRecord = userLuckyDrawLogs.stream().max(Comparator.comparingDouble(UserLuckrdrawLogEntity::getUsedBet));
				usedLimitAmount = maxRecord.get().getUsedBet();
			}
			int userLuckyDrawTimes = 0;  //用户存款金额或注单金额匹配的抽奖次数
			Double usedBet = 0D; //已使用的存款额度或注单总额，用于更新用户红包记录表

	        if ("1".equals(typesOf)) {
	        	//获取用户充值或加款的金额
				Double userDidAmount = userTreasureDao.findAllAmountByTime(user.getUid(),DatePatternUtils.dateToStr(begin,DatePatternConstant.NORM_DATETIME_PATTERN),DatePatternUtils.dateToStr(end,DatePatternConstant.NORM_DATETIME_PATTERN));
				double leftAmount = userDidAmount - usedLimitAmount;
				logger.info("查询用户:【"+user.getUsername()+"】,充值加款总金额:【"+userDidAmount+"】,抽奖已用额度:【" + usedLimitAmount + "】,剩余额度:【" + leftAmount +"】");
				//获取剩余额度匹配的活动次数
				Optional<CagentLuckyDrawDetailEntity> maxTime = details.stream().filter(n -> n.getBalance() <= leftAmount).max(Comparator.comparingDouble(CagentLuckyDrawDetailEntity::getBalance));
	            if (!maxTime.isPresent()) {
	                logger.info("用户:【"+userName+"LuckyDraw】,充值金额未达标");
	                jo.put("status", "faild");
	                jo.put("msg", "充值金额未达标");
	                return jo;
	            }
				userLuckyDrawTimes = maxTime.get().getTimes();
	            usedBet = Double.valueOf(maxTime.get().getBalance());
	        } else if ("2".equals(typesOf)) {
	            logger.info("查询用户注单金额达标请求参数报文:【"+user.getUsername()+"】,开始时间:【"+begin+"】,结束时间:【"+end+"】");
	            Double vilidBetAmount = gameBetInfoDao.selectUserValidBetAmuontList(user.getUid(), begin, end);
				double leftAmount = vilidBetAmount - usedLimitAmount;
				logger.info("查询用户:【"+user.getUsername()+"】,游戏注单总金额:【"+vilidBetAmount+"】,抽奖已用额度:【" + usedLimitAmount + "】,剩余额度:【" + leftAmount +"】");
				//获取剩余额度匹配的活动次数
				Optional<CagentLuckyDrawDetailEntity> maxTime = details.stream().filter(n -> n.getValidbetamount() <= leftAmount).max(Comparator.comparingDouble(CagentLuckyDrawDetailEntity::getValidbetamount));
	            if (!maxTime.isPresent()) {
					logger.info("用户:【"+userName+"LuckyDraw】,注单金额未达标");
					jo.put("status", "faild");
					jo.put("msg", "注单金额未达标");
					return jo;
				}
				userLuckyDrawTimes = maxTime.get().getTimes();
				usedBet = Double.valueOf(maxTime.get().getValidbetamount());
	        }

	        logger.info("查询平台抽奖剩余额度");
	        Map<String, Object> cagentStoredvalue = luckyDrawDao.selectByCidCagentStoredvalue(cagentEntity.getId());
	        logger.info("平台剩余额度：{}",cagentStoredvalue.get("remainvalue").toString());
	        if (Float.parseFloat(cagentStoredvalue.get("remainvalue").toString()) < result) {
	            jo.put("status", "faild");
	            jo.put("msg", "平台额度已不足");
	            return jo;
	        }

	        //查询会员已用抽奖次数
			List<UserLuckrdrawLogEntity> usedLogs = userLuckrdrawLogDao.selectByUidAndLid(user.getUid(),luckyDrawEntity.getId(),beginTime,now,typesOf);

			Optional<UserLuckrdrawLogEntity> maxRecord = usedLogs.stream().max(Comparator.comparingDouble(UserLuckrdrawLogEntity::getUsedBet));
			if (maxRecord.isPresent()) {
				usedBet = usedBet + usedLimitAmount;
			}
	        int usedTime = usedLogs.size();

	        logger.info("用户:【"+ userName +"LuckyDraw】,该活动的可用抽奖次数:【"+ userLuckyDrawTimes + "】,已用抽奖次数:【" + usedTime + "】");

	        if (usedTime >= userLuckyDrawTimes) {
				jo.put("status", "faild");
				jo.put("msg", "已无抽奖次数");
				return jo;
			}
			float amountUsed = luckyDrawEntity.getAmountused();//获取奖池已用金额
			float amountLimit = luckyDrawEntity.getAmountlimit();//获取奖池最大金额

			logger.info("奖池：最大金额:{}，已用金额:{}",amountLimit,amountUsed);
			//奖金池剩余金额
			float luckyBalance = Math.abs(amountLimit-amountUsed);

			//先判断奖金池的金额是否符合抽奖
			if(luckyBalance < min){
				luckyDrawDao.updateStatusByAmount(luckyDrawEntity.getId(), "1");
				jo.put("status", "faild");
				jo.put("msg", "活动已结束");
				return jo;
			}else {
				if(result > luckyBalance){
					result = luckyBalance;
				}
			}

			logger.info("更新抽奖已用金额");
			Map<String, String> hashMap = new HashMap<>();
			//更新抽奖已用金额
			hashMap.put("id", String.valueOf(luckyDrawEntity.getId()));
			hashMap.put("amountUsed", (amountUsed + result)+"");
			updateLuckydraw(hashMap);

			logger.info("添加抽奖日志：uid:{"+user.getUid().toString()+"},lid:{"+luckyDrawEntity.getId()+"},cid:{"+cagentStoredvalue.get("cid").toString()+"},amount:{"+result+"}");

			//查询用户今日所有抽奖次数
			int userHasDrawTimes = 0;
			int userTotalHasDrawTimes = 0;
			//查询用户最近的一条抽奖记录
			UserLuckrdrawLogEntity recentRecord = userLuckrdrawLogDao.selectRecentRecord(user.getUid());
			if (recentRecord != null) {
				if (recentRecord.getAddtime().after(DateUtils.getDayBegin())) {
					userHasDrawTimes = recentRecord.getTodaytimes();
					userTotalHasDrawTimes = recentRecord.getTotaltimes();
				} else {
					userTotalHasDrawTimes = recentRecord.getTotaltimes();
				}
			}


			UserLuckrdrawLogEntity newLuckDarwLog = new UserLuckrdrawLogEntity();
			//添加抽奖日志
			newLuckDarwLog.setAddtime(now);
			newLuckDarwLog.setAmount(result);
			newLuckDarwLog.setUid(user.getUid());
			newLuckDarwLog.setLid(luckyDrawEntity.getId());
			newLuckDarwLog.setCid(cagentEntity.getId());
			newLuckDarwLog.setOrderid("HB" + System.currentTimeMillis());
			newLuckDarwLog.setIp(refurl);
			newLuckDarwLog.setTypesof(typesOf);
			newLuckDarwLog.setUsedBet(usedBet);
			newLuckDarwLog.setTodaytimes(userHasDrawTimes + 1);
			newLuckDarwLog.setTotaltimes(userTotalHasDrawTimes + 1);

			userLuckrdrawLogDao.insertSelective(newLuckDarwLog);

	        jo.put("status", "success");
	        jo.put("result", result);
	        jo.put("msg", "正常");
	        return jo;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户:【"+userName+"LuckyDraw】调用红包抽奖业务异常:{}",e.getMessage());
            throw new Exception(e.getMessage());
        }
	}
}
