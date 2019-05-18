package com.cn.tianxia.api.service.v2.impl;

import java.util.Date;
import java.util.Map;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.common.v2.PatternUtils;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.UserCardDao;
import com.cn.tianxia.api.po.BaseResponse;
import com.cn.tianxia.api.project.v2.UserCardEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.service.v2.BankCardService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.vo.v2.AddBankCardVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/1/25 15:57
 * @Description: 银行卡服务实现类
 */
@Service
public class BankCardServiceImpl implements BankCardService {

    private static final Logger logger = LoggerFactory.getLogger(BankCardServiceImpl.class);

    @Autowired
    private NewUserDao newUserDao;
    @Autowired
    private UserCardDao userCardDao;

    @Override
    public JSONObject addUserCard(AddBankCardVO addBankCardVO) {
        //用户id
        String uid = addBankCardVO.getUid();

        UserEntity userEntity = newUserDao.selectByPrimaryKey(Integer.valueOf(addBankCardVO.getUid()));
        if (StringUtils.isBlank(userEntity.getQkPwd())) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"需要设置取款密码");
        }

        if (StringUtils.isBlank(addBankCardVO.getBankCode())) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"银行代码不能为空");
        }

        //开户姓名
        String cardUserName = addBankCardVO.getCardUserName();
        if (StringUtils.isBlank(cardUserName)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE ,"开卡人不能为空");
        }

        if (!cardUserName.matches(PatternUtils.REALNAMEREGEX)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE ,"请输入合法的中文名字");
        }

        //银行卡号
        String cardNum = addBankCardVO.getCardNum();
        if (StringUtils.isBlank(cardNum)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE, "卡号不能为空");
        } else {
            if (!cardNum.matches(PatternUtils.CARDNUMBERREGEX)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "请输入14至20位的银行卡号");
            }
        }

        //开户地址
        String cardAddress = addBankCardVO.getCardAddress();
        cardAddress = cardAddress.replaceAll("—— 区 ——",""); // 适配前端传过来的地址地级市有"--区--"字符串
        if (StringUtils.isBlank(cardAddress)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE, "开户地址不能为空");
        } else {
            if (!cardAddress.matches(PatternUtils.CARDADDRREGEX)) {
                return BaseResponse.error(BaseResponse.ERROR_CODE, "请输入合法开户地址");
            }
        }

        //密码
        String password = addBankCardVO.getPassword();
        if (StringUtils.isBlank(password)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE, "密码不能为空");
        }

        try {

            DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
            try {
                password = d.encrypt(password);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("用户:{}添加银行卡密码加密出错:{}",uid,e.getMessage());
                return BaseResponse.error(BaseResponse.ERROR_CODE,"添加银行卡密码加密出错:"+e.getMessage());
            }

            if ( !password.equals(userEntity.getQkPwd())) {
                return BaseResponse.error(BaseResponse.ERROR_CODE,"取款密码错误");
            }

            synchronized (this) {
                // 验证银行卡数量
                UserCardEntity userCardEntity = userCardDao.selectUserCardByUid(uid);
                if (userCardEntity != null) {
                    return BaseResponse.error(BaseResponse.ERROR_CODE, "银行卡数量超过最大数");
                }

                String bankId = addBankCardVO.getBankCode();
                Map<String, String> cardType = userCardDao.selectCardTypeByBankId(bankId);
                if (null == cardType || cardType.isEmpty()) {
                    return BaseResponse.error(BaseResponse.ERROR_CODE, "不存在的银行信息，请检查银行卡ID是否存在");
                }

                UserCardEntity newUserCard = new UserCardEntity();
                newUserCard.setUid(Integer.valueOf(uid));
                newUserCard.setCardUsername(cardUserName);
                newUserCard.setBankId(Integer.valueOf(bankId));
                newUserCard.setCardNum(cardNum);
                newUserCard.setCardAddress(cardAddress);
                newUserCard.setAddTime(new Date());
                newUserCard.setIsDelete("0");

                userCardDao.insertSelective(newUserCard);
            }

            return BaseResponse.success("success");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户:{}添加银行卡异常:{}",uid,e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"添加银行卡异常:"+e.getMessage());
        }

    }

    @Override
    public JSONObject delUserCard(String uid, String cardId, String password) {
        UserEntity userEntity = newUserDao.selectByPrimaryKey(Integer.valueOf(uid));
        if (userEntity == null || StringUtils.isBlank(userEntity.getQkPwd())) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"需要设置取款密码");
        }

        if (StringUtils.isBlank(cardId)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"卡号不能为空");
        }

        if (StringUtils.isBlank(password)) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"取款密码不能为空");
        }

        DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
        try {
            password = d.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户:{}添加银行卡密码加密出错:{}",uid,e.getMessage());
            return BaseResponse.error(BaseResponse.ERROR_CODE,"添加银行卡密码加密出错:"+e.getMessage());
        }

        if ( !password.equals(userEntity.getQkPwd())) {
            return BaseResponse.error(BaseResponse.ERROR_CODE,"取款密码错误");
        }

        userCardDao.deleteByPrimaryKey(Integer.valueOf(cardId));
        return BaseResponse.success("success");

    }

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONArray getUserCard(String uid) {
        logger.info("调用获取用户银行卡信息业务=============START=================");
        JSONArray data = new JSONArray();
        try {
            Map<String,String> cardInfo = userCardDao.selectUserCardInfo(uid);
            if(!CollectionUtils.isEmpty(cardInfo)){
                data.add(cardInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取用户银行卡信息业务异常:{}",e.getMessage());
        }
        return data;
    }
}
