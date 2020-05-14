package com.xum.demo.activemq;

import com.xum.demo.redis.RedisUtil;
import com.xum.demo.service.concurrency.ConCurrencyService;
import com.xum.demo.utils.CommonVariable;
import com.xum.demo.utils.ResultSeckill;
import com.xum.demo.utils.SeckillStatEnum;
import com.xum.demo.websocket.WebSocketServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class ActiveMQConsumer {

    private final static Logger LOG = LogManager.getLogger(ActiveMQConsumer.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConCurrencyService conCurrencyService;

    // 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
    @JmsListener(destination = CommonVariable.SECKILL_ACTIVEMQ_DESTINATION_KEY)
    public void receiveQueue(String message) {
        //收到通道的消息之后执行秒杀操作(超卖)
        String[] array = message.split(";");
        String seckillId = array[0];
        String userId = array[1];
        Serializable redisValue = redisUtil.getValue(CommonVariable.SECKILL_ACTIVEMQ_KEY + seckillId);
        String seckillActiveMQValue = "null";
        if (null != redisValue) {
            seckillActiveMQValue = redisValue.toString();
        }
        if (null != seckillActiveMQValue && !"null".equalsIgnoreCase(seckillActiveMQValue)) {
            ResultSeckill resultSeckill = this.conCurrencyService.startSeckil(Long.parseLong(seckillId), Long.parseLong(userId));
            if (resultSeckill.equals(ResultSeckill.ok(SeckillStatEnum.SUCCESS))) {
                WebSocketServer.sendInfo(seckillId, userId, SeckillStatEnum.SUCCESS.getInfo());//推送给前台
            } else if (resultSeckill.equals(ResultSeckill.error(SeckillStatEnum.END))) {
                WebSocketServer.sendInfo(seckillId, userId, SeckillStatEnum.END.getInfo());//推送给前台
                redisUtil.removeValue(CommonVariable.SECKILL_ACTIVEMQ_KEY + seckillId);
            }
        }

    }

}
