package com.zhzw.stampmgr;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.commons.FileIO;
import com.siqiansoft.commons.XmlSerializer;
import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.util.LoginUtil;
import com.zhzw.model.ZhzwChannelItemModel;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * 获取角色、方式、用章类型，返回status
 * status=0,登录时间过长
 * status=1,为书记副书记+方式一
 * status=2,为书记副书记+方式二
 * status=3,为主管+方式一
 * status=4,为主管+方式二
 * status=5,普通科员+方式一
 * status=6,普通科员+方式二
 */
@WebServlet(name = "StampmgrServlet")
public class StampmgrServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter ();
        JSONObject Rst = new JSONObject ();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String stamptype=request.getParameter("stamptype");
        int status=0;
        //获取当前登录人的角色
        LoginModel log = LoginUtil.getLoginObject(request);
        if(log==null){
            //登录过期
            status=0;
        }
        String path = AppData.getInstance().getAppConfigDir()+"/zhzwmanageconfig/stampmgr.xml";
        ZhzwChannelItemModel[] channelItem = null;
        try {
           String[] roles=log.getRoles();
            //定义当前登录人角色
            String role = "";
            //角色类型
            for (int i=0;i<roles.length;i++){
                    //判断书记、副书记
                    if(roles[i].equals("a01")||roles[i].equals("a03")){
                        role = "1";
                        break;
                    }
                    //判断主管领导
                    if(roles[i].equals("a20")){
                        role = "2";
                        break;
                   }
           }
            if(FileIO.existFile(path)) {
                channelItem = (ZhzwChannelItemModel[]) XmlSerializer.getInstance().fromXMLFile(path);
                List<Map<String,String>> leaveTypeList = (List<Map<String, String>>) channelItem[0].getModuleList().get(0).get("LeaveTypeList");
                //for循环，查询印章的名称
                for(int l=0;l<leaveTypeList.size();l++){
                    //用章类型code
                    String LeaveType=leaveTypeList.get(l).get("LeaveType");
                    //查询方式
                    String leaveMode=leaveTypeList.get(l).get("leaveMode");
                    //判断书记、副书记
                    if(role.equals("1")){
                        System.out.println(stamptype);
                        if (stamptype.equals(LeaveType)&&"1".equals(leaveMode)){
                            status=1;
                            break;
                        }
                        if (stamptype.equals(LeaveType)&&"2".equals(leaveMode)){
                            status=2;
                            break;
                        }
                    }
                    //判断主管领导
                    if(role.equals("2")){
                        if (stamptype.equals(LeaveType)&&"1".equals(leaveMode)){
                            status=3;
                            break;
                        }
                        if (stamptype.equals(LeaveType)&&"2".equals(leaveMode)){
                            status=4;
                            break;
                        }
                    }
                    //判断科员的方式
                    System.out.println("+++++++++++++lea"+leaveMode);
                    if (!role.equals("2")&&!role.equals("1")&&"1".equals(leaveMode)&&stamptype.equals(LeaveType)){
                        status=5;
                        break;
                    }
                    if (!role.equals("2")&&!role.equals("1")&&"2".equals(leaveMode)&&stamptype.equals(LeaveType)){
                        status=6;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Rst.put ("status",status);
        out.print (Rst);
        out.flush ();
        out.close ();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}
