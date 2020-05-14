package com.xum.demo.controller;

import com.xum.demo.service.oracle.UserinfoService;
import com.xum.demo.utils.CommonUtils;
import com.xum.demo.utils.CommonVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oracle")
public class OracleTestController {

    private static final Logger LOG = LoggerFactory.getLogger(OracleTestController.class);

    @Autowired
    private UserinfoService userinfoService;

    @RequestMapping(value="/getData", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String selectUserinfoWithBootGrid(@RequestParam(value="current", required=true, defaultValue="1") Integer current,
                                             @RequestParam(value="rowCount", required=true, defaultValue="10") Integer rowCount,
                                             @RequestParam(value="searchPhrase", required=false) String searchPhrase,
                                             @RequestParam(value="sort[Name]", required=false, defaultValue="desc") String sortName) {
        Map<String, Object> mapPara;
        mapPara = CommonUtils.generatePara(current, rowCount, searchPhrase, CommonVariable.ORACLE);

        if (sortName != null) {
            mapPara.put("NameSort", sortName);
        }

        int total;
        total = this.userinfoService.selectAllCount();
        List<Map<String, Object>> authors = this.userinfoService.selectUserinfoWithBootGrid(mapPara);
        return CommonUtils.generateData(authors, current, rowCount, total);
    }

}
