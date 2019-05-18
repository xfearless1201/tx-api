package com.cn.tianxia.api.service.v2.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cn.tianxia.api.common.v2.DatePatternConstant;
import com.cn.tianxia.api.common.v2.DatePatternUtils;
import com.cn.tianxia.api.common.v2.KeyConstant;
import com.cn.tianxia.api.common.v2.PatternUtils;
import com.cn.tianxia.api.common.v2.SystemConfigLoader;
import com.cn.tianxia.api.domain.txdata.v2.CagentDao;
import com.cn.tianxia.api.domain.txdata.v2.JuniorProxyUserDao;
import com.cn.tianxia.api.domain.txdata.v2.NewUserDao;
import com.cn.tianxia.api.domain.txdata.v2.ProxyUserDao;
import com.cn.tianxia.api.domain.txdata.v2.ReserveAccountDao;
import com.cn.tianxia.api.domain.txdata.v2.UserLoginDao;
import com.cn.tianxia.api.domain.txdata.v2.UserQuantityDao;
import com.cn.tianxia.api.domain.txdata.v2.UserTypeDao;
import com.cn.tianxia.api.domain.txdata.v2.UserWalletDao;
import com.cn.tianxia.api.po.v2.RegisterResponse;
import com.cn.tianxia.api.project.v2.CagentEntity;
import com.cn.tianxia.api.project.v2.ReserveAccountEntity;
import com.cn.tianxia.api.project.v2.UserEntity;
import com.cn.tianxia.api.project.v2.UserLoginEntity;
import com.cn.tianxia.api.project.v2.UserQuantityEntity;
import com.cn.tianxia.api.project.v2.UserWalletEntity;
import com.cn.tianxia.api.service.v2.RegisterService;
import com.cn.tianxia.api.service.v2.ShortMessageService;
import com.cn.tianxia.api.utils.DESEncrypt;
import com.cn.tianxia.api.vo.v2.JuniorProxyUserVO;
import com.cn.tianxia.api.vo.v2.ProxyUserVO;
import com.cn.tianxia.api.vo.v2.RegisterVO;

import net.sf.json.JSONObject;

/**
 * @Auther: zed
 * @Date: 2019/1/23 20:01
 * @Description: 注册服务实现
 */
@Service
public class RegisterServiceImpl implements RegisterService {

    private static Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Autowired
    private SystemConfigLoader systemConfigLoader;
    @Autowired
    private NewUserDao newUserDao;
    @Autowired
    private ReserveAccountDao reserveAccountDao;
    @Autowired
    private UserLoginDao userLoginDao;
    @Autowired
    private UserWalletDao userWalletDao;
    @Autowired
    private UserQuantityDao userQuantityDao;
    @Autowired
    private CagentDao cagentDao;

    @Autowired
    private UserTypeDao userTypeDao;

    @Autowired
    private ProxyUserDao proxyUserDao;

    @Autowired
    private JuniorProxyUserDao juniorProxyUserDao;

