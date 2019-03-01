package com.xmq.handler;

import com.alibaba.fastjson.JSON;
import com.xmq.config.Config;
import com.xmq.message.BaseMessage;
import com.xmq.store.file.FileStore;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.handler
 * @Description: 保存本地
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 14:23
 * @Version: 1.0
 */
@Slf4j
public class FileHandler extends AbstractHandler {
    private Config config;
    private FileStore file;
    @Override
    public void handleNext(BaseMessage message) {
        try{
            file.write(message);
        }catch (Exception e){
            log.error("文件保存失败",e.getMessage());
            e.printStackTrace();
        }
    }
    public void handleNext(List<BaseMessage> messages) {
        try{
            file.write(messages);
        }catch (Exception e){
            log.error("文件保存失败",e.getMessage());
            e.printStackTrace();
        }
    }
    public FileHandler(Config config) {
        try{
            this.config = config;
            file  = new FileStore(config);
        }catch (Exception e){

        }


    }
}
