package com.cn.tianxia.api.web.v2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.v2.PatternUtils;
import com.cn.tianxia.api.po.v2.PasswordResponse;
import com.cn.tianxia.api.service.v2.PasswordService;
import com.cn.tianxia.api.utils.RedisUtils;
import com.cn.tianxia.api.vo.v2.PasswordVO;
import com.cn.tianxia.api.web.BaseController;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName PasswordController
 * @Description 密码类接口
 * @author Hardy
 * @Date 2019年2月1日 下午5:34:33
 * @version 1.0.0
 */
@Controller
@RequestMapping("/User")
public class PasswordController extends BaseController{
    
    @Autowired
    private PasswordService passwordService;

    @Autowired
    private RedisUtils redisUtils;
    
    /**
     * 
     * @Description  修改登录密码
     * @param request
     * @param password
     * @param npassword
     * @param renpassword
     * @return
     */
    @RequestMapping("/changePassword")
    @ResponseBody
    public JSONObject updateLoginPassword(HttpServletRequest request, String password, String npassword,String renpassword) {
        logger.info("调用修改用户登录密码接口开始=================START====================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,非法用户");
                return PasswordResponse.error("获取用户ID失败,非法用户");
            }
            String uid = map.get("uid");
            //判断用户请求参数
            JSONObject params = verifyParams(password, npassword, renpassword,1);
            if(params.containsKey("code") && "0".equals(params.getString("code"))){
                //参数错误
                logger.info("调用修改用户登录密码接口请求参数异常:{}",params.getString("msg"));
                return params;
            }
            
            if(password.equalsIgnoreCase(npassword)){
                return PasswordResponse.success("success");
            }
            
            //组装请求参数
            PasswordVO passwordVO = new PasswordVO();
            passwordVO.setUid(uid);
            passwordVO.setPassword(password);
            passwordVO.setNpassword(npassword);
            passwordVO.setRenpassword(renpassword);
            return passwordService.updateLoginPassword(passwordVO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用修改用户登录密码接口异常:{}",e.getMessage());
            return PasswordResponse.error("调用修改用户登录密码接口异常");
        }
    }
    
    /**
     * 
     * @Description 修改取款密码
     * @param request
     * @param password
     * @param npassword
     * @param renpassword
     * @return
     */
    @RequestMapping("/changeQkpwd")
    @ResponseBody
    public JSONObject updateQkPassword(HttpServletRequest request, String password, String npassword, String renpassword) {
        logger.info("调用修改用户取款密码接口开始=================START====================");
        try {
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,非法用户");
                return PasswordResponse.error("获取用户ID失败,非法用户");
            }
            String uid = map.get("uid");
            //判断用户请求参数
            JSONObject params = verifyParams(password, npassword, renpassword,2);
            if(params.containsKey("code") && "0".equals(params.getString("code"))){
                //参数错误
                logger.info("调用修改用户取款密码接口请求参数异常:{}",params.getString("msg"));
                return params;
            }
            
            if(StringUtils.isNotBlank(password) && password.equalsIgnoreCase(npassword)){
                return PasswordResponse.success("success");
            }
            
            //组装请求参数
            PasswordVO passwordVO = new PasswordVO();
            passwordVO.setUid(uid);
            passwordVO.setPassword(password);
            passwordVO.setNpassword(npassword);
            passwordVO.setRenpassword(renpassword);
            return passwordService.updateQkPassword(passwordVO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用修改用户取款密码接口异常:{}",e.getMessage());
            return PasswordResponse.error("调用修改用户取款密码接口异常");
        }
    }
    
    /**
     * 
     * @Description 检查用户取款密码
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/checkQkpwd")
    @ResponseBody
    public JSONObject checkQkpwd(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        logger.info("调用查询用户取款密码接口开始==================start==================");
        try {
            //获取用户ID
            Map<String,String> map = getUserInfoMap(redisUtils, request);
            if(CollectionUtils.isEmpty(map)){
                logger.info("获取用户ID失败,非法用户");
                return PasswordResponse.error("获取用户ID失败,非法用户");
            }
            String uid = map.get("uid");
            //校验用户取款密码
            return passwordService.checkQkpwd(uid);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询用户取款密码异常:{}",e.getMessage());
            return PasswordResponse.error("调用查询用户取款密码异常");
        }
    }
    
    
    /**
     * 
     * @Description 校验请求参数
     * @param password 旧密码
     * @param npassword 新密码
     * @param renpassword 确认密码
     * @param type 密码类型 1 登录密码  2 取款密码
     * @return
     */
    private JSONObject verifyParams(String password, String npassword,String renpassword,int type){
        
        if(StringUtils.isBlank(npassword)){
            return PasswordResponse.error("新密码不能为空");
        }
        
        if(type == 1){
            
            if(StringUtils.isBlank(password)){
                return PasswordResponse.error("密码不能为空");
            }
            
            if(!PatternUtils.isMatch(npassword, PatternUtils.PASSWORDREGEX)){
                return PasswordResponse.error("008");
            }
        }else{
            if(!PatternUtils.isMatch(npassword, PatternUtils.QKPASSWORDREGEX)){
                return PasswordResponse.error("非法密码,请输入4-6位字母数字组合串");
            }
        }
        
        if(!npassword.equals(renpassword)){
            return PasswordResponse.error("两次密码输入不一致");
        }
        
        return PasswordResponse.success("校验成功");
    }
}
