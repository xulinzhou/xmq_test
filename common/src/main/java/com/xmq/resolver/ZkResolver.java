package com.xmq.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.resolver
 * @Description: zk 配置
 * @Author: xulinzhou
 * @CreateDate: 2018/9/18 15:58
 * @Version: 1.0
 */
public class ZkResolver {
    private Logger log = LoggerFactory.getLogger(ZkResolver.class);

    private  ZKClient client;
    /**
     * 端口号
     */
    @Value("${zookeeper.ip}")
    private String zkaddress;
    public ZkResolver() {
        try {
            client = new ZKClient(zkaddress);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
