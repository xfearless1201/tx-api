package com.cn.tianxia.api.service.v2.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.cn.tianxia.api.domain.txdata.v2.PlatformConfigDao;
import com.cn.tianxia.api.game.impl.BGGameServiceImpl;
import com.cn.tianxia.api.project.v2.PlatformConfigEntity;
import com.cn.tianxia.api.service.v2.LotteryDrawService;
import com.cn.tianxia.api.utils.PlatFromConfig;

import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;


/**
 * 
 * @ClassName LotteryDrawServiceImpl
 * @Description 彩票开始接口实现类
 * @author Hardy
 * @Date 2019年3月8日 上午11:28:11
 * @version 1.0.0
 */
@Service
public class LotteryDrawServiceImpl implements LotteryDrawService {
    
    private static final Logger logger = LoggerFactory.getLogger(LotteryDrawServiceImpl.class);

    private static final String  CONTENT_TYPE = "Content-Type";
    private static final String  APPLICATION  = "application/x-www-form-urlencoded";
    private static final String  CHARSET = "charset";
    private static final String  UTF = "utf-8";

    @Autowired
    private PlatformConfigDao platformConfigDao;
    
    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public JSONObject lotteryDrawBGGame(String token, String lotteryId, String method, String isMobile, String pageSize,
            String startTime, String endTime) {
        logger.info("调用BG游戏彩票开奖查询业务开始=================START====================");
        JSONObject result = new JSONObject();
        try {
            //获取所有游戏配置信息
            List<PlatformConfigEntity> platformConfigs = platformConfigDao.findAll();
            if(CollectionUtils.isEmpty(platformConfigs)){
                logger.info("BG游戏维护中,获取BG游戏配置信息为空");
                result.put("code", "error");
                result.put("msg", "查询游戏配置信息失败");
                return result;
            }
            
            //转成map
            Map<String,String> data = platformConfigs.stream().collect(Collectors.toMap(PlatformConfigEntity::getPlatformKey, PlatformConfigEntity::getPlatformConfig));
            if(CollectionUtils.isEmpty(data)){
                logger.info("BG游戏维护中,获取BG游戏配置信息为空");
                result.put("code", "error");
                result.put("msg", "查询游戏配置信息失败");
                return result;
            }
            
            PlatFromConfig pf = new PlatFromConfig();
            pf.InitData(data,"BG");
            if ("0".equals(pf.getPlatform_status())) {
                result.put("code", "error");
                result.put("msg", "process");
                return result;
            }
            
            BGGameServiceImpl gameService = new BGGameServiceImpl(net.sf.json.JSONObject.fromObject(data));
            JSONObject jsonObject = gameService.lotteryCheck(token, lotteryId, method, isMobile, pageSize, startTime, endTime);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用BG游戏彩票开奖查询业务异常:{}",e.getMessage());
            result.put("code", "error");
            result.put("msg", "调用BG游戏彩票开奖查询业务异常");
            return result;
            
        }
    }

    public static void main(String[] arg){
        LotteryDrawServiceImpl lotteryDrawService  = new LotteryDrawServiceImpl();
        net.sf.json.JSONObject jsonObject = lotteryDrawService.lotteryDrawIGGame("12",null);
    }

    @Override
    public net.sf.json.JSONObject lotteryDrawIGGame(String lotteryId, String dateTime) {
        logger.info("lotteryDrawIGGame(String lotteryId, String dateTime = {},{}",lotteryId,dateTime);
        net.sf.json.JSONObject result = new net.sf.json.JSONObject();
        try{
            /**
             * 兼容旧接口,根据前端请求参数lotteryId获取对应的开奖记录
             *
             * lotteryId = ?
             * 12 重庆时时彩
             * 14 新疆时时彩
             * 26 安徽快三
             * 31 广东十一选五
             */
            StringBuilder stringBuilder = new StringBuilder("https://ig185.com/kaijiangweb/getHistoryList.do?");
            String url = null;
            switch(lotteryId){
                case "12":
                    url = "https://ig185.com/kaijiangweb/getLotteryInfo.do?issue=&lotCode=SSC";
                    break;
                case "14":
                    url = "https://ig185.com/kaijiangweb/getLotteryInfo.do?issue=&lotCode=JSSC";
                    break;
                case "26":
                    url = "https://ig185.com/kaijiangweb/getLotteryInfo.do?issue=&lotCode=AHK3";
                    break;
                case "31":
                    url = "https://ig185.com/kaijiangweb/getLotteryInfo.do?issue=&lotCode=GD115";
                    break;
            }
            net.sf.json.JSONObject jsonObject = sendGet(url);
            if(StringUtils.isEmpty(jsonObject.toString())){
                logger.info("查询彩票开奖记录响应数据为空！");
            }
            net.sf.json.JSONObject returnJson = new net.sf.json.JSONObject();
            if(jsonObject.get("errorCode").toString().equals("0")){
                JsonConfig jsonConfig = new JsonConfig();
                jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
                net.sf.json.JSONObject jsonObject1 =net.sf.json.JSONObject.fromObject(jsonObject.get("result"));
                StringBuilder sb = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                returnJson = new net.sf.json.JSONObject();
                if (lotteryId.equals("31")){
                    returnJson.put("issue",jsonObject1.get("preDrawIssue").toString().substring(4,10));
                }else if(lotteryId.equals("14")){
                    returnJson.put("issue",jsonObject1.get("preDrawIssue"));
                }else{
                    returnJson.put("issue",jsonObject1.get("preDrawIssue").toString().substring(5,11));
                }

                Date date  = new Date(Long.valueOf(jsonObject1.get("serverTime").toString()));
                returnJson.put("resultTime",sdf.format(date));
                returnJson.put("resultStr",jsonObject1.getString("preDrawCode").replace("[","").replace("]",""));
                result.put("code","success");
                result.put("message","IG查询开奖记录成功!");
                Map<String,Object> map = new HashMap<>();
                Map<String,Object> map1 = new HashMap<>();
                map1.put("items",returnJson);
                map.put("result",map1);
                result.put("params",map);
                result.put("obj", true);
                return result;
            }

            result.put("code","error");
            result.put("message","IG查询开奖记录失败!");
            result.put("params",new net.sf.json.JSONObject());
            result.put("obj", false);
            return result;

        }catch (Exception e){
            logger.info("查询开奖记录失败！",e);
            result.put("code","error");
            result.put("message","IG查询开奖记录失败!");
            result.put("params",new net.sf.json.JSONObject());
            result.put("obj", false);
            return result;
        }

    }


    public net.sf.json.JSONObject sendGet(String url) throws  Exception{
        logger.debug(" senGet(String url = -start {}" , url );
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url);
        net.sf.json.JSONObject retrunJson = new net.sf.json.JSONObject();
        try {
            get.setRequestHeader(CONTENT_TYPE, APPLICATION);
            get.setRequestHeader(CHARSET, UTF);
            int statusCode = client.executeMethod(get);
            if (statusCode == 200) {
                InputStream inputStream = get.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String readStr;
                while ((readStr = br.readLine()) != null) {
                    retrunJson = net.sf.json.JSONObject.fromObject(readStr);
                }
            }else {
                logger.error("发起HTTP失败,失败状态码："+ statusCode);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  e;
        }finally {
            get.releaseConnection();
        }
        return  retrunJson;


    }

}
