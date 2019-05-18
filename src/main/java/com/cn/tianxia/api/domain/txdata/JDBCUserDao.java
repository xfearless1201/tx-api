package com.cn.tianxia.api.domain.txdata;
/*package com.cn.tianxia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cn.tianxia.dao.UserDao;
import com.cn.tianxia.util.DESEncrypt;
import com.cn.tianxia.util.JDBCTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject; 

*//**
 * 功能概要：UserService实现类
 *  
 *//*
@Service
public class JDBCUserDao{
	@Resource
	private UserDao userDao;
	private String deskey="tianxia88"; 
	public Map<String, Object> selectUserById(Map<String, Object> map) { 
		Map<String, Object> pMap=null;
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="SELECT * FROM t_user WHERE uid = ?";
		try {
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(map.get("uid").toString()));
			rs= pstmt.executeQuery();
			pMap=JDBCTools.convertMap(rs);
		} catch (Exception e) {
			return null;
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		} 
		return pMap;
	}

	public List<Map<String, Object>> selectUserByUserName(String userName) {
		List<Map<String, Object>> pMap=null;
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select username,realname,email,vip_level,mobile,reg_date,login_time,wallet from t_user WHERE uid = ?";
		try {
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,userName);
			rs= pstmt.executeQuery();
			pMap=JDBCTools.convertList(rs);
		} catch (Exception e) {
			return null;
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		} 
		return pMap;
	}
	
	public List<Map<String, Object>> UserLogin(String userName,String passWord) {
		DESEncrypt d=new DESEncrypt(deskey);
		try {
			passWord=d.encrypt(passWord);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		List<Map<String, Object>> pMap=null;
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql=" SELECT * FROM t_user WHERE uid = ? and qk_pwd =?";
		try {
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,userName);
			pstmt.setString(2,passWord);
			rs= pstmt.executeQuery();
			pMap=JDBCTools.convertList(rs);
		} catch (Exception e) {
			return null;
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		} 
		return pMap;
	}

	
	public void insertUser(Map<String, Object> map) {
		DESEncrypt d=new DESEncrypt(deskey);
		
		List<Map<String, Object>> pMap=null;
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="  insert into t_user(username,password,login_ip,reg_ip,ag_username,ag_password,email,vip_level,mobile, "+
		" cagent,is_daili,is_delete,qk_pwd,reg_date,login_time,wallet,top_uid,is_stop,is_mobile,realname,hg_username,type_id) "+
		" values(#{username},#{password},INET_ATON(#{reg_ip}),INET_ATON(#{reg_ip}),#{ag_username},#{ag_password},#{email}, "+
		" #{vip_level},#{mobile},#{cagent},#{is_daili},'0',#{qkpwd},now(),now(), '0',#{top_uid},'0',#{is_mobile},#{realName},'',IFNULL((select "+
		" a.id typeid from t_user_type a left join t_cagent b on a.cid=b.id where b.cagent=#{cagent} and a.isDefault=0 LIMIT 0,1),0)) ";
		
		try { 
			String pwd=map.get("password").toString();
			pwd=d.encrypt(pwd);
			String agpwd=map.get("ag_password").toString();
			agpwd=d.encrypt(agpwd);
			String qkpwd=map.get("qkpwd").toString();
			qkpwd=d.encrypt(qkpwd); 
			
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,map.get("username").toString());
			pstmt.setString(2,pwd);
			pstmt.setString(3,map.get("reg_ip").toString());
			pstmt.setString(4,map.get("reg_ip").toString());
			pstmt.setString(5,map.get("ag_username").toString());
			pstmt.setString(6,agpwd);
			pstmt.setString(7,map.get("email").toString());
			pstmt.setString(8,map.get("vip_level").toString());
			pstmt.setString(9,map.get("mobile").toString());
			pstmt.setString(10,map.get("cagent").toString());
			pstmt.setString(11,map.get("is_daili").toString());
			pstmt.setString(12,qkpwd);
			pstmt.setString(13,map.get("is_mobile").toString());
			pstmt.setString(14,map.get("realName").toString());
			pstmt.setString(15,map.get("cagent").toString()); 
			pstmt.execute(); 
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		}  
	}  
	
	public void updateMoney(Map<String, Object> map) {
		List<Map<String, Object>> pMap=null;
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="update t_user set wallet = wallet + ? where uid=?";
		
		try {  
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1,Integer.parseInt(map.get("wallet").toString()));
			pstmt.setInt(2,Integer.parseInt(map.get("uid").toString()));
			pstmt.execute(); 
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		}  
	}

	
	public   void updateGame(Map<String, Object> map) {
		List<Map<String, Object>> pMap=null;
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String param1="";
		String sql="";
		StringBuffer sbf=new StringBuffer();
		sbf.append("update t_user ");
		if(map.containsKey("is_stop")){
			sbf.append("set is_stop = ?");
			param1=map.get("is_stop").toString();
		}
		if(map.containsKey("hg_username")){
			sbf.append("set hg_username = ?");
			param1=map.get("hg_username").toString();
		}
		if(map.containsKey("password")){
			sbf.append("set password = ?");
			param1=map.get("password").toString();
		}
		if(map.containsKey("qkpwd")){
			sbf.append("set qkpwd = ?");
			param1=map.get("qkpwd").toString();
		}
		if(map.containsKey("login")){
			sbf.append("set login_time = NOW() ,login_ip=INET_ATON(?)");
			param1=map.get("login").toString();
		}
		sbf.append("where username = ?");
		try {  
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,param1);
			pstmt.setString(2,map.get("userName").toString());
			pstmt.execute(); 
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		}  
	}

	
	public void insertTransfer(Map<String, Object> map) { 
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="  insert into t_transfer(uid,billno,username,t_type,t_money,old_money,new_money,type,t_time,ip,result) "+
		" values(?,?,?,?,?,?,?,?,now(),INET_ATON(?),?)"; 
		try {   
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1,Integer.parseInt(map.get("uid").toString()));
			pstmt.setString(2,map.get("billno").toString());
			pstmt.setString(3,map.get("username").toString());
			pstmt.setString(4,map.get("t_type").toString());
			pstmt.setDouble(5,Double.parseDouble(map.get("t_money").toString()));
			pstmt.setDouble(6,Double.parseDouble(map.get("old_money").toString())); 
			pstmt.setDouble(7,Double.parseDouble(map.get("new_money").toString())); 
			pstmt.setString(8,map.get("type").toString());
			pstmt.setString(9,map.get("ip").toString());
			pstmt.setString(10,map.get("result").toString()); 
			pstmt.execute(); 
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		}  
	}
 

	
	public void insertLogin(Map<String, Object> map) {
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql=" insert into t_user_login(uid,login_time,login_ip,is_login,login_num,`status`,is_mobile,address,refurl) "+
		"values(?,now(),INET_ATON(?),'1','1','1',?,?,?)"; 
		try {   
			conn= JDBCTools.getConnection(); 
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1,Integer.parseInt(map.get("uid").toString()));
			pstmt.setString(2,map.get("ip").toString());
			pstmt.setString(3,map.get("is_mobile").toString());
			pstmt.setString(4,map.get("address").toString()); 
			pstmt.setString(5,map.get("refurl").toString()); 
			pstmt.execute(); 
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			JDBCTools.releaseDB(rs, pstmt, conn);
		}  
	}

	
	public List<Map<String, String>> selectTransferCount(Map<String, Object> map) {
		return userDao.selectTransferCount(map);
	}

	
	public List<Map<String, String>> selectTransferInfo(Map<String, Object> map) {
		return userDao.selectTransferInfo(map);
	}

	
	public void insertRechange(Map<String, Object> map) {
		userDao.insertRechange(map);
	}

	
	public List<Map<String, String>> selectReChargeCount(Map<String, Object> map) {
		return userDao.selectReChargeCount(map);
	}

	
	public List<Map<String, String>> selectReChargeInfo(Map<String, Object> map) { 
		return userDao.selectReChargeInfo(map);
	}

	
	public void insertUserCard(Map<String, Object> map) {
		userDao.insertUserCard(map);
	}

	
	public void deleteUserCard(Map<String, Object> map) {
		userDao.deleteUserCard(map);
	}

	
	public List<Map<String, String>> selectUserCard(Map<String, Object> map) { 
		return userDao.selectUserCard(map);
	}

	
	public List<Map<String, String>> checkkpwd(Map<String, Object> map) {
		return userDao.checkkpwd(map);
	}

	
	public void UpdateRechange(Map<String, Object> map) {
		userDao.UpdateRechange(map);
	}

	
	public void insertUserTreasure(Map<String, Object> map) {
		userDao.insertUserTreasure(map);
	}

	
	public List<Map<String, String>> selectWithDrawCount(Map<String, Object> map) { 
		return userDao.selectWithDrawCount(map);
	}

	
	public List<Map<String, String>> selectWithDrawInfo(Map<String, Object> map) { 
		return userDao.selectWithDrawInfo(map);
	}

	
	public Map<String, Object> selectUserInfo(Map<String, Object> map) { 
		return userDao.selectUserInfo(map);
	}

	
	public List<Map<String, String>> selectPlatFromInfo(String KEY) { 
		return userDao.selectPlatFromInfo(KEY);
	}

	
	public List<Map<String, String>> selectWebCom(String type,String cagent) { 
		return userDao.selectWebCom(type,cagent);
	}

	
	public List<Map<String, String>> selectRefererUrl(String domain,String cagent) { 
		
		return userDao.selectRefererUrl(domain,cagent);
	}

	
	public void insertLoginMap(Map<String, String> map) {
		userDao.insertLoginMap(map);
	}

	
	public List<Map<String, String>> selectLoginMap(Map<String, String> map) { 
		return userDao.selectLoginMap(map);
	}

	
	public List<Map<String, Object>> selectReserveAccount(String userName, String cagent) { 
		return userDao.selectReserveAccount(userName, cagent);
	}

	
	public void insertLoginErrorMap(String username) {
		userDao.insertLoginErrorMap(username);
	}

	
	public List<Map<String, Object>> selectLoginErrorMap(String username) { 
		return userDao.selectLoginErrorMap(username);
	}

	
	public void deleteLoginErrorMap(String username) {
		userDao.deleteLoginErrorMap(username);
	}

	
	public List<Map<String, Object>> selectMessageRead(String uid) { 
		return userDao.selectMessageRead(uid);
	}

	
	public List<Map<String, Object>> selectMessageByStatus(String uid, String status, String bdate, String edate) { 
		return userDao.selectMessageByStatus(uid, status, bdate, edate);
	}

	
	public List<Map<String, Object>> selectMessageInfo(String uid, String id) { 
		return userDao.selectMessageInfo(uid, id);
	}

	
	public void updateMessageInfo(String uid, String id) {
		userDao.updateMessageInfo(uid, id);
	}

	
	public void deleteMessage(String uid, String id) {
		userDao.deleteMessage(uid, id);
	}

	
	public List<Map<String, Object>> getProxyUser(String proxyname, String cagent) { 
		return userDao.getProxyUser(proxyname, cagent);
	}

	
	public void deleteLoginMap(String sessionid) {
		userDao.deleteLoginMap(sessionid);
	}

	
	public List<Map<String, String>> getYsepayConfig(String username) { 
		return userDao.getYsepayConfig(username);
	}

	
	public List<Map<String, String>> getBankPayConfig(String uid,String bid) { 
		return userDao.getBankPayConfig(uid,bid);
	}

	
	public void userOnlineDeposit(Map<String, Object> map) {
		 userDao.userOnlineDeposit(map);
	}

	
	public void updateCagentStoredvalue(Map<String, Object> map) {
		userDao.updateCagentStoredvalue(map);
	}

	
	public void insertStoredvalueLog(Map<String, Object> map) {
		userDao.insertStoredvalueLog(map);
	}

	
	public void insertWithdraw(Map<String, Object> map) {
		userDao.insertWithdraw(map);
	}

	
	public Map<String, String> selectUserCardNum(Integer uid) {
		return userDao.selectUserCardNum(uid);
	}

	
	public Map<String, String> queryTotaltimes(Integer uid) {
		return userDao.queryTotaltimes(uid);
	}

	
	public Integer queryDeposittimes(Integer uid) {
		return userDao.queryDeposittimes(uid);
	}

	
	public Map<String, Object> queryWithdrawConfig(String cagent) {
		return userDao.queryWithdrawConfig(cagent);
	}

	
	public String sumTodayWithdraw(Map<String, Object> map) {
		return userDao.sumTodayWithdraw(map);
	}

	
	public Integer insertORUpdate(Map<String, Object> map) {
		return userDao.insertORUpdate(map);
	}

	
	public Map<String, Object> selectWithdrawConfig(Integer uid) {
		return userDao.selectWithdrawConfig(uid);
	}

	
	public List<Map<String, String>>  selectWebComConfig(Map<String, String> map) {
		return userDao.selectWebComConfig(map);
	}

	
	public Map<String, String> selectWebTexttMap(Map<String, String> map) {
		return userDao.selectWebTexttMap(map);
	}

	
	public List<Map<String, String>> selectUserGameStatus(Map<String, Object> map) { 
		return userDao.selectUserGameStatus(map);
	}

	
	public void insertUserGameStatus(Map<String, Object> map) {
		userDao.insertUserGameStatus(map);
	}

	
	public JSONObject queryByTreasurePage(Map<String, String> map) {
		JSONObject json=new JSONObject();
		List<Map<String, String>> treasure=userDao.queryByTreasurePage(map);
		Integer total= userDao.queryByTreasurePageCount(map);
		Integer pageSize=Integer.parseInt(map.get("pageSize"));
		 Integer pageNum=(total+pageSize-1)/pageSize;  
		 json.put("pageNum",pageNum.toString());
		 json.put("total",total==null?"0":total.toString());
		 json.put("data",JSONArray.fromObject(treasure));
		return json;
	}
}
*/