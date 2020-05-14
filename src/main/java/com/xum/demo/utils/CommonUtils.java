package com.xum.demo.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

    /*
    *  current -> currentPage 当前第几页
    *  rowCount -> pageSize   每页的数目
    * */
    public static Map<String, Object> generatePara(int current, int rowCount, String searchPhrase, String dataBaseType) {
        Map<String, Object> mapPara = new HashMap();
        int offset = 0;
        if (current > 0) {
            offset = (current - 1) * rowCount;
        }
        mapPara.put(CommonVariable.OFFSET, offset);
        if (CommonVariable.MYSQL.equalsIgnoreCase(dataBaseType)) {
            mapPara.put(CommonVariable.ROWCOUNT, rowCount);
        } else if (CommonVariable.ORACLE.equalsIgnoreCase(dataBaseType)) {
            rowCount = current * rowCount;
            mapPara.put(CommonVariable.ROWCOUNT, rowCount);
        }
        if (searchPhrase != "" && searchPhrase != null) {
            mapPara.put(CommonVariable.SEARCH_PHRASE, searchPhrase);
        }
        return mapPara;
    }

    public static String generateData(List<Map<String, Object>> listMap, int current, int rowCount, int total) {
        JsonObject data = new JsonObject();
        JsonArray array = generateJSONArray(listMap);
        data.addProperty(CommonVariable.CURRENT, current);
        data.addProperty(CommonVariable.ROWCOUNT, rowCount);
        data.addProperty(CommonVariable.TOTAL, total);
        data.add(CommonVariable.ROWS, array);
        LOG.info("data.toString():" + data.toString());
        return data.toString();
    }

    public static JsonArray generateJSONArray(List<Map<String, Object>> listMap) {
        JsonArray array = new JsonArray();
        JsonObject jsonObj;
        for (Map<String, Object> kv : listMap) {
            jsonObj = new JsonObject();
            for (Map.Entry<String, Object> entry : kv.entrySet()) {
                jsonObj.addProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
            array.add(jsonObj);
        }
        return array;
    }

    public static String generateKey(String prefix, String subffix) {
        return String.format("%_%", prefix, subffix);
    }

}
