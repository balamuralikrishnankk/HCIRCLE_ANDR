package UserDetails;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import sharePreference.SharedPreference;

/**
 * Created by SALAI on 2/22/2016.
 */
public class UserDetails {
    Context _context;
    SharedPreference sp;
    String user_details;
    public UserDetails(Context context){
        this._context=context;
        sp = new SharedPreference();
        user_details = sp.getPreference(context);
    }
    public String getUserId(){
        String id=null;
        try {
            JSONObject jsonObject = new JSONObject(user_details);
            id = jsonObject.getString("id");
        } catch (JSONException e) {
            System.out.println("User Id Exception :" + e.toString());
        }
        return id;
    }
    public String getTokenId(){
        String token = sp.getTokenPreference(_context);
        return token;
    }
    public String getUserRole(){
        String role=null;
        try {
            JSONObject jsonObject = new JSONObject(user_details);
            //username.setText(jsonObject.getString("username"));
            //usermail.setText(jsonObject.getString("email"));
            role = jsonObject.getString("roles");
        } catch (JSONException e) {
            System.out.println("User Role Exception :" + e.toString());
        }
        return role;
    }

}
