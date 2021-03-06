package com.xum.demo.service.mysql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.xum.demo.dao.mysql.DevicesMapper;
import com.xum.demo.pojo.mysql.Devices;
import com.xum.demo.pojo.mysql.DevicesWithBLOBs;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service(value = "devicesService")
public class DevicesService {

    private static final Logger LOG = LogManager.getLogger(DevicesService.class);

    @Resource
    private DevicesMapper devicesMapper;

    public int deleteByPrimaryKey(String id) {
        int affectRow = -1;
        affectRow = this.devicesMapper.deleteByPrimaryKey(id);
        return affectRow;
    }

    public int insert(DevicesWithBLOBs record) {
        int affectRow = -1;
        affectRow = this.devicesMapper.insert(record);
        return affectRow;
    }

    public int insertSelective(DevicesWithBLOBs record) {
        int affectRow = -1;
        affectRow = this.devicesMapper.insertSelective(record);
        return affectRow;
    }

    public DevicesWithBLOBs selectByPrimaryKey(String id) {
        DevicesWithBLOBs devicesWithBLOBs = null;
        devicesWithBLOBs = this.devicesMapper.selectByPrimaryKey(id);
        return devicesWithBLOBs;
    }

    public DevicesWithBLOBs selectByParameter(DevicesWithBLOBs record) {
        DevicesWithBLOBs devicesWithBLOBs = null;
        devicesWithBLOBs = this.devicesMapper.selectByParameter(record);
        return devicesWithBLOBs;
    }

    public int updateByPrimaryKeySelective(DevicesWithBLOBs record) {
        int affectRow = -1;
        affectRow = this.devicesMapper.updateByPrimaryKeySelective(record);
        return affectRow;
    }

    public int updateByPrimaryKeyWithBLOBs(DevicesWithBLOBs record) {
        int affectRow = -1;
        affectRow = this.devicesMapper.updateByPrimaryKeyWithBLOBs(record);
        return affectRow;
    }

    public int updateByPrimaryKey(DevicesWithBLOBs record) {
        int affectRow = -1;
        affectRow = this.devicesMapper.updateByPrimaryKey(record);
        return affectRow;
    }

    public int selectAllCount() {
        int allcount = 0;
        allcount = this.devicesMapper.selectAllCount();
        return allcount;
    }

    public List<Map<String, Object>> selectAllWithMap() {
        List<Map<String, Object>> devices = null;
        devices = this.devicesMapper.selectAllWithMap();
        return devices;
    }

    public List<Devices> selectAllByList() {
        List<Devices> devices = null;
        devices = this.devicesMapper.selectAllByList();
        return devices;
    }

    public List<DevicesWithBLOBs> selectAllWithList() {
        List<DevicesWithBLOBs> devices = null;
        devices = this.devicesMapper.selectAllWithList();
        return devices;
    }

    public List<Map<String, Object>> selectDevicesWithBootGrid(Map<String, Object> mapPara) {
        List<Map<String, Object>> devices = null;
        devices = this.devicesMapper.selectDevicesWithBootGrid(mapPara);
        return devices;
    }

}
