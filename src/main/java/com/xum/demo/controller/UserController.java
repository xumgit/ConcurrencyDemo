package com.xum.demo.controller;

import com.xum.demo.pojo.mysql.User;
import com.xum.demo.service.mysql.UserService;
import com.xum.demo.utils.CommonUtils;
import com.xum.demo.utils.CommonVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static final int successStatus = 200;
    private static final int failedStatus = 100;

    @Autowired
    private UserService userService;

    @RequestMapping(value="/insertUser", method = {RequestMethod.POST})
    @ResponseBody
    public String insertUser(@RequestBody(required = true) User user) {
        String status;
        int insertId = this.userService.insertSelective(user);
        if (insertId < 0) {
            status = "{\"status\" : \"" + failedStatus + "\", \"insertId\" : \"" + insertId + "\"}";
        } else {
            status = "{\"status\" : \"" + successStatus + "\", \"insertId\" : \"" + insertId + "\"}";
        }
        return status;
    }

    @RequestMapping(value="/updateUser", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String updateUser(@RequestBody(required=true) User user) {
        String status = "{\"status\" : \"" + successStatus + "\"}";
        int affectRow = this.userService.updateByPrimaryKeySelective(user);
        if (-1 == affectRow) {
            status = "{\"status\" : \"" + failedStatus + "\"}";
        }
        return status;
    }

    @RequestMapping(value="/deleteUser", method = {RequestMethod.POST, RequestMethod.DELETE})
    @ResponseBody
    public String deleteUser(@RequestParam(value="Id", required=true) Integer Id) {
        String status = "{\"status\" : \"" + successStatus + "\"}";
        int affectRow = this.userService.deleteByPrimaryKey(Id);
        if (-1 == affectRow) {
            status = "{\"status\" : \"" + failedStatus + "\"}";
        }
        return status;
    }

    @RequestMapping(value="/getUsers", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String getUsers(@RequestParam(value="current", required=true, defaultValue="1") Integer current,
                             @RequestParam(value="rowCount", required=true, defaultValue="10") Integer rowCount,
                             @RequestParam(value="searchPhrase", required=false) String searchPhrase,
                             @RequestParam(value="sort[Id]", required=false) String sortId,
                             @RequestParam(value="sort[Name]", required=false) String sortName,
                             @RequestParam(value="sort[Age]", required=false) String sortAge,
                             @RequestParam(value="sort[Sex]", required=false) String sortSex,
                             @RequestParam(value="sort[Province]", required=false) String sortProvince,
                             @RequestParam(value="sort[Email]", required=false) String sortEmail,
                             @RequestParam(value="sort[Address]", required=false) String sortAddress) {
        Map<String, Object> mapPara;
        mapPara = CommonUtils.generatePara(current, rowCount, searchPhrase, CommonVariable.MYSQL);

        if (sortId != null) {
            mapPara.put("sortId", sortId);
        }

        if (sortName != null) {
            mapPara.put("sortName", sortName);
        }

        if (sortAge != null) {
            mapPara.put("sortAge", sortAge);
        }

        if (sortSex != null) {
            mapPara.put("sortSex", sortSex);
        }

        if (sortProvince != null) {
            mapPara.put("sortProvince", sortProvince);
        }

        if (sortEmail != null) {
            mapPara.put("sortEmail", sortEmail);
        }

        if (sortAddress != null) {
            mapPara.put("sortAddress", sortAddress);
        }

        int total;
        total = this.userService.selectAllCount();
        List<Map<String, Object>> users = this.userService.selectUsersWithBootGrid(mapPara);
        return CommonUtils.generateData(users, current, rowCount, total);
    }

}
