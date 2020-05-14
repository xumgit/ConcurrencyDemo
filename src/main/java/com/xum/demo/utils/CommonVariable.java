package com.xum.demo.utils;

import oracle.net.ns.Packet;

public class CommonVariable {

    public final static String OFFSET = "offSet";
    public final static String ROWCOUNT = "rowCount";
    public final static String SEARCH_PHRASE = "searchPhrase";
    public final static String CURRENT = "current";
    public final static String TOTAL = "total";
    public final static String ROWS = "rows";

    public final static String MYSQL = "mysql";
    public final static String ORACLE = "oracle";

    public final static String REDIS_KEY_RED_PACKET = "redisKeyRedPacket";
    public final static String REDIS_KEY_MONEY = "redisKeyMoney";
    public final static String REDIS_KEY_REST_PEOPLE = "redisKeyRestPeople";
    public final static String REDIS_KEY_LOCK = "redisKeyLock";

    public final static int REDIS_KEY_WAIT_TIME = 1;
    public final static int REDIS_KEY_LEASE_TIME = 5;

    public final static String CODE = "code";
    public final static String MESSAGE = "message";
    public final static String MESSAGE_MONEY = "messageMoney";
    public final static String MESSAGE_AFFECT_ROW = "messageAffectRow";

    public final static int SECKILL_ZOOKEEPER_ACQUIRE_TIME = 3;
    public final static String SECKILL_ZOOKEEPER_KEY = "seckillZookeeper";

    public final static String SECKILL_KAFKA_LISTENING_TOPIC_KEY = "seckill_kafka_topic";
    public final static String SECKILL_KAFKA_KEY = "seckillKafka";

    public final static String SECKILL_ACTIVEMQ_DESTINATION_KEY = "seckill_activemq_destination";
    public final static String SECKILL_ACTIVEMQ_KEY = "seckillActiveMQ";

    public final static String SECKILL_REDIS_QUEUE_TOPIC_KEY = "seckill_redis_queue_topic";
    public final static String SECKILL_REDIS_QUEUE_KEY = "seckillRedisQueue";
}
