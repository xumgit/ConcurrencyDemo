package com.xum.demo.service.mysql;

import com.xum.demo.dao.mysql.SeckillMapper;
import com.xum.demo.dao.mysql.SuccessKilledMapper;
import com.xum.demo.pojo.mysql.Seckill;
import com.xum.demo.pojo.mysql.SuccessKilledKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service(value = "seckillService")
public class SeckillService {

    @Resource
    private SeckillMapper seckillMapper;

    @Resource
    private SuccessKilledMapper successKilledMapper;

    @Transactional
    public int deleteByPrimaryKey(Long id) {
        int affectRow = -1;
        affectRow = this.seckillMapper.deleteByPrimaryKey(id);
        return affectRow;
    }

    @Transactional
    public int insert(Seckill record) {
        int affectRow = -1;
        affectRow = this.seckillMapper.insert(record);
        return affectRow;
    }

    @Transactional
    public Seckill selectByPrimaryKey(Long id) {
        Seckill seckill = null;
        seckill = this.seckillMapper.selectByPrimaryKey(id);
        return seckill;
    }

    @Transactional
    public int updateByPrimaryKeySelectiveAndNumber(Seckill record) {
        int affectRow = -1;
        affectRow = this.seckillMapper.updateByPrimaryKeySelectiveAndNumber(record);
        return affectRow;
    }

    @Transactional
    public int updateByPrimaryKeySelective(Seckill record) {
        int affectRow = -1;
        //this.successKilledMapper.deleteByFirstPrimaryKey(record.getSeckillId());
        affectRow = this.seckillMapper.updateByPrimaryKeySelective(record);
        return affectRow;
    }

}
