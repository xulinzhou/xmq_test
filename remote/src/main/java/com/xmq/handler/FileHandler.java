package com.xmq.handler;

import com.xmq.message.BaseMessage;
import com.xmq.store.file.FileStore;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.handler
 * @Description: 保存本地
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 14:23
 * @Version: 1.0
 */
public class FileHandler extends AbstractHandler {
    @Override
    public void handleNext(BaseMessage message) {
        String fileName  = message.getSubject();
        try{
            FileStore file  = new FileStore(message);
            file.write(message.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
