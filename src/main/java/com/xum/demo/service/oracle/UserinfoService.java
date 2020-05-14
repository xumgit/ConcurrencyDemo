package com.xum.demo.service.oracle;

import com.xum.demo.dao.oracle.UserinfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Transactional
@Service(value = "userinfoService")
public class UserinfoService {

    @Resource
    private UserinfoMapper userinfoMapper;

    public int selectAllCount() {
        int allcount = 0;
        allcount = this.userinfoMapper.selectAllCount();
        return allcount;
    }

    public List<Map<String, Object>> selectUserinfoWithBootGrid(Map<String, Object> mapPara) {
        List<Map<String, Object>> userInfoList = null;
        userInfoList = this.userinfoMapper.selectUserinfoWithBootGrid(mapPara);
        return userInfoList;
    }
}
