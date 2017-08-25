package com.test.drinkwaterdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.common.App;
import com.test.drinkwaterdemo.common.DatabaseHelper;
import com.test.drinkwaterdemo.model.Group;
import com.test.drinkwaterdemo.model.ModelDrinkList;


import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ActHistoryNew extends AppCompatActivity {

    String TAG = "ActHistory";
    DatabaseHelper dbHelper;
    //ArrayList<Group> arrayListAllData = new ArrayList<>();

    TestDateBaseAdapter mAdapter;
    ArrayList<ModelDrinkList> arrayListModelDrinkList;

    StickyListHeadersListView stickyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_history_new);

        getDataFromDB();
        initialize();


    }

    public void getDataFromDB() {
        try {
            dbHelper = new DatabaseHelper(this);
            dbHelper.open();

             arrayListModelDrinkList = dbHelper.getAllDataSortByDate();

           /* ArrayList<ModelDrinkList> dateListFromDB = dbHelper.getDateListFromDB();
            arrayListAllData = dbHelper.getGroupChildData(dateListFromDB);
            dbHelper.close();

            for(int i=0;i<arrayListAllData.size();i++)
            {
                App.showLog("==arrayListAllData==","==Date=="+arrayListAllData.get(i).da);
            }*/
            for(int i=0;i<arrayListModelDrinkList.size();i++)
            {
                App.showLog("==arrayListModelDrinkList==ID=="+arrayListModelDrinkList.get(i).getId(),"==Date=="+arrayListModelDrinkList.get(i).getDrink_list_date()+"==Time=="+arrayListModelDrinkList.get(i).getDrink_list_time());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initialize() {
        try {

            mAdapter = new TestDateBaseAdapter(this);

            stickyList = (StickyListHeadersListView) findViewById(R.id.list);




            stickyList.setEmptyView(findViewById(R.id.empty));
            stickyList.setDrawingListUnderStickyHeader(true);
            stickyList.setAreHeadersSticky(true);
            stickyList.setAdapter(mAdapter);


            stickyList.setStickyHeaderTopOffset(-20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
