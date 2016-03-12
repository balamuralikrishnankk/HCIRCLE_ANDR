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
    static String teamName;
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

        channelList = new ArrayList<String>();
        try {
            ConnectServer channelIdList = new ConnectServer("http://"+sp.getServerIP_Preference(context)+"/TabGenAdmin/getChannelsID.php"+
                    "?user_id="+user_id);
            String jsonStr = channelIdList.convertInputStreamToString(channelIdList.getData());
            //sp.savePreference(context,"CHANNEL_DETAILS",jsonStr);
            sp.saveChannelPreference(context,jsonStr);
            //System.out.println(jsonStr);
            if(jsonStr!=null){
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    if (channelIdList.responseCode == 200) {
                        JSONObject jsonObject;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            if(jsonObject.getString("Channel_name").trim().length()!=0)
                                channelList.add(jsonObject.getString("Channel_name"));
                            teamName=(jsonObject.getString("Team_Name"));
                        }
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
        expandableListDetail.put(teamName, channelList);
        //expandableListDetail.put("Lilavati Hospital", sublist2);

        //expandableListDetail.put("RIMS", sublist3);
        //expandableListDetail.put("J.N. Hospital", sublist4);
        return expandableListDetail;
    }
}
