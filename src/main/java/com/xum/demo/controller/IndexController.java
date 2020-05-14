package com.xum.demo.controller;

import com.xum.demo.pojo.mysql.DevicesWithBLOBs;
import com.xum.demo.service.mysql.DevicesService;
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
@RequestMapping("/index")
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    DevicesService devicesService;

    @RequestMapping("/indexOne")
    public String indexOne() {
        return "index/indexOne";
    }


    @RequestMapping("/indexVue")
    public String indexVue() {
        return "vue/index";
    }

    @RequestMapping(value="/updateDevices", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String updateDevices(@RequestBody(required=true) DevicesWithBLOBs devicesWithBLOBs) {
        String status = "{\"status\" : \"200\"}";
        int affectRow = this.devicesService.updateByPrimaryKeySelective(devicesWithBLOBs);
        if (-1 == affectRow) {
            status = "{\"status\" : \"100\"}";
        }
        return status;
    }

    @RequestMapping(value="/deleteDevices", method = {RequestMethod.POST, RequestMethod.DELETE})
    @ResponseBody
    public String deleteDevices(@RequestParam(value="Id", required=true) String Id) {
        String status = "{\"status\" : \"200\"}";
        int affectRow = this.devicesService.deleteByPrimaryKey(Id);
        if (-1 == affectRow) {
            status = "{\"status\" : \"100\"}";
        }
        return status;
    }

    @RequestMapping(value="/saveDevices", method = {RequestMethod.POST})
    @ResponseBody
    public String saveDevices(@RequestBody(required=true) DevicesWithBLOBs devicesWithBLOBs) {
        String status = "{\"status\" : \"200\"}";
        int affectRow = this.devicesService.insertSelective(devicesWithBLOBs);
        if (-1 == affectRow) {
            status = "{\"status\" : \"100\"}";
        }
        return status;
    }

    @RequestMapping(value="/getDevices")
    @ResponseBody
    public String getDevices(@RequestParam(value="current", required=true, defaultValue="1") Integer current,
                             @RequestParam(value="rowCount", required=true, defaultValue="10") Integer rowCount,
                             @RequestParam(value="searchPhrase", required=false) String searchPhrase,
                             @RequestParam(value="sort[TVIPAddress]", required=false) String sortTVIPAddress) {
        Map<String, Object> mapPara;
        mapPara = CommonUtils.generatePara(current, rowCount, searchPhrase, CommonVariable.MYSQL);

        if (sortTVIPAddress != null) {
            mapPara.put("TVIPAddressSort", sortTVIPAddress);
        }

        int total;
        total = this.devicesService.selectAllCount();
        List<Map<String, Object>> authors = this.devicesService.selectDevicesWithBootGrid(mapPara);
        return CommonUtils.generateData(authors, current, rowCount, total);
    }

    @RequestMapping(value="/getData")
    @ResponseBody
    public String getData(@RequestParam(value="searchPhrase", required=false) String searchPhrase) {
        String status = "{\"status\" : \"200\", \"searchPhrase\" : \"" + searchPhrase + "\"}";
        return status;
    }

}
