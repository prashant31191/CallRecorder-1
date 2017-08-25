package com.test.drinkwaterdemo.common;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.activity.ActDrinkTrack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Avinash Kahal on 30-Jun-17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar now = GregorianCalendar.getInstance();
        int dayOfWeek = now.get(Calendar.DATE);



        /*------- Getting Wakeup & Sleep Time -------*/
        long Noti_wakeuptime = Long.parseLong(App.sharePrefrences.getStringPref(App.Noti_wakeuptime));
        long Noti_Sleeptime = Long.parseLong(App.sharePrefrences.getStringPref(App.Noti_Sleeptime));



        /*------- Getting Current Time -------*/
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String currentDateandTime = sdf.format(new Date());
        long CurrenttimeInMillisec = 0;
        try {
            Date mDate = sdf.parse(currentDateandTime);
            CurrenttimeInMillisec = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        /*------- Receive Notification between WakeUp & Sleep Time -------*/
        if( (Noti_wakeuptime < CurrenttimeInMillisec) && (CurrenttimeInMillisec < Noti_Sleeptime)){
            if(dayOfWeek != 1 && dayOfWeek != 7) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.img_glass_custom)
                        .setContentTitle("IT'S TIME TO DRINK WATER.");

                /*---- clear notification from notification bar ----*/
                mBuilder.setAutoCancel(true);

                //.setContentText(Noti_wakeuptime+" "+CurrenttimeInMillisec+" "+Noti_Sleeptime); //context.getResources().getString(R.string.message_timesheet_not_up_to_date)

                Intent resultIntent = new Intent(context, ActDrinkTrack.class);
                android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
                stackBuilder.addParentStack(ActDrinkTrack.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }
        }/*else if(Noti_wakeuptime < CurrenttimeInMillisec){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.img_noti)
                    .setContentTitle("Wait for WakeUp Time");
        }else if(CurrenttimeInMillisec < Noti_Sleeptime){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.img_noti)
                    .setContentTitle("Alarm Stop");
        }*/

    }
}