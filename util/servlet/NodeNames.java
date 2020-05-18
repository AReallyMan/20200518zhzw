package com.zhzw.util.servlet;



import java.util.ArrayList;
import com.siqiansoft.framework.bo.DatabaseBo;
import java.util.HashMap;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;


public class NodeNames extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        final String wid = request.getParameter("wid");
        final JSONObject json = new JSONObject();
        json.put("NodeName", (Object)this.getNode(wid));
        response.getWriter().println(json);
    }

    public List<HashMap<String, String>> getNode(final String wid) {
        final DatabaseBo dbo = new DatabaseBo();
        final String sql = "select WID,FLOWNAME,ACTORNAME,NODENAME,ASSIGNERNAME,SUBMITMODE,STARTTIME,ENDTIME,COMMENTS,NODETYPE from eap_vw_work where actiontype!='W' and instanceid =(select instanceid from eap_vw_work where wid = '" + wid + "') ORDER BY wid";
        ArrayList list = new ArrayList();
        new HashMap();
        try {
            list = dbo.prepareQuery(sql, (String[])null);
        }
        catch (Exception var7) {
            var7.printStackTrace();
        }
        return (List<HashMap<String, String>>)list;
    }
}

