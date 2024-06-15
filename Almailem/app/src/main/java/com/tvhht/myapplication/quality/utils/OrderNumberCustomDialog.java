package com.tvhht.myapplication.quality.utils;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.quality.QualityHeNumberActivity;
import com.tvhht.myapplication.utils.ToastUtils;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

public class OrderNumberCustomDialog extends AppCompatActivity implements
        View.OnClickListener {

    public Activity c;
    public String title = "";
    public String orderNo = "";
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
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        no = (Button) findViewById(R.id.buttonNo);
        barcode_value = (EditText) findViewById(R.id.barcode_value);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        orderNo = getIntent().getStringExtra("ORDER_NO");
        title = getIntent().getStringExtra("TITLE");
        textMessage.setText(title);
        textValue.setText(orderNo);
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
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")) {
                Bundle versionInfo = intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO");
                String DWVersion = versionInfo.getString("DATAWEDGE");

            }

            if (action.equals("com.tvhht.myapplication.ACTION")) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            }


        }
    };

    private void displayScanResult(Intent initiatingIntent, String howDataReceived) {
        // store decoded data
        String decodedData = initiatingIntent.getStringExtra("com.symbol.datawedge.data_string");
        // store decoder type
        String decodedLabelType = initiatingIntent.getStringExtra("com.symbol.datawedge.label_type");

        if (!TextUtils.isEmpty(decodedData)) {
            orderNo = decodedData;
            textValue.setText(orderNo);
            textMessage.setText(title);
            imageIcon.setBackgroundResource(R.drawable.icon_tick_success);
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
                finish();
                if (!TextUtils.isEmpty(orderNo)) {
                    Intent myIntent = new Intent(getApplicationContext(), QualityHeNumberActivity.class);
                    myIntent.putExtra("ORDER_NO", orderNo);
                    startActivity(myIntent);
                } else {
                    ToastUtils.Companion.showToast(getApplicationContext(), "Please Scan Order Number");
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
