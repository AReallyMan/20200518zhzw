
package com.zhzw.util;

import com.siqiansoft.commons.FileIO;
import com.siqiansoft.commons.XmlSerializer;
import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.zhzw.model.ZhzwChannelItemModel;
import org.apache.axis.client.Service;
import org.apache.axis.types.URI;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * 短信发送工具类
 */

public class SMSUtil {

    //请求地址
    private static final String URL_PATH = "http://192.168.1.19/services/cmcc_mas_wbs";

    //批量发短信时， 每批最大发送量
    private static final int MASS_MAX = 30;


/**
     * 根据系统配置情况发送短信
     *
     * @param dbo
     * @param receivers
     * @param modularCode
     * @param loginName
     */

    public static void sendSMS(DatabaseBo dbo, String receivers, String modularCode, String loginName) {
        try {
            String path = AppData.getInstance().getAppConfigDir() + "/zhzwmanageconfig/" + modularCode + ".xml";

            //文件存在
            if (FileIO.existFile(path)) {
                //读取xml
                ZhzwChannelItemModel[] ZhzwChannelItemModels = (ZhzwChannelItemModel[]) XmlSerializer.getInstance().fromXMLFile(path);
                Integer settingCode = (Integer) ZhzwChannelItemModels[0].getModuleList().get(0).get("news");
                String modularName = (String) ZhzwChannelItemModels[0].getModuleList().get(0).get("systemName");

                String message = "【" + modularName + "】 " + loginName + " 向您提交了" + modularName;


/*
                 * 1 : 短信提示
                 * 2 : 系统+短信提示
                 * 0 : 系统提示
                 * 3 : 无提示
                 */

                if (settingCode == 1 || settingCode == 2) {
                    massPhoneMessage(dbo, receivers, message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/**
     * 批量发送短信   总发送量: 1 ~ n ; 分批发送，每批最大为 MASS_MAX
     *
     * @param dbo
     * @param receivers 接收人
     * @param message   短信模板
     * @throws Exception
     */

    private static void massPhoneMessage(DatabaseBo dbo, String receivers, String message) throws Exception {

        String code1 = StringUtils.stripEnd(StringUtils.deleteWhitespace(receivers), ",");
        String code2 = "'" + code1.replace(",", "','") + "'";
        String sql_param = "select name, mobile from eap_contact  where code in (" + code2 + ") ";

        List<HashMap<String, String>> reciptList = dbo.prepareQuery(sql_param, null);
        StringBuffer mobileBuffer = new StringBuffer();
        if (reciptList.size() > 0) {
            for (HashMap<String, String> map : reciptList) {
                String mobile = map.get("MOBILE");
                //String name = map.get("NAME");
                mobileBuffer.append(mobile + ",");
            }
        }
        String mobiles = StringUtils.stripEnd(mobileBuffer.toString(), ",");

        //发送短信
        massPhoneMessage(mobiles, message);
    }


/**
     * 批量发送短信 短信总量: 1 ~ n
     * @param phones  手机号
     * @param message 短信息
     * @return
     * @throws Exception
     */

    public static boolean massPhoneMessage(String phones, String message) throws Exception {
        String[] phoneArry = phones.split(",");

        //单发短信
        if(phoneArry.length == 1){
            URI temp = new URI("tel:" + phoneArry[0]);
            URI[] quantityOne = new URI[]{temp};
            //massMessage(quantityOne, message);
            return true;
        }

        //批量发送短信
        for (int i = 0; i < phoneArry.length; i++) {
            if (i % MASS_MAX == 0) {

                int top = Math.min(i + MASS_MAX, phoneArry.length);
                String[] phoneRange = Arrays.copyOfRange(phoneArry, i, top);
                URI[] quantityMax = new URI[top];
                for (int j = 0; j < phoneRange.length; j++) {
                    quantityMax[j] = new URI("tel:" + phoneRange[j]);
                }

                //发短信
                //massMessage(quantityMax, message);
            }
        }
        return true;
    }


/**
     * 发送短信
     * @param quantityMax
     * @param message
     * @throws Exception
     */

   /* private static void massMessage(URI[] quantityMax, String message)throws Exception{
        URL url = new URL(URL_PATH);
        Service service = new Service();
        SiMockStub stub = new SiMockStub(url, service);
        SendSmsRequest s = new SendSmsRequest();
        s.setApplicationID("P000000000000044");
        s.setDeliveryResultRequest(Boolean.parseBoolean("true"));
        s.setExtendCode("123456");
        s.setMessage(message);
        s.setMessageFormat(MessageFormat.fromValue("GB2312"));
        s.setSendMethod(SendMethodType.fromValue("Normal"));
        s.setDestinationAddresses(quantityMax);
        stub.sendSms(s);
    }*/

}

