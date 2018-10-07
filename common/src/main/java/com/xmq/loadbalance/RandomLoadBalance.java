package com.xmq.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.producer.loadbalance
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 20:35
 * @Version: 1.0
 */
public class RandomLoadBalance extends AbstractLoadBalance  {
    private final Random random = new Random();

    @Override
    public String doSelect(List<String> paths) {
        int size = paths.size();
        int index = random.nextInt(size);
        return paths.get(index);
    }
}
