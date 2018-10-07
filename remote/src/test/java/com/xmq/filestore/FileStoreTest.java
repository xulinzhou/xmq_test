package com.xmq.filestore;

import com.sun.xml.internal.rngom.parse.host.Base;
import com.xmq.NettyApplication;
import com.xmq.config.Config;
import com.xmq.handler.FileHandler;
import com.xmq.handler.Handler;
import com.xmq.message.BaseMessage;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.filestore
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/4 21:22
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NettyApplication.class)
public class FileStoreTest extends TestCase {

    @Resource
    private Config config;

    @Test
    public void fileStoreTest() {
        try {

            List<BaseMessage> bms = new ArrayList<BaseMessage>();
            for(int i=0;i<100;i++){
                BaseMessage bm = new BaseMessage();
                bm.setGroupName("group1");
                bm.setMessageId("11");
                bm.setSubject("TEST");
                bms.add(bm);
            }
            FileHandler fileHandler = new FileHandler(config);
            fileHandler.handleNext(bms);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
