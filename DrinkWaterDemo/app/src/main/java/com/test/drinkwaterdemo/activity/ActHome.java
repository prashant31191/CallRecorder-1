package com.test.drinkwaterdemo.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.common.AlarmReceiver;
import com.test.drinkwaterdemo.common.App;
import com.test.drinkwaterdemo.common.DatabaseHelper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActHome extends AppCompatActivity {

    String TAG = "ActHome";

    EditText et_weight, et_age;
    Button btn_next, btn_next_old;
    TextView tv_wakeuptime, tv_sleeptime, tv_weight_conversion, onlinePayment;
    Spinner spn_alram, spn_weight, spn_gender, spn_exercise, spn_weather;

    String alarm_time[] = {App.Defualt_NotificationTime, "1", "2", "3", "4", "5"};
    String weight_opt[] = {App.Weight_Kg, App.Weight_lbs};
    String gender_opt[] = {App.Gender_Male, App.Gender_Female};
    String exercise_opt[] = {App.Exercise_Sedentary , App.Exercise_Regular, App.Exercise_Active, App.Exercise_Active_Extrem};
    String weather_opt[] = {App.Weather_Monsoon, App.Weather_Summer, App.Weather_Winter};

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    String time = "";
    DecimalFormat f1;
    String from="";
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);

        dbHelper = new DatabaseHelper(this);
        /*dbHelper.DropAllTable();*/

        /*startActivity(new Intent(this, ActOnlinePayment.class));*/

       /* if(App.sharePrefrences.getStringPref(App.Sh_Login).equalsIgnoreCase(App.Sh_True)){
            startActivity(new Intent(this, ActDrinkTrack.class));
        }*/

        initialize();
        checkSharedPreference();
        clickEvent();

    }

    public void initialize(){
        try{
            et_weight = (EditText) findViewById(R.id.et_weight);
            et_age = (EditText) findViewById(R.id.et_age);
            btn_next = (Button) findViewById(R.id.btn_next);
            btn_next_old = (Button) findViewById(R.id.btn_next_old);
            tv_wakeuptime = (TextView) findViewById(R.id.tv_wakeuptime);
            tv_sleeptime = (TextView) findViewById(R.id.tv_sleeptime);
            tv_weight_conversion = (TextView) findViewById(R.id.tv_weight_conversion);
            onlinePayment = (TextView) findViewById(R.id.onlinePayment);
            spn_alram = (Spinner) findViewById(R.id.spn_alram);
            spn_weight = (Spinner) findViewById(R.id.spn_weight);
            spn_gender = (Spinner) findViewById(R.id.spn_gender);
            spn_exercise = (Spinner) findViewById(R.id.spn_exercise);
            spn_weather = (Spinner) findViewById(R.id.spn_weather);


            ArrayAdapter<String> adapter_time = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, alarm_time);
            spn_alram.setAdapter(adapter_time);

            ArrayAdapter<String> adapter_weight = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, weight_opt);
            spn_weight.setAdapter(adapter_weight);

            ArrayAdapter<String> adapter_gender = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, gender_opt);
            spn_gender.setAdapter(adapter_gender);

            ArrayAdapter<String> adapter_exercise = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, exercise_opt);
            spn_exercise.setAdapter(adapter_exercise);

            ArrayAdapter<String> adapter_weather = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, weather_opt);
            spn_weather.setAdapter(adapter_weather);

            et_weight.setTypeface(App.getFont_Regular());
            btn_next.setTypeface(App.getFont_Regular());
            btn_next_old.setTypeface(App.getFont_Regular());
            tv_wakeuptime.setTypeface(App.getFont_Regular());
            tv_sleeptime.setTypeface(App.getFont_Regular());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void checkSharedPreference(){
        try{


            if(App.sharePrefrences.getStringPref(App.Sh_Weight) != null
                    && App.sharePrefrences.getStringPref(App.Sh_Weight).length() > 0) {

                et_weight.setText(""+App.sharePrefrences.getStringPref(App.Sh_Weight));

                /*--- edittext cursor position ---*/
                int position = et_weight.length();
                Editable etext = et_weight.getText();
                Selection.setSelection(etext, position);
            }else {
                et_weight.setText(""+App.Defualt_Weight);

                /*--- edittext cursor position ---*/
                int position = et_weight.length();
                Editable etext = et_weight.getText();
                Selection.setSelection(etext, position);
            }


            if(App.sharePrefrences.getStringPref(App.Sh_Weight_Measure) != null
                    && App.sharePrefrences.getStringPref(App.Sh_Weight_Measure).length() > 0) {

                if(App.Weight_Kg.equalsIgnoreCase(App.sharePrefrences.getStringPref(App.Sh_Weight_Measure))){
                    spn_weight.setSelection(0);
                }else if(App.Weight_lbs.equalsIgnoreCase(App.sharePrefrences.getStringPref(App.Sh_Weight_Measure))){
                    spn_weight.setSelection(1);
                }

            }else {
                spn_weight.setSelection(0);
            }


            if( App.sharePrefrences.getStringPref(App.Noti_wakeuptime) != null
                    && App.sharePrefrences.getStringPref(App.Noti_wakeuptime).length() > 0
                    && App.sharePrefrences.getStringPref(App.Noti_Sleeptime) != null
                    && App.sharePrefrences.getStringPref(App.Noti_Sleeptime).length() > 0) {

                /*--- converting milisec to time ---*/
                tv_wakeuptime.setText(""+App.convertTimeInMiliSeconds(Long.parseLong(App.sharePrefrences.getStringPref(App.Noti_wakeuptime))));
                tv_sleeptime.setText(""+App.convertTimeInMiliSeconds(Long.parseLong(App.sharePrefrences.getStringPref(App.Noti_Sleeptime))));

            }else {
                tv_wakeuptime.setText(""+App.Defualt_WakeUpTime);
                tv_sleeptime.setText(""+App.Defualt_SleepTime);
            }


            if(App.sharePrefrences.getStringPref(App.Sh_Min_Selected) != null
                    && App.sharePrefrences.getStringPref(App.Sh_Min_Selected).length() > 0) {
                spn_alram.setSelection(Integer.parseInt(App.sharePrefrences.getStringPref(App.Sh_Min_Selected)));
            }else {
                spn_alram.setSelection(0);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void clickEvent(){
        try{

            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long Noti_wakeuptime = App.parseTodaysDate(tv_wakeuptime.getText().toString().trim());
                    long Noti_Sleeptime = App.parseTodaysDate(tv_sleeptime.getText().toString().trim());

                    App.showLog(TAG, "Weight: "+ et_weight.getText().toString().trim());
                    App.showLog(TAG, "Weight_Measure: "+ spn_weight.getSelectedItem());
                    App.showLog(TAG, "Noti_wakeuptime: "+ Noti_wakeuptime );
                    App.showLog(TAG, "Noti_Sleeptime: "+Noti_Sleeptime );
                    App.showLog(TAG, "Noti_min: "+spn_alram.getSelectedItem());

                    /* -----  Save to SharedPreference  -----*/
                    App.sharePrefrences.setPref(App.Sh_Weight, et_weight.getText().toString().trim());
                    App.sharePrefrences.setPref(App.Sh_Weight_Measure, ""+spn_weight.getSelectedItem());
                    App.sharePrefrences.setPref(App.Noti_wakeuptime, ""+Noti_wakeuptime);
                    App.sharePrefrences.setPref(App.Noti_Sleeptime, ""+Noti_Sleeptime);
                    App.sharePrefrences.setPref(App.Sh_Min_Selected, ""+spn_alram.getSelectedItemPosition());
                    //App.sharePrefrences.setPref(App.Sh_Login, App.Sh_True);


                    if(App.sharePrefrences.getStringPref(App.Sh_Min_Selected) != null
                            && App.sharePrefrences.getStringPref(App.Sh_Min_Selected).length() > 0
                            && Noti_wakeuptime != 0
                            && Noti_Sleeptime != 0){


                        App.showLog(TAG, "Notification Set");
                        handleNotification(String.valueOf(spn_alram.getSelectedItem()), Noti_wakeuptime, Noti_Sleeptime);

                    }else {
                        Toast.makeText(ActHome.this, "Notification Not Set", Toast.LENGTH_LONG).show();
                    }


                    if(et_weight.getText().toString().trim().length() > 1) {
                        //startActivity(new Intent(ActHome.this, ActDrinkTrack.class));
                        startActivity(new Intent(ActHome.this, ActNewDrinkWater.class));
                    }else {
                        Toast.makeText(ActHome.this, "Enter Valid Weight", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            btn_next_old.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long Noti_wakeuptime = App.parseTodaysDate(tv_wakeuptime.getText().toString().trim());
                    long Noti_Sleeptime = App.parseTodaysDate(tv_sleeptime.getText().toString().trim());

                    App.showLog(TAG, "Weight: "+ et_weight.getText().toString().trim());
                    App.showLog(TAG, "Weight_Measure: "+ spn_weight.getSelectedItem());
                    App.showLog(TAG, "Noti_wakeuptime: "+ Noti_wakeuptime );
                    App.showLog(TAG, "Noti_Sleeptime: "+Noti_Sleeptime );
                    App.showLog(TAG, "Noti_min: "+spn_alram.getSelectedItem());

                    /* -----  Save to SharedPreference  -----*/
                    App.sharePrefrences.setPref(App.Sh_Weight, et_weight.getText().toString().trim());
                    App.sharePrefrences.setPref(App.Sh_Weight_Measure, ""+spn_weight.getSelectedItem());
                    App.sharePrefrences.setPref(App.Noti_wakeuptime, ""+Noti_wakeuptime);
                    App.sharePrefrences.setPref(App.Noti_Sleeptime, ""+Noti_Sleeptime);
                    App.sharePrefrences.setPref(App.Sh_Min_Selected, ""+spn_alram.getSelectedItemPosition());
                    //App.sharePrefrences.setPref(App.Sh_Login, App.Sh_True);


                    if(App.sharePrefrences.getStringPref(App.Sh_Min_Selected) != null
                            && App.sharePrefrences.getStringPref(App.Sh_Min_Selected).length() > 0
                            && Noti_wakeuptime != 0
                            && Noti_Sleeptime != 0){


                        App.showLog(TAG, "Notification Set");
                        handleNotification(String.valueOf(spn_alram.getSelectedItem()), Noti_wakeuptime, Noti_Sleeptime);

                    }else {
                        Toast.makeText(ActHome.this, "Notification Not Set", Toast.LENGTH_LONG).show();
                    }


                    if(et_weight.getText().toString().trim().length() > 1) {
                        startActivity(new Intent(ActHome.this, ActDrinkTrack.class));
                        //startActivity(new Intent(ActHome.this, ActNewDrinkWater.class));
                    }else {
                        Toast.makeText(ActHome.this, "Enter Valid Weight", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            tv_wakeuptime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    from = "tv_wakeuptime";
                    showRimnderDialog(from, tv_wakeuptime.getText().toString().trim());
                }
            });


            tv_sleeptime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    from = "tv_sleeptime";
                    showRimnderDialog(from, tv_sleeptime.getText().toString().trim());
                }
            });


            spn_alram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(spn_alram.getSelectedItemPosition() > 0){
                        spn_alram.setSelection(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spn_weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spn_weight.setSelection(position);

                    if(et_weight.getText().toString().trim().length() > 0) {
                        if (spn_weight.getSelectedItem().equals(App.Weight_lbs)) {
                            int weight_int = (int) (Integer.parseInt(et_weight.getText().toString().trim()) / (2.20462));
                            tv_weight_conversion.setText("( "+weight_int + " kg )");
                        } else {
                            tv_weight_conversion.setText("");
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            et_weight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (spn_weight.getSelectedItem().equals(App.Weight_lbs)) {
                        if(et_weight.getText().toString().trim().length() > 0) {
                            int weight_int = (int) (Integer.parseInt(et_weight.getText().toString().trim()) / (2.20462));
                            tv_weight_conversion.setText("( "+weight_int + " kg )");
                        }else {
                            tv_weight_conversion.setText("");
                        }
                    }else {
                        tv_weight_conversion.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            onlinePayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActHome.this, ActOnlinePayment.class));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void showRimnderDialog(final String from, String get_txtview_time) {
        time = "";
        final View dialogView = View.inflate(ActHome.this, R.layout.inflate_time_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);

        Button btnOkDate = (Button) dialogView.findViewById(R.id.date_time_set);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

        String getTimeFromTv[] = get_txtview_time.split(":");

        App.showLog(TAG, get_txtview_time);
        App.showLog(TAG, getTimeFromTv[0]);
        App.showLog(TAG, getTimeFromTv[1]);

        timePicker.setCurrentHour(Integer.valueOf(getTimeFromTv[0]));
        timePicker.setCurrentMinute(Integer.valueOf(getTimeFromTv[1]));


        f1 = new DecimalFormat("00");

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time = "" + f1.format(hourOfDay) + ":" + f1.format(minute);
            }
        });


        btnOkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time.length() != 0) {
                    App.showLog(TAG, "time: "+time);

                    if(from.equalsIgnoreCase("tv_wakeuptime")){
                        tv_wakeuptime.setText(""+time);
                    }else if(from.equalsIgnoreCase("tv_sleeptime")){
                        tv_sleeptime.setText(""+time);
                    }

                }
                alertDialog.dismiss();

            }
        });


        alertDialog.setView(dialogView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();


    }


    private void handleNotification(String alarm_time, long wakeupTime, long sleepTime) {

        int i = Integer.parseInt(alarm_time);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);



         /*----------- get current time in mili-sec ----------*/
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String currentDateandTime = sdf.format(new Date());
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(currentDateandTime);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        App.showLog(TAG, "alarm_time: "+alarm_time);
        App.showLog(TAG, "wakeupTime: "+wakeupTime);
        App.showLog(TAG, "sleepTime: "+sleepTime);
        App.showLog(TAG, "timeInMilliseconds: "+timeInMilliseconds);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMilliseconds, 1000 * 60 * i, pendingIntent);



    }


}