    @Autowired
    private ShortMessageService shortMessageService;

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject verifyAccount(String cagent, String userName) {
        logger.info("------------异步检查用户业务开始---------------------");
        if (StringUtils.isBlank(cagent)) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "error", "平台号为空");
        }

        String systemAgent = systemConfigLoader.getProperty("cagent");
        if (StringUtils.isNotBlank(systemAgent)) {
            cagent = systemAgent;
        }

        if (!cagent.matches(PatternUtils.CAGENTREGEX)) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "error", "平台号格式不正确");
        }

        if (StringUtils.isBlank(userName) || !userName.matches(PatternUtils.USERNAMEREGEX)) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "001", "用户名格式不正确");
        }

        // 检测用户是否存在
        UserEntity userEntity = newUserDao.selectByUsername(cagent + userName);
        if (null != userEntity) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "009", "用户已存在");
        }
        // 检测是否为系统保留账户
        ReserveAccountEntity reserveAccountEntity = reserveAccountDao.selectReserveAccount(userName, cagent);
        if (null != reserveAccountEntity) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "009", "用户已存在");
        }
        return RegisterResponse.success("000");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public JSONObject register(RegisterVO registerVO) {
        logger.info("------------用户注册业务开始---------------------");
        try {
            String cagent = registerVO.getCagent();
            String userName = registerVO.getUserName();
            String refererUrl = registerVO.getRefererUrl();
            registerVO.setRefererUrl(refererUrl.split("/")[2]);
            refererUrl = registerVO.getRefererUrl();
            String systemAgent = systemConfigLoader.getProperty("cagent"); // 配置文件中获取平台号
            if (StringUtils.isNotBlank(systemAgent)) {
                cagent = systemAgent;
                registerVO.setCagent(systemAgent);
            }
            //验证注册请求参数
            JSONObject verifyParam = verifyParams(registerVO); // 验证参数
            if (null != verifyParam) {
                return verifyParam;
            }
            String isMobile = registerVO.getIsMobile();
            if (StringUtils.isBlank(isMobile)) {
                registerVO.setIsMobile("0");
                isMobile = "0";
            }
            // 构造新用户实体类
            UserEntity newUser = sealUserEntity(registerVO);
            // 生成agpwd
            String agpwd = cagent + RandomUtils.nextInt(100000, 999999);
            String referralCode = StringUtils.isBlank(registerVO.getReferralCode())?"":registerVO.getReferralCode().replace(" ","");
            String remark = StringUtils.isBlank(registerVO.getRemark())?"":registerVO.getRemark();
            if (PatternUtils.isMatch(remark, PatternUtils.COMMONREGEX)) {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE, "请输入合法字符");
            }
            //平台商信息
            CagentEntity cagentEntity = cagentDao.selectByCagent(cagent);
            if (null == cagentEntity) {
                logger.info("获取到的平台商信息:cagentEntity为空，平台编码cagent:{}", cagent);
                return RegisterResponse.error(RegisterResponse.ERROR_CODE,"平台商信息异常,平台编码:"+ cagent);
            }
            logger.info("获取到的平台商信息:cagent:{}",cagentEntity);

            // 通过平台编码查询用户的分层ID
            Integer typeId = userTypeDao.getUserTypeId(cagentEntity.getId());
            if (typeId == null) {
                typeId = 0;
            }
            logger.info("用户注册业务,注册用户名:【" + userName + "】,平台编码:【" + cagent + "】,平台商对应用户分层id:{}",typeId);
            // 根据推荐账号,更新代理商
            int topUid=0;//一级代理
            int juniorUid=0;//二级代理
            int count = 0;// 代理等级2 一级代理商 3 二级代理商
            // 先根据推荐码进行判断用户来源
            if (StringUtils.isNotBlank(referralCode)) {
                count = StringUtils.countMatches(referralCode.toUpperCase(), "A");
            }

            boolean hasReferralCode = false; //通过推荐码获取代理商信息标志

            logger.info("用户注册业务,注册用户名:【" + userName + "】,平台编码:【" + cagent + "】,推荐码:【" + referralCode + "】");
            // 判断用户代理等级没有则归类到平台中心
            if (count == 2) {
                ProxyUserVO proxyUser = proxyUserDao.getProxyUserByrefererCode(referralCode,cagent);
                if (null != proxyUser) {
                    hasReferralCode = true;
                    logger.info("用户注册业务,注册用户名:【" + userName + "】,平台编码:【" + cagent + "】,对应代理商为一级代理商");
                    if (StringUtils.isNotBlank(proxyUser.getPid().toString())) {
                        topUid = proxyUser.getPid().intValue();
                    }
                    if (0 != proxyUser.getdUserType()) {
                        typeId = proxyUser.getdUserType();
                    }
                }else{

                    ProxyUserVO proxyUserVO = proxyUserDao.findProxyUserByrefererCode(referralCode);
                    if (proxyUserVO != null){
                        logger.info("用户填入的推荐码信息为：dUserType{},pid{}",proxyUserVO.getdUserType(),proxyUserVO.getPid());
                        logger.info("用户【"+userName+"】填写平台推荐码错误！推荐码与当前注册平台没有关联！ 注册失败！  一级代理推荐码！");
                        throw  new Exception("平台推荐码输入错误,请重新输入或不填写推荐码！");
                    }

                    logger.info("用户【"+userName+"】填写的平台推荐码错误！找不到对应代理的推荐码！");

                }
            } else if (count == 3) {
                JuniorProxyUserVO juniorProxyUser = juniorProxyUserDao.getJuniorProxyUserByrefererCode(referralCode,cagent);
                if (null != juniorProxyUser) {
                    hasReferralCode = true;
                    logger.info("用户注册业务,注册用户名:【" + userName + "】,平台编码:【" + cagent + "】,对应代理商为二级代理商");
                    if (null != juniorProxyUser.getUpId()) {
                        topUid = Integer.parseInt(juniorProxyUser.getUpId());
                    }
                    if (0 != juniorProxyUser.getdUserType()) {
                        typeId = juniorProxyUser.getdUserType();
                    }
                    if (null != juniorProxyUser.getPid()) {
                        juniorUid = juniorProxyUser.getPid().intValue();
                    }
                }else{
                    JuniorProxyUserVO juniorProxyUserVO = juniorProxyUserDao.findJuniorProxyUserByrefererCode(referralCode);

                    if(juniorProxyUserVO!= null){
                        logger.info("用户【"+userName+"】填入的推荐码信息为：dUserType{},pid{}",juniorProxyUserVO.getdUserType(),juniorProxyUserVO.getPid());
                        logger.info("用户【"+userName+"】填写平台推荐码错误！推荐码与当前注册平台没有关联！ 注册失败！ 二级代理推荐码！");
                        throw  new Exception("平台推荐码输入错误,请重新输入或不填写推荐码！");
                    }

                    logger.info("用户【"+userName+"】填写的平台推荐码错误！找不到对应代理的推荐码！");

                }
            }
            if (!hasReferralCode) {
                // 根据来源域名更新代理商
                String proxyname = registerVO.getProxyname();
                if(StringUtils.isNotBlank(proxyname) && PatternUtils.isMatch(proxyname, PatternUtils.COMMONREGEX)){
                    return RegisterResponse.error(RegisterResponse.ERROR_CODE, "请输入合法的代理商账号");
                }

                if(StringUtils.isBlank(proxyname)){
                    String domain = refererUrl;// cdsr.com
                    logger.info("来源域名更新代理商_first：" + userName + "--------" + "代理商:" + cagent + "-------" + "域名" + domain);
                    if (domain.indexOf(":") > 0) { // 去掉":"后面的":"和端口号
                        domain = domain.substring(0, domain.indexOf(":"));
                    }
                    logger.info("来源域名更新代理商_second：" + userName + "--------" + "代理商:" + cagent + "-------" + "域名" + domain);
                    if (!domain.matches(PatternUtils.IPREGEX) && !"localhost".equals(domain)) {
                        Pattern p = Pattern.compile(PatternUtils.DOMAINREGEX);
                        Matcher m = p.matcher(domain);
                        List<String> strList = new ArrayList<>();
                        while (m.find()) {
                            strList.add(m.group());
                        }
                        domain = strList.toString();
                        domain = domain.substring(1, domain.length() - 1);
                    }
                    logger.info("来源域名更新代理商_third：" + userName + "--------" + "代理商:" + cagent + "-------" + "域名" + domain);
                    if (StringUtils.isNotBlank(domain)) {    //如果是非法域名，proxyname为空
                        // 根据平台号查出所有代理商账号域名
                        List<Map<String, String>> ProxyList = newUserDao.selectProxyByCagent(cagentEntity.getId());
                        if (CollectionUtils.isNotEmpty(ProxyList) && ProxyList.get(0) != null) {
                            // 遍历代理商账户对应域名列表
                            loop:for (Map<String, String> proxy : ProxyList) {
                                if (null != proxy && proxy.containsKey("domain")
                                        && StringUtils.isNotBlank(proxy.get("domain"))) {
                                    // 代理域名不为空时，如果域名匹配上代理域名，设置代理账号名称，跳出循环
                                    String[] proxyDomains = proxy.get("domain").split(";");
                                    for (String proxyDomain : proxyDomains) {
                                        if (domain.equals(proxyDomain.trim())) {
                                            proxyname = proxy.get("user_name");
                                            logger.info("域名【" + domain + "】查询到的代理商proxyname:{}", proxyname);
                                            break loop;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                logger.info("用户名:【" + userName + "】,平台编码:【" + cagent + "】,通过域名:【" + refererUrl + "】查询到的代理用户名proxyName:{}",proxyname);
                if (StringUtils.isNotBlank(proxyname)) {
                    //获取用户代理信息
                    ProxyUserVO proxyUser = proxyUserDao.getProxyUser(proxyname, cagentEntity.getId());

                    if (null != proxyUser) {
                        logger.info("用户名:【" + userName + "】,平台编码:【" + cagent + "】,对应代理商为一级代理商");
                        if (null != proxyUser.getPid()) {
                            topUid = proxyUser.getPid().intValue();
                        }
                        if (0 != proxyUser.getdUserType()) {
                            typeId = proxyUser.getdUserType();
                        }
                    } else {
                        JuniorProxyUserVO juniorProxyUser = juniorProxyUserDao.getJuniorProxyUser(proxyname, cagentEntity.getId());
                        if (null != juniorProxyUser) {
                            logger.info("用户名:【" + userName + "】,平台编码:【" + cagent + "】,对应代理商为二级代理商");
                            if (null != juniorProxyUser.getPid()) {
                                juniorUid = juniorProxyUser.getPid().intValue();
                            }
                            if (StringUtils.isNotBlank(juniorProxyUser.getUpId())) {
                                topUid = Integer.parseInt(juniorProxyUser.getUpId());
                            }
                            if (0 != juniorProxyUser.getdUserType()) {
                                typeId = juniorProxyUser.getdUserType();
                            }
                        } else {
                            logger.info("用户名:【" + userName + "】,平台编码:【" + cagent + "】,域名:【" + refererUrl + "】,代理用户名【" + proxyname + "】,未找到代理商信息,为平台会员");
                        }
                    }
                }
            }

            //构建用户信息VO类
            remark = StringUtils.isBlank(referralCode)?remark:remark + "\n 推介码=" + referralCode;
            newUser.setRmk(remark);
            newUser.setAgPassword(agpwd);
            newUser.setRegurl(refererUrl);
            if ("mobile".equals(registerVO.getFrom()))  // 注册来源
                newUser.setLoginmobile(registerVO.getMobileNo());
            newUser.setTopUid(topUid);
            newUser.setJuniorUid(juniorUid);
            newUser.setTypeId(typeId);
            newUser.setIsDelete("0");
            DESEncrypt d = new DESEncrypt(KeyConstant.DESKEY);
            // 密码加密
            newUser.setPassword(d.encrypt(newUser.getPassword()));
            if (StringUtils.isNotBlank(newUser.getQkPwd())) {
                newUser.setQkPwd(d.encrypt(newUser.getQkPwd()));
            }
            if (StringUtils.isNotBlank(newUser.getAgPassword())) {
                newUser.setAgPassword(d.encrypt(newUser.getAgPassword()));
            }
            logger.info("插入用户表,User:{}",newUser);
            // 插入用户表
            newUserDao.insertSelective(newUser);

            UserEntity userEntity = newUserDao.selectByUsername(registerVO.getCagent() + registerVO.getUserName());
            if (userEntity != null) {
                // 插入用户打码量表\
                UserQuantityEntity userQuantityEntity = new UserQuantityEntity();
                userQuantityEntity.setCid(cagentEntity.getId());
                userQuantityEntity.setUid(userEntity.getUid());
                userQuantityEntity.setMarkingQuantity(0.0);
                userQuantityEntity.setUserQuantity(0.0);
                userQuantityEntity.setUserQuantityHistory(0.0);
                userQuantityEntity.setUserWinamount(0.0);
                userQuantityEntity.setWinamount(0.0);

                logger.info("插入用户打码量表,userQuantity:{}",userQuantityEntity);
                userQuantityDao.insertSelective(userQuantityEntity);

                // 插入用户钱包表
                UserWalletEntity userWalletEntity = new UserWalletEntity();
                userWalletEntity.setUid(userEntity.getUid());
                userWalletEntity.setType("1");
                userWalletEntity.setBalance(0.0);
                userWalletEntity.setFrozenBalance(0.0);
                userWalletEntity.setUptime(new Date());

                logger.info("插入用户积分表,userWallet:{}",userWalletEntity);
                userWalletDao.insertSelective(userWalletEntity);

                String address = registerVO.getAddress();
                // 登录日志
                UserLoginEntity userLoginEntity = new UserLoginEntity();
                userLoginEntity.setUid(userEntity.getUid());
                userLoginEntity.setLoginIp(registerVO.getLoginIp());
                userLoginEntity.setIsMobile(isMobile);
                userLoginEntity.setIsLogin((byte) 1);
                userLoginEntity.setLoginNum(1);
                userLoginEntity.setStatus("1");
                userLoginEntity.setRefurl(refererUrl);
                userLoginEntity.setAddress(address);
                userLoginEntity.setLoginTime(new Date());

                logger.info("插入用户登录日志,userLogin:{}",userLoginEntity);
                userLoginDao.insertSelective(userLoginEntity);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", userEntity.getUid());
                jsonObject.put("username", userEntity.getUsername());
                jsonObject.put("realname", userEntity.getRealname());
                jsonObject.put("ag_username", userEntity.getAgUsername());
                jsonObject.put("hg_username", userEntity.getHgUsername());
                jsonObject.put("ag_password", userEntity.getAgPassword());

                jsonObject.put("loginmobile", userEntity.getLoginmobile() == null?"":userEntity.getLoginmobile());
                jsonObject.put("cagent", userEntity.getCagent());
                jsonObject.put("balance", userEntity.getWallet());
                jsonObject.put("isMobile", isMobile);

                Double integralBalance = userWalletDao.getIntegralBalance(userEntity.getUid());
                if (integralBalance == null) {
                    integralBalance = 0.00D;
                }
                jsonObject.put("integral", integralBalance);

                jsonObject.put("cid", cagentEntity.getId());
                jsonObject.put("typeid", userEntity.getTypeId());// 分层ID
                jsonObject.put("login_time", DatePatternUtils.dateToStr(userEntity.getLoginTime(), DatePatternConstant.NORM_DATETIME_PATTERN));

                logger.info("用户注册业务成功,返回信息:{}",jsonObject.toString());

                return RegisterResponse.success(jsonObject);
            }
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "用户注册异常：插入用户失败，查询返回为空");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户注册异常：" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "用户注册异常：" + e.getMessage());
        }

    }

    /**
     * 验证参数
     *
     * @param registerVO
     * @return
     */
    private JSONObject verifyParams(RegisterVO registerVO) {
        boolean fromPC = !"mobile".equals(registerVO.getFrom());
        // 推荐码
        String referralCode = registerVO.getReferralCode();
        if (StringUtils.isNotBlank(referralCode) && referralCode.matches(PatternUtils.COMMONREGEX)) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "请输入合法推荐码");
        }
        // 代理平台
        String cagent = registerVO.getCagent();
        if (StringUtils.isBlank(cagent)) {
            logger.error("注册用户错误:代理平台号不能为空！");
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "error","代理平台号不能为空！");
        }
        // 手机号
        String mobileNo = registerVO.getMobileNo();
        if (StringUtils.isBlank(mobileNo)) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "003", "手机号不能为空")
                    : RegisterResponse.error(RegisterResponse.ERROR_CODE, "手机号不能为空");
        }
        if (!mobileNo.matches(PatternUtils.PHONENOREGEX)) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "018", "手机号格式不正确")
                    : RegisterResponse.error(RegisterResponse.ERROR_CODE, "手机号格式不正确");
        }

        // 用户名
        String userName = registerVO.getUserName();
        if (StringUtils.isNotBlank(userName) && !userName.matches(PatternUtils.USERNAMEREGEX)) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "002","用户名格式不正确")
                    : RegisterResponse.error(RegisterResponse.ERROR_CODE, "用户名格式不正确");
        } else if (StringUtils.isNotBlank(userName) && userName.matches(PatternUtils.PHONENOREGEX) && !userName.equals(mobileNo)) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "002","用户名格式不正确")
                    : RegisterResponse.error(RegisterResponse.ERROR_CODE, "用户名格式不正确");
        } else if (StringUtils.isBlank(userName)) {
            registerVO.setUserName(mobileNo);
            userName = mobileNo;
        }
        // 真实姓名
        String realName = registerVO.getRealName();
        if (StringUtils.isNotBlank(realName) && !realName.matches(PatternUtils.REALNAMEREGEX)) {
            return RegisterResponse.error(RegisterResponse.ERROR_CODE, "真实姓名不合法");
        } else if (StringUtils.isBlank(realName)) {
            registerVO.setRealName("会员");
        }
        if (fromPC) {
            // 登录密码
            String passWord = registerVO.getPassWord();
            if (StringUtils.isBlank(passWord)) {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE, "005","登录密码不能为空");
            }
            // 确认登录密码
            String rePassWord = registerVO.getRepassWord();
            if (StringUtils.isBlank(rePassWord)) {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE, "006","确认登录密码不能为空");
            }
            // 登录密码和确认登录密码不同
            if (!passWord.equals(rePassWord)) {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE, "007","登录密码和确认登录密码不同");
            }
            // 登录密码长度小于5或大于20
            if (passWord.length() < 5 || passWord.length() > 20) {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE, "008","登录密码长度小于5或大于20");
            }
            // 取款密码
            String qkpwd = registerVO.getQkpwd();
            String reqkpwd = registerVO.getReqkpwd();
            if (StringUtils.isNotBlank(qkpwd)) {
                if (StringUtils.isBlank(reqkpwd)) {
                    return RegisterResponse.error(RegisterResponse.ERROR_CODE, "014","确认取款密码不能为空");
                }
                if (!qkpwd.equals(reqkpwd)) {
                    return RegisterResponse.error(RegisterResponse.ERROR_CODE, "015", "取款密码两次输入不一致");
                }
                if (qkpwd.length() < 4) {
                    return RegisterResponse.error(RegisterResponse.ERROR_CODE, "016", "取款密码长度小于4");
                }

                if (qkpwd.equals(passWord)) {
                    return RegisterResponse.error(RegisterResponse.ERROR_CODE, "017", "取款密码不能与登录密码一致");
                }
            } else {
                registerVO.setQkpwd(""); // 如果取款密码为空，设置取款密码为空
            }
        } else {
            String passWord = registerVO.getPassWord(); // 手机端如果密码为空，默认123456
            if (StringUtils.isBlank(passWord)) {
                registerVO.setPassWord(com.cn.tianxia.api.utils.pay.RandomUtils.generateString(6));
            }
            registerVO.setQkpwd("");
        }
        // 验证用户名是否已存在
        // 检测用户是否存在
        UserEntity userEntity = newUserDao.selectByUsername(cagent + userName);
        if (null != userEntity) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "009", "该用户已存在")
                    :RegisterResponse.error(RegisterResponse.ERROR_CODE, "该用户已存在");
        }

        // 检测游离表的用户是否存在
        UserEntity disUserEntity = newUserDao.selectDisUserByUserName(cagent + userName);
        if (null != disUserEntity) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "009", "该用户已存在")
                    :RegisterResponse.error(RegisterResponse.ERROR_CODE, "该用户已存在");
        }

        // 检测用户是否为系统保留账户
        ReserveAccountEntity reserveAccountEntity = reserveAccountDao.selectReserveAccount(userName, cagent);
        if (null != reserveAccountEntity) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "009", "该用户已存在")
                    :RegisterResponse.error(RegisterResponse.ERROR_CODE, "该用户已存在");
        }

        // 检测手机号是否已绑定
        UserEntity userEntity1 = newUserDao.selectUserByMobile(cagent, mobileNo);
        if (null != userEntity1) {
            return fromPC ? RegisterResponse.error(RegisterResponse.ERROR_CODE, "019", "手机号已绑定")
                    :RegisterResponse.error(RegisterResponse.ERROR_CODE, "手机号已绑定");
        }
        // 验证码
        if (!fromPC) {
            // 手机端验证短信验证码
            String msgCode = registerVO.getMsgCode();
            if (StringUtils.isBlank(msgCode)) {
                return RegisterResponse.error(RegisterResponse.ERROR_CODE, "短信验证码为空，请输入短信验证码");
            }
            JSONObject verifyMsgCode = shortMessageService.verifyMsgCode(cagent, mobileNo,  msgCode,"1", -5);
            if (null != verifyMsgCode) {
                return verifyMsgCode;
            }
        }
        return null;
    }

    private UserEntity sealUserEntity(RegisterVO registerVO) {
        UserEntity entity = new UserEntity();

        String userName = registerVO.getCagent().toLowerCase() + registerVO.getUserName().toLowerCase();
        entity.setUsername(userName);
        entity.setPassword(registerVO.getPassWord());
        entity.setRegIp(registerVO.getLoginIp());
        entity.setLoginIp(registerVO.getLoginIp());
        entity.setHgUsername(userName);
        entity.setAgUsername(userName);
        entity.setEmail("");
        entity.setVipLevel("1");
        entity.setMobile(registerVO.getMobileNo());
        entity.setIsDaili("0");
        entity.setTopUid(0);
        entity.setIsMobile(registerVO.getIsMobile());
        entity.setCagent(registerVO.getCagent());
        entity.setQkPwd(registerVO.getQkpwd());
        entity.setRealname(registerVO.getRealName());
        entity.setAgPassword(registerVO.getAgpassword());

        return entity;
    }
}
