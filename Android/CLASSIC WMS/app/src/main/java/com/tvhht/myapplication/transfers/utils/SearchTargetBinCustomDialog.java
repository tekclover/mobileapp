package com.tvhht.myapplication.transfers.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.home.HomePageActivity;
import com.tvhht.myapplication.transfers.model.CreateTransferRequest;
import com.tvhht.myapplication.transfers.model.InhouseTransferLine;
import com.tvhht.myapplication.transfers.model.SearchTargetBin;
import com.tvhht.myapplication.transfers.model.TargetBinResponse;
import com.tvhht.myapplication.transfers.viewmodel.TransferLiveDataModel;
import com.tvhht.myapplication.utils.ApiConstant;
import com.tvhht.myapplication.utils.PrefConstant;
import com.tvhht.myapplication.utils.ToastUtils;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

public class SearchTargetBinCustomDialog extends AppCompatActivity implements
        View.OnClickListener {


    public String title = "Please Scan the Target Bin";
    public String value = "";
    public Dialog d;
    public Button yes, no;
    public TextView textMessage;
    ImageView imageIcon;
    EditText barcode_value, textValue;
    CreateTransferRequest createTransferRequest;
    ProgressDialog pd;
    TransferLiveDataModel model;
    CreateTransferRequest createTransferRequest1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_one_edit_activity);
        yes = (Button) findViewById(R.id.buttonYes);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textValue = (EditText) findViewById(R.id.textTitle);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        barcode_value = (EditText) findViewById(R.id.barcode_value);
        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        textMessage.setText(title);
        textValue.setText(value);

        createTransferRequest = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getContext()
        ).getInventoryObjectInfo();
        imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);

        pd = new ProgressDialog(SearchTargetBinCustomDialog.this);
        pd.setMessage("Loading...");


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


        if (decodedData.toString().length() > ApiConstant.INSTANCE.getCode_length_two()) {
            value = decodedData.toString();
            textValue.setText(value);
            textMessage.setText("Target Bin Number Scanned");
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
                if (!value.isEmpty() && value.length() > 4) {

                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().getContext()
                    ).saveBooleanValue(
                            "CASE_CODE_UPDATED",
                            false
                    );
                    submitData(textValue.getText().toString());
                } else {
                    ToastUtils.Companion.showToast(SearchTargetBinCustomDialog.this, "Please Scan the Target Bin");
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

    }

    public void submitData(String finalBin) {
        if (pd != null)
            pd.show();
        ArrayList<InhouseTransferLine> listRecords = new ArrayList<>();
        createTransferRequest1 = new CreateTransferRequest();

        for (int i = 0; i < createTransferRequest.getInhouseTransferLine().size(); i++) {

            listRecords.add(new InhouseTransferLine(
                    createTransferRequest.getInhouseTransferLine().get(i).getCaseCode(),
                    createTransferRequest.getInhouseTransferLine().get(i).getCompanyCodeId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getConfirmedBy(),
                    createTransferRequest.getInhouseTransferLine().get(i).getConfirmedOn(),
                    createTransferRequest.getInhouseTransferLine().get(i).getCreatedOn(),
                    createTransferRequest.getInhouseTransferLine().get(i).getCreatedby(),
                    createTransferRequest.getInhouseTransferLine().get(i).getLanguageId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getPackBarcodes(),
                    createTransferRequest.getInhouseTransferLine().get(i).getPalletCode(),
                    createTransferRequest.getInhouseTransferLine().get(i).getPlantId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getSourceItemCode(),
                    createTransferRequest.getInhouseTransferLine().get(i).getSourceStockTypeId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getSourceStorageBin(),
                    createTransferRequest.getInhouseTransferLine().get(i).getSpecialStockIndicatorId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getTargetStockTypeId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getTargetItemCode(),
                    finalBin,
                    createTransferRequest.getInhouseTransferLine().get(i).getTransferConfirmedQty(),
                    createTransferRequest.getInhouseTransferLine().get(i).getTransferOrderQty(),
                    createTransferRequest.getInhouseTransferLine().get(i).getTransferUom(),
                    createTransferRequest.getInhouseTransferLine().get(i).getWarehouseId()));

        }

        createTransferRequest1.setCompanyCodeId(createTransferRequest.getCompanyCodeId());
        createTransferRequest1.setCreatedby(createTransferRequest.getCreatedby());
        createTransferRequest1.setCreatedOn(createTransferRequest.getCreatedOn());
        createTransferRequest1.setLanguageId(createTransferRequest.getLanguageId());
        createTransferRequest1.setPlantId(createTransferRequest.getPlantId());
        createTransferRequest1.setWarehouseId(createTransferRequest.getWarehouseId());
        createTransferRequest1.setTransferMethod("ONESTEP");
        createTransferRequest1.setTransferTypeId(3);
        createTransferRequest1.setInhouseTransferLine(listRecords);

        if (model == null)
            model = ViewModelProviders.of(this).get(TransferLiveDataModel.class);

        String wareId = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getApplicationContext()
        ).getStringValue(PrefConstant.INSTANCE.getWARE_HOUSE_ID());
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        SearchTargetBin searchTargetBin = new SearchTargetBin();
        list1.add(finalBin);
        list2.add(wareId);
        searchTargetBin.setPackBarcodes(list1);
        searchTargetBin.setWarehouseId(list2);

        model.getTargetBinData(searchTargetBin).observe(this, this::processBin);


    }

    private void processBin(List<TargetBinResponse> targetBinResponse) {

        if (targetBinResponse != null && targetBinResponse.size() > 0) {

            if (model == null)
                model = ViewModelProviders.of(this).get(TransferLiveDataModel.class);


            model.createDataToUpload(createTransferRequest1).observe(this, this::processAccounts);

        } else {
            if (pd != null)
                pd.dismiss();
            finish();
            ToastUtils.Companion.showToast(getApplicationContext(), "Please scan a valid Bin location");
            finish();
        }
    }


    private void processAccounts(CreateTransferRequest listVO) {
        if (pd != null)
            pd.dismiss();
        if (listVO != null) {
            ToastUtils.Companion.showToast(getApplicationContext(), "Inventory Updated Successfully");
            finish();
            HomePageActivity.Companion.getInstance().reload();
        } else {
            ToastUtils.Companion.showToast(getApplicationContext(), "Unable to update Inventory details");
            finish();
            HomePageActivity.Companion.getInstance().reload();
        }
    }
}
