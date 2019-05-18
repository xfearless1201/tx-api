package com.cn.tianxia.api.utils.pay;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @ClassName XmlUtils
 * @Description xml解析工具类
 * @author Hardy
 * @Date 2018年12月29日 下午2:45:25
 * @version 1.0.0
 */
public class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * 
     * @Description 解析xml文件
     * @param xmlStr
     * @return
     */
    public static JSONObject parseXml(String xmlStr) {
        logger.info("解析xml开始===========START================");
        try {
            JSONObject jsonObject = new JSONObject();
            SAXReader reader = new SAXReader();
            InputStream in = new ByteArrayInputStream(xmlStr.getBytes("utf-8"));
            Document dom = reader.read(in);
            Element root=dom.getRootElement();
            Iterator<Element> eles = root.elementIterator();
            while(eles.hasNext()){
                Element node = eles.next();
                String key = node.getName();
                String val = node.getStringValue();
                jsonObject.put(key, val);  
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("解析xml异常:{}",e.getMessage());
        }
        return null;
    }
    /**
     * map转xml
     * @param parentName 主节点
     * @param params map参数
     * *@param isCDATA 是否转义
     * @return
     * @throws IOException
     */
    public static String getXmlStr(String parentName, Map<String, Object> params, boolean isCDATA) throws Exception{
        Document doc = DocumentHelper.createDocument();
        doc.addElement(parentName);
        String xml = iteratorXml(doc.getRootElement(),parentName,params,isCDATA);
        return formatXML(xml);
    }
    /**
     * 格式化xml,显示为容易看的XML格式
     * 
     * @param inputXML
     * @return
     */
    public static String formatXML(String inputXML){
        String requestXML = null;
        XMLWriter writer = null;
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new StringReader(inputXML));
            if (document != null) {
                StringWriter stringWriter = new StringWriter();
                OutputFormat format = new OutputFormat("    ", true);//格式化，每一级前的空格
                format.setNewLineAfterDeclaration(false);   //xml声明与内容是否添加空行
                format.setSuppressDeclaration(false);       //是否设置xml声明头部
                format.setNewlines(true);       //设置分行
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
            }
            return requestXML;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }finally {
            if (writer != null) { 
                try {
                    writer.close();
                } catch (IOException e) {
                    
                }
            }
        }
    }
    /**
     * 
     * MapToXml循环遍历创建xml节点
     * 此方法在value中加入CDATA标识符
     * 
     * @param element 根节点
     * @param parentName 子节点名字
     * @param params map数据
     * @return String-->Xml
     */
    
    @SuppressWarnings("unchecked")
    public static String iteratorXml(Element element,String parentName,Map<String,Object> params,boolean isCDATA) {
        Element e = element.addElement(parentName);
        Set<String> set = params.keySet();
        for (Iterator<String> it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            if(params.get(key) instanceof Map) {
                iteratorXml(e,key,(Map<String,Object>)params.get(key),isCDATA);
            }else {
                String value = params.get(key)==null?"":params.get(key).toString();
                if(!isCDATA) {
                    e.addElement(key).addText(value);   
                }else {
                    e.addElement(key).addCDATA(value);  
                }
            }
        }
        return e.asXML(); 
    }
    /**
     * xml转json
     * @param xmlStr
     * @return
     * @throws DocumentException
     */
    public static JSONObject xml2Json(String xmlStr){
        try {
            Document doc= DocumentHelper.parseText(xmlStr);
            JSONObject json=new JSONObject();
            dom4j2Json(doc.getRootElement(), json);
            return json;
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.info("xml转换json异常");
            return null;
        }
    }
    /**
     * xml转json
     * @param element
     * @param json
     */
    public static void dom4j2Json(Element element,JSONObject json){
        //如果是属性
        for(Object obj:element.attributes()){
            Attribute attr=(Attribute)obj;
            if(!isEmpty(attr.getValue())){
                json.put("@"+attr.getName(), attr.getValue());
            }
        }
        List<Element> childElement=element.elements();
        if(childElement.isEmpty()&&!isEmpty(element.getText())){//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for(Element e:childElement){//有子元素
            if(!e.elements().isEmpty()){//子元素也有子元素
                JSONObject childJson=new JSONObject();
                dom4j2Json(e,childJson);
                Object obj=json.get(e.getName());
                if(obj!=null){
                    JSONArray jsonArr=null;
                    if(obj instanceof JSONObject){//如果此元素已存在,则转为jsonArray
                        JSONObject jsonObj=(JSONObject)obj;
                        json.remove(e.getName());
                        jsonArr=new JSONArray();
                        jsonArr.add(jsonObj);
                        jsonArr.add(childJson);
                    }
                    if(obj instanceof JSONArray){
                        jsonArr=(JSONArray)obj;
                        jsonArr.add(childJson);
                    }
                    json.put(e.getName(), jsonArr);
                }else{
                    if(!childJson.isEmpty()){
                        json.put(e.getName(), childJson);
                    }
                }


            }else{//子元素没有子元素
                for(Object o:element.attributes()){
                    Attribute attr=(Attribute)o;
                    if(!isEmpty(attr.getValue())){
                        json.put("@"+attr.getName(), attr.getValue());
                    }
                }
                if(!e.getText().isEmpty()){
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }
    /**
     * 获取xml某节点下的所有内容
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param xmlStr 源xml
     * @param beginStr 开始节点
     * @param endStr 结束节点
     * @return
     */
    public static String getXmlStr(String reqXml, String beginStr, String endStr) {
        String resXml = reqXml.substring(reqXml.indexOf(beginStr), reqXml.indexOf(endStr));
        return resXml;
    }
    public static boolean isEmpty(String str) {

        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }
}
