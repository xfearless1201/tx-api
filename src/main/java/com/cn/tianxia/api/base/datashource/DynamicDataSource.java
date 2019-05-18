package com.cn.tianxia.api.base.datashource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource{
    
    @Override
	protected Object determineCurrentLookupKey() {
	    return DataSourceHolder.peek();
	}

}
