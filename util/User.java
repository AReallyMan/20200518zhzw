package com.zhzw.util;

import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.tree.NodeModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 用户工具类
 */
public class User
{
    /**
     * 获取当前等六人的下属
     * @param login
     * @param box
     * @return
     * @throws Exception
     */
    public static NodeModel getloginunderling(final LoginModel login, final String box) throws Exception {
        final DatabaseBo dbo = new DatabaseBo();
        final String logindeptcode = login.getDeptCode();
        final String logincode = login.getPersonCode();
        final ArrayList<HashMap<String, String>> loginUserlist = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,RANK,DEPTCODE from eap_account where code=?", new String[] { logincode });
        final String loginrank = loginUserlist.get(0).get("RANK");
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        ArrayList<HashMap<String, String>> lstDept = null;
        ArrayList<HashMap<String, String>> lstUser = null;
        if (!logindeptcode.equals("default")) {
            if (logindeptcode.equals("LD001")) {
                if (loginrank.equals("1")) {
                    if (logincode.equals("zhuangj")) {
                        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where (code='XX001' or code='JGDW001' or code='TGW001') and status='A' order by sn", (String[])null);
                        if (lstDept.size() == 0) {
                            return root;
                        }
                        lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>? and status='A') or (deptcode!='LD001' and status='A') order by sn", new String[] { loginrank });
                    }
                    else if (logincode.equals("liys")) {
                        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code='ZHFW001' and status='A' order by sn", (String[])null);
                        if (lstDept.size() == 0) {
                            return root;
                        }
                        lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>? and status='A') or (deptcode!='LD001' and status='A') order by sn", new String[] { loginrank });
                    }
                    else if (logincode.equals("huogf")) {
                        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code='ZCFG001' and status='A' order by sn", (String[])null);
                        if (lstDept.size() == 0) {
                            return root;
                        }
                        lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>? and status='A') or (deptcode!='LD001' and status='A') order by sn", new String[] { loginrank });
                    }
                    else if (logincode.equals("hangjl")) {
                        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where (code='GHJS001' or code='KFGL001' ) and status='A' order by sn", (String[])null);
                        if (lstDept.size() == 0) {
                            return root;
                        }
                        lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>? and status='A') or (deptcode!='LD001' and status='A') order by sn", new String[] { loginrank });
                    }
                    else if (logincode.equals("liyl")) {
                        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where (code='BZ001' or code='JMFA001') and status='A' order by sn", (String[])null);
                        if (lstDept.size() == 0) {
                            return root;
                        }
                        lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>? and status='A') or (deptcode!='LD001' and status='A') order by sn", new String[] { loginrank });
                    }
                    else {
                        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where  code!='default' and status='A' order by sn", (String[])null);
                        if (lstDept.size() == 0) {
                            return root;
                        }
                        lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>0 and status='A' and code!='yanlg') or (deptcode!='LD001' and status='A')  order by sn", (String[])null);
                    }
                }
                else {
                    lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code!='default' and  status='A' order by sn", (String[])null);
                    if (lstDept.size() == 0) {
                        return root;
                    }
                    lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where (deptcode='LD001' and rank>? and status='A') or (deptcode!='LD001' and status='A') order by sn", new String[] { loginrank });
                }
            }
            else {
                lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code=?  and status='A' order by sn", new String[] { logindeptcode });
                if (lstDept.size() == 0) {
                    return root;
                }
                lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where deptcode=? and rank>? and status='A' order by sn", new String[] { logindeptcode, loginrank });
            }
            for (int i = 0; i < lstDept.size(); ++i) {
                final HashMap<String, String> map = lstDept.get(i);
                final String str = map.get("PCODE");
                if (str == null || str.equals("")) {
                    final NodeModel node = new NodeModel();
                    root.addSubNode(node);
                    node.setId((String)map.get("CODE"));
                    node.setTitle((String)map.get("NAME"));
                    node.setNodes(getSubNode1(box, map.get("CODE"), lstDept, lstUser));
                    node.setTarget("FRAME1");
                }
            }
            return root;
        }
        return root;
    }

    /**
     * 获取领导
     * @param box
     * @return
     * @throws Exception
     */
    public static NodeModel getlader(final String box) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        ArrayList<HashMap<String, String>> lstDept = null;
        final DatabaseBo dbo = new DatabaseBo();
        lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code='LD001'  and status='A'", (String[])null);
        if (lstDept.size() == 0) {
            return root;
        }
        final ArrayList<HashMap<String, String>> lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where deptcode='LD001' and status='A' order by sn", (String[])null);
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str == null || str.equals("")) {
                final NodeModel node = new NodeModel();
                root.addSubNode(node);
                node.setId((String)map.get("CODE"));
                node.setTitle((String)map.get("NAME"));
                node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
            }
        }
        return root;
    }

    /**
     * 获取其他用户
     * @param login
     * @param box
     * @return
     * @throws Exception
     */
    public static NodeModel getpeople(final LoginModel login, final String box) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        final DatabaseBo dbo = new DatabaseBo();
        final ArrayList<HashMap<String, String>> lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code!='default' and code!='LD001' and orgcode=? and timeslice=? and status='A' order by sn", new String[] { login.getOrgCode(), login.getTimeSlice() });
        if (lstDept.size() == 0) {
            return root;
        }
        final ArrayList<HashMap<String, String>> lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where orgcode=? and timeslice=? and status='A' and rank=2 order by sn", new String[] { login.getOrgCode(), login.getTimeSlice() });
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str == null || str.equals("")) {
                final NodeModel node = new NodeModel();
                root.addSubNode(node);
                node.setId((String)map.get("CODE"));
                node.setTitle((String)map.get("NAME"));
                node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
            }
        }
        return root;
    }

    /**
     * 获取司机
     * @param box
     * @return
     * @throws Exception
     */
    public static NodeModel getDriverTree(final String box) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        final ArrayList<HashMap<String, String>> lstDept = new ArrayList<HashMap<String, String>>();
        final DatabaseBo dbo = new DatabaseBo();
        final HashMap<String, String> deptMap = new HashMap<String, String>();
        deptMap.put("CODE", "sijiban");
        deptMap.put("NAME", "\u53f8\u673a\u73ed");
        deptMap.put("PCODE", null);
        lstDept.add(deptMap);
        if (lstDept.size() == 0) {
            return root;
        }
        final ArrayList<HashMap<String, String>> lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select  m.CODE,m.NAME,m.rolecode as deptcode  from (select u.rolecode,a.name,u.usercode,a.code from eap_account a  join  eap_userrole u  on  u.usercode = a.code order by u.usercode ) m where m.rolecode='sijiban'", (String[])null);
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final NodeModel node = new NodeModel();
            root.addSubNode(node);
            node.setId((String)map.get("CODE"));
            node.setTitle((String)map.get("NAME"));
            node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
        }
        return root;
    }

    /**
     *
     * @param login
     * @param box
     * @return
     * @throws Exception
     */
    public static NodeModel getLdUserTree(final LoginModel login, final String box) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        final DatabaseBo dbo = new DatabaseBo();
        final ArrayList<HashMap<String, String>> lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code!='default' and orgcode=? and timeslice=? and status='A' order by sn", new String[] { login.getOrgCode(), login.getTimeSlice() });
        if (lstDept.size() == 0) {
            return root;
        }
        final ArrayList<HashMap<String, String>> lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where orgcode=? and timeslice=? and status='A' and rank!=2 order by sn", new String[] { login.getOrgCode(), login.getTimeSlice() });
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str == null || str.equals("")) {
                final NodeModel node = new NodeModel();
                root.addSubNode(node);
                node.setId((String)map.get("CODE"));
                node.setTitle((String)map.get("NAME"));
                node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
            }
        }
        return root;
    }

    public static NodeModel getLoUserTree(final LoginModel login, final String box) throws Exception {
        final NodeModel root = new NodeModel();
        root.setTitle(AppData.getInstance().getOrgName());
        final DatabaseBo dbo = new DatabaseBo();
        final String deptcode = login.getDeptCode();
        final ArrayList<HashMap<String, String>> lstDept = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,PCODE from eap_department where code=?  and status='A'", new String[] { login.getDeptCode() });
        if (lstDept.size() == 0) {
            return root;
        }
        final ArrayList<HashMap<String, String>> lstUser = (ArrayList<HashMap<String, String>>)dbo.prepareQuery("select CODE,NAME,DEPTCODE from eap_account where deptcode=? and status='A' order by sn", new String[] { login.getDeptCode() });
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str == null || str.equals("")) {
                final NodeModel node = new NodeModel();
                root.addSubNode(node);
                node.setId((String)map.get("CODE"));
                node.setTitle((String)map.get("NAME"));
                node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
            }
        }
        return root;
    }

    /**
     * 获取子节点树
     * @param box
     * @param deptcode
     * @param lstDept
     * @param lstUser
     * @return
     */
    private static NodeModel[] getSubNode(final String box, final String deptcode, final ArrayList<HashMap<String, String>> lstDept, final ArrayList<HashMap<String, String>> lstUser) {
        final ArrayList<NodeModel> list = new ArrayList<NodeModel>();
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str != null) {
                if (!str.equals("")) {
                    if (str.equals(deptcode)) {
                        final NodeModel node = new NodeModel();
                        node.setTitle((String)map.get("NAME"));
                        node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
                        list.add(node);
                    }
                }
            }
        }
        for (int i = 0; i < lstUser.size(); ++i) {
            final HashMap<String, String> map = lstUser.get(i);
            final String str = map.get("DEPTCODE");
            if (str != null) {
                if (!str.equals("")) {
                    if (str.equals(deptcode)) {
                        final NodeModel node = new NodeModel();
                        node.setId(String.valueOf(map.get("CODE")) + "." + map.get("NAME"));
                        node.setBox(box);
                        node.setIcon("user.gif");
                        node.setTitle((String)map.get("NAME"));
                        list.add(node);
                    }
                }
            }
        }
        final NodeModel[] nodes = new NodeModel[list.size()];
        list.toArray(nodes);
        return nodes;
    }

    /**
     * 获取子节点树
     * @param box
     * @param deptcode
     * @param lstDept
     * @param lstUser
     * @return
     */
    private static NodeModel[] getSubNode1(final String box, final String deptcode, final ArrayList<HashMap<String, String>> lstDept, final ArrayList<HashMap<String, String>> lstUser) {
        final ArrayList<NodeModel> list = new ArrayList<NodeModel>();
        for (int i = 0; i < lstDept.size(); ++i) {
            final HashMap<String, String> map = lstDept.get(i);
            final String str = map.get("PCODE");
            if (str != null) {
                if (!str.equals("")) {
                    if (str.equals(deptcode)) {
                        final NodeModel node = new NodeModel();
                        node.setTitle((String)map.get("NAME"));
                        node.setNodes(getSubNode(box, map.get("CODE"), lstDept, lstUser));
                        list.add(node);
                    }
                }
            }
        }
        for (int i = 0; i < lstUser.size(); ++i) {
            final HashMap<String, String> map = lstUser.get(i);
            final String str = map.get("DEPTCODE");
            if (str != null) {
                if (!str.equals("")) {
                    if (str.equals(deptcode)) {
                        final NodeModel node = new NodeModel();
                        node.setId(String.valueOf(map.get("CODE")) + "." + map.get("NAME"));
                        node.setBox(box);
                        node.setIcon("user.gif");
                        node.setTitle((String)map.get("NAME"));
                        node.setAction("querytodo.cmd?$ACTION=list&actor=" + map.get("CODE"));
                        list.add(node);
                    }
                }
            }
        }
        final NodeModel[] nodes = new NodeModel[list.size()];
        list.toArray(nodes);
        return nodes;
    }
}

