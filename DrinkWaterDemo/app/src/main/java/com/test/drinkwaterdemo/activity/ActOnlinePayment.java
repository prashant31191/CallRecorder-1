package com.test.drinkwaterdemo.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.test.drinkwaterdemo.R;
import com.test.drinkwaterdemo.common.App;

public class ActOnlinePayment extends AppCompatActivity {

    String TAG = "ActOnlinePayment";

    CheckBox checkbox_address, checkbox_terms_condition, checkbox_advance_payment;
    EditText edt_patientname, edt_uhid, edt_patientrel, edt_mobile;
    EditText edt_fullnm, edt_emailid, edt_mobilepersonal, edt_addline1, edt_addline2, edt_city, edt_state;
    EditText edt_amount, edt_amount1, edt_billno, edt_manual_receipt_no, edt_representative_nm;
    TextView tv_submit;
    LinearLayout ll_address_same, ll_advance_payment;


    Spinner spinner_payment_reson, spinner_country, spinner_payment_type;

    String opt_payment_reson[] = {App.PaymentReson_InPatient, App.PaymentReson_HomeCollection, App.PaymentReson_HealthCheckUp};
    String opt_country[] = {App.Country};
    String opt_payment_type[] = {App.PaymentType_CreditCard, App.PaymentType_DebitCard, App.PaymentType_CreditDebitCard};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.act_online_payment);
        setContentView(R.layout.act_online_payment_same);

        initialize();
        clickEvent();

    }

    public void initialize() {
        try {

            edt_patientname = (EditText) findViewById(R.id.edt_patientname);
            edt_uhid = (EditText) findViewById(R.id.edt_uhid);
            edt_patientrel = (EditText) findViewById(R.id.edt_patientrel);
            edt_mobile = (EditText) findViewById(R.id.edt_mobile);
            edt_fullnm = (EditText) findViewById(R.id.edt_fullnm);
            edt_emailid = (EditText) findViewById(R.id.edt_emailid);
            edt_mobilepersonal = (EditText) findViewById(R.id.edt_mobilepersonal);
            edt_addline1 = (EditText) findViewById(R.id.edt_addline1);
            edt_addline2 = (EditText) findViewById(R.id.edt_addline2);
            edt_city = (EditText) findViewById(R.id.edt_city);
            edt_state = (EditText) findViewById(R.id.edt_state);
            edt_amount = (EditText) findViewById(R.id.edt_amount);
            edt_amount1 = (EditText) findViewById(R.id.edt_amount1);
            edt_billno = (EditText) findViewById(R.id.edt_billno);
            edt_manual_receipt_no = (EditText) findViewById(R.id.edt_manual_receipt_no);
            edt_representative_nm = (EditText) findViewById(R.id.edt_representative_nm);
            checkbox_address = (CheckBox) findViewById(R.id.checkbox_address);
            checkbox_terms_condition = (CheckBox) findViewById(R.id.checkbox_terms_condition);
            checkbox_advance_payment = (CheckBox) findViewById(R.id.checkbox_advance_payment);
            tv_submit = (TextView) findViewById(R.id.tv_submit);
            ll_address_same = (LinearLayout) findViewById(R.id.ll_address_same);
            ll_advance_payment = (LinearLayout) findViewById(R.id.ll_advance_payment);


            spinner_payment_reson = (Spinner) findViewById(R.id.spinner_payment_reson);
            spinner_country = (Spinner) findViewById(R.id.spinner_country);
            spinner_payment_type = (Spinner) findViewById(R.id.spinner_payment_type);

            ArrayAdapter<String> adapter_payment_reson = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, opt_payment_reson);
            spinner_payment_reson.setAdapter(adapter_payment_reson);

            ArrayAdapter<String> adapter_country = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, opt_country);
            spinner_country.setAdapter(adapter_country);

            ArrayAdapter<String> adapter_payment_type = new ArrayAdapter<String>(getApplicationContext(), R.layout.inflate_spinner_time, opt_payment_type);
            spinner_payment_type.setAdapter(adapter_payment_type);


            edt_city.setText("Mumbai");
            edt_state.setText("Maharashtra");

            edt_patientname.setTypeface(App.getFont_Regular());
            edt_uhid.setTypeface(App.getFont_Regular());
            edt_patientrel.setTypeface(App.getFont_Regular());
            edt_mobile.setTypeface(App.getFont_Regular());
            edt_fullnm.setTypeface(App.getFont_Regular());
            edt_emailid.setTypeface(App.getFont_Regular());
            edt_mobilepersonal.setTypeface(App.getFont_Regular());
            edt_addline1.setTypeface(App.getFont_Regular());
            edt_addline2.setTypeface(App.getFont_Regular());
            edt_city.setTypeface(App.getFont_Regular());
            edt_state.setTypeface(App.getFont_Regular());
            edt_amount.setTypeface(App.getFont_Regular());
            edt_amount1.setTypeface(App.getFont_Regular());
            edt_billno.setTypeface(App.getFont_Regular());
            tv_submit.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickEvent() {
        try {
            spinner_payment_reson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spinner_payment_reson.getSelectedItem().equals(App.PaymentReson_HomeCollection)) {
                        edt_patientrel.setText("");
                        edt_billno.setText("");

                        edt_patientrel.setVisibility(View.GONE);
                        edt_billno.setVisibility(View.GONE);

                        ll_advance_payment.setVisibility(View.VISIBLE);
                        edt_manual_receipt_no.setVisibility(View.VISIBLE);
                        edt_representative_nm.setVisibility(View.VISIBLE);
                    } else {
                        edt_manual_receipt_no.setText("");
                        edt_representative_nm.setText("");

                        ll_advance_payment.setVisibility(View.GONE);
                        edt_manual_receipt_no.setVisibility(View.GONE);
                        edt_representative_nm.setVisibility(View.GONE);

                        edt_billno.setVisibility(View.VISIBLE);
                        edt_patientrel.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });

            checkbox_advance_payment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(checkbox_advance_payment.isChecked()){
                        edt_manual_receipt_no.setVisibility(View.GONE);
                        edt_representative_nm.setVisibility(View.GONE);
                    }else {
                        edt_manual_receipt_no.setText("");
                        edt_representative_nm.setText("");

                        edt_manual_receipt_no.setVisibility(View.VISIBLE);
                        edt_representative_nm.setVisibility(View.VISIBLE);
                    }
                }
            });

            checkbox_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkbox_address.isChecked()) {
                        edt_fullnm.setText(edt_patientname.getText().toString().trim());
                        edt_mobilepersonal.setText(edt_mobile.getText().toString().trim());
                    } else {
                        edt_fullnm.setText("");
                        edt_mobilepersonal.setText("");
                    }

                }
            });

            tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str_edt_patientname = edt_patientname.getText().toString().trim();
                    String str_edt_uhid = edt_uhid.getText().toString().trim();
                    String str_edt_patientrel = edt_patientrel.getText().toString().trim();
                    String str_edt_mobile = edt_mobile.getText().toString().trim();
                    String str_edt_fullnm = edt_fullnm.getText().toString().trim();
                    String str_edt_emailid = edt_emailid.getText().toString().trim();
                    String str_edt_mobilepersonal = edt_mobilepersonal.getText().toString().trim();
                    String str_edt_addline1 = edt_addline1.getText().toString().trim();
                    String str_edt_addline2 = edt_addline2.getText().toString().trim();
                    String str_edt_city = edt_city.getText().toString().trim();
                    String str_edt_state = edt_state.getText().toString().trim();
                    String str_edt_amount = edt_amount.getText().toString().trim();
                    String str_edt_amount1 = edt_amount1.getText().toString().trim();
                    String str_edt_billno = edt_billno.getText().toString().trim();
                    String str_edt_manual_receipt_no = edt_manual_receipt_no.getText().toString().trim();
                    String str_edt_representative_nm = edt_representative_nm.getText().toString().trim();

                    Boolean isTrue = checkValidation(str_edt_patientname, str_edt_uhid, str_edt_patientrel, str_edt_mobile, str_edt_fullnm,
                                            str_edt_emailid, str_edt_mobilepersonal, str_edt_addline1, str_edt_addline2,
                                            str_edt_amount, str_edt_amount1, str_edt_billno, str_edt_manual_receipt_no, str_edt_representative_nm);

                    if(isTrue){
                        App.showSnackBar(edt_billno, "true");
                        App.showLog(TAG," payment_reson: "+spinner_payment_reson.getSelectedItem()+
                                        " patientname: "+str_edt_patientname+
                                        " uhid: "+str_edt_uhid+
                                        " patientrel: "+str_edt_patientrel+
                                        " mobile: "+str_edt_mobile+
                                        " fullnm: "+str_edt_fullnm+
                                        " emailid: "+str_edt_emailid+
                                        " mobilepersonal: "+str_edt_mobilepersonal+
                                        " addline1: "+str_edt_addline1+
                                        " addline2: "+str_edt_addline2+
                                        " city: "+str_edt_city+
                                        " state: "+str_edt_state+
                                " country: "+spinner_country.getSelectedItem()+
                                        " amount: "+str_edt_amount+
                                        " amount1: "+str_edt_amount1+
                                        " billno: "+str_edt_billno+
                                        " manual_receipt_no: "+str_edt_manual_receipt_no+
                                        " representative_nm: "+str_edt_representative_nm+
                                " payment_type: "+spinner_payment_type.getSelectedItem());
                    }
                }
            });

            /*From: ActOnlinePayment :---:
            payment_reson: In Patient Payment patientname: patient name uhid: UID number patientrel: relationship mobile: 1234567 fullnm: patient name emailid: 2106.ak@gmail.com mobilepersonal: 1234567 addline1: address 1 addline2: address 2 city: Mumbai state: Maharashtra country: India amount: 123456 amount1: 123 billno: bill. number manual_receipt_no:  representative_nm:  payment_type: Indian/Internationally issued Credit Card*/

            /*From: ActOnlinePayment :---:
            payment_reson: Home Collection patientname: patient name uhid: UID number patientrel:  mobile: 1234567 fullnm: patient name emailid: 2106.ak@gmail.com mobilepersonal: 1234567 addline1: address 1 addline2: address 2 city: Mumbai state: Maharashtra country: India amount: 123456 amount1: 123 billno: bill. number manual_receipt_no: manual representative_nm: representative payment_type: Indian/Internationally issued Credit Card*/



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean checkValidation(String str_edt_patientname, String str_edt_uhid, String str_edt_patientrel, String str_edt_mobile, String str_edt_fullnm,
                                   String str_edt_emailid, String str_edt_mobilepersonal, String str_edt_addline1, String str_edt_addline2,
                                   String str_edt_amount, String str_edt_amount1, String str_edt_billno, String str_edt_manual_receipt_no, String str_edt_representative_nm) {

        try {

            if (str_edt_patientname == null || str_edt_patientname.trim().length() == 0 ) {
                edt_patientname.requestFocus();
                App.showSnackBar(edt_patientname, "Please enter patient name.");
                return false;
            }else if (str_edt_uhid == null || str_edt_uhid.trim().length() == 0 ) {
                App.showSnackBar(edt_uhid, "Please enter uhid.");
                edt_uhid.requestFocus();
                return false;
            }else if ( (edt_patientrel.getVisibility() == View.VISIBLE) && ( str_edt_patientrel == null || str_edt_patientrel.trim().length() == 0)) {
                App.showSnackBar(edt_patientrel, "Please enter patient relationship.");
                edt_patientrel.requestFocus();
                return false;
            }else if (str_edt_mobile == null || str_edt_mobile.trim().length() == 0) {
                App.showSnackBar(edt_mobile, "Please enter valid mobile number.");
                edt_mobile.requestFocus();
                return false;
            } else if ( str_edt_fullnm == null || str_edt_fullnm.trim().length() == 0) {
                App.showSnackBar(edt_fullnm, "Please enter name.");
                edt_fullnm.requestFocus();
                return false;
            }else if (str_edt_emailid == null || str_edt_emailid.trim().length() == 0) {
                App.showSnackBar(edt_emailid, "Please enter valid email id.");
                edt_emailid.requestFocus();
                return false;
            }else if (str_edt_mobilepersonal == null || str_edt_mobilepersonal.trim().length() == 0) {
                App.showSnackBar(edt_mobilepersonal, "Please enter valid mobile number.");
                edt_mobilepersonal.requestFocus();
                return false;
            }else if (str_edt_addline1 == null || str_edt_addline1.trim().length() == 0) {
                App.showSnackBar(edt_addline1, "Please enter address.");
                edt_addline1.requestFocus();
                return false;
            }else if (str_edt_addline2 == null ||  str_edt_addline2.trim().length() == 0) {
                App.showSnackBar(edt_addline1, "Please enter address.");
                edt_addline2.requestFocus();
                return false;
            }else if (str_edt_amount == null || str_edt_amount.trim().length() == 0 ) {
                App.showSnackBar(edt_amount, "Please enter amount.");
                edt_amount.requestFocus();
                return false;
            }else if (str_edt_amount1 == null || str_edt_amount1.trim().length() == 0 ) {
                App.showSnackBar(edt_amount1, "Please re-enter amount.");
                edt_amount1.requestFocus();
                return false;

            }else if ((edt_billno.getVisibility() == View.VISIBLE) && (str_edt_billno == null || str_edt_billno.length() == 0 )) {
                App.showSnackBar(edt_billno, "Please enter bill number.");
                edt_billno.requestFocus();
                return false;
            }else if ((edt_manual_receipt_no.getVisibility() == View.VISIBLE) && (str_edt_manual_receipt_no == null || str_edt_manual_receipt_no.length() == 0 )) {
                App.showSnackBar(edt_manual_receipt_no, "Please enter manual receipt number.");
                edt_manual_receipt_no.requestFocus();
                return false;
            }else if ((edt_representative_nm.getVisibility() == View.VISIBLE) && (str_edt_representative_nm == null || str_edt_representative_nm.length() == 0 )) {
                App.showSnackBar(edt_representative_nm, "Please enter representative name.");
                edt_representative_nm.requestFocus();
                return false;
            }else if(checkbox_terms_condition.isChecked() == false){
                App.showSnackBar(checkbox_terms_condition, "Please accept terms and condition.");
                checkbox_terms_condition.requestFocus();
                return false;
            } else {
                return true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
