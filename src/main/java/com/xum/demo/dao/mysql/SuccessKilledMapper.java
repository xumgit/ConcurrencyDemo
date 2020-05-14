package com.xum.demo.dao.mysql;

import com.xum.demo.pojo.mysql.SuccessKilled;
import com.xum.demo.pojo.mysql.SuccessKilledKey;

public interface SuccessKilledMapper {
    int deleteByPrimaryKey(SuccessKilledKey key);

    int deleteByFirstPrimaryKey(Long seckillId);

    int insert(SuccessKilled record);

    int insertSelective(SuccessKilled record);

    SuccessKilled selectByPrimaryKey(SuccessKilledKey key);

    int updateByPrimaryKeySelective(SuccessKilled record);

    int updateByPrimaryKey(SuccessKilled record);
}