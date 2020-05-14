package com.xum.demo.service.mysql;

import com.xum.demo.dao.mysql.SuccessKilledMapper;
import com.xum.demo.pojo.mysql.SuccessKilled;
import com.xum.demo.pojo.mysql.SuccessKilledKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service(value = "successKilledService")
public class SuccessKilledService {

    @Resource
    private SuccessKilledMapper successKilledMapper;

    @Transactional
    public int deleteByPrimaryKey(SuccessKilledKey key) {
        int affectRow = -1;
        affectRow = this.successKilledMapper.deleteByPrimaryKey(key);
        return affectRow;
    }

    @Transactional
    public int deleteByFirstPrimaryKey(Long seckillId) {
        int affectRow = -1;
        affectRow = this.successKilledMapper.deleteByFirstPrimaryKey(seckillId);
        return affectRow;
    }

    @Transactional
    public int insert(SuccessKilled record) {
        int affectRow = -1;
        affectRow = this.successKilledMapper.insert(record);
        return affectRow;
    }

    @Transactional
    public int updateByPrimaryKeySelective(SuccessKilled record) {
        int affectRow = -1;
        affectRow = this.successKilledMapper.updateByPrimaryKeySelective(record);
        return affectRow;
    }

}
