package com.test.drinkwaterdemo.common;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Avinash Kahal on 30-Jun-17.
 */

public class App extends Application {

    public static String TAG = "App";
    static Context context;
    private static App mInstance;

    static Typeface tf_Regular, tf_Bold;

    public static String strFolderDBName = "DRINK_WATER";
    public static String DB_NAME = "drinkwater.db";
    /*public static String DB_PATH = "/data/data/" + "com.test.drinkwaterdemo" + "/databases/";*/

    //add dot to folder name...& remove from onCreate "create_DB_Folder"
    public static String DB_PATH = "/sdcard/" + strFolderDBName + "/";


    public static String PREF_NAME = "DRINKWATER_PREFERENCE";
    public static SharePrefrences sharePrefrences;

    public static String Defualt_Weight = "60";
    public static String Defualt_NotificationTime = "60";
    public static String Defualt_WakeUpTime = "10:00";
    public static String Defualt_SleepTime = "21:00";

    public static String Weight_Kg = "kg";  //min=30 to max=150
    public static String Weight_lbs = "lbs";

    //http://www.shapefit.com/calculators/human-water-requirement-calculator.html
    public static String Gender_Male = "Male";  //difference = 960 ml
    public static String Gender_Female = "Female";

    public static String Exercise_Sedentary = "0 min Sedentary"; //0 ml
    public static String Exercise_Regular = "20 min Regular"; //225 ml
    public static String Exercise_Active = "40 min Active"; // 450 ml
    public static String Exercise_Active_Extrem = "60 min Active"; // 675 ml

    public static String Weather_Monsoon = "Normally Cool (Monsoon)"; //150 ml   Normally Cool
    public static String Weather_Summer = "Extremly Hot (Summer)"; //300 ml     Extremly Heat
    public static String Weather_Winter = "Extremly Cold (Winter)"; //0 ml       Extremly Cold




    /*The recommended water intake for
    -> men is 3 liters (about 14 glasses) of total fluids consumption a day.
    -> women is 2.2 liters (about 10 glasses) a day.
    -> While babies and infants need 0.7 to 0.8 liters of water daily from breast milk or formula.
    -> Small children need 1.3 liters to 1.7 liters everyday.
    -> Boys, age ranges from 9 to 13 need 2.5 liters every day.
    -> Girls, ages 9 to 13 need 2 to 2.5 liters daily.

    "Drink eight 8 glasses of water daily." That's about 1.9 liters, which isn't that far from the Institute of Medicine guidelines. */





    public static String PaymentReson_InPatient = "In Patient Payment";
    public static String PaymentReson_HomeCollection = "Home Collection";
    public static String PaymentReson_HealthCheckUp = "Executive Health Checkup";

    public static String Country = "India";


    public static String PaymentType_CreditCard = "Indian/Internationally issued Credit Card";
    public static String PaymentType_DebitCard = "Indian/Internationally issued Debit Card";
    public static String PaymentType_CreditDebitCard = "Indian/Internationally issued Credit/Debit Card";



    /* -----   SharePrefrences  ----- */

    /*App.sharePrefrences.setPref(App.Noti_Sleeptime, ""+Noti_Sleeptime);
    App.sharePrefrences.getStringPref(App.Noti_wakeuptime);*/

    public static String Noti_wakeuptime = "Noti_wakeuptime";
    public static String Noti_Sleeptime = "Noti_Sleeptime";
    public static String Sh_Min_Selected = "Min_Selected";
    public static String Sh_ToggleBnt = "Is_Checked";
    public static String Sh_Weight = "Weight";
    public static String Sh_Weight_Measure = "Weight_Measure";
    public static String Sh_Login = "Login";
    public static String Sh_True = "True";
    public static String Sh_Flase = "False";
    public static String Sh_Glass_Custome_On = "Glass_Custome_On";

    public static String Sh_Glass100 = "100";
    public static String Sh_Glass150 = "150";
    public static String Sh_Glass200 = "200";
    public static String Sh_Glass300 = "300";
    public static String Sh_Glass500 = "500";
    public static String Sh_Glass750 = "750";
    public static String Sh_Glass_Custome = "Glass_Custome";


    /*---- Please do not change name as they are same as drawable image name-----*/
    public static String Sh_Glass100_Img = "img_glass100";
    public static String Sh_Glass150_Img = "img_glass150";
    public static String Sh_Glass200_Img = "img_glass200";
    public static String Sh_Glass300_Img = "img_glass300";
    public static String Sh_Glass500_Img = "img_glass500";
    public static String Sh_Glass750_Img = "img_glass750";
    public static String Sh_Glass_Custome_Img = "img_custom_glass";



    /*-------- New Design Glass & Img------------*/
    public static String Sh_Glass250 = "250";
    public static String Sh_Glass250_Img = "drink_glass";

    public static String Sh_Glass550 = "550";
    public static String Sh_Glass550_Img = "drink_bottle";

    public static String Sh_Glass750_New = "750";
    public static String Sh_Glass750_Img_New = "drink_bottle_large";


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        mInstance = this;

        sharePrefrences = new SharePrefrences(App.this);

        create_Folder();  // only for testing purpose of local Database

    }

    public static void showLog(String a1, String a2) {
        System.out.println("From: " + a1 + " :---: " + a2);
    }

    public static void showSnackBar(View view, String strMessage) {
        Snackbar snackbar = Snackbar.make(view, strMessage, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static String convertTimeInMiliSeconds(long timeInMiliSec){

        Date date = new Date(timeInMiliSec);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);

        App.showLog(TAG, dateFormatted);

        return dateFormatted;
    }

    public static Typeface getFont_Regular() {
        tf_Regular = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        return tf_Regular;
    }

    public static Typeface getFont_Bold() {
        tf_Bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        return tf_Bold;
    }

    public static long parseTodaysDate(String time) {

        String inputPattern = "HH:mm";

        String outputPattern = "HHmmss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;
        long timeInMilliseconds = 0;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

            Date mDate = outputFormat.parse(str);
            timeInMilliseconds = mDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String timeCurrentHHMM(){
        String currentDateandTime = "";
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            currentDateandTime = sdf.format(new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        return currentDateandTime;
    }

    public static String dateCurrentYYYYMMDD(){
        String currentDateandTime = "";
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            currentDateandTime = sdf.format(new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        return currentDateandTime;
    }

    public static String convert24hrto12hr(String hrIn24){
        String hrIn12 = "";

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(hrIn24);

            hrIn12 = new SimpleDateFormat("K:mm a").format(dateObj);

            App.showLog(TAG, String.valueOf(dateObj));
            App.showLog(TAG, new SimpleDateFormat("K:mm").format(dateObj));

        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return hrIn12;
    }


    public static void create_Folder() {
        FileOutputStream out = null;
        try {
            String directoryPath = Environment.getExternalStorageDirectory() + File.separator + App.strFolderDBName;
            File appDir = new File(directoryPath);

            App.showLog(TAG, "appDir: "+appDir);

            if (!appDir.exists() && !appDir.isDirectory()) {
                if (appDir.mkdirs()) {
                    App.showLog(TAG, "CREATE_FOLDER CREATED");
                } else {
                    App.showLog(TAG, "UNABLE TO CREATE FOLDER !!");
                }
            } else {
                App.showLog(TAG,"APP DIR ALREADY EXISTS !!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

}
