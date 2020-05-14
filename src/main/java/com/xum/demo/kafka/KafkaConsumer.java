package com.xum.demo.kafka;

import com.xum.demo.redis.RedisUtil;
import com.xum.demo.service.concurrency.ConCurrencyService;
import com.xum.demo.utils.CommonVariable;
import com.xum.demo.utils.ResultSeckill;
import com.xum.demo.utils.SeckillStatEnum;
import com.xum.demo.websocket.WebSocketServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class KafkaConsumer {

    private final static Logger LOG = LogManager.getLogger(KafkaConsumer.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConCurrencyService conCurrencyService;

    /**
     * 监听seckill主题,有消息就读取
     * @param message
     */
    @KafkaListener(topics = {CommonVariable.SECKILL_KAFKA_LISTENING_TOPIC_KEY})
    public void receiveMessage(String message){
        //收到通道的消息之后执行秒杀操作
        String[] array = message.split(";");
        String seckillId = array[0];
        String userId = array[1];
        Serializable redisValue = redisUtil.getValue(CommonVariable.SECKILL_KAFKA_KEY + seckillId);
        String seckillKafkaValue = "null";
        if (null != redisValue) {
            seckillKafkaValue = redisValue.toString();
        }
        //LOG.info("message:" + message + ",value:" + seckillKafkaValue);
        if (null != seckillKafkaValue && !"null".equalsIgnoreCase(seckillKafkaValue)) {
            ResultSeckill resultSeckill = this.conCurrencyService.startSeckil(Long.parseLong(seckillId), Long.parseLong(userId));
            //LOG.info("result message:" + resultSeckill.get(CommonVariable.MESSAGE));
            if (resultSeckill.equals(ResultSeckill.ok(SeckillStatEnum.SUCCESS))) {
                WebSocketServer.sendInfo(seckillId, userId, SeckillStatEnum.SUCCESS.getInfo());//推送给前台
            } else if (resultSeckill.equals(ResultSeckill.error(SeckillStatEnum.END))) {
                WebSocketServer.sendInfo(seckillId, userId, SeckillStatEnum.END.getInfo());//推送给前台
                //redisUtil.cacheValue(CommonVariable.SECKILL_KAFKA_KEY + seckillId, SeckillStatEnum.END.getState());//秒杀结束
                redisUtil.removeValue(CommonVariable.SECKILL_KAFKA_KEY + seckillId);
            }
        } else {
           //WebSocketServer.sendInfo(array[0], "秒杀失败"); //推送给前台
        }
    }

}
