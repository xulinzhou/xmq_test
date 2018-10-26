package com.xmq;

import static org.junit.Assert.assertTrue;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.InputStream;
import java.net.URLEncoder;

/**
 *
 */
public class AppTest 
{
    /**
     *
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        //for(int i=1;i<300;i++){
            String random = "182027";
            int ramdom = (int)((Math.random()*9+1)*10000);
            String ramdom1 = String.valueOf((int)((Math.random()*9+1)*100000));
            String tel = random+String.valueOf(ramdom);
            System.out.println("tel"+tel);
            String url = "http://tou.cnqjw.com/plugin.php?id=tom_weixin_vote&mod=save&tpxm=1&tptel=13554023456&formhash=a0f903ac&tomhash=284211&vid=2&tid=12475&act=tpadd";
            //1.使用默认的配置的httpclient
            System.out.println(url);
            CloseableHttpClient client = HttpClients.createDefault();
            //2.使用get方法
            HttpGet httpGet = new HttpGet(URLEncoder.encode(url));
            InputStream inputStream = null;
            CloseableHttpResponse response = null;

            try {
                //3.执行请求，获取响应
                response = client.execute(httpGet);
                //看请求是否成功，这儿打印的是http状态码
                System.out.println(response.getStatusLine().getStatusCode());
                //4.获取响应的实体内容，就是我们所要抓取得网页内容
                HttpEntity entity = response.getEntity();
                System.out.println(entity.getContent().toString());
            } catch(Exception e ){
                e.printStackTrace();;
            }
        //}

    }
}
