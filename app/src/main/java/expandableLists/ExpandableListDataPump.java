package expandableLists;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connectServer.ConnectServer;
import sharePreference.SharedPreference;

public class ExpandableListDataPump {
    static String user_id="";
    static String team_name;
    static List<String> channelList;
    static List<String> channel_ids;


    public static HashMap<String, List<String>> getData(Context context) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        SharedPreference sp = new SharedPreference();
        String user_details = sp.getPreference(context);

        try{
            JSONObject jObj = new JSONObject(user_details);
            user_id=jObj.getString("id");
            System.out.println("User ID: "+user_id);
            //username = jObj.getString("username");
        }catch(Exception e){
            System.out.println("Unable to read user ID: "+e.toString());
        }

        //getting list of Channel lists
        //channelList = OrganisationDetails.getListOfChannel(user_id);

        try {
            ConnectServer channelIdList = new ConnectServer("http://"+sp.getServerIP_Preference(context)+"/TabGenAdmin/getChannelsID.php"+
                    "?user_id="+user_id);
            String jsonStr = channelIdList.convertInputStreamToString(channelIdList.getData());
            //sp.savePreference(context,"CHANNEL_DETAILS",jsonStr);
            sp.saveChannelPreference(context,jsonStr);
            //System.out.println(jsonStr);
            if(jsonStr!=null){
                try {
                    JSONObject jsonObj1 = new JSONObject(jsonStr);
                    JSONArray jsonArray1 = jsonObj1.getJSONArray("team_list");
                    JSONArray jsonArray2 = jsonObj1.getJSONArray("channels");
                    for(int i=0;i<jsonArray1.length();i++){//for every item(team) in the team list
                        JSONObject jsonObj2 = jsonArray1.getJSONObject(i);
                        team_name = jsonObj2.getString("team_name");//getting the team name

                        JSONObject jsonObj3 = jsonArray2.getJSONObject(i);//getting json objects for channels
                        JSONArray jsonArray3 = jsonObj3.getJSONArray(team_name);
                        channelList = new ArrayList<String>();
                        channel_ids = new ArrayList<String>();
                        for(int j=0;j<jsonArray3.length();j++){
                            JSONObject jsonObj4 = jsonArray3.getJSONObject(j);
                            channelList.add(jsonObj4.getString("Channel_name"));
                            channel_ids.add(jsonObj4.getString("Channel_ID"));
                        }
                        expandableListDetail.put(team_name, channelList);
                    }
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }
        }
        catch(Exception e){
            System.out.println("Exception here: " + e.toString());
        }
        /*
        List<String> sublist1 = new ArrayList<String>();
        sublist1.add("Laboratory Group");
        sublist1.add("Cardiology Dept");

        List<String> sublist2 = new ArrayList<String>();
        sublist2.add("Laboratory Group");
        sublist2.add("Cardiology Dept");
        sublist2.add("ENT");

        List<String> sublist3 = new ArrayList<String>();
        sublist3.add("Laboratory Group");
        sublist3.add("Cardiology Dept");
        sublist3.add("ENT");

        List<String> sublist4 = new ArrayList<String>();
        sublist4.add("Laboratory Group");
        sublist4.add("Cardiology Dept");
        sublist4.add("ENT");
        */

        //expandableListDetail.put("Lilavati Hospital", sublist2);

        //expandableListDetail.put("RIMS", sublist3);
        //expandableListDetail.put("J.N. Hospital", sublist4);
        return expandableListDetail;
    }
}
