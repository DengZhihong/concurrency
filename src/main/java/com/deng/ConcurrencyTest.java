package com.deng;

import com.deng.annotatios.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
@NotThreadSafe
public class ConcurrencyTest {
    //请求总数
    public static int clientTotal = 5000;
    //同时并发执行的线程数
    public static int threadTotal = 200;
    //计数
    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        //给定并发的数
        final Semaphore semaphore = new Semaphore(threadTotal);
        //只有当clientTotal到0的时候才去执行别的代码
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

        for (int i = 0; i < clientTotal; i++) {
            //线程池执行
            executorService.execute(() ->{
                try {
                    //每次执行需要获取值,判断是否能执行,如果到一定的数量时,将会阻塞
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (Exception e) {
                    log.error("error", e);
                }
                //执行完一个,计数值减一
                countDownLatch.countDown();
            });

        }
        //保证请求执行完
        countDownLatch.await();
        executorService.shutdown();
        log.info("count:{}",count);
    }

    private static void add() {
        count++;
    }


}
