package com.zhzw.util.servlet;



import java.util.List;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import com.siqiansoft.framework.bo.DatabaseBo;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class SelectNodeStatusServlet extends HttpServlet
{
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final DatabaseBo dbo = new DatabaseBo();
        final String wid = request.getParameter("wid");
        final String sql = "select * from EAP_VW_WORK where wid = '" + wid + "'";
        try {
            String status = "";
            final List<HashMap<String, String>> list = (List<HashMap<String, String>>)dbo.prepareQuery(sql, (String[])null);
            if (list.size() > 0) {
                final String flowStatus = list.get(0).get("FLOWSTATUS");
                if (flowStatus.equals("F")) {
                    status = "true";
                }
                else {
                    status = "false";
                }
            }
            final JSONObject json = new JSONObject();
            json.put("status", (Object)status);
            response.getWriter().println(json);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

