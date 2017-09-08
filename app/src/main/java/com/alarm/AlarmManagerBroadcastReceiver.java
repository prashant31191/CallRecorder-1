package com.alarm;

/**
 * Created by Adminsss on 14-03-2016.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.Utils.App;
import com.azapps.callrecorder.MainActivity;
import com.azapps.callrecorder.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    Context mContext;



    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();
        StringBuilder msgStr2 = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }


        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));
        App.showLog("====alarm===#####===onReceive===msgStr="+msgStr.toString());

        //Release the lock
        wl.release();

        if(App.isStartAlarm == false) {

            Format formatter2 = new SimpleDateFormat("hh:mm a");
            msgStr2.append(formatter2.format(new Date()));

        // 111   App.showLog("==display time======strDisplayTime=="+App.sharePrefrences.getStringPref(PreferencesKeys.strDisplayTime));
            App.showLog("==current time====current notify==msgStr2=="+msgStr2.toString());

         //111   if(App.sharePrefrences.getStringPref(PreferencesKeys.strDisplayTime).equalsIgnoreCase(msgStr2.toString()))
            {
                setStudyReminderNotification(context);
            }

        }
        else
        {
            App.isStartAlarm = false;
        }


    }

    public void setStudyReminderNotification(Context context) {
        App.showLog("===#####======fire=====setStudyReminderNotification==========#####===");
        {
            try {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT); //PendingIntent.FLAG_UPDATE_CURRENT)

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("Time to study")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setColor(0x60000000);

                NotificationManager notificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                int notifitionIdTime = (int) System.currentTimeMillis();
                notificationManager.notify(notifitionIdTime /* ID of notification */, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void SetAlarm(Context context)
    {
        App.showLog("===#####===========SetAlarm==========#####===");

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);

    //After after 0.90 minute
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30 , pi); //5 -> 60 (1minute)
    }




    public void SetFrequencyAlarm(Context context)
    {
        App.showLog("===#####===========SetAlarm==========#####===");
        String freqTag = "";
        App.showLog("===##=====freqTag=====##==="+freqTag);
        // 111 if(App.sharePrefrences.getStringPref(PreferencesKeys.strReminder).equalsIgnoreCase("1"))
        {


            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            intent.putExtra(ONE_TIME, Boolean.FALSE);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            //After after 5 seconds
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);

            //After after 0.90 minute
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30 , pi); //5 -> 60 (1minute)

            // 111      String strHourOfDay = App.sharePrefrences.getStringPref(PreferencesKeys.strHourOfDay);
            // 111   String strMinute = App.sharePrefrences.getStringPref(PreferencesKeys.strMinute);


            // 111  freqTag = App.sharePrefrences.getStringPref(PreferencesKeys.strSchedual);

            App.showLog("===##=====freqTag===PreferencesKeys.strSchedual==##==="+freqTag);

            if (freqTag.equalsIgnoreCase("Daily")) {
                // For the daily shecual notification
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                // 111     calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strHourOfDay));
                // 111      calendar.set(Calendar.MINUTE, Integer.parseInt(strMinute));
                calendar.set(Calendar.SECOND, 0);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

                return;
            } else if (freqTag.equalsIgnoreCase("Weekly")) {
                // For the daily shecual notification
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                // 111      calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strHourOfDay));
                // 111      calendar.set(Calendar.MINUTE, Integer.parseInt(strMinute));
                calendar.set(Calendar.SECOND, 0);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pi);

                return;
            } else if (freqTag.equalsIgnoreCase("Monthly")) {
                // For the daily shecual notification
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                // 111 calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strHourOfDay));
                // 111 calendar.set(Calendar.MINUTE, Integer.parseInt(strMinute));
                calendar.set(Calendar.SECOND, 0);

                calendar.add(Calendar.SECOND, 30);
                calendar.add(Calendar.MONTH, 1);

                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pi);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);



                return;
            } else {
                App.showLog("=======Alarm Not set=======");
            }
        }
        // 111 else
        {
            App.showLog("=======Alarm Not set====Reminder Off=====");
            CancelAlarm(context);
        }



    }





    public void CancelAlarm(Context context)
    {
        App.showLog("===#####===========CancelAlarm==========#####===");
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }



}
