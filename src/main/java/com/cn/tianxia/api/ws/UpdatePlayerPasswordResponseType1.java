/**
 * UpdatePlayerPasswordResponseType1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cn.tianxia.api.ws;

public class UpdatePlayerPasswordResponseType1  implements java.io.Serializable {
    private UpdatePlayerPasswordResponse updatePlayerPasswordResult;

    public UpdatePlayerPasswordResponseType1() {
    }

    public UpdatePlayerPasswordResponseType1(
           UpdatePlayerPasswordResponse updatePlayerPasswordResult) {
           this.updatePlayerPasswordResult = updatePlayerPasswordResult;
    }


    /**
     * Gets the updatePlayerPasswordResult value for this UpdatePlayerPasswordResponseType1.
     * 
     * @return updatePlayerPasswordResult
     */
    public UpdatePlayerPasswordResponse getUpdatePlayerPasswordResult() {
        return updatePlayerPasswordResult;
    }


    /**
     * Sets the updatePlayerPasswordResult value for this UpdatePlayerPasswordResponseType1.
     * 
     * @param updatePlayerPasswordResult
     */
    public void setUpdatePlayerPasswordResult(UpdatePlayerPasswordResponse updatePlayerPasswordResult) {
        this.updatePlayerPasswordResult = updatePlayerPasswordResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdatePlayerPasswordResponseType1)) return false;
        UpdatePlayerPasswordResponseType1 other = (UpdatePlayerPasswordResponseType1) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.updatePlayerPasswordResult==null && other.getUpdatePlayerPasswordResult()==null) || 
             (this.updatePlayerPasswordResult!=null &&
              this.updatePlayerPasswordResult.equals(other.getUpdatePlayerPasswordResult())));
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
        if (getUpdatePlayerPasswordResult() != null) {
            _hashCode += getUpdatePlayerPasswordResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdatePlayerPasswordResponseType1.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.oxypite.com/", ">UpdatePlayerPasswordResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updatePlayerPasswordResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "UpdatePlayerPasswordResponse"));
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
