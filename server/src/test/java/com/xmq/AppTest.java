package com.xmq;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }


    public static void main(String[] args) {

        new AppTest().test();


    }
    public void test(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(

                this::EchoServer,

                0,

                1,

                TimeUnit.SECONDS);
    }
    private   void EchoServer(){
        System.out.println("====================");
    }
}
