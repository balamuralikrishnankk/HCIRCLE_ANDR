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

    public static List<String> getListOfTemplates(Context context,String user_id){
        //Getting list of Organisation for a particular user
        SharedPreference sp = new SharedPreference();
        String ip = sp.getServerIP_Preference(context);
        System.out.println(ip);
        List<String> list = new ArrayList<String>();
        try {
            ConnectServer templateList = new ConnectServer("http://"+ip+"/TabGenAdmin/getTemplateListByUserId.php?user_id="+user_id);
            String jsonStr = templateList.convertInputStreamToString(templateList.getData());
            System.out.println(jsonStr);
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
            ConnectServer channelIdList = new ConnectServer("http://128.199.111.18/TabGenAdmin/getChannelsID.php");
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
    public static String getChannelId(String team_name,String channel_name,Context context){
        String channel_id=null;
        SharedPreference sp = new SharedPreference();
        String channelDetails = sp.getChannelPreference(context);
        if(channelDetails!=null) {
            System.out.println("Channel is not null: " + channelDetails);
            try {
                JSONObject jsonObj1 = new JSONObject(channelDetails);
                JSONArray jsonArray1 = jsonObj1.getJSONArray("team_list");
                JSONArray jsonArray2 = jsonObj1.getJSONArray("channels");
                for(int i=0;i<jsonArray1.length();i++){//for every item(team) in the team list
                    JSONObject jsonObj2 = jsonArray1.getJSONObject(i);
                    String teamName = jsonObj2.getString("team_name");//getting the team name

                    JSONObject jsonObj3 = jsonArray2.getJSONObject(i);//getting json objects for channels
                    JSONArray jsonArray3 = jsonObj3.getJSONArray(teamName);
                    //List<String> channelList = new ArrayList<String>();
                    for(int j=0;j<jsonArray3.length();j++){
                        JSONObject jsonObj4 = jsonArray3.getJSONObject(j);
                        if (channel_name.equals(jsonObj4.getString("Channel_name")) && team_name.equals(teamName)) {
                            channel_id = jsonObj4.getString("Channel_ID");// setting channel id
                            break;
                        }
                    }
                    if(channel_id!=null) break;
                }
            }catch(Exception e){
                System.out.print("Channel ID Exception occurs here: " + e.toString());
            }
        }
        return channel_id;
    }
}
