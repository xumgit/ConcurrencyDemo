package com.xum.demo.service.mysql;

import com.xum.demo.dao.mysql.RedPacketMapper;
import com.xum.demo.pojo.mysql.RedPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RedPacketService {

    private static final Logger LOG = LogManager.getLogger(RedPacketService.class);

    @Resource
    RedPacketMapper redPacketMapper;

    public int deleteByPrimaryKey(Integer id) {
        int affectRow = -1;
        affectRow = this.redPacketMapper.deleteByPrimaryKey(id);
        return affectRow;
    }

    public int deleteByPacketIdBatch(int[] redPacketIdArr) {
        int affectRow = -1;
        affectRow = this.redPacketMapper.deleteByPacketIdBatch(redPacketIdArr);
        return affectRow;
    }

    public int insert(RedPacket record) {
        int affectRow = -1;
        affectRow = this.redPacketMapper.insert(record);
        return affectRow;
    }

    public int insertBatch(List<RedPacket> recordList) {
        int affectRow = -1;
        affectRow = this.redPacketMapper.insertBatch(recordList);
        return affectRow;
    }

    public int truncateRedPacketTable() {
        int affectRow = -1;
        affectRow = this.redPacketMapper.truncateRedPacketTable();
        return affectRow;
    }
}
