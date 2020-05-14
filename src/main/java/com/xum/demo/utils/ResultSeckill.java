package com.xum.demo.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultSeckill extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public ResultSeckill() {
        put(CommonVariable.CODE, 0);
    }

    public static ResultSeckill error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static ResultSeckill error(String msg) {
        return error(500, msg);
    }

    public static ResultSeckill error(int code, String msg) {
        ResultSeckill r = new ResultSeckill();
        r.put(CommonVariable.CODE, code);
        r.put(CommonVariable.MESSAGE, msg);
        return r;
    }
    public static ResultSeckill error(Object msg) {
        ResultSeckill r = new ResultSeckill();
        r.put(CommonVariable.MESSAGE, msg);
        return r;
    }
    public static ResultSeckill ok(Object msg) {
        ResultSeckill r = new ResultSeckill();
        r.put(CommonVariable.MESSAGE, msg);
        return r;
    }


    public static ResultSeckill ok(Map<String, Object> map) {
        ResultSeckill r = new ResultSeckill();
        r.putAll(map);
        return r;
    }

    public static ResultSeckill ok() {
        return new ResultSeckill();
    }

    @Override
    public ResultSeckill put(String key, Object value) {
        super.put(key, value);
        return this;
    }

}
