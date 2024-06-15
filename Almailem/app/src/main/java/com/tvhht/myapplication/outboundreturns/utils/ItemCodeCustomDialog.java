package com.tvhht.myapplication.outboundreturns.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.utils.ApiConstant;
import com.tvhht.myapplication.utils.ToastUtils;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

import androidx.appcompat.app.AppCompatActivity;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

public class ItemCodeCustomDialog extends AppCompatActivity implements
        View.OnClickListener {

    public Activity c;
    //public String title = this.getResources().getString(R.string.verify_part_no);
    public String value = "";
    public String value2 = "";
    public Dialog d;
    public Button yes, no;
    public TextView textMessage, textValue;
    ImageView imageIcon;
    EditText barcode_value;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_one_activity);
        yes = (Button) findViewById(R.id.buttonYes);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textValue = (TextView) findViewById(R.id.textTitle);
        no = (Button) findViewById(R.id.buttonNo);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        barcode_value = (EditText) findViewById(R.id.barcode_value);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        textMessage.setText(this.getResources().getString(R.string.verify_part_no));
        value= getIntent().getStringExtra("EXTRA_ITEM_CODE");
        value2=value;
        textValue.setText(value);
        imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);



    }


    private void registerReceivers() {

        Log.d("LOG", "registerReceivers()");

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION");   // for notification result
        filter.addAction("com.symbol.datawedge.api.ACTION");                // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT);    // needed to get version info

        // register to received broadcasts via DataWedge scanning
        filter.addAction("com.tvhht.myapplication.ACTION");
        filter.addAction("com.tvhht.myapplication.service.ACTION");
        registerReceiver(myBroadcastReceiver, filter);
    }

    // Unregister scanner status notification
    public void unRegisterScannerStatus() {
        Log.d("LOG", "unRegisterScannerStatus()");
        Bundle b = new Bundle();
        b.putString("com.symbol.datawedge.api.APPLICATION_NAME", getPackageName());
        b.putString(
                "com.symbol.datawedge.api.NOTIFICATION_TYPE",
                "SCANNER_STATUS"
        );
        Intent i = new Intent();
        i.setAction(ACTION);
        i.putExtra("com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION", b);
        this.sendBroadcast(i);
    }




    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            Log.d("LOG", "DataWedge Action:" + action);

            // Get DataWedge version info
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO"))
            {
                Bundle versionInfo = intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO");
                String DWVersion = versionInfo.getString("DATAWEDGE");

            }

            if (action.equals("com.tvhht.myapplication.ACTION"))
            {
                //  Received a barcode scan
                try
                {
                    displayScanResult(intent, "via Broadcast");
                }
                catch (Exception e)
                {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            }


        }
    };

    private void displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        // store decoded data
        String decodedData = initiatingIntent.getStringExtra("com.symbol.datawedge.data_string");
        // store decoder type
        String decodedLabelType = initiatingIntent.getStringExtra("com.symbol.datawedge.label_type");


        if (decodedData.toString().length() > ApiConstant.INSTANCE.getCode_length_three()) {
            value = decodedData.toString();
            textValue.setText(value);
            if (value.equalsIgnoreCase(value2)) {
                textMessage.setText("Part No Scanned");
                imageIcon.setBackgroundResource(R.drawable.icon_tick_success);
            }
            else
            {
                textMessage.setText("Invalid Part No");
                imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
        unRegisterScannerStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes: {
                if (value.equalsIgnoreCase(value2)) {
                finish();
                    Intent myIntent = new Intent(getApplicationContext(), PickingQuantityActivity.class);
                    startActivity(myIntent);
                } else {

                    ToastUtils.Companion.showToast(getApplicationContext(),"Invalid Part No");
                }

            }
            break;
            case R.id.buttonNo:
                WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().getContext()
                ).saveBooleanValue(
                        "CASE_CODE_UPDATED",
                        false
                );
                finish();
                break;
            default:
                break;
        }
        finish();
    }
}
