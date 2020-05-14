package com.xum.demo.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultUtil extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public ResultUtil() {
        put(CommonVariable.CODE, 0);
    }

    public static ResultUtil error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static ResultUtil error(String msg, Object id) {
        return error(500, msg, id);
    }

    public static ResultUtil error(int code, String msg, Object id) {
        ResultUtil r = new ResultUtil();
        r.put(CommonVariable.CODE + id, code);
        r.put(CommonVariable.MESSAGE + id, msg);
        r.put(CommonVariable.MESSAGE_MONEY + id, 0);
        r.put(CommonVariable.MESSAGE_AFFECT_ROW + id, -1);
        return r;
    }
    public static ResultUtil error(Object msg, Object id) {
        ResultUtil r = new ResultUtil();
        r.put(CommonVariable.MESSAGE + id, msg);
        r.put(CommonVariable.MESSAGE_MONEY + id, 0);
        r.put(CommonVariable.MESSAGE_AFFECT_ROW + id, -1);
        return r;
    }
    public static ResultUtil ok(Object msg, Object money, Object affectRow, Object id) {
        ResultUtil r = new ResultUtil();
        r.put(CommonVariable.MESSAGE + id, msg);
        r.put(CommonVariable.MESSAGE_MONEY + id, money);
        r.put(CommonVariable.MESSAGE_AFFECT_ROW + id, affectRow);
        return r;
    }


    public static ResultUtil ok(Map<String, Object> map) {
        ResultUtil r = new ResultUtil();
        r.putAll(map);
        return r;
    }

    public static ResultUtil ok() {
        return new ResultUtil();
    }

    @Override
    public ResultUtil put(String key, Object value) {
        super.put(key, value);
        return this;
    }

}
