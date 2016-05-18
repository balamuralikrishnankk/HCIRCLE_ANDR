package globals;

import android.app.Application;

import Utils.FontsOverride;


/**
 * Created by user on 7/14/2015.
 */
public class App extends Application {

   private static App instance;

    public static synchronized App get(){
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        FontsOverride.setDefaultFont(this, "fonttype", "fontawesome-webfont.ttf");

    }
}
