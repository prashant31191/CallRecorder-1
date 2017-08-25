package com.test.drinkwaterdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;
import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.common.App;
import com.test.drinkwaterdemo.common.DatabaseHelper;
import com.test.drinkwaterdemo.model.ModelDrinkList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class ActNewDrinkWater extends AppCompatActivity {

    String TAG = "ActNewDrinkWater";
    DatabaseHelper dbHelper;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    DrinkWaterAdapter drinkWaterAdapter;
    LinearLayout ll_glass, ll_bottleSmall, ll_bottleLarge;
    public ArrayList<ModelDrinkList> arrayDrinkList = new ArrayList<ModelDrinkList>();
    CircularProgressBar progressBar;
    TextView tv_intakeWater, tv_goalIntakeWater, tv_progress, tv_todayDrinkTotal;
    RelativeLayout rl_viewHistroy, rl_todayList;
    private Paint p = new Paint();
    int GoalToDrink; //in ml



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_new_drink_water);

        requestPermissions();
        initialize();
        checkSharedPreference();
        clickEvenet();
        getDataFromDB();

    }

    public void requestPermissions() {
        try {
            ActivityCompat.requestPermissions(ActNewDrinkWater.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            //new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        try {

            dbHelper = new DatabaseHelper(this);

            progressBar = (CircularProgressBar) findViewById(R.id.progressBar);
            ll_glass = (LinearLayout) findViewById(R.id.ll_glass);
            ll_bottleSmall = (LinearLayout) findViewById(R.id.ll_bottleSmall);
            ll_bottleLarge = (LinearLayout) findViewById(R.id.ll_bottleLarge);
            rl_viewHistroy = (RelativeLayout) findViewById(R.id.rl_viewHistroy);
            rl_todayList = (RelativeLayout) findViewById(R.id.rl_todayList);
            tv_intakeWater = (TextView) findViewById(R.id.tv_intakeWater);
            tv_goalIntakeWater = (TextView) findViewById(R.id.tv_goalIntakeWater);
            tv_todayDrinkTotal = (TextView) findViewById(R.id.tv_todayDrinkTotal);
            tv_progress = (TextView) findViewById(R.id.tv_progress);

            nestedScrollView = (NestedScrollView)findViewById(R.id.nestedScrollView);
            nestedScrollView.getParent().requestChildFocus(nestedScrollView, nestedScrollView);

            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActNewDrinkWater.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);


            /*---- swipe remove item from listview ------*/
            //initSwipe();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkSharedPreference() {
        try {
            if (App.sharePrefrences.getStringPref(App.Sh_Weight) != null
                    && App.sharePrefrences.getStringPref(App.Sh_Weight).length() > 0) {

                /*----  weight->only digit && weight_measure->kg/lbs -------*/
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

                    GoalToDrink = ((weight_int * 10) / 30) * 100;  // this will give with last 2 digit as 0
                    App.showLog(TAG, "GoalToDrink: " + GoalToDrink);

                    /*---- convert ml to litres  in float-------*/
                    float GoalToDrinkFloat =  (float)GoalToDrink  / 1000;
                    tv_goalIntakeWater.setText(GoalToDrinkFloat + " litres");
                }


            } else {
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickEvenet(){
        try{

            ll_glass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelDrinkList model = new ModelDrinkList();
                    model.setDrink_list_weight(App.sharePrefrences.getStringPref(App.Sh_Weight) + " " + App.sharePrefrences.getStringPref(App.Sh_Weight_Measure));
                    model.setDrink_list_time(App.timeCurrentHHMM());
                    model.setDrink_list_date(App.dateCurrentYYYYMMDD());
                    model.setDrink_list_img(App.Sh_Glass250_Img);
                    model.setDrink_list_quantityml(App.Sh_Glass250);
                    arrayDrinkList.add(model);

                    dbHelper.insertDrinkList(model);
                    getDataFromDB();
                }
            });


            ll_bottleSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelDrinkList model = new ModelDrinkList();
                    model.setDrink_list_weight(App.sharePrefrences.getStringPref(App.Sh_Weight) + " " + App.sharePrefrences.getStringPref(App.Sh_Weight_Measure));
                    model.setDrink_list_time(App.timeCurrentHHMM());
                    model.setDrink_list_date(App.dateCurrentYYYYMMDD());
                    model.setDrink_list_img(App.Sh_Glass550_Img);
                    model.setDrink_list_quantityml(App.Sh_Glass550);
                    arrayDrinkList.add(model);

                    dbHelper.insertDrinkList(model);
                    getDataFromDB();
                }
            });


            ll_bottleLarge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelDrinkList model = new ModelDrinkList();
                    model.setDrink_list_weight(App.sharePrefrences.getStringPref(App.Sh_Weight) + " " + App.sharePrefrences.getStringPref(App.Sh_Weight_Measure));
                    model.setDrink_list_time(App.timeCurrentHHMM());
                    model.setDrink_list_date(App.dateCurrentYYYYMMDD());
                    model.setDrink_list_img(App.Sh_Glass750_Img_New);
                    model.setDrink_list_quantityml(App.Sh_Glass750_New);
                    arrayDrinkList.add(model);

                    dbHelper.insertDrinkList(model);
                    getDataFromDB();
                }
            });

            rl_viewHistroy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActNewDrinkWater.this, ActHistoryNew.class);
                    startActivity(i1);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getDataFromDB(){
        try {
            ArrayList<ModelDrinkList> drinkData = dbHelper.getTodaysDrinkList(App.dateCurrentYYYYMMDD());

            if (drinkData != null && drinkData.size() >= 0) {
                int todaysDrinkWater = 0;
                int progressInPercentage = 0;

                for (int i = 0; i < drinkData.size(); i++) {
                    todaysDrinkWater += Integer.parseInt(drinkData.get(i).getDrink_list_quantityml());
                }

                tv_intakeWater.setText(todaysDrinkWater+" ml");

                if(todaysDrinkWater == 0){
                    rl_todayList.setVisibility(View.GONE);
                }else {
                    rl_todayList.setVisibility(View.VISIBLE);
                }

                /*---  convert to floag---*/
                float TodayDrinkFloat = (float)todaysDrinkWater/ 1000;
                tv_todayDrinkTotal.setText(""+TodayDrinkFloat);

                /*----  Get In Percentage ---*/
                progressInPercentage = (todaysDrinkWater*100)/GoalToDrink;
                //progressBar.setProgress(  progressInPercentage);
                tv_progress.setText( ""+progressInPercentage );

                arcProgressBarAnimation(progressInPercentage);

                if (todaysDrinkWater > GoalToDrink) {
                    Toast.makeText(this, "You have reached daily drink target.", Toast.LENGTH_SHORT).show();
                }

            }


            SetAdapter(drinkData);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void arcProgressBarAnimation(int progressInPercentage) {
        try {

            final int intArcProgress = progressInPercentage;
            final int[] count = {(int) progressBar.getProgress()};

            final Handler handler = new Handler();

            if (progressBar.getProgress() >= intArcProgress) {

                final Runnable runnable = new Runnable() {
                    public void run() {
                        if (count[0] >= intArcProgress  ) {
                            if(count[0] <= 0){
                                progressBar.setProgress(0);
                            }else {
                                progressBar.setProgress(count[0]);
                                handler.postDelayed(this, 30);
                                count[0]--;
                            }
                        }
                    }
                };

                handler.post(runnable);
            } else if(progressBar.getProgress() <= intArcProgress && progressBar.getProgress() <= 100  ) {

                final Runnable runnable = new Runnable() {
                    public void run() {
                        if (count[0] <= intArcProgress ) {
                            if(count[0] >= 100 ){
                                progressBar.setProgress(100);
                            }else {
                                progressBar.setProgress(count[0]);
                                handler.postDelayed(this, 30);
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

    public void SetAdapter(ArrayList<ModelDrinkList> drinkData){
        try {
            drinkWaterAdapter = new DrinkWaterAdapter(ActNewDrinkWater.this, drinkData);
            recyclerView.setAdapter(drinkWaterAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public class DrinkWaterAdapter extends RecyclerView.Adapter<DrinkWaterAdapter.VersionViewHolder> {
        ArrayList<ModelDrinkList> mArrModelDrinkList;
        Context mContext;
        private ViewBinderHelper binderHelper = new ViewBinderHelper();


        public DrinkWaterAdapter(Context context, ArrayList<ModelDrinkList> arrayListFollowers) {
            mArrModelDrinkList = arrayListFollowers;
            mContext = context;

            // uncomment if you want to open only one row at a time
            binderHelper.setOpenOnlyOne(true);
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lay_drinkwater_row, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                ModelDrinkList model = mArrModelDrinkList.get(i);


                if (mArrModelDrinkList != null && 0 <= i && i < mArrModelDrinkList.size()) {
                    final String data = "" + i;// = mArrListMyJobListModel.get(i).id;

                    // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
                    // put an unique string id as value, can be any string which uniquely define the data
                    binderHelper.bind(versionViewHolder.swipeLayout, data);

                    // Bind your data here
                    //holder.bind(data);
                }

                versionViewHolder.glass_quantity.setText(model.getDrink_list_quantityml().toString()+" ml");
                versionViewHolder.glass_time.setText(""+ App.convert24hrto12hr(model.getDrink_list_time().toString()) );

                String mDrawableName = model.getDrink_list_img().toString();
                int resId = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
                Picasso.with(getApplicationContext())
                        .load(resId)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.img_custom_glass)
                        .into(versionViewHolder.glass_img);


                /*----- to remove last line-view -----*/
                int sizeOfArr = mArrModelDrinkList.size()-1;
                if( i == sizeOfArr ){
                    versionViewHolder.viewLast.setVisibility(View.GONE);
                }

                versionViewHolder.deleteLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.delete_id_DrinkList(mArrModelDrinkList.get(i).getId());
                        getDataFromDB();


                        mArrModelDrinkList.remove(i);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i, mArrModelDrinkList.size());
                    }
                });

                versionViewHolder.glass_quantity.setTypeface(App.getFont_Regular());
                versionViewHolder.glass_time.setTypeface(App.getFont_Regular());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrModelDrinkList.size();
        }

        /**
         * Only if you need to restore open/close state when the orientation is changed.
         * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
         */
        public void saveStates(Bundle outState) {
            binderHelper.saveStates(outState);
        }

        /**
         * Only if you need to restore open/close state when the orientation is changed.
         * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
         */
        public void restoreStates(Bundle inState) {
            binderHelper.restoreStates(inState);
        }

        public void removeItem(int position) {

            dbHelper.delete_id_DrinkList(mArrModelDrinkList.get(position).getId());
            getDataFromDB();


            mArrModelDrinkList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mArrModelDrinkList.size());
        }

        class VersionViewHolder extends RecyclerView.ViewHolder {
            SwipeRevealLayout swipeLayout;
            ImageView glass_img;
            TextView glass_quantity, glass_time;
            View viewLast, deleteLayout;

            public VersionViewHolder(View itemView) {
                super(itemView);

                swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipeLayout);
                deleteLayout = itemView.findViewById(R.id.delete_layout);
                glass_quantity = (TextView) itemView.findViewById(R.id.glass_quantity);
                glass_time = (TextView) itemView.findViewById(R.id.glass_time);
                glass_img = (ImageView) itemView.findViewById(R.id.glass_img);
                viewLast = (View) itemView.findViewById(R.id.viewLast);
            }

        }
    }

    public void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    drinkWaterAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX < 0) {

                        /*p.setColor(Color.RED);
                        c.drawRect(background,p);*/

                        int intValues = (int) getResources().getDimension(R.dimen.size_20);

                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        p.setColor(Color.RED);
                        p.setTextSize(intValues);
                        c.drawText("will be removed", background.centerX(),  background.centerY() , p);

                        /*p.setColor(Color.RED);
                        c.drawRect(background,p);*/

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    
}
