package com.xmq;

import com.xmq.startup.MetaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import javax.annotation.Resource;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 18:02
 * @Version: 1.0
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MetaApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MetaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
              new MetaServer().start();
    }
}
