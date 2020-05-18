package com.zhzw.util.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//遍历一个月的每一天
public class bianlitian {
    private static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
    public static List<String> getDatesByYearAndMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, 1);
        List<String> list = new ArrayList<String>();
        do{
          //  System.out.println(calendar.getTime());

            String s = sdf.format(calendar.getTime());
           // System.out.println(s);

            list.add(s);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }while(month-1==calendar.get(Calendar.MONTH));
        for(String d:list){
            //TODO 这里可以插入遍历代码
           // System.out.println(d.toLocaleString());
           //System.out.println(d);
        }
        //TODO 也可以返回list后再自行进行对应的遍历处理
        return list;

    }
}
