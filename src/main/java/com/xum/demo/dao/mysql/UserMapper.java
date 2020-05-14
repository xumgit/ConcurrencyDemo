package com.xum.demo.dao.mysql;

import java.util.List;
import java.util.Map;

import com.xum.demo.pojo.mysql.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    int selectAllCount();

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<Map<String, Object>> selectUsersWithBootGrid(Map<String, Object> mapPara);
}