/**
 * LogoutPlayerResponseType0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cn.tianxia.api.ws;

public class LogoutPlayerResponseType0  implements java.io.Serializable {
    private LogoutPlayerResponse logoutPlayerResult;

    public LogoutPlayerResponseType0() {
    }

    public LogoutPlayerResponseType0(
           LogoutPlayerResponse logoutPlayerResult) {
           this.logoutPlayerResult = logoutPlayerResult;
    }


    /**
     * Gets the logoutPlayerResult value for this LogoutPlayerResponseType0.
     * 
     * @return logoutPlayerResult
     */
    public LogoutPlayerResponse getLogoutPlayerResult() {
        return logoutPlayerResult;
    }


    /**
     * Sets the logoutPlayerResult value for this LogoutPlayerResponseType0.
     * 
     * @param logoutPlayerResult
     */
    public void setLogoutPlayerResult(LogoutPlayerResponse logoutPlayerResult) {
        this.logoutPlayerResult = logoutPlayerResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LogoutPlayerResponseType0)) return false;
        LogoutPlayerResponseType0 other = (LogoutPlayerResponseType0) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.logoutPlayerResult==null && other.getLogoutPlayerResult()==null) || 
             (this.logoutPlayerResult!=null &&
              this.logoutPlayerResult.equals(other.getLogoutPlayerResult())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getLogoutPlayerResult() != null) {
            _hashCode += getLogoutPlayerResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LogoutPlayerResponseType0.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.oxypite.com/", ">LogoutPlayerResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logoutPlayerResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "LogoutPlayerResponse"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
