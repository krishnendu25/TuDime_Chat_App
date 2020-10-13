package obj.quickblox.sample.chat.java.services.fcm;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.ui.activity.Splash_Activity;
import obj.quickblox.sample.chat.java.utils.NotificationUtils;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import com.quickblox.users.model.QBUser;

import java.util.Map;

public class PushListenerService extends QBFcmPushListenerService {
    private static final String TAG = PushListenerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    protected void showNotification(String message) {
        NotificationUtils.showNotification(App.getInstance(), Splash_Activity.class,
                App.getInstance().getString(R.string.notification_title), message,
                R.drawable.ic_notification, NOTIFICATION_ID);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            Log.d(TAG, "App has logged user" + qbUser.getId());
            LoginService.start(this, qbUser);
        }
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);

            showNotification(message);

    }
}
