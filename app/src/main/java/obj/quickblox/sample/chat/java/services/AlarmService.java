package obj.quickblox.sample.chat.java.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.activity.SplashActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmService extends WakeIntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    void doReminderWork(Intent intent) {
        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, SplashActivity.class);
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