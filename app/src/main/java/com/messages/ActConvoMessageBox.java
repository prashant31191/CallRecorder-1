package com.messages;

/**
 * Created by prashant.patel on 9/7/2017.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.Utils.AppFlags;
import com.azapps.callrecorder.R;

import java.util.ArrayList;

public class ActConvoMessageBox extends Activity implements OnClickListener {


    //  GUI Widget
    Button btnSent, btnInbox, btnDraft;
    TextView lblMsg, lblNo;

    RecyclerView recyclerView;

    // Cursor Adapter
    SimpleCursorAdapter adapter;

    String id = "";
    String number = "";
    String message = "";
    String thread_id = "";
    String data = "";


    ArrayList<MessageModel> mArrayListMessage = new ArrayList<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_box);

        if (getIntent().getExtras().getString("thread_id") != null) {
            thread_id = getIntent().getExtras().getString("thread_id");


            AppFlags.showLog("=====thread_id=="+thread_id);
            Uri uri = Uri.parse("content://sms/");

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver contentResolver = getContentResolver();
            Cursor c = contentResolver.query(uri, null, "thread_id=" + thread_id, null, "date asc");

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                // The Cursor is now set to the right position

                id = c.getString(c.getColumnIndex("_id"));
                number = c.getString(c.getColumnIndex("address"));
                message = c.getString(c.getColumnIndex("body"));
                thread_id = c.getString(c.getColumnIndex("thread_id"));

                mArrayListMessage.add(new MessageModel(id, number, message, thread_id, data));
            }
        }
        // Init GUI Widget
        btnInbox = (Button) findViewById(R.id.btnInbox);
        btnInbox.setOnClickListener(this);

        btnSent = (Button) findViewById(R.id.btnSentBox);
        btnSent.setOnClickListener(this);

        btnDraft = (Button) findViewById(R.id.btnDraft);
        btnDraft.setOnClickListener(this);

        //lvMsg = (ListView) findViewById(R.id.lvMsg);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActConvoMessageBox.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        if (mArrayListMessage != null && mArrayListMessage.size() > 0) {
            notificationAdapter = new NotificationAdapter(ActConvoMessageBox.this, mArrayListMessage);
            recyclerView.setAdapter(notificationAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            Snackbar.make(recyclerView, "No Messages found", Snackbar.LENGTH_SHORT);
        }

    }

    @Override
    public void onClick(View v) {


      /*  Uri uri = Uri.parse("content://sms/");
        cursor = contentResolver.query(uri, null, "thread_id=" + value, null, "date asc");
*/
        if (v == btnInbox) {

            mArrayListMessage = new ArrayList<>();

            // Create Inbox box URI
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "thread_id"};

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, null, null, null);


            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                // The Cursor is now set to the right position

                id = c.getString(c.getColumnIndex("_id"));
                number = c.getString(c.getColumnIndex("address"));
                message = c.getString(c.getColumnIndex("body"));
                thread_id = c.getString(c.getColumnIndex("thread_id"));

                mArrayListMessage.add(new MessageModel(id, number, message, thread_id, data));
            }


            /*// Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[] { "body", "address", "thread_id" }, new int[] {
                    R.id.lblMsg, R.id.lblNumber, R.id.lblThreadId });
            lvMsg.setAdapter(adapter);*/

        }

        if (v == btnSent) {

            mArrayListMessage = new ArrayList<>();

            // Create Sent box URI
            Uri sentURI = Uri.parse("content://sms/sent");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "thread_id"};

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(sentURI, reqCols, null, null, null);


            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                // The Cursor is now set to the right position

                id = c.getString(c.getColumnIndex("_id"));
                number = c.getString(c.getColumnIndex("address"));
                message = c.getString(c.getColumnIndex("body"));
                thread_id = c.getString(c.getColumnIndex("thread_id"));

                mArrayListMessage.add(new MessageModel(id, number, message, thread_id, data));
            }


           /* // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[] { "body", "address", "thread_id" }, new int[] {
                R.id.lblMsg, R.id.lblNumber, R.id.lblThreadId });
            lvMsg.setAdapter(adapter);*/

        }

        if (v == btnDraft) {
            mArrayListMessage = new ArrayList<>();

            // Create Draft box URI
            Uri draftURI = Uri.parse("content://sms/draft");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "thread_id"};

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(draftURI, reqCols, null, null, null);


            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                // The Cursor is now set to the right position

                id = c.getString(c.getColumnIndex("_id"));
                number = c.getString(c.getColumnIndex("address"));
                message = c.getString(c.getColumnIndex("body"));
                thread_id = c.getString(c.getColumnIndex("thread_id"));

                mArrayListMessage.add(new MessageModel(id, number, message, thread_id, data));
            }


          /*  // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[] { "body", "address", "thread_id" }, new int[] {
                R.id.lblMsg, R.id.lblNumber, R.id.lblThreadId });
            lvMsg.setAdapter(adapter);*/

        }


        if (mArrayListMessage != null && mArrayListMessage.size() > 0) {
            notificationAdapter = new NotificationAdapter(ActConvoMessageBox.this, mArrayListMessage);
            recyclerView.setAdapter(notificationAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            Snackbar.make(recyclerView, "No Messages found", Snackbar.LENGTH_SHORT);
        }
    }

    NotificationAdapter notificationAdapter;


    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VersionViewHolder> {
        ArrayList<MessageModel> mArrList_a_MessageModel;
        Context mContext;


        public NotificationAdapter(Context context, ArrayList<MessageModel> arrayListFollowers) {
            mArrList_a_MessageModel = arrayListFollowers;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                MessageModel messageModel = mArrList_a_MessageModel.get(i);

                if (messageModel.number != null) {
                    versionViewHolder.lblNumber.setText(messageModel.number);
                }

                if (messageModel.message != null) {
                    versionViewHolder.lblMsg.setText(messageModel.message);
                }


                versionViewHolder.rlMain.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            AppFlags.showLog("==thread_id===" + mArrList_a_MessageModel.get(i).thread_id);

                           /* if(mArrList_a_MessageModel.get(i).match_id !=null) {
                                Intent intente = new Intent(ActLiveMatchList.this, ActScoreCard.class);
                                intente.putExtra(AppFlags.tagFrom, "ActLiveMatchList");
                                intente.putExtra(AppFlags.tagTitle, mArrList_a_MessageModel.get(i).series_name);
                                intente.putExtra(AppFlags.tagData, mArrList_a_MessageModel.get(i).match_id);
                                startActivity(intente);
                                animStartActivity();

                            }*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrList_a_MessageModel.size();
        }


        public void removeItem(int position) {
            mArrList_a_MessageModel.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mArrList_a_MessageModel.size());
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {

            TextView lblNumber, lblMsg;
            RelativeLayout rlMain;

            public VersionViewHolder(View itemView) {
                super(itemView);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                lblNumber = (TextView) itemView.findViewById(R.id.lblNumber);
                lblMsg = (TextView) itemView.findViewById(R.id.lblMsg);
            }

        }
    }


    public class MessageModel {
        String id;
        String number;
        String message;
        String thread_id;
        String data;


        public MessageModel(String id, String number, String message, String thread_id, String data) {
            this.id = id;
            this.number = number;
            this.message = message;
            this.thread_id = thread_id;
            this.data = data;
        }

    }
}