package com.zhzw.stampmgr;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.zhzw.model.ZhzwChannelItemModel;
import com.zhzw.util.service.ManageConfig;

import java.text.SimpleDateFormat;
import java.util.*;
public class Demo {
    public static long getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        return min;
    }

    public static void main(String[] args) throws Exception {
        DatabaseBo dbo = new DatabaseBo();
        //查询信息
        String sql = "select content,pk,usercode1,warn,leadcode,starttime from YZOA_LEADER_SCHEDULE";
        try {
            ArrayList<HashMap<String, String>> list = dbo.prepareQuery(sql, null);
            for (int i = 0; i < list.size(); i++) {
                String warn = list.get(i).get("warn");
                String content = list.get(i).get("content");
                String pk = list.get(i).get("pk");
                String usercode1 = list.get(i).get("usercode1");
                String starttime = list.get(i).get("starttime");
                System.out.println(starttime);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date start = sdf.parse(starttime);
                System.out.println("er"+start);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
