package com.zhzw.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URLDecoder;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.model.tree.NodeModel;
import com.siqiansoft.framework.model.LoginModel;

/**
 * 获取部门人员树
 */
public class getDeptUserTree
{
    /**
     * 获取部门人员树
     * @param login  登陆人
     * @param box
     * @param cx
     * @return
     * @throws Exception
     */
    public static NodeModel getDeptUserTree(final LoginModel login, final String box, String cx) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        final DatabaseBo dbo = new DatabaseBo();
        cx = URLDecoder.decode(cx, "UTF-8");
        System.out.println("getDeptUserTree add cx=========================================:" + cx);
        System.out.println("getDeptUserTree add box=========================================:" + box);
        if (!cx.equals("") && !isContainChinese(cx)) {
            root.setTitle("无有效查询结果！");
            return root;
        }
        root.setTitle(AppData.getInstance().getOrgName());
        String sql = "select CODE,NAME,PCODE from eap_department where orgcode=? and timeslice=? and status='A' and code!='root' and code!='b11'and code!='b21'and code!='b22' and code!='b24' and code!='b26' ";
        if (!cx.equals("")) {
            sql = String.valueOf(sql) + " and (CODE in (select DEPTCODE from eap_account where  status='A' and name like '%" + cx + "%' ) or code in (select pcode from eap_department where code in (select DEPTCODE from eap_account where  status='A' and name like '%" + cx + "%') and pcode is not null))";
        }
        sql = String.valueOf(sql) + " order by sn";
        final ArrayList<HashMap<String,String>> lstDept = dbo.prepareQuery(sql, new String[] { login.getOrgCode(), login.getTimeSlice() });
        System.out.println("lstDeptsize:" + lstDept.size());
        if (lstDept.size() == 0) {
            root.setTitle("无有效查询结果！");
            return root;
        }
        String sql2 = "select CODE,NAME,DEPTCODE from eap_account where orgcode=? and timeslice=? and status='A' and code!='admin'";
        if (!cx.equals("")) {
            sql2 = String.valueOf(sql2) + " and name like '%" + cx + "%'";
        }
        sql2 = String.valueOf(sql2) + " order by sn";
        final ArrayList<HashMap<String,String>> lstUser = dbo.prepareQuery(sql2, new String[] { login.getOrgCode(), login.getTimeSlice() });
        for (int i1 = 0; i1 < lstDept.size(); ++i1) {
            final HashMap<String,String> map1 = lstDept.get(i1);
            final String str = map1.get("PCODE");
            final String code = map1.get("CODE");
            if (str == null || str.equals("")) {
                final NodeModel node1 = new NodeModel();
                root.addSubNode(node1);
                node1.setId(String.valueOf(map1.get("CODE")) + "^" + map1.get("NAME"));
                node1.setTitle((String)map1.get("NAME"));
                node1.setBox("checkbox");
                if ("radio".equals(box)) {
                    node1.setBox("radio");
                }
                System.out.println(box);
                System.out.println("title============" + node1.getTitle());
                node1.setNodes(getSubNode(box, map1.get("CODE"), lstDept, lstUser));
            }
        }
        return root;
    }

    /**
     * 是否为中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(final String str) {
        final Pattern p = Pattern.compile("[一-龥]");
        final Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 获取子节点
     * @param box
     * @param deptcode
     * @param lstDept
     * @param lstUser
     * @return
     */
    private static NodeModel[] getSubNode(final String box, final String deptcode, final ArrayList<HashMap<String,String>> lstDept, final ArrayList<HashMap<String,String>> lstUser) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String,String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str != null && !str.equals("") && str.equals(deptcode)) {
                final NodeModel node = new NodeModel();
                node.setId(String.valueOf(map.get("CODE")) + "^" + map.get("NAME"));
                node.setTitle((String)map.get("NAME"));
                node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
                node.setBox("checkbox");
                if ("radio".equals(box)) {
                    node.setBox("radio");
                }
                list.add(node);
            }
        }
        for (int i = 0; i < lstUser.size(); ++i) {
            final HashMap<String,String> map = lstUser.get(i);
            final String str = map.get("DEPTCODE");
            if (str != null && !str.equals("") && str.equals(deptcode)) {
                final NodeModel node = new NodeModel();
                node.setId(String.valueOf(map.get("CODE")) + "." + map.get("NAME"));
                node.setBox("checkbox");
                node.setIcon("user.gif");
                node.setTitle((String)map.get("NAME"));
                list.add(node);
            }
        }
        final NodeModel[] nodes = new NodeModel[list.size()];
        list.toArray(nodes);
        return nodes;
    }

    private static NodeModel[] getSubNode_Group(final String groupcode, final ArrayList<HashMap<String,String>> lstGroup, final ArrayList<HashMap<String,String>> lstUser) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < lstGroup.size(); ++i) {
            final HashMap<String,String> map = lstGroup.get(i);
            final String str = map.get("GROUPCODE");
            if (str != null && !str.equals("") && str.equals(groupcode)) {
                final NodeModel node = new NodeModel();
                node.setId(String.valueOf(map.get("CODE")) + "^" + map.get("NAME"));
                node.setTitle((String)map.get("NAME"));
                node.setBox("checkbox");
                list.add(node);
            }
        }
        for (int i = 0; i < lstUser.size(); ++i) {
            final HashMap<String,String> map = lstUser.get(i);
            final String str = map.get("GROUPCODE");
            if (str != null && !str.equals("") && str.equals(groupcode)) {
                final NodeModel node = new NodeModel();
                node.setId(String.valueOf(map.get("CODE")) + "." + map.get("NAME"));
                node.setBox("checkbox");
                node.setIcon("user.gif");
                node.setTitle((String)map.get("NAME"));
                list.add(node);
            }
        }
        final NodeModel[] nodes = new NodeModel[list.size()];
        list.toArray(nodes);
        return nodes;
    }

    public static NodeModel getGroupUserTree(final LoginModel login, final String box, String cx) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        final DatabaseBo dbo = new DatabaseBo();
        cx = URLDecoder.decode(cx, "UTF-8");
        System.out.println("getGroupUserTree add cx=========================================:" + cx);
        if (!cx.equals("") && !isContainChinese(cx)) {
            root.setTitle("无有效查询结果！");
            return root;
        }
        root.setTitle(AppData.getInstance().getOrgName());
        String sql_group = "select CODE,NAME from eap_group where orgcode=? and type='P'";
        if (!cx.equals("")) {
            sql_group = String.valueOf(sql_group) + " and (CODE in (select u.GROUPCODE from eap_usergroup u join eap_account a on u.usercode=a.code where  name like '%" + cx + "%' ))";
        }
        sql_group = String.valueOf(sql_group) + " order by sn";
        final ArrayList<HashMap<String,String>> lstGroup = dbo.prepareQuery(sql_group, new String[] { login.getOrgCode() });
        System.out.println("lstGroupsize:" + lstGroup.size());
        if (lstGroup.size() == 0) {
            root.setTitle("无有效查询结果！");
            return root;
        }
        String sql_user = "select a.CODE,a.NAME,u.GROUPCODE from eap_usergroup u join eap_account a on u.usercode=a.code where u.orgcode=?";
        if (!cx.equals("")) {
            sql_user = String.valueOf(sql_user) + " and name like '%" + cx + "%'";
        }
        sql_user = String.valueOf(sql_user) + " order by a.sn";
        final ArrayList lstUser = dbo.prepareQuery(sql_user, new String[] { login.getOrgCode() });
        for (int i1 = 0; i1 < lstGroup.size(); ++i1) {
            final HashMap<String,String> map1 = lstGroup.get(i1);
            final String code = map1.get("CODE");
            final NodeModel node1 = new NodeModel();
            root.addSubNode(node1);
            node1.setId(String.valueOf(map1.get("CODE")) + "^" + map1.get("NAME"));
            node1.setTitle((String)map1.get("NAME"));
            node1.setBox("checkbox");
            System.out.println("title============" + node1.getTitle());
            node1.setNodes(getSubNode_Group(map1.get("CODE"), lstGroup, lstUser));
        }
        return root;
    }
}

