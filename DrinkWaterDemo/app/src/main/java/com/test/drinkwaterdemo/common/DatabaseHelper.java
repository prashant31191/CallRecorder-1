package com.test.drinkwaterdemo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.test.drinkwaterdemo.model.Group;
import com.test.drinkwaterdemo.model.ModelDrinkList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Avinash Kahar on 30-Jun-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    static Context context;
    SQLiteDatabase db;

    public static final String TABLE_NAME_DRINK_LIST = "Drink_List";
    public static final String Drink_List_ID = "ID";
    public static final String Drink_List_Weight = "Weight";
    public static final String Drink_List_Time = "Time";
    public static final String Drink_List_Date = "Date";
    public static final String Drink_List_Img = "Img";
    public static final String Drink_List_QuantityML = "QuantityML";


    public DatabaseHelper(Context context) {
        super(context, App.DB_PATH + App.DB_NAME, null, DATABASE_VERSION);
        App.showLog(TAG, "===Table path and name=== " + App.DB_PATH + App.DB_NAME);
        this.context = context;
    }

    public void open() throws SQLException {
        db = this.getWritableDatabase();
    }

    @Override
    public synchronized void close() {

        if (db != null)
            db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        App.showLog(TAG, "===Table created===");

        //db.execSQL("CREATE TABLE IF NOT EXISTS Drink_List(id INTEGER,drink_list_weight TEXT,drink_list_time TEXT,drink_list_img TEXT,drink_list_quantityml TEXT);");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DRINK_LIST + "(" +
                        Drink_List_ID + " INTEGER PRIMARY KEY," +
                        Drink_List_Weight + " TEXT," +
                        Drink_List_Time + " TEXT," +
                        Drink_List_Date + " TEXT," +
                        Drink_List_Img + " TEXT," +
                        Drink_List_QuantityML + " TEXT);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DRINK_LIST);

        App.showLog(TAG, "===Table Update===");

        // Create tables again
        onCreate(db);
    }


    public void DropAllTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DRINK_LIST);
        onCreate(db);
        App.showLog(TAG, "===All Table Drop===");
    }


    public void insertDrinkList(ModelDrinkList model) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            App.showLog(TAG, "insertDrinkList: Qunatity " + model.getDrink_list_quantityml());

            values.put(Drink_List_Weight, model.getDrink_list_weight());
            values.put(Drink_List_Time, model.getDrink_list_time());
            values.put(Drink_List_Date, model.getDrink_list_date());
            values.put(Drink_List_Img, model.getDrink_list_img());
            values.put(Drink_List_QuantityML, model.getDrink_list_quantityml());
            db.insert(TABLE_NAME_DRINK_LIST, null, values);
            db.close();

        } catch (Exception e) {
        }
    }

    public void updateDrinkList(String drink_list_id, String glass_quantity) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            App.showLog(TAG, "updateDrinkList: Qunatity " + glass_quantity);

            values.put(Drink_List_ID, drink_list_id);
            values.put(Drink_List_QuantityML, glass_quantity);
            db.update(TABLE_NAME_DRINK_LIST, values, Drink_List_ID + " = '" + drink_list_id + "'", null);
            db.close();

        } catch (Exception e) {
        }
    }

    public ArrayList<ModelDrinkList> getTodaysDrinkList(String dateCurrentYYYYMMDD) {

        ArrayList<ModelDrinkList> data = new ArrayList<ModelDrinkList>();
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NAME_DRINK_LIST + " WHERE " + Drink_List_Date + " = '" + dateCurrentYYYYMMDD +"' ORDER BY " + Drink_List_ID + " ASC";

            SQLiteDatabase dbs = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {

                        ModelDrinkList model = new ModelDrinkList();

                        String id = cursor.getString(cursor
                                .getColumnIndex(Drink_List_ID));
                        String weight = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Weight));
                        String time = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Time));
                        String date = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Date));
                        String img = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Img));
                        String quantity = cursor.getString(cursor
                                .getColumnIndex(Drink_List_QuantityML));

                        try {
                            model.setId(id);
                            model.setDrink_list_weight(weight);
                            model.setDrink_list_time(time);
                            model.setDrink_list_date(date);
                            model.setDrink_list_img(img);
                            model.setDrink_list_quantityml(quantity);
                            data.add(model);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbs.close(); // Closing database connection

            }
        } catch (Exception e) {

        }
        return data;
    }

    public void delete_id_DrinkList(String drink_list_id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            App.showLog(TAG, "delete_id_DrinkList: id " + drink_list_id);

            db.execSQL("delete from " + TABLE_NAME_DRINK_LIST + " where " + Drink_List_ID + " = '" + drink_list_id + "'");
        } catch (Exception e) {
        }
    }

    public void deleteTable_DrinkList() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME_DRINK_LIST, null, null);
        } catch (Exception e) {
        }
    }


    public ArrayList<ModelDrinkList> getDateListFromDB() {
        ArrayList<ModelDrinkList> data = new ArrayList<ModelDrinkList>();
        try {
            //String selectQuery = "SELECT  * FROM " + TABLE_NAME_DRINK_LIST + " GROUP BY " + Drink_List_Date + " ORDER BY " + Drink_List_ID + " ASC";
            String selectQuery = "SELECT " + Drink_List_Date +",SUM("+Drink_List_QuantityML+ ")  FROM " + TABLE_NAME_DRINK_LIST + " GROUP BY " + Drink_List_Date + " ORDER BY " + Drink_List_ID + " DESC";

            SQLiteDatabase dbs = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        ModelDrinkList model = new ModelDrinkList();
                        String date = cursor
                                .getString(cursor.getColumnIndex(Drink_List_Date));
                        String quantity = cursor.getString(cursor
                                .getColumnIndex("SUM("+Drink_List_QuantityML+")"));

                        try {
                            model.setDrink_list_date(date);
                            model.setDrink_list_quantityml(quantity);
                            data.add(model);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbs.close(); // Closing database connection
            }
        } catch (Exception e) {
        }
        return data;
    }

    public ArrayList<Group> getGroupChildData(ArrayList<ModelDrinkList> dateListFromDB) {
        Cursor cursor = null;
        SQLiteDatabase dbs = this.getReadableDatabase();

        ArrayList<Group> list = new ArrayList<Group>();
        ArrayList<ModelDrinkList>  ch_list;

        for (int k = 0; k < dateListFromDB.size(); k++) {

            App.showLog(TAG, "1st: "+dateListFromDB.get(k).getDrink_list_date());
            App.showLog(TAG, "1st: "+dateListFromDB.get(k).getDrink_list_quantityml());

            Group obj1 = new Group();
            ch_list  = new ArrayList<ModelDrinkList>();

            obj1.setDateGroup(dateListFromDB.get(k).getDrink_list_date());
            obj1.setGroupQuantityML(dateListFromDB.get(k).getDrink_list_quantityml());

            String selectQuery = "SELECT  * FROM " + TABLE_NAME_DRINK_LIST + " WHERE " + Drink_List_Date +" = '"+ dateListFromDB.get(k).getDrink_list_date() + "'";
            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {

                        String id = cursor.getString(cursor
                                .getColumnIndex(Drink_List_ID));
                        String weight = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Weight));
                        String time = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Time));
                        String date = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Date));
                        String img = cursor.getString(cursor
                                .getColumnIndex(Drink_List_Img));
                        String quantity = cursor.getString(cursor
                                .getColumnIndex(Drink_List_QuantityML));

                        ModelDrinkList objGetset1 = new ModelDrinkList();
                        objGetset1.setId(id);
                        objGetset1.setDrink_list_weight(weight);
                        objGetset1.setDrink_list_time(time);
                        objGetset1.setDrink_list_date(date);
                        objGetset1.setDrink_list_img(img);
                        objGetset1.setDrink_list_quantityml(quantity);
                        ch_list.add(objGetset1);


                    } while (cursor.moveToNext());
                }
            }

            obj1.setChildItem(ch_list);
            list.add(obj1);

        }
        cursor.close();
        dbs.close(); // Closing database connection

        App.showLog(TAG, "listsize:"+list.size());
        return list;

    }

    public ArrayList<ModelDrinkList> getAllDataSortByDate() {
        ArrayList<ModelDrinkList> data = new ArrayList<ModelDrinkList>();
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NAME_DRINK_LIST + " ORDER BY " + Drink_List_Date + " DESC";
            SQLiteDatabase dbs = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        ModelDrinkList model = new ModelDrinkList();
                        String date = cursor.getString(cursor.getColumnIndex(Drink_List_Date));
                        String quantity = cursor.getString(cursor.getColumnIndex("SUM("+Drink_List_QuantityML+")"));

                        try {
                            model.setDrink_list_date(date);
                            model.setDrink_list_quantityml(quantity);
                            data.add(model);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbs.close(); // Closing database connection
            }
        } catch (Exception e) {
        }
        return data;
    }


}
