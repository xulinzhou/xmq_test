package com.xmq.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.config
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/30 17:56
 * @Version: 1.0
 */
@Data
@Component
public class Config {
    @Value("${spring.datasource.driver}")
    private  String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${file.path}")
    private String filePath;

    @Value("${broker.path}broker/")
    private String path;

    @Value("${netty.port}")
    private int port = 1000;

}
