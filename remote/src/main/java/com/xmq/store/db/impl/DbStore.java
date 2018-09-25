package com.xmq.store.db.impl;

import com.xmq.message.BaseMessage;
import com.xmq.store.db.IDbStore;
import com.xmq.store.file.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:15
 * @Version: 1.0
 */
@Service
public class DbStore  {

    private static final Logger log = LoggerFactory.getLogger(DbStore.class);

    @Value("${spring.datasource.driver}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private final static String SAVE_MESSAGE_SQL = "INSERT INTO messages(id,subject,sender_ip,group,content,status,created,modifyed,received_time) VALUES(?,?,?,?,?,?,?,?,?)";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void DbStore() {
        javax.sql.DataSource dataSource = DataBaseUtils.createDataSource(driver, url, username,
                password);
        jdbcTemplate = new JdbcTemplate(dataSource);

    }

    public void save(BaseMessage message) {
       // String sql = String.format(SAVE_MESSAGE_SQL, message.getGroupName(),message.getMessageId());
        //jdbcTemplate.execute(sql);
    }

}
