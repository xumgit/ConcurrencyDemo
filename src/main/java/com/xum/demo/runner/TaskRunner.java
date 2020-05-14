package com.xum.demo.runner;

import com.xum.demo.redis.RedisUtil;
import com.xum.demo.redisson.RedPacketMessage;
import com.xum.demo.redisson.RedPacketQueue;
import com.xum.demo.utils.CommonVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("redPacket")
public class TaskRunner implements ApplicationRunner {

    private final static Logger LOG = LoggerFactory.getLogger(TaskRunner.class);

    @Autowired
    private RedisUtil redisUtil;

    ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setName("RedPacketDelayWorker");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void run(ApplicationArguments var){
        executorService.execute(() -> {
            while (true) {
                try {
                    RedPacketMessage message = RedPacketQueue.getQueue().consume();
                    if(message != null){
                        long redPacketId = message.getRedPacketId();
                        LOG.info("redPacket {} overtime", redPacketId);
                        /**
                         * 获取剩余红包个数以及金额
                         */
                        int num = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId).toString());
                        int restMoney = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
                        LOG.info("rest redPacket count {}，rest money {}", num, restMoney);
                        /**
                         * 清空红包数据
                         */
                        redisUtil.removeValue(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId);
                        redisUtil.removeValue(CommonVariable.REDIS_KEY_MONEY + redPacketId);
                        /**
                         * 异步更新数据库、异步退回红包金额
                         */
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
