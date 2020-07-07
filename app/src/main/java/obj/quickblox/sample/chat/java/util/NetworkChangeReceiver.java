package obj.quickblox.sample.chat.java.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.quickblox.users.model.QBUser;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
/**
 * Created by KRISHNENDU MANNA  00/00/2020
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {

                if ( SharedPrefsHelper.getInstance().get_LOGIN_SERVICE_STATUS().equalsIgnoreCase("stop"))
                {
                    if (SharedPrefsHelper.getInstance().hasQbUser()) {
                        QBUser qbUser = SharedPrefsHelper.getInstance().getQbUser();
                        LoginService.start(context, qbUser);
                    }
                }
            } else {
                if ( SharedPrefsHelper.getInstance().get_LOGIN_SERVICE_STATUS().equalsIgnoreCase("start"))
                { LoginService.stop(context);}
            }
        } catch (Exception e) { }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }

            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e)
        {return false;}
    }
}
