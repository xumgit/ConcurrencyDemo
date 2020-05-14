package com.xum.demo.controller;

import com.xum.demo.activemq.ActiveMQSender;
import com.xum.demo.kafka.KafkaSender;
import com.xum.demo.pojo.mysql.Seckill;
import com.xum.demo.redis.RedisSender;
import com.xum.demo.redis.RedisUtil;
import com.xum.demo.redisson.RedPacketMessage;
import com.xum.demo.redisson.RedPacketQueue;
import com.xum.demo.service.concurrency.ConCurrencyService;
import com.xum.demo.service.mysql.RedPacketService;
import com.xum.demo.service.mysql.SeckillService;
import com.xum.demo.service.mysql.SuccessKilledService;
import com.xum.demo.utils.CommonVariable;
import com.xum.demo.utils.ResultSeckill;
import com.xum.demo.utils.ResultUtil;
import com.xum.demo.utils.SeckillStatEnum;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.Destination;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/conCurrency")
public class ConCurrencyController {

    private final static Logger LOG = LogManager.getLogger(ConCurrencyController.class);
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize,
                                                        corePoolSize + 1,
                                                        10l,
                                                         TimeUnit.SECONDS,
                                                         new LinkedBlockingQueue<>(100000));

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConCurrencyService conCurrencyService;

    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private SuccessKilledService successKilledService;

    @Autowired
    private KafkaSender kafkaSender;

    @Autowired
    private ActiveMQSender activeMQSender;

    @Autowired
    private RedisSender redisSender;

    // 抢红包
    @RequestMapping(value = "/testOne")
    @ResponseBody
    public String testOne(@RequestParam(value="redPacketId", defaultValue="1") Integer redPacketId,
                          @RequestParam(value="redPacketMoney", defaultValue="1000") Integer redPacketMoney,
                          @RequestParam(value="redPacketCount", defaultValue="10") Integer redPacketCount,
                          @RequestParam(value="redPacketRestPeople", defaultValue="10") Integer redPacketRestPeople) {
        StringBuilder stringBuilder = new StringBuilder();
        int simulationGrabRedPacketPeople = 2*redPacketRestPeople; //模拟多少人抢红包
        CountDownLatch latch = new CountDownLatch(simulationGrabRedPacketPeople);
        redisUtil.cacheValue(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId, redPacketCount); //模拟红包个数
        redisUtil.cacheValue(CommonVariable.REDIS_KEY_REST_PEOPLE + redPacketId, redPacketRestPeople); //模拟抢红包剩余人数
        redisUtil.cacheValue(CommonVariable.REDIS_KEY_MONEY + redPacketId, redPacketMoney); //模拟红包money
        int[] arr = {redPacketId}; //先删除redPacketId这个分发的红包
        int affectDeleteRow = this.redPacketService.deleteByPacketIdBatch(arr);
        stringBuilder.append(String.format("delete redPacketId = {%s}, row = {%s}", String.valueOf(redPacketId), String.valueOf(affectDeleteRow)));
        stringBuilder.append("\n");
        for (int i = 1; i <= simulationGrabRedPacketPeople; i++) {
            int userId = i;
            Runnable task = () -> {
                long restRedPacket = redisUtil.decr(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId,1);
                if (restRedPacket >= 0) {
                    ResultUtil resultUtil = conCurrencyService.testOneService(userId, redPacketId);
                    stringBuilder.append(String.format("Thread_{%s}, get money {%s}, message {%s}, save database flag {%s}",
                                            String.valueOf(userId),
                                            resultUtil.get(CommonVariable.MESSAGE_MONEY + redPacketId).toString(),
                                            resultUtil.get(CommonVariable.MESSAGE + redPacketId).toString(),
                                            resultUtil.get(CommonVariable.MESSAGE_AFFECT_ROW + redPacketId).toString()));
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append(String.format("Thread_{%s}, red packet had delivery completed", String.valueOf(userId)));
                    stringBuilder.append("\n");
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Integer restMoney = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
            stringBuilder.append(String.format("rest red packet money: {%s}", String.valueOf(restMoney)));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return stringBuilder.toString();
    }

    // 抢红包
    @RequestMapping(value = "/testTwo")
    @ResponseBody
    public String testTwo(@RequestParam(value="redPacketId", defaultValue="2") Integer redPacketId,
                          @RequestParam(value="redPacketMoney", defaultValue="1000") Integer redPacketMoney,
                          @RequestParam(value="redPacketCount", defaultValue="10") Integer redPacketCount,
                          @RequestParam(value="queueFlag", defaultValue="no") String queueFlag) {
        long startTime = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("corePoolSize:" + corePoolSize + "\n");
        int simulationGrabRedPacketPeople = 2*redPacketCount; //模拟多少人抢红包
        if ("yes".equalsIgnoreCase(queueFlag)) {
            simulationGrabRedPacketPeople = 9; //模拟多少人抢红包, 小于抢红包的人数10个
        }
        CountDownLatch latch = new CountDownLatch(simulationGrabRedPacketPeople);
        redisUtil.cacheValue(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId, redPacketCount); //模拟红包个数
        redisUtil.cacheValue(CommonVariable.REDIS_KEY_MONEY + redPacketId, redPacketMoney); //模拟money
        if ("yes".equalsIgnoreCase(queueFlag)) {
            /**
             * 加入延迟队列 24s秒过期
             */
            RedPacketMessage message = new RedPacketMessage(redPacketId,24);
            RedPacketQueue.getQueue().produce(message);
        }
        int[] arr = {redPacketId}; //先删除redPacketId这个分发的红包
        int affectDeleteRow = this.redPacketService.deleteByPacketIdBatch(arr);
        stringBuilder.append(String.format("delete redPacketId = {%s}, row = {%s}", String.valueOf(redPacketId), String.valueOf(affectDeleteRow)));
        stringBuilder.append("\n");
        for (int i = 1; i <= simulationGrabRedPacketPeople; i++) {
            int userId = i;
            Runnable task = () -> {
                Integer restMoney = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
                if (restMoney > 0) {
                    ResultUtil resultUtil = conCurrencyService.testTwoService(userId, redPacketId);
                    stringBuilder.append(String.format("Thread_{%s}, get money {%s}, message {%s}, save database flag {%s}",
                                            String.valueOf(userId),
                                            resultUtil.get(CommonVariable.MESSAGE_MONEY + redPacketId).toString(),
                                            resultUtil.get(CommonVariable.MESSAGE + redPacketId).toString(),
                                            resultUtil.get(CommonVariable.MESSAGE_AFFECT_ROW + redPacketId).toString()));
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append(String.format("Thread_{%s}, red packet had delivery completed", String.valueOf(userId)));
                    stringBuilder.append("\n");
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Integer restMoney = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
            stringBuilder.append(String.format("rest red packet money: {%s}", String.valueOf(restMoney)));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        stringBuilder.append("\n");
        long endTime = System.currentTimeMillis();
        long consume = (endTime - startTime);
        stringBuilder.append(String.format("total consume time: {%s}ms", String.valueOf(consume)));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    // 秒杀 zookeeper分布式锁
    @RequestMapping(value = "/seckill/zookeeper")
    @ResponseBody
    public String startZkLock(@RequestParam(value="seckillId", defaultValue="1000") Long seckillId,
                              @RequestParam(value="seckillNumber", defaultValue="100") Integer seckillNumber,
                              @RequestParam(value="seckillPeople", defaultValue="1000") Long seckillPeople) {
        long startTime = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        seckill.setNumber(seckillNumber);
        this.seckillService.updateByPrimaryKeySelective(seckill);
        this.successKilledService.deleteByFirstPrimaryKey(seckillId);
        for (int i = 1; i <= seckillPeople; i++) {
            long userId = i;
            Runnable task = () -> {
                ResultSeckill resultSeckill = this.conCurrencyService.startSeckilZksLock(seckillId, userId);
                /*stringBuilder.append(String.format("Thread_{%}, message: {%s}", String.valueOf(userId),
                                            String.valueOf(resultSeckill.get(CommonVariable.MESSAGE))));*/
                //stringBuilder.append(userId);
                stringBuilder.append("\n");
            };
            executor.execute(task);
        }
        stringBuilder.append("\n");
        long endTime = System.currentTimeMillis();
        long consume = (endTime - startTime);
        stringBuilder.append(String.format("total consume time: {%s}ms", String.valueOf(consume)));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    // 秒杀 kafka分布式锁
    @RequestMapping(value = "/seckill/kafka")
    @ResponseBody
    public String startKafakaQueue(@RequestParam(value="seckillId", defaultValue="1000") Long seckillId,
                                   @RequestParam(value="seckillNumber", defaultValue="100") Integer seckillNumber,
                                   @RequestParam(value="seckillPeople", defaultValue="1000") Long seckillPeople) {
        long startTime = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        final long killId =  seckillId;

        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        seckill.setNumber(seckillNumber);
        this.seckillService.updateByPrimaryKeySelective(seckill);
        this.successKilledService.deleteByFirstPrimaryKey(seckillId);
        redisUtil.cacheValue(CommonVariable.SECKILL_KAFKA_KEY + seckillId, SeckillStatEnum.START.getState());
        for (int i = 1; i <= seckillPeople; i++) {
            long userId = i;
            Runnable task = () -> {
                if(redisUtil.getValue(CommonVariable.SECKILL_KAFKA_KEY + seckillId) != "null"){
                    //思考如何返回给用户信息ws
                    kafkaSender.sendChannelMess(CommonVariable.SECKILL_KAFKA_LISTENING_TOPIC_KEY, seckillId + ";" + userId);
                }else{
                    LOG.info("finished");
                }
            };
            executor.execute(task);
        }
       /* try {
            Thread.sleep(3000);
            //redisUtil.removeValue(CommonVariable.SECKILL_KAFKA_KEY + seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        stringBuilder.append("\n");
        long endTime = System.currentTimeMillis();
        long consume = (endTime - startTime);
        stringBuilder.append(String.format("total consume time: {%s}ms", String.valueOf(consume)));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    // 秒杀 activemq分布式锁
    @RequestMapping(value = "/seckill/activemq")
    @ResponseBody
    public String startActiveMQQueue(@RequestParam(value="seckillId", defaultValue="1000") Long seckillId,
                                     @RequestParam(value="seckillNumber", defaultValue="100") Integer seckillNumber,
                                     @RequestParam(value="seckillPeople", defaultValue="1000") Long seckillPeople) {
        long startTime = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        final long killId =  seckillId;

        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        seckill.setNumber(seckillNumber);
        this.seckillService.updateByPrimaryKeySelective(seckill);
        this.successKilledService.deleteByFirstPrimaryKey(seckillId);
        redisUtil.cacheValue(CommonVariable.SECKILL_ACTIVEMQ_KEY + seckillId, SeckillStatEnum.START.getState());
        for (int i = 1; i <= seckillPeople; i++) {
            long userId = i;
            Runnable task = () -> {
                if(redisUtil.getValue(CommonVariable.SECKILL_ACTIVEMQ_KEY + seckillId) != "null"){
                    Destination destination = new ActiveMQQueue(CommonVariable.SECKILL_ACTIVEMQ_DESTINATION_KEY);
                    //思考如何返回给用户信息ws
                    activeMQSender.sendChannelMess(destination,seckillId + ";" + userId);
                }else{
                    LOG.info("finished");
                }
            };
            executor.execute(task);
        }
        stringBuilder.append("\n");
        long endTime = System.currentTimeMillis();
        long consume = (endTime - startTime);
        stringBuilder.append(String.format("total consume time: {%s}ms", String.valueOf(consume)));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    // 秒杀 redis分布式锁
    @RequestMapping(value = "/seckill/redis")
    @ResponseBody
    public String startRedisQueue(@RequestParam(value="seckillId", defaultValue="1000") Long seckillId,
                                  @RequestParam(value="seckillNumber", defaultValue="100") Integer seckillNumber,
                                  @RequestParam(value="seckillPeople", defaultValue="1000") Long seckillPeople) {
        long startTime = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        final long killId =  seckillId;

        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        seckill.setNumber(seckillNumber);
        this.seckillService.updateByPrimaryKeySelective(seckill);
        this.successKilledService.deleteByFirstPrimaryKey(seckillId);
        redisUtil.cacheValue(CommonVariable.SECKILL_REDIS_QUEUE_KEY + seckillId, SeckillStatEnum.START.getState());
        for (int i = 1; i <= seckillPeople; i++) {
            long userId = i;
            Runnable task = () -> {
                if(redisUtil.getValue(CommonVariable.SECKILL_REDIS_QUEUE_KEY + seckillId) != "null"){
                   redisSender.sendChannelMess(CommonVariable.SECKILL_REDIS_QUEUE_TOPIC_KEY ,seckillId + ";" + userId);
                }else{
                    LOG.info("finished");
                }
            };
            executor.execute(task);
        }
        stringBuilder.append("\n");
        long endTime = System.currentTimeMillis();
        long consume = (endTime - startTime);
        stringBuilder.append(String.format("total consume time: {%s}ms", String.valueOf(consume)));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
