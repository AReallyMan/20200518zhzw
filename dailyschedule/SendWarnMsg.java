package com.zhzw.dailyschedule;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.zhzw.model.ZhzwChannelItemModel;
import com.zhzw.util.service.ManageConfig;

import javax.servlet.ServletOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
public class SendWarnMsg {
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

    /**
     * 领导日程，发送leadcode
     * 日程安排科员：usercode1
     * 日程安排管理员：leadcode
     */
  public static void SendMsg(){
      DatabaseBo dbo=new DatabaseBo();
      LoginModel login = new LoginModel();
      //查询信息
      String sql="select content,pk,usercode1,warn,leadcode,starttime from YZOA_LEADER_SCHEDULE";
      try {
          ZhzwChannelItemModel[] channel = ManageConfig.getConfig("dailyschedule");
          //简约1、明细2
          String methodApp = channel[0].getModuleList().get(0).get("methodApp")+"";
          //datetype 1领导，2日程
          String datetype = channel[0].getModuleList().get(0).get("datetype")+"";
          String[] roles = login.getRoles();
          System.out.println("roles===="+roles);
          boolean adm=false;
          if(Arrays.asList(roles).contains("rc")){
              adm=true;
          }
          ArrayList<HashMap<String, String>> list = dbo.prepareQuery(sql, null);
          String[] leadcode=null;
          for(int i=0;i<list.size();i++){
              String warn = list.get(i).get("WARN");
              String content = list.get(i).get("CONTENT");
              String pk = list.get(i).get("PK");
              String usercode1 = list.get(i).get("USERCODE1");
              String starttime = list.get(i).get("STARTTIME");
              System.out.println("starttime==========="+starttime);
              SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              Date start = sdf.parse(starttime);
              System.out.println("start==========="+start);
              Date nowDate=new Date();
              //两者之间分钟差
              long minute=getDatePoor(nowDate,start);
              System.out.println("minute============"+minute);
              if("1".equals(datetype)||adm) {
                  leadcode = list.get(i).get("LEADCODE").split(",");
              }else if("2".equals(datetype)&&!adm){
                  leadcode = list.get(i).get("USERCODE1").split(",");
              }
              String url="";
              //methodApp=1 url为简约型，=2是明细型，两者url不同
              if("1".equals(methodApp)){
                  url="../dailyschedule/leadschedulej.cmd?$ACTION=todetails&$leadjlistbind-key='"+pk+"'&$FIELDSET=leadjlistbind";
              }else {
                  url="../dailyschedule/leadschedule.cmd?$MOBILE=N&$FIELDSET=list$$TABLE=topicmeeting-meetinglist-am01$list-pageno&$SYSTEM=topicmeeting&$MODULE=meetinglist&$ACTION=detail&$list-key='"+pk+"'&id='"+pk+"'";
              }
              //15分钟
              if(minute==15&&"1".equals(warn)){
                  System.out.println("15-------------");
                  for (int k=0;k<leadcode.length;k++){
                      System.out.println("15+lena="+leadcode.length);
                      ManageConfig.sendMess(content,content,pk,usercode1,leadcode[k],url,"日程管理");
                  }
              }
              //30分钟
              if(minute==30&&"2".equals(warn)){
                  System.out.println("30-------------");
                  for (int k=0;k<leadcode.length;k++){
                      ManageConfig.sendMess(content,content,pk,usercode1,leadcode[k],url,"日程管理");
                  }
              }
              //60分钟
              if(minute==60&&"3".equals(warn)){
                  System.out.println("60-------------");
                  for (int k=0;k<leadcode.length;k++){
                      ManageConfig.sendMess(content,content,pk,usercode1,leadcode[k],url,"日程管理");
                  }
              }
              //120分钟
              if(minute==120&&"4".equals(warn)){
                  System.out.println("120-------------");
                  for (int k=0;k<leadcode.length;k++){
                      ManageConfig.sendMess(content,content,pk,usercode1,leadcode[k],url,"日程管理");
                  }
              }
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
}
