package com.xum.demo.service.concurrency;

import com.xum.demo.pojo.mysql.RedPacket;
import com.xum.demo.pojo.mysql.Seckill;
import com.xum.demo.pojo.mysql.SuccessKilled;
import com.xum.demo.redis.RedisUtil;
import com.xum.demo.redisson.RedissLockUtil;
import com.xum.demo.service.mysql.RedPacketService;
import com.xum.demo.service.mysql.SeckillService;
import com.xum.demo.service.mysql.SuccessKilledService;
import com.xum.demo.utils.CommonVariable;
import com.xum.demo.utils.ResultSeckill;
import com.xum.demo.utils.ResultUtil;
import com.xum.demo.utils.SeckillStatEnum;
import com.xum.demo.zookeeper.ZkLockUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ConCurrencyService {

    private final static Logger LOG = LogManager.getLogger(ConCurrencyService.class);

    private Lock lock = new ReentrantLock(true); // 公平锁

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private SuccessKilledService successKilledService;

    @Transactional
    public ResultUtil testOneService(int userId, int redPacketId) {
        Integer money = 0;
        int affectRow = -1;
        boolean res = false;
        try {
            res = RedissLockUtil.tryLock(CommonVariable.REDIS_KEY_LOCK + redPacketId, TimeUnit.SECONDS,
                                            CommonVariable.REDIS_KEY_WAIT_TIME, CommonVariable.REDIS_KEY_LEASE_TIME);
            if (res) {
                long restPeople = redisUtil.decr(CommonVariable.REDIS_KEY_REST_PEOPLE + redPacketId,1);
                if (restPeople == 0) {
                    money = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
                } else {
                    Integer restMoney = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
                    Random random = new Random();
                    money = random.nextInt((int) (restMoney / (restPeople + 1) * 2 - 1)) + 1;
                }
                redisUtil.decr(CommonVariable.REDIS_KEY_MONEY + redPacketId, money);
                RedPacket redPacket =  new RedPacket();
                redPacket.setRedpacketid(redPacketId);
                redPacket.setUserid(userId);
                redPacket.setMoney(Long.valueOf(money));
                redPacket.setCreatetime(new Timestamp(System.currentTimeMillis()));
                affectRow = saveRedPacket(redPacket);
            } else {
                redisUtil.incr(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId,1);
            }
        } catch (Exception e) {
            LOG.error("Thread_{}, error {}", userId, e.getMessage());
        } finally {
            if (res) {
                RedissLockUtil.unlock(CommonVariable.REDIS_KEY_LOCK + redPacketId);
            }
        }
        return ResultUtil.ok("grad red packet success", money, affectRow, redPacketId);
    }

    @Transactional
    public ResultUtil testTwoService(int userId, int redPacketId) {
        Integer money = 0;
        int affectRow = -1;
        boolean res = false;
        try {
            res = RedissLockUtil.tryLock(CommonVariable.REDIS_KEY_LOCK + redPacketId, TimeUnit.SECONDS,
                                            CommonVariable.REDIS_KEY_WAIT_TIME, CommonVariable.REDIS_KEY_LEASE_TIME);
            if (res) {
                long restRedPacket = redisUtil.decr(CommonVariable.REDIS_KEY_RED_PACKET + redPacketId, 1);
                if (restRedPacket < 0) {
                    return ResultUtil.error("red packet had delivery completed, grad red packet failed", redPacketId);
                } else {
                    if (restRedPacket == 0) {
                        money = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
                    } else {
                        Integer restMoney = Integer.parseInt(redisUtil.getValue(CommonVariable.REDIS_KEY_MONEY + redPacketId).toString());
                        Random random = new Random();
                        money = random.nextInt((int) (restMoney / (restRedPacket + 1) * 2 - 1)) + 1;
                    }
                    redisUtil.decr(CommonVariable.REDIS_KEY_MONEY + redPacketId, money);
                    RedPacket redPacket =  new RedPacket();
                    redPacket.setRedpacketid(redPacketId);
                    redPacket.setUserid(userId);
                    redPacket.setMoney(Long.valueOf(money));
                    redPacket.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    affectRow = saveRedPacket(redPacket);
                }
            } else {
                return ResultUtil.error("red packet had delivery completed", redPacketId);
            }
        } catch (Exception e) {
            LOG.error("Thread_{}, error {}", userId, e.getMessage());
        } finally {
            if (res) {
                RedissLockUtil.unlock(CommonVariable.REDIS_KEY_LOCK + redPacketId);
            }
        }
        return ResultUtil.ok("grad red packet success", money, affectRow, redPacketId);
    }

    @Async
    public int saveRedPacket(RedPacket redPacket) {
        return this.redPacketService.insert(redPacket);
    }

    @Transactional
    public ResultSeckill startSeckilZksLock(long seckillId, long userId) {
        boolean res = false;
        try {
            res = ZkLockUtil.acquire(CommonVariable.SECKILL_ZOOKEEPER_ACQUIRE_TIME, TimeUnit.SECONDS);
            if (res) {
                Seckill seckill = this.seckillService.selectByPrimaryKey(seckillId);
                Integer seckillNumber = seckill.getNumber();
                if (seckillNumber > 0) {
                    SuccessKilled successKilled = new SuccessKilled();
                    successKilled.setSeckillId(seckillId);
                    successKilled.setUserId(userId);
                    successKilled.setState((byte)1);
                    successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                    int affectRow = this.successKilledService.insert(successKilled);
                    if (affectRow > 0) {
                        seckill.setNumber(seckillNumber - 1);
                        this.seckillService.updateByPrimaryKeySelective(seckill);
                    }
                } else {
                    return ResultSeckill.error(SeckillStatEnum.END);
                }
            } else {
                return ResultSeckill.error(SeckillStatEnum.MUCH);
            }
        } catch (Exception e) {
            LOG.error("Thread_{}, error {}", userId, e.getMessage());
        } finally {
            if (res) {//释放锁
                ZkLockUtil.release();
            }
        }
        return ResultSeckill.ok(SeckillStatEnum.SUCCESS);
    }

    @Transactional
    public ResultSeckill startSeckil(long seckillId, long userId) {
        Seckill seckill = this.seckillService.selectByPrimaryKey(seckillId);
        Integer seckillNumber = seckill.getNumber();
        if (seckillNumber > 0) {
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setSeckillId(seckillId);
            successKilled.setUserId(userId);
            successKilled.setState((byte)1);
            successKilled.setCreateTime(new Timestamp(new Date().getTime()));
            int affectRow = this.successKilledService.insert(successKilled);
            if (affectRow > 0) {
                seckill.setNumber(seckillNumber - 1);
                this.seckillService.updateByPrimaryKeySelective(seckill);
            }
            return ResultSeckill.ok(SeckillStatEnum.SUCCESS);
        } else {
            return ResultSeckill.error(SeckillStatEnum.END);
        }
    }

}
