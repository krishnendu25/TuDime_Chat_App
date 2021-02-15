package com.TuDime.services.fcm;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.TuDime.App;
import com.TuDime.R;
import com.TuDime.services.LoginService;
import com.TuDime.ui.activity.Splash_Activity;
import com.TuDime.utils.NotificationUtils;
import com.TuDime.Prefrences.SharedPrefsHelper;
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

            showNotification("You Have Received A New Message/Call");

    }
}
