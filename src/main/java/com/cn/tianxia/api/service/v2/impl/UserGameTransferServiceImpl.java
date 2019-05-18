package com.cn.tianxia.api.service.v2.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.domain.txdata.TransferDao;
import com.cn.tianxia.api.domain.txdata.v2.CagentDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.PlatformConfigDao;
import com.cn.tianxia.api.domain.txdata.v2.PlatformStatusDao;
import com.cn.tianxia.api.domain.txdata.v2.UserGamestatusDao;
import com.cn.tianxia.api.project.Transfer;
import com.cn.tianxia.api.project.v2.CagentEntity;
import com.cn.tianxia.api.project.v2.PlatformConfigEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.project.v2.UserGamestatusEntity;
import com.cn.tianxia.api.service.v2.UserCapitalService;
import com.cn.tianxia.api.service.v2.UserGameTransferService;

@Service
public class UserGameTransferServiceImpl implements UserGameTransferService {

    private static final Logger logger = LoggerFactory.getLogger(UserGameTransferServiceImpl.class);

    @Autowired
    private TransferDao transferDao;

    @Autowired
    private CagentDao cagentDao;

    @Autowired
    private NewUserDao newUserDao;

    @Autowired
    private UserGamestatusDao userGamestatusDao;

    @Autowired
    private PlatformConfigDao platformConfigDao;

    @Autowired
    private PlatformStatusDao platformStatusDao;

    @Autowired
    private UserCapitalService UserCapitalService;


    @Override
    public int deductUserMoney(String uid , Double money){
        logger.info("deductUserMoney(String uid = {}, Double money = {}    -start",uid,money);
        Integer i = 0;
        try{
           i=  transferDao.updateWelletTransferOut(uid, money);
        }catch (Exception e){
            logger.info("扣除用户【"+uid+"】钱包失败！",e);
            return  0;
        }
        logger.info("deductUserMoney(String uid , Double money )  uid:"+uid+"执行行数 i: "+ i);

        return i;
    }

    @Override
    public int addUserMoney(String uid , Double money){
        logger.info("addUserMoney(String uid = {}, Double money = {}    -start",uid,money);
        Integer i = 0;
        try{
            i=  transferDao.updateWelletTransferIn(uid, money);
        }catch (Exception e){
            logger.info("添加用户【"+uid+"】钱包失败！",e);
            return  i;
        }
        logger.info("addUserMoney(String uid , Double money )  uid:"+uid+"执行行数 i: "+ i);

        return i;
    }



    @Override
    public int insertUserTransferIn(Transfer transfer) {
        logger.info("insertUserTransferIn(Transfer transfer = {}",transfer);
        try {
            // 首先插入转账转入成功订单
            transferDao.insertSelective(transfer);
            // 添加用户金额
            Integer i = transferDao.updateWelletTransferIn(String.valueOf(transfer.getUid()), Double.valueOf(transfer.gettMoney()));
            logger.info("insertUserTransferIn(Map<String, Object> data)写入数据成功！ 订单号:"+transfer.getBillno() +"执行行数 i："+ i);
            return 1;
        } catch (Exception e) {
            logger.info("用户【"+transfer.getUsername()+"】,订单号：【"+transfer.getBillno()+"】写入转入数据异常:{}",e);
            return 0;
        }

    }

    @Override
    public int insertUserTransferOut(Transfer transfer) {
        logger.info("insertUserTransferOut(Transfer transfer = {}",transfer);
        try {
            // 首先插入转账转入成功订单
            transferDao.insertSelective(transfer);
            logger.info("insertUserTransferOut(Map<String, Object> data)写入数据成功! 订单编号："+transfer.getBillno());
            return 1;
        } catch (Exception e) {
            logger.info("用户【"+transfer.getUsername()+"】,订单号：【"+transfer.getBillno()+"】写入转入数据异常:{}",e);
            return 0;
        }
    }

    @Override
    public int insertUserTransferFaild(Transfer transfer) {
        logger.info("insertUserTransferFaild(Transfer transfer = -start{}",transfer);
        return transferDao.insertTransferFaild(transfer);
    }


    @Override
    public int insertUserTransferOutFaild(Transfer transfer) {
        logger.info("insertUserTransferOutFaild(Transfer transfer = -start {}",transfer);
        try {
            transferDao.insertTransferFaild(transfer);
            return 1;
        } catch (Exception e) {
            logger.info("用户【"+transfer.getUsername()+"】,订单号：【"+transfer.getBillno()+"】写入转入数据异常:{}",e);
            return 0;
        }

    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public Map<String, String> selectPlatformGameStatusByCagent(String cagent) {
        //通过用户平台编码查询平台游戏状态
        CagentEntity cagentEntity = cagentDao.selectByCagent(cagent);
        if(cagent != null){
            Map<String, String> map =
                    transferDao.selectPlatformGameStatusByCagent(cagentEntity.getId());
            if(!CollectionUtils.isEmpty(map)){
                return map;
            }
        }
        return null;
    }

    /**
     * 获取用户余额
     */
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public double getUserBalance(int uid) {
        Double balance = transferDao.getBalance(uid);
        if(balance == null){
            balance = 0.00D;
        }
        return balance;
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public int selectUserGameStatusBy(String uid, String gametype) {
        Integer status = transferDao.selectUserGameStatus(uid, gametype);
        if(status == null){
            return 0;
        }
        return status;
    }

    @Override
    public int insertUserGameStatus(String uid, String gametype) {
        return transferDao.insertUserGameStatus(uid, gametype);
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public String selectUserTypeHandicap(String game, String typeId) {
        //通过用户ID查询用户的分层信息
        return transferDao.selectUserTypeHandicap(game, typeId);
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public UserEntity selectUserInfoByUid(Integer uid) {
        return newUserDao.selectByPrimaryKey(uid);
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public UserGamestatusEntity getUserGamestatusByGameType(String uid, String gametype) {
        return userGamestatusDao.selectByGameType(uid, gametype);
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public Map<String, String> getPlatformConfig() {
        Map<String,String> data = new HashMap<String, String>();
        //查询所有游戏配置
        List<PlatformConfigEntity> platformConfigs = platformConfigDao.findAll();
        if(!CollectionUtils.isEmpty(platformConfigs)){
            for (PlatformConfigEntity platformConfig : platformConfigs) {
                data.put(platformConfig.getPlatformKey(), platformConfig.getPlatformConfig());
            }
        }
        return data;
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public Map<String, String> getPlatformStatusByCid(String cid) {
        return platformStatusDao.selectByCid(cid);
    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public Short getPlatformStatusByCidAndType(String cid,String type) {
        return platformStatusDao.selectGameStatusByCidAndType(cid,type);
    }

}
