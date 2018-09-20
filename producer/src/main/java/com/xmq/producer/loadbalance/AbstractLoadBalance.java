package com.xmq.producer.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.producer.loadbalance
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 20:39
 * @Version: 1.0
 */
public abstract class AbstractLoadBalance implements LoadBalance{

    private final Random random = new Random();

    public String select(List<String> paths) {
        if (paths.size() == 0) return null;
        if (paths.size() == 1) {
            return paths.get(0);
        } return doSelect(paths);
    }
    public abstract String doSelect(List<String> paths);

}
