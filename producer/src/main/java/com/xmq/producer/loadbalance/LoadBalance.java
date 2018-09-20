package com.xmq.producer.loadbalance;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.producer.loadbalance
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 20:34
 * @Version: 1.0
 */
public interface LoadBalance {
    String select(List<String> paths );
}
