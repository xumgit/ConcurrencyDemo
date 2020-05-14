package com.xum.demo.dao.oracle;

import com.xum.demo.pojo.oracle.Userinfo;

import java.util.List;
import java.util.Map;

public interface UserinfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Userinfo record);

    int insertSelective(Userinfo record);

    int selectAllCount();

    Userinfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Userinfo record);

    int updateByPrimaryKey(Userinfo record);

    List<Map<String, Object>> selectUserinfoWithBootGrid(Map<String, Object> mapPara);
}