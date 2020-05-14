package com.xum.demo.dao.mysql;

import com.xum.demo.pojo.mysql.Seckill;

public interface SeckillMapper {
    int deleteByPrimaryKey(Long seckillId);

    int insert(Seckill record);

    int insertSelective(Seckill record);

    Seckill selectByPrimaryKey(Long seckillId);

    int updateByPrimaryKeySelectiveAndNumber(Seckill record);

    int updateByPrimaryKeySelective(Seckill record);

    int updateByPrimaryKey(Seckill record);
}