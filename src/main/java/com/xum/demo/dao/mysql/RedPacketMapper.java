package com.xum.demo.dao.mysql;

import com.xum.demo.pojo.mysql.RedPacket;

import java.util.List;

public interface RedPacketMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByPacketIdBatch(int[] redPacketIdArr);

    int insert(RedPacket record);

    int insertBatch(List<RedPacket> recordList);

    int insertSelective(RedPacket record);

    RedPacket selectByPrimaryKey(Integer id);

    int truncateRedPacketTable();

    int updateByPrimaryKeySelective(RedPacket record);

    int updateByPrimaryKey(RedPacket record);
}