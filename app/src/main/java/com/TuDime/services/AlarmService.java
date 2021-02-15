package com.TuDime.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import com.TuDime.R;
import com.TuDime.ui.activity.Splash_Activity;

public class AlarmService extends WakeIntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    void doReminderWork(Intent intent) {
        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, Splash_Activity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification note = new Notification(R.drawable.calendar, "Alarm",
                System.currentTimeMillis());
       // note.setLatestEventInfo(this, "Title", "Text", pi);
        note.defaults |= Notification.DEFAULT_ALL;
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        int id = 123456789;
        manager.notify(id, note);
    }
}