package com.xum.demo.dao.mysql;

import java.util.List;
import java.util.Map;

import com.xum.demo.pojo.mysql.Devices;
import com.xum.demo.pojo.mysql.DevicesWithBLOBs;

public interface DevicesMapper {
    int deleteByPrimaryKey(String id);

    int insert(DevicesWithBLOBs record);

    int insertSelective(DevicesWithBLOBs record);

    DevicesWithBLOBs selectByPrimaryKey(String id);
    
    DevicesWithBLOBs selectByParameter(DevicesWithBLOBs record);

    int updateByPrimaryKeySelective(DevicesWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(DevicesWithBLOBs record);

    int updateByPrimaryKey(Devices record);
    
    int selectAllCount();
    
    List<Map<String, Object>> selectAllWithMap();
    
    List<Devices> selectAllByList();
    
    List<DevicesWithBLOBs> selectAllWithList();
    
    List<Map<String, Object>> selectDevicesWithBootGrid(Map<String, Object> mapPara);
}