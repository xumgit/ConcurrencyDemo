package com.xum.demo.service.mysql;

import com.xum.demo.dao.mysql.UserMapper;
import com.xum.demo.pojo.mysql.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Transactional
@Service(value = "userService")
public class UserService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    public int deleteByPrimaryKey(int id) {
        int affectRow = -1;
        affectRow = this.userMapper.deleteByPrimaryKey(id);
        return affectRow;
    }

    public int insert(User record) {
        int insertId = -1;
        insertId = this.userMapper.insert(record);
        return insertId;
    }

    public int insertSelective(User record) {
        int affectRow = -1;
        affectRow = this.userMapper.insertSelective(record);
        return affectRow;
    }

    public int selectAllCount() {
        int allcount = 0;
        allcount = this.userMapper.selectAllCount();
        return allcount;
    }

    public User selectByPrimaryKey(int id) {
        User user = null;
        user = this.userMapper.selectByPrimaryKey(id);
        return user;
    }

    public int updateByPrimaryKeySelective(User record) {
        int affectRow = -1;
        affectRow = this.userMapper.updateByPrimaryKeySelective(record);
        return affectRow;
    }

    public int updateByPrimaryKey(User record) {
        int affectRow = -1;
        affectRow = this.userMapper.updateByPrimaryKey(record);
        return affectRow;
    }

    public List<Map<String, Object>> selectUsersWithBootGrid(Map<String, Object> mapPara) {
        List<Map<String, Object>> users = null;
        users = this.userMapper.selectUsersWithBootGrid(mapPara);
        return users;
    }

}
