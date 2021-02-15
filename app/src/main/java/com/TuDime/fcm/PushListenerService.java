package com.TuDime.fcm;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import java.util.Map;

import com.TuDime.App;
import com.TuDime.R;
import com.TuDime.ui.activity.Splash_Activity;
import com.TuDime.utils.ActivityLifecycle;
import com.TuDime.utils.NotificationUtils;

public class PushListenerService extends QBFcmPushListenerService {
    private static final String TAG = PushListenerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    protected void showNotification(String message) {
        NotificationUtils.showNotification(App.getInstance(), Splash_Activity.class,
                App.getInstance().getString(R.string.notification_title), message,
                R.drawable.notification_icon, NOTIFICATION_ID);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);
        if (ActivityLifecycle.getInstance().isBackground()) {
            showNotification(message);
        }
    }
}