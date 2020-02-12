package dataTypes;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.myapplication.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class MyNotification {
    public static void add(Context c,String title,String text,Calendar calendar )
    {
        AlarmManager alarmManager =(AlarmManager) c.getSystemService(ALARM_SERVICE);


        Intent i= new Intent(c,NotificationReceiver.class);
        i.putExtra("title",title);
        i.putExtra("text",text);
        int temp=(int)System.currentTimeMillis();
        i.putExtra("code",temp);
        PendingIntent broadcast = PendingIntent.getBroadcast(c,temp,i,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),broadcast);

    }

}
