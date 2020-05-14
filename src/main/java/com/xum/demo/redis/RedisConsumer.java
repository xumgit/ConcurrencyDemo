package com.xum.demo.redis;

import com.xum.demo.service.concurrency.ConCurrencyService;
import com.xum.demo.utils.CommonVariable;
import com.xum.demo.utils.ResultSeckill;
import com.xum.demo.utils.SeckillStatEnum;
import com.xum.demo.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class RedisConsumer {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConCurrencyService conCurrencyService;

    public void receiveMessage(String message) {
        Thread th = Thread.currentThread();

        String[] array = message.split(";");
        String seckillId = array[0];
        String userId = array[1];
        Serializable redisValue = redisUtil.getValue(CommonVariable.SECKILL_REDIS_QUEUE_KEY + seckillId);
        String seckillRedisQueueValue = "null";
        if (null != redisValue) {
            seckillRedisQueueValue = redisValue.toString();
        }
        if (null != seckillRedisQueueValue && !"null".equalsIgnoreCase(seckillRedisQueueValue)) {
            ResultSeckill resultSeckill = this.conCurrencyService.startSeckil(Long.parseLong(seckillId), Long.parseLong(userId));
            if (resultSeckill.equals(ResultSeckill.ok(SeckillStatEnum.SUCCESS))) {
                WebSocketServer.sendInfo(seckillId, userId, SeckillStatEnum.SUCCESS.getInfo());//推送给前台
            } else if (resultSeckill.equals(ResultSeckill.error(SeckillStatEnum.END))) {
                WebSocketServer.sendInfo(seckillId, userId, SeckillStatEnum.END.getInfo());//推送给前台
                redisUtil.removeValue(CommonVariable.SECKILL_REDIS_QUEUE_KEY + seckillId);
            }
        }
    }

}
