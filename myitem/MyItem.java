package com.zhzw.myitem;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.db.ConditionModel;
import com.siqiansoft.framework.util.PageControl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MyItem {
    DatabaseBo dbo = new DatabaseBo();
    private List<HashMap<String, String>> list = null;
    List<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
    /**
     * LoginModel log,log参数是当前登录人
     * ConditionModel[] cs，cs配置的是列表中的条件
     * PagingModel page，page分页使用
     */
    public List<HashMap<String, String>> myDucument(LoginModel login, ConditionModel[] cs, PagingModel page) {
        String userCode = login.getUserCode();
        String sql="SELECT * FROM (SELECT a.*,b.wid FROM OA_DUCUMENT a LEFT JOIN (SELECT * FROM EAP_DONE WHERE wid IN  (SELECT MAX(wid) wid FROM EAP_DONE GROUP BY INSTANCEID  )) b ON a.INSTANCEID=b.INSTANCEID ) WHERE STATUS='1' AND STATE ='1' AND  APPLYCODE like '"+userCode+"'";
        //userCode=applicantcode
        //列表上的条件配置
        if ((cs != null) && (!"".equals(cs))) {
            for (int i = 0; i < cs.length; i++) {
                if (!cs[i].getValue().equals(null) && "" != cs[i].getValue()) {
                    if (cs[i].getId().equals("bytype")) {
                        //根据类型查询
                        sql = sql + " and TYPE like '%" + cs[i].getValue() + "%'";
                    }
                    if (cs[i].getId().equals("byname")) {
                        //根据内容查询
                        sql = sql + " and TITLE like '%" + cs[i].getValue() + "%'";
                    }
                }
            }
        }
        //倒叙排列
        sql = sql + " ORDER BY APPLYTIME DESC";
        try {
            list = dbo.prepareQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //列表信息的分页
        int from = page.getFrom();
        int to = page.getTo();
        int pageRows = page.getPageRows();
        int curPage = page.getCurPage();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (!list1.contains(list.get(i))) {
                    list1.add(list.get(i));
                }
            }
        }
        if (list1.size() == 0) {
            PageControl.calcPage(page, 0, 0);
            return null;
        }
        if ((from == 1) && (to == 0)) {
            to = list1.size();
        }
        int rowsCount = list1.size();
        int pageCount = list1.size() / pageRows + (list1.size() % pageRows == 0 ? 0 : 1);
        page.setPageCount(pageCount);
        page.setRowsCount(rowsCount);
        if (to > list1.size()) {
            to = list1.size();
        }
        page.setTo(to);
        return list1.subList(from - 1, to);
    }
}
