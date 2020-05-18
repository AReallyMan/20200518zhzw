package com.zhzw.util;

import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.util.PojoMap;
import com.siqiansoft.ou.model.AccountModel;
import com.siqiansoft.workflow.runtime.FormData;

import java.util.HashMap;
import java.util.List;

/**
 * 办理人规则
 */
public class Rulls {
    /**
     * 查询替班人
     */
    public static AccountModel getReplacer(FormData form)
            throws Exception
    {
        String replacer = form.getValue("f01", "replacercode").toString();
        System.out.println("获取替班人为："+replacer);
        String timeSlice = AppData.getInstance().getCurTimeSlice();
        System.out.println("获取timeSlice为："+timeSlice);
        DatabaseBo dbo = new DatabaseBo();
        HashMap map = dbo.getRowData("select * from eap_account where code=? and timeslice=?", new String[] { replacer, timeSlice });
        if ((map == null) || (map.size() == 0)) {
            return null;
        }
        return (AccountModel) PojoMap.getPOJOByMap(map, "com.siqiansoft.ou.model.AccountModel");
    }
    /**
     * 查询值班领导
     */
    public static AccountModel geDutyLeader(FormData form)throws Exception
    {
        DatabaseBo dbo = new DatabaseBo();
        String usercode = form.getValue("f01", "usercode").toString();
        //根据申请人code查询值班领导
        String sql = "select GROUPNAME from OA_DUTYGROUP where id in ( select DISTINCT(rid) from OA_DUTYGROUPMEMBER where usercode = '"+usercode+"')";
        List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
        String leaderName = "";
        if(list.size()>0){
            leaderName = list.get(0).get("GROUPNAME");
        }
        String timeSlice = AppData.getInstance().getCurTimeSlice();

        HashMap map = dbo.getRowData("select * from eap_account where name=? and timeslice=?", new String[] { leaderName, timeSlice });
        if ((map == null) || (map.size() == 0)) {
            return null;
        }
        return (AccountModel) PojoMap.getPOJOByMap(map, "com.siqiansoft.ou.model.AccountModel");
    }

}
