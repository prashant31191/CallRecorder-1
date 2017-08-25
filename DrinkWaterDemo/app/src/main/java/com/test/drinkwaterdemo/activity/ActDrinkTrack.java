package com.test.drinkwaterdemo.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.squareup.picasso.Picasso;
import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.common.App;
import com.test.drinkwaterdemo.common.DatabaseHelper;
import com.test.drinkwaterdemo.model.ModelDrinkList;

import java.util.ArrayList;

public class ActDrinkTrack extends AppCompatActivity {

    String TAG = "ActDrinkTrack";

    ProgressBar progressBar;

    ArcProgress arc_progress;
    TextView tv_water_consume, tv_water_target;

    LinearLayout ll_glass100, ll_glass150, ll_glass200;
    LinearLayout ll_glass300, ll_glass500, ll_glass750;

    ImageView img_custome_glass, img_add_glass;
    TextView tv_progress, tv_custome_glass;
    LinearLayout ll_lay3, ll_custome_glass;
    ImageView img_add_icon, img_cross, img_profile, img_history;

    int ttl_drink;
    int progressStatus = 0;

    RecyclerView recyclerView;
    BottleAdapter bottleAdapter;
    public ArrayList<ModelDrinkList> arrayDrinkList = new ArrayList<ModelDrinkList>();
    int customeGlassValue;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_drink_track);

        dbHelper = new DatabaseHelper(this);

        requestPermissions();
        initialize();
        checkSharedPreference();
        clickEvent();
        getDataFromDB();

    }

    public void requestPermissions() {
        try {
            ActivityCompat.requestPermissions(ActDrinkTrack.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            //new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*----- Permission granted -----*/
                    //Toast.makeText(ActDrinkTrack.this, "Congrets", Toast.LENGTH_SHORT).show();
                } else {
                    /*----- Permission denied -----*/
                    Toast.makeText(ActDrinkTrack.this, "You wont able to use the app", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActDrinkTrack.this, "Go to setting and give permission to use the app", Toast.LENGTH_LONG).show();
                        }
                    }, 2000);

                }
                return;
            }
        }
    }

    public void initialize() {
        try {

            ll_glass100 = (LinearLayout) findViewById(R.id.ll_glass100);
            ll_glass150 = (LinearLayout) findViewById(R.id.ll_glass150);
            ll_glass200 = (LinearLayout) findViewById(R.id.ll_glass200);
            ll_glass300 = (LinearLayout) findViewById(R.id.ll_glass300);
            ll_glass500 = (LinearLayout) findViewById(R.id.ll_glass500);
            ll_glass750 = (LinearLayout) findViewById(R.id.ll_glass750);


            img_add_glass = (ImageView) findViewById(R.id.img_add_glass);
            img_custome_glass = (ImageView) findViewById(R.id.img_custome_glass);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            tv_custome_glass = (TextView) findViewById(R.id.tv_custome_glass);
            tv_progress = (TextView) findViewById(R.id.tv_progress);
            tv_water_consume = (TextView) findViewById(R.id.tv_water_consume);
            tv_water_target = (TextView) findViewById(R.id.tv_water_target);
            arc_progress = (ArcProgress) findViewById(R.id.arc_progress);
            progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.water), android.graphics.PorterDuff.Mode.SRC_IN);

            ll_custome_glass = (LinearLayout) findViewById(R.id.ll_custome_glass);
            ll_lay3 = (LinearLayout) findViewById(R.id.ll_lay3);


            img_add_icon = (ImageView) findViewById(R.id.img_add_icon);
            img_cross = (ImageView) findViewById(R.id.img_cross);
            img_profile = (ImageView) findViewById(R.id.img_profile);
            img_history = (ImageView) findViewById(R.id.img_history);


            /*-------  show item in single line ------- */
            /*recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActDrinkTrack.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);*/


            /*-------  show item in grid view ------- */
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            int numberOfColumns = 3;
            recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

            //initSwipe();

            tv_custome_glass.setTypeface(App.getFont_Regular());
            tv_progress.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkSharedPreference() {
        try {
            if (App.sharePrefrences.getStringPref(App.Sh_Weight) != null
                    && App.sharePrefrences.getStringPref(App.Sh_Weight).length() > 0) {

                String weight = App.sharePrefrences.getStringPref(App.Sh_Weight);
                String weight_measure = App.sharePrefrences.getStringPref(App.Sh_Weight_Measure);
                int weight_int = Integer.parseInt(weight);

                App.showLog(TAG, "weight: " + weight);
                App.showLog(TAG, "weight_measure: " + weight_measure);

                if (weight_measure.equalsIgnoreCase(App.Weight_lbs)) {
                    weight_int = (int) (weight_int / (2.20462));
                }


                App.showLog(TAG, "weight_int after conversion: " + weight_int + " " + weight_measure);

                if (weight != null && weight.length() > 0) {

                    ttl_drink = ((weight_int * 10) / 30) * 100;  // this will give with last 2 digit as 0

                    progressBar.setMax(ttl_drink);
                    tv_progress.setText("Daily Drink Target: 0/" + ttl_drink + " ml");
                }


            } else {
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickEvent() {
        try {

            img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //App.sharePrefrences.setPref(App.Sh_Login, App.Sh_Flase);
                    startActivity(new Intent(ActDrinkTrack.this, ActHome.class));
                }
            });

            img_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(ActDrinkTrack.this, ActHistory.class));
                }
            });

            ll_glass100.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(App.Sh_Glass100, App.Sh_Glass100_Img);
                }
            });

            ll_glass150.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(App.Sh_Glass150, App.Sh_Glass150_Img);
                }
            });

            ll_glass200.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(App.Sh_Glass200, App.Sh_Glass200_Img);
                }
            });

            ll_glass300.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(App.Sh_Glass300, App.Sh_Glass300_Img);
                }
            });

            ll_glass500.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(App.Sh_Glass500, App.Sh_Glass500_Img);
                }
            });

            ll_glass750.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(App.Sh_Glass750, App.Sh_Glass750_Img);
                }
            });

            img_custome_glass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModelValues(tv_custome_glass.getText().toString().trim(), App.Sh_Glass_Custome_Img);
                }
            });

            img_add_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_lay3.setVisibility(View.VISIBLE);
                    img_cross.setVisibility(View.VISIBLE);

                    v.setVisibility(View.INVISIBLE);
                    img_profile.setVisibility(View.GONE);
                    img_history.setVisibility(View.GONE);

                    if (App.sharePrefrences.getStringPref(App.Sh_Glass_Custome_On).equalsIgnoreCase(App.Sh_True)) {
                        ll_custome_glass.setVisibility(View.VISIBLE);

                        tv_custome_glass.setText(App.sharePrefrences.getStringPref(App.Sh_Glass_Custome));
                    }

                }
            });

            img_cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_lay3.setVisibility(View.GONE);
                    v.setVisibility(View.GONE);

                    img_add_icon.setVisibility(View.VISIBLE);
                    img_profile.setVisibility(View.VISIBLE);
                    img_history.setVisibility(View.VISIBLE);

                }
            });

            img_add_glass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCutomeGlassAddDialog();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setModelValues(String glassSize, String glassImg) {
        try {
            /*progressStatus += Integer.parseInt(glassSize);
            progressBar.setProgress(progressStatus);
            tv_progress.setText("Daily Drink Target: " + progressStatus + "/" + ttl_drink + " ml");*/

            ll_lay3.setVisibility(View.GONE);
            img_cross.setVisibility(View.GONE);
            img_add_icon.setVisibility(View.VISIBLE);
            img_profile.setVisibility(View.VISIBLE);
            img_history.setVisibility(View.VISIBLE);

            ModelDrinkList model = new ModelDrinkList();
            model.setDrink_list_weight(App.sharePrefrences.getStringPref(App.Sh_Weight) + " " + App.sharePrefrences.getStringPref(App.Sh_Weight_Measure));
            model.setDrink_list_time(App.timeCurrentHHMM());
            model.setDrink_list_date(App.dateCurrentYYYYMMDD());
            model.setDrink_list_img(glassImg);
            model.setDrink_list_quantityml(glassSize);
            arrayDrinkList.add(model);

            dbHelper.insertDrinkList(model);

            getDataFromDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getDataFromDB() {
        try {
            dbHelper.open();
            ArrayList<ModelDrinkList> drinkData = dbHelper.getTodaysDrinkList(App.dateCurrentYYYYMMDD());
            dbHelper.close();

            if (drinkData != null && drinkData.size() >= 0) {

                progressStatus = 0;
                for (int i = 0; i < drinkData.size(); i++) {
                    progressStatus += Integer.parseInt(drinkData.get(i).getDrink_list_quantityml());
                }

                progressBar.setProgress(progressStatus);
                tv_progress.setText("Daily Drink Target: " + progressStatus + "/" + ttl_drink + " ml");

                arcProgressBarAnimation(progressStatus, ttl_drink);

                tv_water_consume.setText(progressStatus + " ml");
                tv_water_target.setText(ttl_drink + " ml");

                if (progressStatus > ttl_drink) {
                    showSnackBar("You have reached daily drink target.");
                }

                SetAdapter(drinkData);
            } /*else {
                Toast.makeText(ActDrinkTrack.this, "No Data Found In DB", Toast.LENGTH_SHORT).show();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void arcProgressBarAnimation(int progressStatus, int ttl_drink) {
        try {

            final int intArcProgress = (100 * progressStatus) / ttl_drink;
            final int[] count = {arc_progress.getProgress()};
            App.showLog(TAG, "intArcProgress " + intArcProgress);
            App.showLog(TAG, "arc_progress.getProgress " + arc_progress.getProgress());

            final Handler handler = new Handler();


            if (arc_progress.getProgress() >= intArcProgress) {

                final Runnable runnable = new Runnable() {
                    public void run() {
                        if (count[0] >= intArcProgress  ) {
                            if(count[0] <= 0){
                                arc_progress.setProgress(0);
                            }else {
                                arc_progress.setProgress(count[0]);
                                handler.postDelayed(this, 5);
                                count[0]--;
                            }
                        }
                    }
                };

                handler.post(runnable);
            } else if(arc_progress.getProgress() <= intArcProgress && arc_progress.getProgress() <= 100  ) {

                final Runnable runnable = new Runnable() {
                    public void run() {
                        if (count[0] <= intArcProgress ) {
                            if(count[0] >= 100 ){
                                arc_progress.setProgress(100);
                            }else {
                                arc_progress.setProgress(count[0]);
                                handler.postDelayed(this, 5);
                                count[0]++;
                            }
                        }
                    }
                };

                handler.post(runnable);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetAdapter(ArrayList<ModelDrinkList> drinkData) {
        bottleAdapter = new BottleAdapter(ActDrinkTrack.this, drinkData);
        recyclerView.setAdapter(bottleAdapter);
        bottleAdapter.notifyDataSetChanged();
    }


    public class BottleAdapter extends RecyclerView.Adapter<BottleAdapter.VersionViewHolder> {
        ArrayList<ModelDrinkList> mArrListModelBottle;
        Context mContext;


        public BottleAdapter(Context context, ArrayList<ModelDrinkList> arrayListBottle) {
            mArrListModelBottle = arrayListBottle;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_bottle_lay, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                final ModelDrinkList ModelBottle = mArrListModelBottle.get(i);

                versionViewHolder.glass_quantity.setText(ModelBottle.getDrink_list_quantityml() + " ml");
                versionViewHolder.glass_time.setText("Time " + ModelBottle.getDrink_list_time());

                versionViewHolder.glass_quantity.setTypeface(App.getFont_Regular());
                versionViewHolder.glass_time.setTypeface(App.getFont_Regular());


                String mDrawableName = ModelBottle.getDrink_list_img();
                int resId = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());

                Picasso.with(getApplicationContext())
                        .load(resId)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.img_custom_glass)
                        .into(versionViewHolder.glass_img);

                /*.load(R.drawable.drink_water)*/

                versionViewHolder.cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showSnackBar(ModelBottle.getBottle_type());
                        showCustomeDialog(ModelBottle, i);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrListModelBottle.size();
        }


        public void removeItem(int position, String glassID, String getGlassQuantity) {

            try {

                dbHelper.delete_id_DrinkList(glassID);

                getDataFromDB();

                mArrListModelBottle.remove(position);
                notifyItemRemoved(position);
                bottleAdapter.notifyDataSetChanged();


                App.showLog(TAG, "remove position " + position);
                App.showLog(TAG, "remove glassID " + glassID);
                //App.showLog(TAG, "remove bottle_value " + bottle_value);
                App.showLog(TAG, "remove progressStatus " + progressStatus);
                App.showLog(TAG, "remove arrayDrinkList.size " + arrayDrinkList.size());

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        public void updateItem(int position, String glassID, String glassOldQuantity, String glassNewQuantity) {

            try {

                dbHelper.updateDrinkList(glassID, glassNewQuantity);


                getDataFromDB();

                mArrListModelBottle.remove(position);
                notifyItemRemoved(position);
                bottleAdapter.notifyDataSetChanged();


                App.showLog(TAG, "remove position " + position);
                App.showLog(TAG, "remove glassID " + glassID);
                //App.showLog(TAG, "remove bottle_old_value " + bottle_old_value);
                //App.showLog(TAG, "remove bottle_new_value " + bottle_new_value);
                App.showLog(TAG, "remove progressStatus " + progressStatus);
                App.showLog(TAG, "remove arrayDrinkList.size " + arrayDrinkList.size());

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        class VersionViewHolder extends RecyclerView.ViewHolder {
            CardView cardItemLayout;
            TextView glass_quantity, glass_time;
            ImageView glass_img;


            public VersionViewHolder(View itemView) {
                super(itemView);

                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
                glass_quantity = (TextView) itemView.findViewById(R.id.glass_quantity);
                glass_time = (TextView) itemView.findViewById(R.id.glass_time);
                glass_img = (ImageView) itemView.findViewById(R.id.glass_img);

            }

        }
    }

    public void showCustomeDialog(final ModelDrinkList model, final int position) {
        try {
            final Dialog dialog = new Dialog(ActDrinkTrack.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.inflate_popup_delete);


            final EditText et_set_glass = (EditText) dialog.findViewById(R.id.et_set_glass);
            Button btn_update_glass = (Button) dialog.findViewById(R.id.btn_update_glass);
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
            TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
            TextView tvOK = (TextView) dialog.findViewById(R.id.tvOk);


            et_set_glass.setText(model.getDrink_list_quantityml());
            tvMessage.setText("Do you want to reduce " + model.getDrink_list_quantityml() + " ml");
            tvCancel.setText("No");
            tvOK.setText("Yes");

            /*--- edittext cursor position ---*/
            int getposs = et_set_glass.length();
            Editable etext = et_set_glass.getText();
            Selection.setSelection(etext, getposs);

            tvMessage.setTypeface(App.getFont_Regular());
            tvCancel.setTypeface(App.getFont_Regular());
            tvOK.setTypeface(App.getFont_Regular());

            dialog.show();

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            tvOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.showLog(TAG, "remove position on button: " + position);
                    App.showLog(TAG, "remove quantity: " + model.getDrink_list_quantityml());

                    bottleAdapter.removeItem(position, model.getId(), model.getDrink_list_quantityml());
                    dialog.dismiss();
                }
            });

            btn_update_glass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String glassOldQuantity = model.getDrink_list_quantityml();
                    String glassNewQuantity = et_set_glass.getText().toString().trim();
                    /*----- replace old to new in MODEL ------*/
                    model.setDrink_list_quantityml(et_set_glass.getText().toString().trim());

                    bottleAdapter.updateItem(position, model.getId(), glassOldQuantity, glassNewQuantity);

                    //dbHelper.updateDrinkList(model.getId() , et_set_glass.getText().toString().trim());

                    //model.setDrink_list_quantityml(et_set_glass.getText().toString().trim());
                    //bottleAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCutomeGlassAddDialog() {
        try {
            final Dialog dialog = new Dialog(ActDrinkTrack.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.inflate_popup_inputglass);


            final EditText etMessage = (EditText) dialog.findViewById(R.id.etMessage);
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
            TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
            TextView tvOK = (TextView) dialog.findViewById(R.id.tvOk);


            if (ll_custome_glass.getVisibility() == View.GONE) {
                etMessage.setText("");
            } else {
                etMessage.setText(tv_custome_glass.getText().toString().trim());

                /*--- edittext cursor position ---*/
                int getposs = etMessage.length();
                Editable etext = etMessage.getText();
                Selection.setSelection(etext, getposs);
            }

            etMessage.setTypeface(App.getFont_Regular());
            tvMessage.setTypeface(App.getFont_Regular());
            tvCancel.setTypeface(App.getFont_Regular());
            tvOK.setTypeface(App.getFont_Regular());

            dialog.show();

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            tvOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    customeGlassValue = Integer.parseInt(etMessage.getText().toString().trim());

                    if (customeGlassValue > 0 && customeGlassValue < 5000) {
                        ll_custome_glass.setVisibility(View.VISIBLE);
                        tv_custome_glass.setText(etMessage.getText().toString().trim());

                        App.sharePrefrences.setPref(App.Sh_Glass_Custome_On, App.Sh_True);
                        App.sharePrefrences.setPref(App.Sh_Glass_Custome, tv_custome_glass.getText().toString().trim());

                    } else {
                        showSnackBar("Value not valid");
                    }


                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSnackBar(String msg) {
        try {
            /*Snackbar snackbar = Snackbar.make(getCurrentFocus(), msg, Snackbar.LENGTH_LONG);
            snackbar.show();*/
            Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
