package com.zhzw.dailyschedule;
import java.util.ArrayList;
import java.util.HashMap;
import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.tree.NodeModel;
/**
 * 查询镇级以上领导树
 */
public class LeaderTree {
    /**
     * 查询用户树形结构
     * @param login  当前登录人
     * @param box    多选文本
     * @return
     * @throws Exception
     */
    public static NodeModel getDeptUserTree(LoginModel login, String box)throws Exception{
        NodeModel root = new NodeModel();
        System.out.println("bob++++++++++++:"+box);
        root.setTitle(AppData.getInstance().getOrgName());
        DatabaseBo dbo = new DatabaseBo();
        //查询当前部门
        ArrayList<HashMap<String,String>> lstsDept = dbo.prepareQuery("SELECT DEPTPATH FROM EAP_DEPARTMENT WHERE CODE ='d00' ORDER BY SN", new String[] {});
        if (lstsDept.size() == 0) {return root;}
        String deptpath=lstsDept.get(0).get("DEPTPATH");
        String[] path=deptpath.split("/");
        String paths="";
        for(int i=0;i<path.length;i++){
            paths+="'"+path[i]+"',";
        }
        paths=paths.substring(0,paths.length()-1);
        //查询当前部门
        ArrayList<HashMap<String,String>> lstDept = dbo.prepareQuery("SELECT CODE,NAME,PCODE FROM EAP_DEPARTMENT WHERE CODE in ("+paths+")", new String[] { });
        if (lstDept.size() == 0) {
            return root;
        }
        ArrayList lstUser;
        //根据部门查询部门下员工
        lstUser = dbo.prepareQuery("SELECT CODE,NAME,DEPTCODE FROM EAP_ACCOUNT WHERE DEPTCODE='d00' AND ORGCODE=? AND TIMESLICE=? AND STATUS='A' ORDER BY SN", new String[] {login.getOrgCode(), login.getTimeSlice() });

        for (int i = 0; i < lstDept.size(); i++) {
            HashMap map = (HashMap)lstDept.get(i);
            String str = (String)map.get("PCODE");
            System.out.println("str++++++:"+str);
            if ((str == null) || (str.equals("")))
            {
                NodeModel node = new NodeModel();
                root.addSubNode(node);
                node.setId((String)map.get("CODE"));
                node.setTitle((String)map.get("NAME"));
                System.out.println(map.get("NAME")+"===============map.deptname");

                if ((box != null) && (box.equals("checkbox"))) {
                    node.setBox("checkbox");
                    node.setBoxTag("checked");
                    //	node.setKey("checked");
                    System.out.println("----------------------------");
                }
                node.setNodes(getSubNode(box, (String)map.get("CODE"),lstDept, lstUser));
            }
        }
        return root;
    }


    //迭代部门下人员信息
    private static NodeModel[] getSubNode(String box,  String deptcodes,ArrayList<HashMap<String, String>> lstDept, ArrayList<HashMap<String, String>> lstUser) {
        ArrayList list = new ArrayList();
        System.out.println("======-=-=-=-=-=--=:"+lstDept);
        for (int i = 0; i < lstDept.size(); i++) {
            HashMap map = (HashMap)lstDept.get(i);
            String str = (String)map.get("PCODE");
            if ((str == null) || (str.equals("")) ) {
                continue;
            }
            System.out.println(deptcodes+"==deptcode=============code=="+map.get("CODE"));
            NodeModel node = new NodeModel();
            node.setId((String)map.get("CODE"));
            node.setTitle((String)map.get("NAME"));
            System.out.println(map.get("NAME")+"===============map.deptname1111");
            node.setNodes(getSubNode(box, (String)map.get("CODE"), lstDept, lstUser));
            list.add(node);
        }
        for (int i = 0; i < lstUser.size(); i++) {
            HashMap<String,String> map = (HashMap)lstUser.get(i);
            String str = (String)map.get("DEPTCODE");
            if ((str == null) || (str.equals("")) ) {
                continue;
            }
                NodeModel node = new NodeModel();
                node.setId((String)map.get("CODE") + "." + (String)map.get("NAME"));
                node.setBox(box);
                node.setIcon("user.gif");

                System.out.println(map.get("NAME")+"========++++++++++++++++===map.username");
                node.setTitle((String)map.get("NAME"));
                list.add(node);

        }
        NodeModel[] nodes = new NodeModel[list.size()];
        list.toArray(nodes);
        return nodes;
    }

}