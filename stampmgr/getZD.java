package com.zhzw.stampmgr;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.dict.ItemModel;
import com.zhzw.model.ZhzwChannelItemModel;
import com.zhzw.util.service.ManageConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getZD {
    /**
     * 获取印章类型
     * 返回给字典的方法
     * @return
     */
    public static ItemModel[] getCodeName(){
        ItemModel[] file=null;
        ZhzwChannelItemModel[] channel = ManageConfig.getConfig("stampmgr");
        List<Map<String,String>> leaveTypeList = (List<Map<String, String>>) channel[0].getModuleList().get(0).get("LeaveTypeList");
        file = new ItemModel[leaveTypeList.size()];
        for(int i=0;i< leaveTypeList.size();i++){
            String code=leaveTypeList.get(i).get("LeaveType");
            String name=leaveTypeList.get(i).get("name");
            file[i]=new ItemModel();
            file[i].setKey(code);
            file[i].setValue(name);
        }
        return file;
    }

    /**
     * 获取部门字典(可以实现，最后选择了用平台的自定义sql语句)
     * @return
     */
    public static ItemModel[] getDeptName(){
        ItemModel[] file=null;
        DatabaseBo dbo=new DatabaseBo();
        //查询部门编码、部门名称
        String sql="SELECT CODE ,NAME  FROM EAP_DEPARTMENT";
       try {
           ArrayList<HashMap<String, String>> hashMaps = dbo.prepareQuery(sql, null);
           file=new ItemModel[hashMaps.size()];
           for(int i=0;i<hashMaps.size();i++){
               String code = hashMaps.get(i).get("CODE");
               String name = hashMaps.get(i).get("NAME");
               file[i]=new ItemModel();
               file[i].setKey(code);
               file[i].setValue(name);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
        return file;
    }
}
