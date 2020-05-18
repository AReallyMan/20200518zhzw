package com.zhzw.stampmgr;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.db.ConditionModel;
import com.siqiansoft.framework.util.PageControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * @author yyz
 * @data    2019/12/5
 * 印章管理
 * 判断当前登录人，当为admin时显示列表所有信息，当为其他用户，只展示自己的列表信息
 */
public class LeaderLeave {
    private DatabaseBo dbo = new DatabaseBo();
    private List<HashMap<String,String>> list = null;
    List<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
    /**
     LoginModel log,log参数是当前登录人
     ConditionModel[] cs，cs配置的是列表中的条件
     PagingModel page，page分页使用
     */
    public List<HashMap<String, String>> myLeave(LoginModel log, ConditionModel[] cs, PagingModel page) {
        String sql="";
        //获取登录人的编码
        String usercode = log.getUserCode();
        String[] roles = log.getRoles();
        //人员角色号为rc的则是管理员
        if(Arrays.asList(roles).contains("rc")){
            //if ("admin".equals(usercode)){
            //admin时查询展示所有人的信息
            sql = "select * from STAMPMGR_STAMP_PROCESS where 1=1";
        }else {
            //不是管理员时，查询展示自己的列表信息
            //sql = "select * from STAMPMGR_STAMP_PROCESS where APPLICANTCODE = '"+usercode+"' O";
            sql="select * from STAMPMGR_STAMP_PROCESS where (APPLICANTCODE = '"+usercode+"' or OFFICECLERKCODE='"+usercode+"' or OFFICEHEADCODE='"+usercode+"' or DEPTHEADCODE='"+usercode+"' or DIRECTDEPUTYCODE='"+usercode+"' or GOVEDIRECTCODE='"+usercode+"' or GOVEDEPUTYCODE='"+usercode+"' or PARTYBUILDHEADCODE='"+usercode+"' or PARTYBUILDCLERKCODE='"+usercode+"')";
        }
        //列表上的条件配置
        if ((cs != null) && (!"".equals(cs))) {
            for (int i = 0; i < cs.length; i++) {
                if(!cs[i].getValue().equals(null)&&""!=cs[i].getValue()){
                    if (cs[i].getId().equals("bystpname")) {
                        //根据印章类型查询
                        sql = sql + " and STAMPTYPE like '%" + cs[i].getValue() + "%'";
                    }
                    if (cs[i].getId().equals("bydeptname")) {
                        //根据科室查询
                        sql = sql + " and APPLICANTDEPT like '%" + cs[i].getValue() + "%'";
                    }
                    if (cs[i].getId().equals("bytime")) {
                        //根据申请时间查询
                        sql = sql + " and APPLICANTTIME like '%" + cs[i].getValue() + "%'";
                    }
                    if ("byapname".equals(cs[i].getId())) {
                        //根据申请人姓名查询
                        sql = sql + " and APPLICANTNAME like '%" + cs[i].getValue() + "%'";
                    }
                }
            }
        }
        //倒叙排列
        sql = sql + " ORDER BY id DESC";
        try {
            list = dbo.prepareQuery(sql,null);
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        //列表信息的分页
        int from = page.getFrom();
        int to = page.getTo();
        int pageRows = page.getPageRows();
        int curPage = page.getCurPage();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                if(!list1.contains(list.get(i))){
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
		/*System.out.println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + "页");
		System.out.println("这是第" + curPage + "页，总共" + pageCount + "页");*/
        return list1.subList(from - 1, to);
    }
}