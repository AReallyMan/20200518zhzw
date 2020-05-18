package com.zhzw.dailyschedule;
import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.util.LoginUtil;
import com.zhzw.model.ZhzwChannelItemModel;
import com.zhzw.util.SMSUtil;
import com.zhzw.util.service.ManageConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class ScheduleServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        //获取项目路径
        String Ac =request.getParameter("Ac");
        System.out.println("ac========+++"+Ac);
        //点击进入详情后的删除按钮
        if("delete".equals(Ac)){
            this.delete(request,response);
        }
        //页面表单上的《批量》删除日程
        if("delete1".equals(Ac)){
            this.delete1(request,response);
        }
        //修改日程信息
        if("update".equals(Ac)){
            this.update(request,response);
        }
        //表单集合模块的删除（系统生成的页面，因为不勾选删除报错，这边重写）
        if("modeldelete".equals(Ac)){
            this.modeldelete(request,response);
        }
        //新增日程
        if("insert".equals(Ac)){
            this.insert(request,response);
        }
        //首页展示的日程
        if("find".equals(Ac)) {
            this.leaderSchedule (request, response);
        }
        //读取datetype
        if("readDateType".equals(Ac)){
            this.readDateType(request,response);
        }
    }
    //表单集合模块的删除
    private void modeldelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        boolean flag=false;
        List<HashMap<String,String>> list=null;
        LoginModel login = LoginUtil.getLoginObject(request);
        //登录人编码
        String userCode=login.getUserCode();
        System.out.println("usercode======"+userCode);
        //登录人的角色编码
        String[] roles = login.getRoles();
        DatabaseBo dbo = new DatabaseBo();
        PrintWriter out = response.getWriter ();
        JSONObject Rst = new JSONObject ();
        String id = request.getParameter("ids");
        String[] ids = id.split(",");
        for(int i=0;i<ids.length;i++){
            //根据或者的id查询数据库中信息
            String sql="SELECT * FROM YZOA_LEADER_SCHEDULE WHERE  PK='"+ids[i]+"'";
            try{
                list = dbo.prepareQuery(sql, null);
                String usercode1=list.get(0).get("USERCODE1");
                //System.out.println("for循环中的usercode1==="+usercode1);
                //登录人等于新增日程的人，则可以删除
                if (userCode.equals(usercode1)){
                    flag=true;
                }else {
                    flag=false;
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            //当为rc管理员时，可以删除
            if(Arrays.asList(roles).contains("rc")){
                flag=true;
            }
            //flag为true则可以删除，否则不可以
            if (flag) {
                dbo.deleteRows(ids, "YZOA_LEADER_SCHEDULE");
            }
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        Rst.put ("flag",flag);
        out.print (Rst);
        out.flush ();
        out.close ();
    }
    //批量删除操作
    private void delete1(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        boolean flag=false;
        List<HashMap<String,String>> list=null;
        LoginModel login = LoginUtil.getLoginObject(request);
        //登录人编码
        String userCode=login.getUserCode();
        System.out.println("usercode======"+userCode);
        //登录人的角色编码
        String[] roles = login.getRoles();
        DatabaseBo dbo = new DatabaseBo();
        PrintWriter out = response.getWriter ();
        JSONObject Rst = new JSONObject ();
        String id = request.getParameter("id");
        //System.out.println("id======"+id);
        String[] ids = id.split(",");
        for(int i=0;i<ids.length;i++){
            //根据获取表单的id查询信息
            String sql="SELECT * FROM YZOA_LEADER_SCHEDULE WHERE  PK='"+ids[i]+"'";
            try{
                list = dbo.prepareQuery(sql, null);
                String usercode1=list.get(0).get("USERCODE1");
                System.out.println("for循环中的usercode1==="+usercode1);
                //登录人等于新增日程的人，则可以删除
                if (userCode.equals(usercode1)){
                    flag=true;
                    System.out.println("true!!!!!!!!!!!!!");
                }else {
                    flag=false;
                    System.out.println("flase!!!!!!!!!!!!!");
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //List<HashMap<String,String>> list = null;
        try {
            //当为rc管理员时，可以删除
            if(Arrays.asList(roles).contains("rc")){
                flag=true;
            }
            if(flag) {
                dbo.deleteRows(ids, "Yzoa_Leader_Schedule");
                System.out.println("执行了删除操作");
            }
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        Rst.put ("flag",flag);
        out.print (Rst);
        out.flush ();
        out.close ();
    }
   //删除日程
    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String remarks="";
        DatabaseBo dbo = new DatabaseBo();
        PrintWriter out = response.getWriter ();
        JSONObject Rst = new JSONObject ();
        String pk = request.getParameter("pk");
        String tablename="Yzoa_Leader_Schedule";
        List<HashMap<String,String>> list = null;
        try {
            dbo.deleteRow(pk,tablename);
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        Rst.put ("json","delete-success!");
        out.print (Rst);
        out.flush ();
        out.close ();
    }
    //新增日程
    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //remarks标识是否为领导日程1是2不是
        String remarks="";
        //登录人角色
        String[] roles;
        boolean flag=false;
        //当为管理员 adm为y,不是则为n
        String adm="";
        LoginModel login = LoginUtil.getLoginObject(request);
        //判断管理员
        roles=login.getRoles();
        System.out.println("111111111111rc ="+Arrays.asList(roles).contains("rc"));
        if(Arrays.asList(roles).contains("rc")){
            adm="y";
        }else {
            adm="n";
        }
        //判断当前登录人角色
        String role = login.getDeptCode();
        String usercode1=login.getUserCode();
        //判断1领导日程还是2日程安排
        String datetype=this.readXml("datetype");
        //判断1简约型还是2明细型
        String methodApp=this.readXml("methodApp");
        System.out.println("insert中的--------------"+datetype);
        DatabaseBo dbo = new DatabaseBo();
        PrintWriter out = response.getWriter();
        JSONObject Rst = new JSONObject ();
        HashMap<String,String> map = new HashMap<String, String>();
        String pk = request.getParameter("pk");
        String content = request.getParameter("content");
        String type = request.getParameter("type");
        String address = request.getParameter("address");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        String selectlead = request.getParameter("selectlead");
        String entourage = request.getParameter("entourage");
        String people = request.getParameter("people");
        String visibility = request.getParameter("visibility");
        String priority = request.getParameter("priority");
        String warn = request.getParameter("warn");
        String leadcode = request.getParameter("leadcode");
        String entouragecode = request.getParameter("entouragecode");
        map.put("LEADCODE",leadcode);
        map.put("ENTOURAGECODE",entouragecode);
        map.put("USERCODE1",usercode1);
        map.put("CONTENT",content);
        map.put("TYPE",type);
        map.put("USERNAME",people);
        map.put("ADDRESS",address);
        map.put("STARTTIME",starttime);
        map.put("ENDTIME",endtime);
        map.put("SELECTLEAD",selectlead);
        map.put("ENTOURAGE",entourage);
        map.put("VISIBILITY",visibility);
        map.put("PRIORITY",priority);
        map.put("WARN",warn);
        //判断是否是镇领导
        if("d00".equals(role)){
            flag=true;
        }
        //isNull,为空则为y,不为空为n
        String isNull;
        //System.out.println("111111111111leadcode="+leadcode);
        if("".equals(leadcode)||leadcode==null){
            isNull="y";
        }else {
            isNull="n";
        }
        //当是管理员并且是日程安排模块，为自己新增时（leadcode存的有领导编码，也可能是日程安排模块中领导和科员编码）
        if("y".equals(adm)&&"2".equals(datetype)&&"y".equals(isNull)&&flag){
            remarks = "1";
            map.put("REMARKS", "1");
        }
        if("y".equals(adm)&&"2".equals(datetype)&&"y".equals(isNull)&&!flag){
            remarks = "2";
            map.put("REMARKS", "2");
        }
        //日程安排模块管理员新增时，当选择很多人员时，拆分遍历leadcode，判断有无领导，有则在领导中显示
        String[] code=leadcode.split(",");
        if("y".equals(adm)&&"2".equals(datetype)&&"n".equals(isNull)){
            System.out.println("是管理员2不为空进来了:长度"+code.length);
           for(int i=0;i<code.length;i++){
               System.out.println("进去for"+code.length);
               //根据d00查询领导
               String sql = "SELECT * from EAP_ACCOUNT where deptcode='d00' and code = '"+code[i]+"'";
               System.out.println("执行了sql"+sql);
               try{
                   ArrayList<HashMap<String, String>> list = dbo.prepareQuery(sql, null);
                   System.out.println("list.leng="+list.size());
                   if(list.size()>0){
                       System.out.println("是管理员2不为空进来了+code包含aa20进来了");
                       remarks = "1";
                       map.put("REMARKS", "1");
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
        }
        //REMARKS=datetype,1表示是领导日程,当不是管理员新增时
        if ("1".equals(datetype)) {
            remarks = "1";
            map.put("REMARKS", "1");
        }
        //2表示是日程安排,判断是否为领导，是领导存入1，不是存入2
        //if不是管理员
        if("n".equals(adm)) {
            if ("2".equals(datetype) && flag) {
                remarks = "1";
                map.put("REMARKS", "1");
            }
            if ("2".equals(datetype) && !flag) {
                remarks = "2";
                map.put("REMARKS", "2");
            }
        }
        /*
        上边的remarks，之前是为了信息互通，领导时为1，科员时为2
        现在需求，更改为信息不互通，只需要区分是datetype，领导日程1，还是日程安排2；
         */
        map.put("REMARKS",datetype);
        try {
            dbo.insert(map,"Yzoa_Leader_Schedule");
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        //methodApp=1 url为简约型，=2是明细型，两者url不同
        String url="";
        if("1".equals(methodApp)){
            url="../dailyschedule/leadschedulej.cmd?$ACTION=todetails&$leadjlistbind-key='"+pk+"'&$FIELDSET=leadjlistbind";
        }else {
            url="../dailyschedule/leadschedule.cmd?$MOBILE=N&$FIELDSET=list$$TABLE=topicmeeting-meetinglist-am01$list-pageno&$SYSTEM=topicmeeting&$MODULE=meetinglist&$ACTION=detail&$list-key='"+pk+"'&id='"+pk+"'";
        }
        //读取xml获取消息提醒(领导日程+简约型)
        if(datetype.equals("1")){
            String[] leadcodes = leadcode.split(",");
            for(int i=0;i<leadcodes.length;i++){
                String news = this.readXml("news");
                //系统提示
                if(news.equals("0")){
                    //给传阅人发消息
                    ManageConfig.sendMess(content,content,pk,usercode1,leadcodes[i],url,"日程管理");
                    //短信提示
                }else if(news.equals("1")){
                    //给传阅人发短信
                    SMSUtil.sendSMS(dbo,leadcodes[i],"dailyschedule",login.getUserName());
                    //系统+短信提示
                }else if(news.equals("2")){
                    //给传阅人发消息
                    ManageConfig.sendMess(content,content,pk,usercode1,leadcodes[i],url,"日程管理");
                    //给传阅人发短信
                    SMSUtil.sendSMS(dbo,leadcodes[i],"dailyschedule",login.getUserName());
                }
            }
        }
        //日程安排+简约型
        if(datetype.equals("2")){
                String news = this.readXml("news");
                //系统提示
                if(news.equals("0")){
                    //给传阅人发消息
                    ManageConfig.sendMess(content,content,pk,usercode1,usercode1,url,"日程管理");
                    //短信提示
                }else if(news.equals("1")){
                    //给传阅人发短信
                    SMSUtil.sendSMS(dbo,usercode1,"dailyschedule",login.getUserName());
                    //系统+短信提示
                }else if(news.equals("2")){
                    //给传阅人发消息
                    ManageConfig.sendMess(content,content,pk,usercode1,usercode1,url,"日程管理");
                    //给传阅人发短信
                    SMSUtil.sendSMS(dbo,usercode1,"dailyschedule",login.getUserName());
                }
        }
        Rst.put ("remarks",remarks);
        out.print (Rst);
        out.flush ();
        out.close ();
    }
    //修改日程
    private void update(HttpServletRequest request, HttpServletResponse response)
    {
        System.out.println("updatekkkkkkkkkkk");
        DatabaseBo dbo = new DatabaseBo();
        String datetype=this.readXml("datetype");
        HashMap<String,String> map = new HashMap<String, String>();
        String updatepeople = request.getParameter("updatepeople");
        System.out.println("updatepeople"+updatepeople);
        String pk = request.getParameter("pk");
        String content = request.getParameter("content");
        String leadcode = request.getParameter("leadcode");
        String type = request.getParameter("type");
        String address = request.getParameter("address");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        String selectlead = request.getParameter("selectlead");
       // String leadcode = request.getParameter("leadcode");
        String entourage = request.getParameter("entourage");
        String username = request.getParameter("username");
        String visibility = request.getParameter("visibility");
        String priority = request.getParameter("priority");
        String warn = request.getParameter("warn");
        LoginModel login = LoginUtil.getLoginObject(request);
        String[] roles = login.getRoles();

        ArrayList<HashMap<String, String>> codeMaps= new ArrayList<>();
       if("".equals(updatepeople)||updatepeople==null){
           //System.out.println("weikong");
           String[] userNames = username.split(",");
           System.out.println(userNames.length);
           String code = "";
           for (int i = 0; i < userNames.length; i++) {
               //从account表中根据传进来的参数查询code
               String sql = "select code from EAP_ACCOUNT where name = '" + userNames[i] + "'";
               System.out.println("执行了sql="+sql);
               try {
                    codeMaps = dbo.prepareQuery(sql, null);
                   code += codeMaps.get(0).get("CODE")+",";
                   System.out.println("code="+code);

               }catch (Exception e){
                   e.printStackTrace();
               }
           }
           map.put("TIME", code);
       }else{
           System.out.println("buweikong");
           map.put("TIME",updatepeople);
       }
        map.put("USERNAME",username);
        map.put("LEADCODE",leadcode);
        map.put("VISIBILITY",visibility);
        map.put("PRIORITY",priority);
        map.put("WARN",warn);
        map.put("PK",pk);
        map.put("CONTENT",content);
        map.put("TYPE",type);
        map.put("ADDRESS",address);
        map.put("STARTTIME",starttime);
        map.put("ENDTIME",endtime);
        map.put("SELECTLEAD",selectlead);
        map.put("ENTOURAGE",entourage);
        try {
            dbo.update(map,"Yzoa_Leader_Schedule");
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        //this.leaderSchedule(request,response);
    }
    //领导日程查询(首页展示)
    private void leaderSchedule(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DatabaseBo dbo = new DatabaseBo();

        String remarks = this.readXml("datetype");
        //this.readXml()
        LoginModel login = LoginUtil.getLoginObject(request);
        System.out.println("leader----------remarks="+remarks);
        String date = request.getParameter("date");
        //当date属性为空，则为他赋值当前时间，没有值默认查询当前时间
        if(date==null){
            SimpleDateFormat sdf=new SimpleDateFormat ("yyyy-MM-dd");
            date = sdf.format(new Date());
        }
        //获取本周的日期和星期，值为list_time
        List<HashMap<String,String>> list_time = this.time(date);
        List<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>> ();
        //person 领导或者人员
        List<HashMap<String,String>> person = null;
        //定义每天的的字段
        HashMap<String,Object> day = null;
        String content = request.getParameter("content1");
        if(content != null && !"".equals(content)){
            content = URLDecoder.decode(request.getParameter("content1"), "UTF-8");
        }
        //String content= URLDecoder.decode(request.getParameter("content1"), "UTF-8");
        for (HashMap<String,String> date1:list_time) {
            person = new ArrayList<HashMap<String, String>>();
            //datetime为日期
            String datetime = date1.get("DATE");
            //获取新增日程的信息，首页展示
            try {
                if(this.getMsgIndex(datetime,content,remarks,login).size()!=0){
                    person.addAll(this.getMsgIndex(datetime,content,remarks,login));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(person.size()!=0) {
                day = new HashMap<String, Object> ();
                //dat1 是日期和时间
                day.put ("date", date1);
                //person 是领导或者人员
                day.put ("person",person);
                list.add (day);
            }
        }
        String[] roles=login.getRoles();

        request.setAttribute("remarks",remarks);
        request.setAttribute("date",date);
        request.setAttribute ("list",list);
        request.getRequestDispatcher ("dailyschedule/leader.jsp").forward (request,response);
    }
    //从数据库中取得首页需要展示的数据
    private List<HashMap<String,String>> getMsgIndex(String date,String content,String remarks, LoginModel login) throws ParseException {
        DatabaseBo dbo = new DatabaseBo();
        String datetype=this.readXml("datetype");
        //登录人code
        String usercode=login.getUserCode();
        //String[] roles=login.getRoles();
       // String remarks = this.readXml("datetype");
        List<HashMap<String,String>> list = null;
        String sql="";
        if("1".equals(remarks)){
            //查询领导日程的信息
            sql = "select type as datetype,starttime,visibility,priority,warn,endtime,pk,content,username,CASE when SELECTLEAD is null OR SELECTLEAD = '' then USERNAME else SELECTLEAD end SELECTLEAD,'" + date + "' as datetime,address,type, CONCAT(CONCAT(SUBSTR(STARTTIME,6,11),'至'),SUBSTR(ENDTIME,6,11)) as time,'dailyschedule/leadschedule.cmd?$MOBILE=N&$FIELDSET=list$$TABLE=topicmeeting-meetinglist-am01$list-pageno&$SYSTEM=topicmeeting&$MODULE=meetinglist&$ACTION=detail&$list-key='|| pk ||'&'||'id='||pk as role from YZOA_LEADER_SCHEDULE where  Endtime>='" + date + " 00:00:00' AND Starttime<='" + date + " 23:59:59'  and REMARKS='1' " ;
        }
        if("2".equals(remarks)) {
            //查询日程安排的信息
            sql = "select type as datetype,visibility,priority,warn,starttime,endtime,pk,content,username,selectlead,'" + date + "' as datetime,address,type, CONCAT(CONCAT(SUBSTR(STARTTIME,6,11),'至'),SUBSTR(ENDTIME,6,11)) as time,'dailyschedule/leadschedule.cmd?$MOBILE=N&$FIELDSET=list$$TABLE=topicmeeting-meetinglist-am01$list-pageno&$SYSTEM=topicmeeting&$MODULE=meetinglist&$ACTION=detail&$list-key='|| pk ||'&'||'id='||pk as role from YZOA_LEADER_SCHEDULE where  Starttime<='" + date + " 23:59:59'  and Endtime>='" + date + " 00:00:00' and REMARKS='2'";
        }
        String[] roles=login.getRoles();

        //领导日程，并且登录人是领导(看自己新增的、1或者2的或者，别人选了你的)
        if(!Arrays.asList(roles).contains("rc")&&"1".equals(datetype)&&(Arrays.asList(roles).contains("a02")||Arrays.asList(roles).contains("a01")||Arrays.asList(roles).contains("a24")||Arrays.asList(roles).contains("a20")||Arrays.asList(roles).contains("a50"))){
            sql+= " and ( USERCODE1='"+login.getUserCode()+"' or VISIBILITY='1' or VISIBILITY='2' or LEADCODE like '%"+usercode+"%')";
        }
        //领导日程，并且登录人不是领导（自己新增的，或者1）
        if("1".equals(datetype)&&(!Arrays.asList(roles).contains("a02")&&!Arrays.asList(roles).contains("a01")&&!Arrays.asList(roles).contains("a24")&&!Arrays.asList(roles).contains("a20")&&!Arrays.asList(roles).contains("a50"))){
            sql+= " and ( USERCODE1='"+login.getUserCode()+"' or  VISIBILITY='1')";
        }

        //日程安排，登录人不是管理员,并且是领导（自己新增的、1或者2的）
        if("2".equals(datetype)&&!Arrays.asList(roles).contains("rc")&&(Arrays.asList(roles).contains("a02")||Arrays.asList(roles).contains("a01")||Arrays.asList(roles).contains("a24")||Arrays.asList(roles).contains("a20")||Arrays.asList(roles).contains("a50"))){
            sql+= " and ( USERCODE1='"+login.getUserCode()+"' or VISIBILITY='1' or VISIBILITY='2' or TIME like '%"+login.getUserCode()+"%')";
        }
        //日程安排，登录人不是管理员,并且不是领导（1的，或者自己新增的）
        if("2".equals(datetype)&&!Arrays.asList(roles).contains("rc")&&!(Arrays.asList(roles).contains("a02")||Arrays.asList(roles).contains("a01")||Arrays.asList(roles).contains("a24")||Arrays.asList(roles).contains("a20")||Arrays.asList(roles).contains("a50"))){
            sql+= " and ( VISIBILITY='1' or USERCODE1='"+login.getUserCode()+"' or TIME like '%"+login.getUserCode()+"%')";
        }
        //end
        if(!"".equals(content)&&content!=null){
            //content模糊查询
            sql += " and content like '%"+content+"%'";
            System.out.println("拼接了like content"+sql);
        }
        sql+="order by STARTTIME ASC";
        try {
            list = dbo.prepareQuery(sql,null);
            System.out.println("list"+list);
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        return list;
    }
    //获取当前日期的本周所有日期
    private List<HashMap<String,String>> time(String date){
        List<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> map = null;
        SimpleDateFormat sdf=new SimpleDateFormat ("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time= null;
        try {
            time = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(time);

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        for(int i=0;i<7;i++){
            map = new HashMap<String, String>();
            if(i==0){  map.put("DAY","星期一"); }
            if(i==1){  map.put("DAY","星期二"); }
            if(i==2){  map.put("DAY","星期三"); }
            if(i==3){  map.put("DAY","星期四"); }
            if(i==4){  map.put("DAY","星期五"); }
            if(i==5){  map.put("DAY","星期六"); }
            if(i==6){  map.put("DAY","星期日"); }
            map.put("DATE",sdf.format(cal.getTime()));
            list.add(map);
            cal.add(Calendar.DATE,1);
        }
        return list;
    }
    //读取xml,获取日程类型
    public String readXml(String type){
        //读取xml方法调用
        ZhzwChannelItemModel[] channel = ManageConfig.getConfig("dailyschedule");
        Map<String, Object> mapXml = channel[0].getModuleList().get(0);
        //datetype=1 领导日程  datetype=2日程安排
        String datetype = (String) mapXml.get(type);
        return datetype;
    }
    public void readDateType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter ();
        JSONObject Rst = new JSONObject ();
        LoginModel login = LoginUtil.getLoginObject(request);
        String userName = login.getUserName();
        //登录人编码
        String userCode=login.getUserCode();
        //flag 领导则为true  不是领导则为false
        boolean flag=false;
        String[] roles = login.getRoles();
        String deptCode = login.getDeptCode();
        //是否是管理员
        boolean adm=false;
        if(Arrays.asList(roles).contains("rc")){
            adm=true;
        }else{
            adm=false;
        }
        if("d00".equals(deptCode)||Arrays.asList(roles).contains("rc")){
            flag=true;
        }else{
            flag=false;
        }
        String datetype = this.readXml("datetype");
        String priority = this.readXml("priority");
        String warn = this.readXml("warn");
        Rst.put ("userCode",userCode);
        Rst.put ("adm",adm);
        Rst.put ("flag",flag);
        Rst.put ("datetype",datetype);
        Rst.put ("priority",priority);
        Rst.put ("warn",warn);
        Rst.put ("userName",userName);
        out.print (Rst);
        out.flush ();
        out.close ();
    }
}
