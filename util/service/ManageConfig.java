package com.zhzw.util.service;

import com.siqiansoft.commons.DebugService;
import com.siqiansoft.commons.FileIO;
import com.siqiansoft.commons.XmlSerializer;
import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.grid.ColumnModel;
import com.siqiansoft.framework.model.grid.GridModel;
import com.siqiansoft.framework.model.view.FieldModel;
import com.siqiansoft.platform.view.bo.FieldBo;
import com.siqiansoft.platform.view.bo.TempletBo;
import com.siqiansoft.platform.view.bo.grid.ColumnBo;
import com.zhzw.model.ZhzwChannelItemModel;
import com.zhzw.util.DateUtil;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询管理端配置信息
 */
public class ManageConfig {
    DatabaseBo dbo = new DatabaseBo();

    /**
     * 查询是否放入待办列表中
     * @param modulecode
     * @return
     */
    public static boolean todoWork(String modulecode){
        boolean status = true;
        try {
            ZhzwChannelItemModel[] channelItem = getConfig(modulecode);
            if(channelItem != null && !"".equals(channelItem)) {//文件存在
                String todoWork = (String)channelItem[0].getModuleList().get(0).get("todoWork");
                if(todoWork.equals("2")){//无需放入待办列表
                    status = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 根据模块号查询的对应配置系统信息选择
     * @param modulecode 模块号code
     * @return
     */
    public static boolean readNews(String modulecode){
        boolean status = true;
        try {
            ZhzwChannelItemModel[] channelItem = getConfig(modulecode);
            if(channelItem != null && !"".equals(channelItem)) {//文件存在
                String news = (String)channelItem[0].getModuleList().get(0).get("news");
                if(news.equals("1") || news.equals("3")){//无系统提示
                    status = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 查询模块配置状态
     * @param modulecode 模块code
     * @return
     */
    public static boolean getStatus(String modulecode){
        boolean status = true;
        try {
            ZhzwChannelItemModel[] channelItem = getConfig(modulecode);
            if(channelItem != null && !"".equals(channelItem)) {//文件存在
                String configStatus = (String)channelItem[0].getStatus();
                if(!configStatus.equals("0")){//不启用
                    status = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 根据模块号查询对应的模块配置信息对象数组
     * @param modulecode 模块code
     * @return
     */
    public static ZhzwChannelItemModel[] getConfig(String modulecode){
        String path = AppData.getInstance().getAppConfigDir()+"/zhzwmanageconfig/"+modulecode+".xml";
        //读取xml
        ZhzwChannelItemModel[] channelItem = {} ;
        try {
            if(FileIO.existFile(path)) {//文件存在
                channelItem = (ZhzwChannelItemModel[]) XmlSerializer.getInstance().fromXMLFile(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelItem;
    }

    /**
     * 修改记录集合的域的类型
     * @param system
     * @param module
     * @param grid
     * @param field
     * @param type
     */
    public static void updateViemXml(String system,String module,String grid,String field,String type) {
        try {
            ColumnBo cb = new ColumnBo();
            ColumnModel cm = cb.getColumn(system, module, grid, field);
            cm.setShowType(type);
            cb.updateColumn(system, module, grid, cm);
            AppData.getInstance().loadAppConfig(getBasePath() + "WEB-INF/config");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    public static void updateFormXml(String system,String module,String grid,String field,String type){
        try{
            FieldBo fb = new FieldBo();
            FieldModel fieldModel = fb.getField(system,module,grid,field);//获取字段对象
            fieldModel.setCtrlType(type);//修改字段类型
            fb.updateField(system,module,grid,fieldModel);
            TempletBo templetBo = new TempletBo();
            String content = templetBo.readTemplet(system,module,grid);
            templetBo.saveTemplet(system,module,grid,content);
            AppData.getInstance().loadAppConfig(getBasePath() + "WEB-INF/config");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 修改模块收藏功能列表信息
     */
    public static void updateCollect(String systemCode,String optionalHusks,String reminder){
        System.out.println("#########获取系统名为："+systemCode+"           是否有收藏optionalHusks："+optionalHusks);
        //定义收藏类型
        String showtype = "";
        //定义红灯提醒类型
        String remindertype = "";
        if(optionalHusks != null && !"".equals(optionalHusks)){
            showtype = "CONST";
        }else{
            showtype = "HIDDEN";
        }
        try {
            if(systemCode.equals("workmanager")){//工作管理
                if(reminder != null && !"".equals(reminder)){
                    remindertype = "READONLY";
                }else{
                    remindertype = "HIDDEN";
                }
                ManageConfig.updateViemXml(systemCode, "todo", "g01", "residueday", remindertype);
                ManageConfig.updateViemXml(systemCode, "todo", "g01", "collect", showtype);
                ManageConfig.updateViemXml(systemCode, "done", "g01", "collect", showtype);
            }else if(systemCode.equals("addressbooks")){//公共通讯录列表
                ManageConfig.updateViemXml(systemCode, "addresslist", "addresslist_list", "collection", showtype);
                ManageConfig.updateViemXml(systemCode, "addresslist", "addresslist_list", "cancelcollect", showtype);
            }else if(systemCode.equals("noticemanagement")){//通知公告管理
                if(optionalHusks != null && !"".equals(optionalHusks)){
                    showtype = "COMBOX";
                }else{
                    showtype = "HIDDEN";
                }
                ManageConfig.updateViemXml(systemCode, "tempcement", "tempcementlist", "collections", showtype);
            }
            else if(systemCode.equals("myemail")){//资料传送已发送列表
                ManageConfig.updateViemXml(systemCode, "outbox", "list", "collect", showtype);
            }else if(systemCode.equals("dailyschedule")){//日程安排集合和表单
                ZhzwChannelItemModel[] channel = ManageConfig.getConfig("dailyschedule");
                String datetype= (String) channel[0].getModuleList().get(0).get("datetype");
                System.out.println("util++++++++++++++++="+datetype);
                //领导日程 READONLY
                if("1".equals(datetype)) {
                    System.out.println("进去1");
                    //更改记录集合
                    ManageConfig.updateViemXml("dailyschedule", "leadschedulej", "leadschedulej", "people", "HIDDEN");
                    ManageConfig.updateViemXml("dailyschedule", "leadschedulej", "leadschedulej", "selectlead", "READONLY");
                }else{
                    System.out.println("进去2");
                    //日程安排
                    //更改记录集合
                    ManageConfig.updateViemXml("dailyschedule", "leadschedulej", "leadschedulej", "selectlead", "HIDDEN");
                    ManageConfig.updateViemXml("dailyschedule", "leadschedulej", "leadschedulej", "people", "READONLY");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取项目路径
     */
    public static String getBasePath()
    {
        URL url = AppData.class.getResource("AppData.class");
        String path = url.getPath();
        int nPos = path.lastIndexOf("WEB-INF");
        if (nPos == -1) {
            DebugService.getInstance().error("平台初始化错误:请检查平台JAR包是否部署在应用的WEB-INF/lib目录下!错误路径为:" + path);
            return "";
        }
        path = path.substring(0, nPos);
        if (path.indexOf("file:/") == 0) {
            path = path.substring(5);
        }
        DebugService.getInstance().info("应用所在目录：" + path);
        return path;
    }

    /**
     * 模块草稿箱数据插入
     * @param system  系统号
     * @param module  模块号
     * @param usercode  暂存人code
     * @param wid   暂存任务在主表的id
     * @param title   标题
     * @param startTime  开始时间
     * @param url   详情链接
     * @param type   类型
     * @return    草稿id
     */
    public static String draftManager(String system,String module,String usercode,String wid,String title,
                                      String startTime,String url,String type,String status) {
        String pk = "";
        try {
            DatabaseBo dbo = new DatabaseBo();
            HashMap<String, String> map = new HashMap<>();
            map.put("system", system);
            map.put("module", module);
            map.put("usercode", usercode);
            map.put("wid", wid);
            map.put("title", title);
            map.put("url", url);
            map.put("starttime", startTime);
            map.put("type", type);
            if(status.equals("insert")){
                pk = dbo.insert(map, "OA_DRAFT");
            }else{
                //获取id
                String sql = "select id from OA_DRAFT where wid = '"+wid+"' and type = '"+type+"'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                if(list.size()>0){
                    map.put("id",list.get(0).get("ID"));
                }
                dbo.update(map,"OA_DRAFT");
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return pk;
    }

    /**
     * 消息发送
     * @param title 主题
     * @param content 内容
     * @param id 主表id
     * @param sendcode 发送人code
     * @param reception 接收人code
     * @param url 详情链接
     * @param type 模块类型
     */
    public static void sendMess(String title,String content,String id,String sendcode,String reception,String url,String type){
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("title", title);
            hashMap.put("content", content);
            hashMap.put("url", url);
            hashMap.put("typename", type);
            hashMap.put("messagetype", "1");
            hashMap.put("sendcode", sendcode);
            Date d = new Date();
            hashMap.put("time", DateUtil.transferDateToString(d, "yyyy-MM-dd HH:mm:ss"));
            hashMap.put("reception", reception);
            hashMap.put("isread", "0");
            hashMap.put("zhuid", id);
            DatabaseBo dbo = new DatabaseBo();
            String cid = dbo.insert((Object)hashMap, "OA_MESSAGECENTER");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
