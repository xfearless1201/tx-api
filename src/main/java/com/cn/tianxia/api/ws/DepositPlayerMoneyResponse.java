/**
 * DepositPlayerMoneyResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cn.tianxia.api.ws;

public class DepositPlayerMoneyResponse  implements java.io.Serializable {
    private MoneyResponse depositPlayerMoneyResult;

    public DepositPlayerMoneyResponse() {
    }

    public DepositPlayerMoneyResponse(
           MoneyResponse depositPlayerMoneyResult) {
           this.depositPlayerMoneyResult = depositPlayerMoneyResult;
    }


    /**
     * Gets the depositPlayerMoneyResult value for this DepositPlayerMoneyResponse.
     * 
     * @return depositPlayerMoneyResult
     */
    public MoneyResponse getDepositPlayerMoneyResult() {
        return depositPlayerMoneyResult;
    }


    /**
     * Sets the depositPlayerMoneyResult value for this DepositPlayerMoneyResponse.
     * 
     * @param depositPlayerMoneyResult
     */
    public void setDepositPlayerMoneyResult(MoneyResponse depositPlayerMoneyResult) {
        this.depositPlayerMoneyResult = depositPlayerMoneyResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DepositPlayerMoneyResponse)) return false;
        DepositPlayerMoneyResponse other = (DepositPlayerMoneyResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.depositPlayerMoneyResult==null && other.getDepositPlayerMoneyResult()==null) || 
             (this.depositPlayerMoneyResult!=null &&
              this.depositPlayerMoneyResult.equals(other.getDepositPlayerMoneyResult())));
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
        if (getDepositPlayerMoneyResult() != null) {
            _hashCode += getDepositPlayerMoneyResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DepositPlayerMoneyResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.oxypite.com/", ">DepositPlayerMoneyResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("depositPlayerMoneyResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.oxypite.com/", "DepositPlayerMoneyResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.oxypite.com/", "MoneyResponse"));
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
