package com.test.drinkwaterdemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.test.drinkwaterdemo.R;

public class ActOptionMenu extends AppCompatActivity {

    TextView tv_drinkWater, tv_onlinePayment, tv_newDrinkWater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_option_menu);

        initialize();
        clickEvenet();

    }

    public void initialize() {
        try {
            tv_drinkWater = (TextView)findViewById(R.id.tv_drinkWater);
            tv_onlinePayment = (TextView)findViewById(R.id.tv_onlinePayment);
            tv_newDrinkWater = (TextView)findViewById(R.id.tv_newDrinkWater);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickEvenet() {
        try {

            tv_drinkWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActOptionMenu.this, ActHome.class));
                }
            });

            tv_onlinePayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActOptionMenu.this, ActOnlinePayment.class));
                }
            });

            /*tv_newDrinkWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActOptionMenu.this, ActNewDrinkWater.class));
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
