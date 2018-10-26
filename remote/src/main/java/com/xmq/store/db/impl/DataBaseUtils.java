package com.xmq.store.db.impl;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.support.ibatis.DruidDataSourceFactory;

public class DataBaseUtils {

	
	public static DataSource createDataSource(String driver, String url, String username, String password) {
        checkNotNull(driver);
        checkNotNull(url);
        checkNotNull(username);
        checkNotNull(password);

        DataSource dataSource = null;   
        
        Properties p =new Properties();  
        p.put("initialSize", "1");  
        p.put("minIdle", "1");  
        p.put("maxActive", "20");  
        p.put("maxWait", "60000");  
        p.put("timeBetweenEvictionRunsMillis", "60000");  
        p.put("minEvictableIdleTimeMillis", "300000");             
        p.put("validationQuery", "SELECT 'x' from dual");  
        p.put("testWhileIdle", "true");  
        p.put("testOnBorrow", "false");  
        p.put("testOnReturn", "false");  
        p.put("poolPreparedStatements", "true");  
        p.put("maxPoolPreparedStatementPerConnectionSize", "20");  
        p.put("filters", "stat");  

         p.put("url",url);  
         p.put("username", username);  
         p.put("password", password);  
         
         try {
			dataSource =   com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource(p);
		} catch (Exception e) {
			e.printStackTrace();
		}        
        
      return dataSource;
    }
}
