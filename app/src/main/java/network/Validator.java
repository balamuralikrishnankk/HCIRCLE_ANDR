package network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import globals.App;


/**
 * Created by yashesh on 6/7/2015.
 */
public class Validator {

        public static boolean isConnected(){

            ConnectivityManager connMgr = (ConnectivityManager)
                    App.get().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
              return true;
            } else {
                return false;
            }

        }

}
