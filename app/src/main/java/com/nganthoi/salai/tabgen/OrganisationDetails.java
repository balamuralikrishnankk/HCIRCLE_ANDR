package com.nganthoi.salai.tabgen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectServer;

/**
 * Created by Lenovo on 28-Dec-15.
 */
public class OrganisationDetails {
    public static List<String> getListOfOrganisationUnits(String username){
        //Getting list of Organisation Units for a particular user
        List<String> list = new ArrayList<String>();
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("createdBy", username);
            ConnectServer orgUnitList = new ConnectServer("http://188.166.210.24:8065/api/v1/organisationUnit/track");
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

    public static List<String> getListOfOrganisations(String username){
        //Getting list of Organisation for a particular user
        List<String> list = new ArrayList<String>();
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("createdBy", username);
            ConnectServer orgUnitList = new ConnectServer("http://188.166.210.24:8065/api/v1/organisation/track");
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

    public static List<String> getListOfTemplates(String role){
        //Getting list of Organisation for a particular user
        List<String> list = new ArrayList<String>();
        try {
            ConnectServer templateList = new ConnectServer("http://188.166.210.24/getTemplateListByRole.php");
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
}
