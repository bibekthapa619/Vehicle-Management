package dataTypes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.myapplication.MainActivity;
import com.myapplication.R;

import java.util.Date;

import static android.content.Intent.getIntent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =PendingIntent.getActivity(context,intent.getIntExtra("code",0),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =new NotificationCompat.Builder(context)
                                            .setContentTitle(intent.getStringExtra("title"))
                                            .setContentText(intent.getStringExtra("text"))
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(R.drawable.projectlogo)
                                            .setAutoCancel(true);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        builder.setLights(Color.RED, 3000, 3000);

        notificationManager.notify(intent.getIntExtra("code",0) ,builder.build());


    }


}
