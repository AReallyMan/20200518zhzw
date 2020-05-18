package com.zhzw.util.service;

import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.framework.bo.DatabaseBo;

public class RequestUtil {
    private DatabaseBo dbo = new DatabaseBo();
    /**
     * 请求成功
     * @param jsonRst
     * @return
     */
    public JSONObject success(JSONObject jsonRst) {
        JSONObject json=new JSONObject();
        json.put("code", "1");
        json.put("Msg", "请求成功");
        json.put("data", jsonRst);
        return json;
    }


    /**
     * 请求失败
     * @param Msg
     * @return
     */
    public  JSONObject faild(String Msg) {
        JSONObject json=new JSONObject();
        json.put("code", "0");
        json.put("Msg", Msg);
        return json;
    }

    /**
     * 初始化 一个json对象 并给初始化一对键值
     * @param key
     * @param value
     * @return
     */
    public JSONObject getJsonObject(String key,Object value){
        JSONObject json=new JSONObject();
        json.put(key, value);
        return json;
    }
}
