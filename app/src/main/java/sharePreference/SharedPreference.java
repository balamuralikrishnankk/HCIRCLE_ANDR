package sharePreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lenovo on 25-Dec-15.
 */
public class SharedPreference {
    public static final String PREFERENCE_NAME="NAME";
    public static final String PREFERENCE_KEY="KEY";
    public SharedPreference(){
        super();
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
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        text = sharedPreferences.getString(PREFERENCE_KEY,null);
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
