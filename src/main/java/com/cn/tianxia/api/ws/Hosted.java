/**
 * Hosted.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cn.tianxia.api.ws;

public interface Hosted extends javax.xml.rpc.Service {
    public java.lang.String getHostedSoapAddress();

    public HostedSoap getHostedSoap() throws javax.xml.rpc.ServiceException;

    public HostedSoap getHostedSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
