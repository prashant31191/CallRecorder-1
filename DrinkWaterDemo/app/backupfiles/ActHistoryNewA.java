package com.test.drinkwaterdemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.common.App;
import com.test.drinkwaterdemo.common.DatabaseHelper;
import com.test.drinkwaterdemo.model.Group;
import com.test.drinkwaterdemo.model.ModelDrinkList;

import java.util.ArrayList;

public class ActHistoryNewA extends AppCompatActivity {

    String TAG = "ActHistory";
    DatabaseHelper dbHelper;

    ExpandListAdapter ExpAdapter;
    ExpandableListView ExpandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_history_new);

        initialize();
        getDataFromDB();

    }

    public void initialize() {
        try {

            dbHelper = new DatabaseHelper(this);

            ExpandList = (ExpandableListView) findViewById(R.id.lvExp);
            ExpandList.setGroupIndicator(null);
            ExpandList.setChildIndicator(null);
            ExpandList.setChildDivider(null);
            ExpandList.setDivider(null);
            ExpandList.setDividerHeight(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataFromDB() {
        try {

            dbHelper.open();
            ArrayList<ModelDrinkList> dateListFromDB = dbHelper.getDateListFromDB();
            ArrayList<Group> list = dbHelper.getGroupChildData(dateListFromDB);
            dbHelper.close();

            ExpAdapter = new ExpandListAdapter(ActHistoryNewA.this, list);
            ExpandList.setAdapter(ExpAdapter);

            /*---  by defualt Expand '0 number' groups -- click event work ---*/
            //ExpandList.expandGroup(0);

            /*---  by defualt Expand all groups -- click event work ---*/
            /*for(int i=0; i<list.size(); i++){
                ExpandList.expandGroup(i);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ExpandListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<Group> groups;


        public ExpandListAdapter(Context context, ArrayList<Group> groups) {
            this.context = context;
            this.groups = groups;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<ModelDrinkList> chList = groups.get(groupPosition).getChildItem();
            return chList.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final ModelDrinkList model = (ModelDrinkList) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.inflate_log_childview, null);
            }

            TextView glass_quantity = (TextView) convertView.findViewById(R.id.glass_quantity);
            TextView glass_time = (TextView) convertView.findViewById(R.id.glass_time);
            ImageView glass_img = (ImageView) convertView.findViewById(R.id.glass_img);
            View viewLast = (View) convertView.findViewById(R.id.viewLast);


            glass_quantity.setText(model.getDrink_list_quantityml().toString());
            glass_time.setText(""+ App.convert24hrto12hr(model.getDrink_list_time().toString()));

            glass_quantity.setTypeface(App.getFont_Regular());
            glass_time.setTypeface(App.getFont_Regular());

            String mDrawableName = model.getDrink_list_img().toString();
            int resId = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());

            Picasso.with(getApplicationContext())
                    .load(resId)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.img_custom_glass)
                    .into(glass_img);

            /*---  if child possible is last than divider GONE ---*/
            if(isLastChild == true){
                viewLast.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            ArrayList<ModelDrinkList> chList = groups.get(groupPosition).getChildItem();
            return chList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            final Group group = (Group) getGroup(groupPosition);
            /*----- Expand all groups -- click event not working ----*/
            //ExpandList.expandGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.inflate_log_groupview, null);
            }
            TextView glass_date = (TextView) convertView.findViewById(R.id.glass_date);
            TextView glass_total_ml = (TextView) convertView.findViewById(R.id.glass_total_ml);



            if(App.dateCurrentYYYYMMDD().equalsIgnoreCase(group.getDateGroup())){
                glass_date.setText("Today");
                /*---  by defualt Expand '0 number' groups -- click event work ---*/
                ExpandList.expandGroup(0);
            }else {
                glass_date.setText(group.getDateGroup());
            }

            glass_total_ml.setText("Total "+group.getGroupQuantityML()+" ml");

            glass_date.setTypeface(App.getFont_Bold());
            glass_total_ml.setTypeface(App.getFont_Bold());

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }


}
