package com.nganthoi.salai.tabgen;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectServer;
import sharePreference.SharedPreference;

/**
 * Created by Lenovo on 28-Dec-15.
 */
public class OrganisationDetails {
    public static List<String> getListOfOrganisationUnits(String username,Context context){
        //Getting list of Organisation Units for a particular user
        List<String> list = new ArrayList<String>();
        SharedPreference sp = new SharedPreference();
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("createdBy", username);
            ConnectServer orgUnitList = new ConnectServer("http://"+sp.getServerIP_Preference(context)+":8065/api/v1/organisationUnit/track");
            String jsonStr = orgUnitList.convertInputStreamToString(orgUnitList.putData(jObj));
            //System.out.println(jsonStr);
            if(jsonStr!=null){
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    if (orgUnitList.responseCode == 200) {
                        JSONObject jsonObject;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            list.add(jsonObject.getString("organisation_unit"));
                        }
                    }
                    else list.add(" ");
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }else list.add(" ");
        }
        catch(Exception e){
            System.out.println("Exception here: "+e.toString());
            list.add(" ");
        }
        return list;
        /**********************************************************/
    }

    public static List<String> getListOfOrganisations(String username,Context context){
        //Getting list of Organisation for a particular user
        List<String> list = new ArrayList<String>();
        SharedPreference sp = new SharedPreference();
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("createdBy", username);
            ConnectServer orgUnitList = new ConnectServer("http://"+sp.getServerIP_Preference(context)+":8065/api/v1/organisation/track");
            String jsonStr = orgUnitList.convertInputStreamToString(orgUnitList.putData(jObj));
            //System.out.println(jsonStr);
            if(jsonStr!=null){
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    if (orgUnitList.responseCode == 200) {
                        JSONObject jsonObject;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            list.add(jsonObject.getString("name"));
                        }
                    }
                    else list.add(" ");
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }else list.add(" ");
        }
        catch(Exception e){
            System.out.println("Exception here: "+e.toString());
            list.add(" ");
        }
        return list;
        /**********************************************************/
    }

    public static List<String> getListOfTemplates(Context context,String role){
        //Getting list of Organisation for a particular user
        SharedPreference sp = new SharedPreference();
        String ip = sp.getServerIP_Preference(context);
        System.out.println(ip);
        List<String> list = new ArrayList<String>();
        try {
            ConnectServer templateList = new ConnectServer("http://"+ip+"/TabGen/getTemplateListByRole.php");
            String jsonStr = templateList.convertInputStreamToString(templateList.putData("Role="+role));
            //System.out.println(jsonStr);
            if(jsonStr!=null){
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    if (templateList.responseCode == 200) {
                        JSONObject jsonObject;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            list.add(jsonObject.getString("Template_Name"));
                        }
                    }
                    else list.add(" ");
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }else list.add(" ");
        }
        catch(Exception e){
            System.out.println("Exception here: "+e.toString());
            list.add(" ");
        }
        return list;
        /**********************************************************/
    }

    public static List<String> getListOfChannel(String user_id){
        //Getting list of Organisation for a particular user
        List<String> list = new ArrayList<String>();
        try {
            ConnectServer channelIdList = new ConnectServer("http://128.199.111.18/TabGen/getChannelsID.php");
            String jsonStr = channelIdList.convertInputStreamToString(channelIdList.putData("user_id="+user_id));
            //System.out.println(jsonStr);
            if(jsonStr!=null){
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    if (channelIdList.responseCode == 200) {
                        JSONObject jsonObject;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            list.add(jsonObject.getString("Channel_name"));
                        }
                    }
                    else list.add(" ");
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }else list.add(" ");
        }
        catch(Exception e){
            System.out.println("Exception here: "+e.toString());
            list.add(" ");
        }
        return list;
        /**********************************************************/
    }
    public static String getChannelId(String channel_name,Context context){
        String channel_id=null;
        SharedPreference sp = new SharedPreference();
        String channelDetails = sp.getChannelPreference(context);
        if(channelDetails!=null) {
            System.out.println("Channel is not null: " + channelDetails);
            try {
                JSONArray jsonArray = new JSONArray(channelDetails);
                JSONObject jsonObject;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
               /*System.out.println("Title: "+title+"------->Channel name: "+jsonObject.getString("Channel_name")+" ---->ID: "+
                        jsonObject.getString("Channel_ID"));*/
                    if (channel_name.equals(jsonObject.getString("Channel_name"))) {
                        channel_id = jsonObject.getString("Channel_ID");// setting channel id
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return channel_id;
    }
}
