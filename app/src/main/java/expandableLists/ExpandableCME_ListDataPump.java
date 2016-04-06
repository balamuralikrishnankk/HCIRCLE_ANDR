package expandableLists;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connectServer.ConnectServer;
import sharePreference.SharedPreference;

public class ExpandableCME_ListDataPump {
    static String user_id="";
    static String team_name;
    static List<String> channelList;


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

            //sp.saveChannelPreference(context,jsonStr);

            if(jsonStr!=null){
                try {
                    JSONObject jsonObj1 = new JSONObject(jsonStr);
                    JSONArray jsonArray1 = jsonObj1.getJSONArray("team_list");
                    //JSONArray jsonArray2 = jsonObj1.getJSONArray("channels");
                    for(int i=0;i<jsonArray1.length();i++){//for every item(team) in the team list
                        JSONObject jsonObj2 = jsonArray1.getJSONObject(i);
                        team_name = jsonObj2.getString("team_name");//getting the team name
                        channelList = new ArrayList<String>();
                        channelList.add("CME Channel 1");
                        channelList.add("CME Channel 2");
                        channelList.add("CME Channel 3");
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
        return expandableListDetail;
    }
}
