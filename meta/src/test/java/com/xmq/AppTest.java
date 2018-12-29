package com.xmq;

import static org.junit.Assert.assertTrue;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.junit.Test;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    public static void main(String[] args)  throws Exception{
        /*System.out.println("======================");
        HashedWheelTimer timer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS, 16);

        System.out.println("befort======"+LocalTime.now());

        timer.newTimeout((timeout) -> {
            System.out.println("after======"+LocalTime.now());
            System.out.println("timeout======"+timeout);
        }, 5, TimeUnit.SECONDS);*/


        final Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory(), 5, TimeUnit.SECONDS, 2);


        TimerTask task1 = new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("task 1 will run per 5 seconds ");
                timer.newTimeout(this, 5, TimeUnit.SECONDS);//结束时候再次注册
            }
        };
        Timeout time =   timer.newTimeout(task1, 5, TimeUnit.SECONDS);


        TimerTask task2 = new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("task 2 will run per 10 seconds");
                timer.newTimeout(this, 10, TimeUnit.SECONDS);//结束时候再注册
            }
        };
        timer.newTimeout(task2, 10, TimeUnit.SECONDS);


        //该任务仅仅运行一次
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("task 3 run only once ! ");
            }
        }, 15, TimeUnit.SECONDS);


         time.cancel();
        //阻塞main线程
        System.in.read();
    }
}
