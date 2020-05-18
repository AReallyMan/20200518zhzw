package com.zhzw.dailyschedule;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.db.ConditionModel;
import com.siqiansoft.framework.model.view.FieldModel;
import com.siqiansoft.framework.util.LoginUtil;
import com.siqiansoft.framework.util.PageControl;
import com.siqiansoft.platform.view.bo.FieldBo;
import com.zhzw.model.ZhzwChannelItemModel;
import com.zhzw.util.service.ManageConfig;
import com.zhzw.util.service.riqijihe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * 领导日程简约型，首页面信息展示数据查询类方法
 */
public class IndexMsg {
    DatabaseBo dbo = new DatabaseBo();
    /**
     * LoginModel log,log参数是当前登录人
     * ConditionModel[] cs，cs配置的是列表中的条件
     * PagingModel page，page分页使用
     */
    public List<HashMap<String, String>> myMessage(LoginModel log, ConditionModel[] cs, PagingModel page) throws Exception {
        String datetype=this.readXml("datetype");
        List<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
        String[] roles=log.getRoles();
        //登录人code
        String usercode=log.getUserCode();
        String sql="";
        if("1".equals(datetype)){
            //查询领导日程信息（简约型）
            sql="SELECT PK,STARTTIME,ENDTIME,CONTENT, CASE when SELECTLEAD is null OR SELECTLEAD = '' then USERNAME else SELECTLEAD end SELECTLEAD,USERNAME as people,ADDRESS, SUBSTR(STARTTIME, 0, 10) as riqi,SUBSTR(STARTTIME, 12, 5) as shijian,VISIBILITY,PRIORITY,WARN  FROM YZOA_LEADER_SCHEDULE WHERE 1=1 and remarks='1'";
        }
        if("2".equals(datetype)){
            //查询日程安排信息（明细型）
            sql="SELECT PK,STARTTIME,ENDTIME,CONTENT,SELECTLEAD,(CASE WHEN SELECTLEAD IS null OR SELECTLEAD='' THEN USERNAME ELSE SELECTLEAD END)  AS people,ADDRESS, SUBSTR(STARTTIME, 0, 10) as riqi,SUBSTR(STARTTIME, 12, 5) as shijian,VISIBILITY,PRIORITY,WARN  FROM YZOA_LEADER_SCHEDULE WHERE 1=1 and REMARKS='2'";
        }

        //领导日程，不是管理员并且登录人是领导
        if(!Arrays.asList(roles).contains("rc")&&"1".equals(datetype)&&(Arrays.asList(roles).contains("a02")||Arrays.asList(roles).contains("a01")||Arrays.asList(roles).contains("a24")||Arrays.asList(roles).contains("a20")||Arrays.asList(roles).contains("a50"))){
            sql+= " and ( USERCODE1='"+log.getUserCode()+"' or VISIBILITY='1' or VISIBILITY='2' or LEADCODE like '%"+usercode+"%')";
        }
        //领导日程，并且登录人不是领导
        if("1".equals(datetype)&&(!Arrays.asList(roles).contains("a02")&&!Arrays.asList(roles).contains("a01")&&!Arrays.asList(roles).contains("a24")&&!Arrays.asList(roles).contains("a20")&&!Arrays.asList(roles).contains("a50"))){
            sql+= " and ( USERCODE1='"+log.getUserCode()+"' or  VISIBILITY='1')";
        }

        //日程安排，登录人不是管理员,并且是领导
        if("2".equals(datetype)&&!Arrays.asList(roles).contains("rc")&&(Arrays.asList(roles).contains("a02")||Arrays.asList(roles).contains("a01")||Arrays.asList(roles).contains("a24")||Arrays.asList(roles).contains("a20")||Arrays.asList(roles).contains("a50"))){
            sql+= " and ( USERCODE1='"+log.getUserCode()+"' or VISIBILITY='1' or VISIBILITY='2' or TIME like '%"+log.getUserCode()+"%')";
        }
        //日程安排，登录人不是管理员,并且不是领导
        if("2".equals(datetype)&&!Arrays.asList(roles).contains("rc")&&!(Arrays.asList(roles).contains("a02")||Arrays.asList(roles).contains("a01")||Arrays.asList(roles).contains("a24")||Arrays.asList(roles).contains("a20")||Arrays.asList(roles).contains("a50"))){
            sql+= " and ( VISIBILITY='1' or USERCODE1='"+log.getUserCode()+"' or TIME like '%"+log.getUserCode()+"%')";
        }
        //end

        String time = "";
        //列表上的条件配置
        if ((cs != null) && (!"".equals(cs))) {
            for (int i = 0; i < cs.length; i++) {
                if (!cs[i].getValue().equals(null) && "" != cs[i].getValue()) {
                    if (cs[i].getId().equals("bycontent")) {
                        //根据内容模糊查询
                        sql = sql + " and CONTENT like '%" + cs[i].getValue() + "%'";
                    }
                    if (cs[i].getId().equals("bytime")) {
                        time = cs[i].getValue();
                        //根据时间段内（大于开始时间，小于结束时间）查询
                        sql = sql + " and (substr(STARTTIME,0,10) <= '" + cs[i].getValue() + "' and substr(ENDTIME,0,10) >= '"+cs[i].getValue()+"')";
                    }
                }
            }
        }
        //倒叙排列
        sql = sql + " ORDER BY RIQI DESC";

        //对日期判断是否跨天，显示问题
        List<HashMap<String,String>> listNew = new ArrayList<HashMap<String, String>>();
        List<HashMap<String,String>> listAll=dbo.prepareQuery(sql,null);
        //riqijihe ri=new riqijihe();

        for(int i=0;i<listAll.size();i++){
            System.out.println("进去for  l="+listAll.size());
            String start= listAll.get(i).get("STARTTIME");
            String startJ=start.substring(0,10);
            String end= listAll.get(i).get("ENDTIME");
            String endJ=end.substring(0,10);
            System.out.println("start="+start+"end"+end);
            //相同则为同一天,否则就是跨天
            if(startJ.equals(endJ)){
                HashMap<String,String> mapEqual=new HashMap<>();
                System.out.println("t同一天");
                mapEqual=listAll.get(i);
                System.out.println("tongyit"+mapEqual.size());
                mapEqual.put("RIQI",startJ);
                mapEqual.put("SHIJIAN",start.substring(5,16)+"至"+end.substring(5,16));
                if (time != null && !"".equals(time)) {
                    if(time.equals(startJ)){
                        listNew.add(mapEqual);
                    }
                }else {
                    listNew.add(mapEqual);
                }
            }else {
                System.out.println("butong");
                //跨几天显示几条
                List<String> days = riqijihe.getDays(startJ, endJ);
                Iterator<String> iterator = days.iterator();
                System.out.println("daysl="+days.size());
                HashMap<String,String> map = listAll.get(i);
                while (iterator.hasNext()){
                    HashMap<String,String> maps = new HashMap<>();
                    maps.putAll(map);
                    //HashMap<String,String> map = new HashMap<>();
                    String date= iterator.next();
                    maps.put("RIQI",date);
                   // System.out.println("riqi"+maps.get("RIQI"));
                    maps.put("SHIJIAN",start.substring(5,16)+"至"+end.substring(5,16));
                   // System.out.println("SHij"+maps.get("SHIJIAN"));
                   // System.out.println("listsize"+listNew.size());
                    if (time != null && !"".equals(time)) {
                        if(time.equals(date)){
                            listNew.add(maps);
                        }
                    }else{
                        listNew.add(maps);
                    }

                }
            }
        }
       // System.out.println("获取集合*************"+listNew.toString()+"长度为################"+listNew.size());

        //列表信息的分页
        int from = page.getFrom();
        int to = page.getTo();
        int pageRows = page.getPageRows();
        int curPage = page.getCurPage();

        if (listNew.size() == 0) {
            PageControl.calcPage(page, 0, 0);
            return null;
        }
        if ((from == 1) && (to == 0)) {
            to = listNew.size();
        }
        int rowsCount = listNew.size();
        int pageCount = listNew.size() / pageRows + (listNew.size() % pageRows == 0 ? 0 : 1);
        page.setPageCount(pageCount);
        page.setRowsCount(rowsCount);
        if (to > listNew.size()) {
            to = listNew.size();
        }
        page.setTo(to);
        return listNew.subList(from - 1, to);
    }
    //读取xml,获取日程类型
    public String readXml(String type){
        ZhzwChannelItemModel[] channel = ManageConfig.getConfig("dailyschedule");
        Map<String, Object> mapXml = channel[0].getModuleList().get(0);
        String result = (String) mapXml.get(type);
        return result;
    }

}
