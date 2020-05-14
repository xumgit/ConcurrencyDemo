package com.xum.demo.redisson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class RedPacketMessage implements Delayed {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认延迟3秒
     */
    private static final long DELAY_MS = 1000L * 3;

    /**
     * 红包 ID
     */
    private final long redPacketId;

    /**
     * 创建时间戳
     */
    private final long timestamp;

    /**
     * 过期时间
     */
    private final long expire;

    /**
     * 描述信息
     */
    private final String description;

    public RedPacketMessage(long redPacketId, long expireSeconds) {
        this.redPacketId = redPacketId;
        this.timestamp = System.currentTimeMillis();
        this.expire = this.timestamp + expireSeconds * 1000L;
        this.description = String.format("redPacket{%s}--creatTime{%s},overTime{%s}", redPacketId,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(F),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(expire), ZoneId.systemDefault()).format(F));
    }

    public RedPacketMessage(long redPacketId) {
        this.redPacketId = redPacketId;
        this.timestamp = System.currentTimeMillis();
        this.expire = this.timestamp + DELAY_MS;
        this.description = String.format("redPacket{%s}--creatTime{%s},overTime{%s}", redPacketId,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(F),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(expire), ZoneId.systemDefault()).format(F));
    }

    public long getRedPacketId() {
        return redPacketId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getExpire() {
        return expire;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

}
