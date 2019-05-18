package com.cn.tianxia.api.domain.txdata;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.tianxia.api.po.v2.TransferInOutUnionPO;
import com.cn.tianxia.api.project.Transfer;
import com.cn.tianxia.api.project.v2.TransferEntity;

public interface TransferDao {
    
    int deleteByPrimaryKey(Integer id);

    int insert(Transfer record);
    
    int insertAll(Transfer record);

    int insertSelective(Transfer record);

    Transfer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Transfer record);

    int updateByPrimaryKey(Transfer record);
    
    int insertTransferFaild(Transfer record);

    /**
     * 功能描述: 获取用户平台信息
     *
     * @Author: Horus
     * @Date: 2019/1/19 13:42
     * @param uid
     * @param platCode
     * @return: com.cn.tianxia.transfer.po.TransferInOutUnionPO
     **/
    TransferInOutUnionPO getUserCagentByUid(@Param("uid") String uid, @Param("platCode") String platCode);

    /**
     *
     * @Description 获取用户游戏平台状态
     * @param uid
     * @param gametype
     * @return
     */
    Integer selectUserGameStatus(@Param("uid") String uid, @Param("gametype") String gametype);

    /**
     *
     * @Description 获取用户余额
     * @param uid
     * @return
     */
    Double getUserWalletBalance(@Param("uid") String uid);

    Double getBalance(@Param("uid") int uid);

    /**
     *
     * @Description 插入用户游戏状态
     * @param uid
     * @param gametype
     * @return
     */
    Integer insertUserGameStatus(@Param("uid") String uid, @Param("gametype") String gametype);

    /**
     *
     * @Description 修改用户钱包余额
     * @param uid
     * @param money
     * @return
     */
     Integer updateWelletTransferIn(@Param("uid") String uid, @Param("money") Double money);


    Integer updateWelletTransferOut(@Param("uid") String uid, @Param("money") Double money);

    /**
     * 功能描述: 获取用户平台游戏开关状态
     *
     * @Author: Horus
     * @Date: 2019/1/19 13:52
     * @param cid
     * @param column
     * @return: Map
     **/
    Map<String,String> selectPlatGameStatus(@Param("cid") String cid, @Param("column") String column);

    /**
     *
     * @Description 获取游戏盘口
     * @param data
     * @return
     */
    String selectUserTypeHandicap(@Param("game") String game, @Param("typeId") String typeId);
    /**
     * 修改资金流水状态
     * @param billNo
     * @return
     */
    public int updateStatusByBillNo(@Param("billNo") String billNo, @Param("status") Integer status, @Param("result") String result);

    /**
     * 修改资金流水状态
     * @param billNo
     * @return
     */
    public int updateStatusByBillNoCount(@Param("billNo") String billNo, @Param("status") Integer status, @Param("result") String result);
    /**
     * 获取待处理中订单数
     * @param uid
     * @return
     */
    public int selectWaitIng(@Param("uid") Integer uid);
    
    /**
     * 
     * @Description 查询平台游戏状态
     * @param cid
     * @return
     */
    public Map<String,String> selectPlatformGameStatusByCagent(@Param("cid") Integer cid);
    
    
    /**
     * 
     * @Description 分页查询转账订单记录列表
     * @param uid
     * @param tType
     * @param type
     * @param bdate
     * @param edate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<TransferEntity> findAllByPage(@Param("uid") Integer uid,@Param("tType") String tType,
                                       @Param("type") String type,
                                       @Param("bdate") Date bdate,@Param("edate") Date edate,
                                       @Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize);
    
  
    /**
     * 
     * @Description 统计用户总条数
     * @param uid
     * @param tType
     * @param type
     * @param bdate
     * @param edate
     * @return
     */
    Map<String,String> sumTransferTotalCounts(@Param("uid") Integer uid,@Param("tType") String tType,
                                       @Param("type") String type,
                                       @Param("bdate") Date bdate,@Param("edate") Date edate);
}