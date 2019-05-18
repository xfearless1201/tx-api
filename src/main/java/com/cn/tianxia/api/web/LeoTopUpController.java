package com.cn.tianxia.api.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.api.common.PayUtil;
import com.cn.tianxia.api.utils.IPTools;
import com.cn.tianxia.api.utils.ipseeker.IPSeeker;
import com.cn.tianxia.api.utils.pay.HttpUtils;
import com.cn.tianxia.api.utils.pay.MD5Utils;
import com.cn.tianxia.api.utils.pay.MapUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName LeoTopUpController
 * @Description LEO充值接口
 * @author Hardy
 * @Date 2019年1月14日 下午9:53:46
 * @version 1.0.0
 */
@Controller
@RequestMapping("Notify")
public class LeoTopUpController {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(LeoTopUpController.class);
    
    static final String payUrl = "https://www.arcpay.info/gateway/payApi/PayApiController/pay";
    
    static final String redirectUrl="redirect:http://pay.tx1888.com/pay.action";
    
    static final String notifyUrl="http://www.baidu.com";
    
    @RequestMapping("/LEONotify.do")
    public String topUp(HttpServletRequest request,HttpServletResponse response,Integer bank,String amount){
        logger.info("[LEO]充值开始==========START============");
        try {
            if(bank == null){
                bank = 0;
            }
            if(StringUtils.isBlank(amount)){
                amount = "20000";//默认5万
            }
            String orderAmount = new DecimalFormat("0.00").format(Double.parseDouble(amount));
            //请求ip
            String ip = StringUtils.isBlank(IPTools.getIp(request))?"127.0.0.1":IPTools.getIp(request);
            String orderNo = PayUtil.createOrderNo("TX", "LEO",2);
            Map<String,String> data = sealRequest(orderNo, orderAmount, bank, ip);
            
            //生成签名
            StringBuffer sb = new StringBuffer();
            Map<String,String> map = MapUtils.sortByKeys(data);
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = map.get(key);
                if(StringUtils.isBlank(val) || "sign".equalsIgnoreCase(key)) continue;
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.toString().replaceFirst("&", "");
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            data.put("sign", sign);
            String html = HttpUtils.toPostForm(data, payUrl);
            return pritln(response,html);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[LEO]充值异常:{}",e.getMessage());
            return pritln(response, "[LEO]充值异常:"+e.getMessage());
        }
    }
    
    
    
    private Map<String,String> sealRequest(String orderNo,String amount,int type,String ip){
        Map<String,String> map = new HashMap<String,String>();
        map.put("version","1.0");//接口版本号,固定值:1.0
        map.put("merchantNo","2019010375");//商户号,商户平台提供
        map.put("memberOrderId",orderNo);//商户唯一订单号
        map.put("payType","B2C");//支付方式
        map.put("createTime",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单时间,格式:yyyyMMddHHmmss
        map.put("orderAmount",amount);//订单金额:单位： 元 兩位小數
        map.put("bankCode",getBankCode(type));//（网银必填） 取值参照网银代码表 4.1
        map.put("goodsInfo","TOP-UP");//商品描述
        String longitude = getIPXY(ip).get("y");// Varchar(20) 必填 加上提高成功率
        String latitude = getIPXY(ip).get("x");// Varchar(20) 必填 加上提高成功率
        map.put("longitude",longitude);//经度,加上提高成功率
        map.put("latitude",latitude);//纬度,加上提高成功率
        map.put("clientIP",ip);//客户IP,加上提高成功率
        map.put("noticeUrl",notifyUrl);//异步通知地址
        map.put("signType","0");//0 MD5 默认 1 RSA
        map.put("key", "az994fMNjw2xDETdEdtvwMbSTj5EsEAn");
        return map;
    }
    
    private String pritln(HttpServletResponse response,String html){
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
            PrintWriter printWriter = response.getWriter();
            printWriter.print(html);
            printWriter.flush();
            printWriter.close();
            return printWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private String getBankCode(int type){
        switch (type) {
        case 1:
            return "CCB";//中国建设银行
        case 2:
            return "PSBC";//中国邮政储蓄银行
        case 3:
            return "ABC";//中国农业银行
        case 4:
            return "ICBC";//中国工商银行
        case 5:
            return "HSBC";//汇丰银行
        case 6:
            return "CEB";//中国光大银行
        case 7:
            return "CIB";//兴业银行
        case 8:
            return "CITIC";//中信银行
        case 9:
            return "SPDB";//上海浦东发展银行
        case 10:
            return "SHBANK";//上海银行
        case 11:
            return "COMM";//交通银行
        case 12:
            return "BOC";//中国银行
        case 13:
            return "BEA";//东亚银行
        case 14:
            return "BCM";//交通银行
        case 15:
            return "BOB";//北京银行
        case 16:
            return "PAB";//平安银行
        case 17:
            return "GDB";//广东发展银行
        case 18:
            return "GZCB";//广州银行
        case 19:
            return "CMBC";//招商银行
        case 20:
            return "CMSB";//中国民生银行
        default:
            return "PSBC";//中国邮政储蓄银行
        }
    }
    
    private Map<String, String> getIPXY(String ip) {
        if (null == ip) {
            ip = "";
        }
        Map<String, String> xyMap = new HashMap<>();
        try {
            URL url = new URL("http://ip-api.com/json/" + ip);
            InputStream inputStream = url.openStream();
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputReader);
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = reader.readLine()) != null) {
                sb.append(str.trim());
            }
            reader.close();
            logger.info("获取经纬度:" + sb.toString());
            // 解析sb内容
            Map<String, Object> strMap = JSONObject.fromObject(sb.toString());
            String status = strMap.get("status").toString();
            if (strMap != null && status.equals("success")) {
                String lat = strMap.get("lat").toString();
                String lon = strMap.get("lon").toString();
                xyMap.put("x", lat);
                xyMap.put("y", lon);
            } else {
                xyMap.put("x", "0");
                xyMap.put("y", "0");
            }
            return xyMap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xyMap;
    }
    
    @RequestMapping("/IPAddressNotify.do")
    @ResponseBody
    public String IPAddressNotify(HttpServletRequest request,HttpServletResponse response,String ip){
        IPSeeker ips = IPSeeker.getInstance();
        String result = ips.getAddress(ip);
        return result;
    }
    
}
