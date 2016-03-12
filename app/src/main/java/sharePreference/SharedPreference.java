package sharePreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lenovo on 25-Dec-15.
 */
public class SharedPreference {
    public static final String PREFERENCE_NAME="NAME";
    public static final String PREFERENCE_KEY="KEY";
    public static final String CHANNEL_NAME="CHANNEL_NAME";
    public static final String CHANNEL_KEY="CHANNEL_DETAILS";
    public static final String TOKEN="TOKEN";
    public static final String TOKEN_KEY="TOKEN_KEY";
    public static final String SERVER_IP="SERVER_IP";
    public static final String IP_KEY="IP_KEY";
    public static final String TEAM_NAME="TEAM_NAME";
    public static final String TEAM_KEY="TEAM_KEY";
    public SharedPreference(){
        super();
    }
    /*********************************************************************************/
    public void saveChannelPreference(Context context,String text){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(CHANNEL_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHANNEL_KEY,text);
        editor.commit();
    }
    public String getChannelPreference(Context context){
        String text;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(CHANNEL_NAME,Context.MODE_PRIVATE);
        text = sharedPreferences.getString(CHANNEL_KEY,null);
        return text;
    }
    /**************************************************************************/

    public void saveServerIP_Preference(Context context,String text){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(SERVER_IP,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(IP_KEY,text);
        editor.commit();
    }
    public String getServerIP_Preference(Context context){
        String text;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(SERVER_IP,Context.MODE_PRIVATE);
        text = sharedPreferences.getString(IP_KEY,null);
        return text;
    }
    /*************************************************************************************/

    public void saveTeamNamePreference(Context context,String text){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(TEAM_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TEAM_KEY,text);
        editor.commit();
    }

    public String getTeamNamePreference(Context context){
        String text;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(TEAM_NAME,Context.MODE_PRIVATE);
        text = sharedPreferences.getString(TEAM_KEY,null);
        return text;
    }
    /******************************************************************************************/
    /*************************************************************************/
    public void savePreference(Context context,String key,String value){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_APPEND);
        editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public void savePreference(Context context,String text){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PREFERENCE_KEY,text);
        editor.commit();
    }

    public String getPreference(Context context){
        String text;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_APPEND);
        text = sharedPreferences.getString(PREFERENCE_KEY,null);
        return text;
    }
    /*************************************************************************************/

    public void saveTokenPreference(Context context,String text){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(TOKEN,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY,text);
        editor.commit();
    }

    public String getTokenPreference(Context context){
        String text;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(TOKEN,Context.MODE_PRIVATE);
        text = sharedPreferences.getString(TOKEN_KEY,null);
        return text;
    }

    /**************************************************************************************************/


    public String getPreference(Context context,String key){
        String text;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        text = sharedPreferences.getString(key,null);
        return text;
    }

    public void clearPreference(Context context){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public Boolean isEmptyPreference(Context context){
        String text = null;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        text = sharedPreferences.getString(PREFERENCE_KEY,null);
        if(text == "" || text==null)
            return false;
        else return true;
    }
}
