package com.tvhht.myapplication.transfers.utils;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.home.HomePageActivity;
import com.tvhht.myapplication.putaway.model.StorageBinResponse;
import com.tvhht.myapplication.putaway.viewmodel.PutAwayLiveData;
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

import rx.Observer;

public class SearchTargetBinCustomDialog extends AppCompatActivity implements
        View.OnClickListener {


    public String title = "Please Scan the Target Bin";
    public String value = "";
    public Dialog d;
    public Button yes, no;
    public TextView textMessage;
    ImageView imageIcon;
    EditText textValue;
    CreateTransferRequest createTransferRequest;
    ProgressDialog pd;
    TransferLiveDataModel viewModel;
    CreateTransferRequest createTransferRequest1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_one_edit_activity);
        initViewModel();
        yes = (Button) findViewById(R.id.buttonYes);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textValue = (EditText) findViewById(R.id.textTitle);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        textMessage.setText(title);
        textValue.setText(value);
        textValue.setEnabled(false);
        textValue.setClickable(false);
        textValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        textValue.setBackgroundColor(Color.TRANSPARENT);
        createTransferRequest = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getContext()
        ).getInventoryObjectInfo();
        imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);

        pd = new ProgressDialog(SearchTargetBinCustomDialog.this);
        pd.setMessage("Loading...");
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this).get(TransferLiveDataModel.class);
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
            value = decodedData != null ? decodedData.replace(" ", "") : "";
            textValue.setText(null);
            textValue.setText(value);
            binValidation();
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
                if (!TextUtils.isEmpty(value)) {
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
                    createTransferRequest.getInhouseTransferLine().get(i).getWarehouseId(),
                    createTransferRequest.getInhouseTransferLine().get(i).getManufacturerName()));

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


        String wareId = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getApplicationContext()
        ).getStringValue(PrefConstant.INSTANCE.getWARE_HOUSE_ID());
        String companyCodeId = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getApplicationContext()
        ).getStringValue(PrefConstant.INSTANCE.getCOMPANY_CODE_ID());
        String languageId = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getApplicationContext()
        ).getStringValue(PrefConstant.INSTANCE.getLANGUAGE_ID());
        String plantId = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getApplicationContext()
        ).getStringValue(PrefConstant.INSTANCE.getPLANT_ID());

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<String> companyCodeIdList=new ArrayList<>();
        ArrayList<String> languageIdList=new ArrayList<>();
        ArrayList<String> plantIdList=new ArrayList<>();
        SearchTargetBin searchTargetBin = new SearchTargetBin();
        list1.add(finalBin);
        list2.add(wareId);
        companyCodeIdList.add(companyCodeId);
        languageIdList.add(languageId);
        plantIdList.add(plantId);
        searchTargetBin.setPackBarcodes(list1);
        searchTargetBin.setWarehouseId(list2);
        searchTargetBin.setLanguageIdList(languageIdList);
        if (!wareId.equals(ApiConstant.WAREHOUSE_ID_200)) {
            searchTargetBin.setCompanyCodeIdList(companyCodeIdList);
            searchTargetBin.setPlantIdList(plantIdList);
        }
        viewModel.getTargetBinData(searchTargetBin).observe(this, this::processBin);
    }

    private void processBin(List<TargetBinResponse> targetBinResponse) {
        if (targetBinResponse != null && targetBinResponse.size() > 0) {
            viewModel.createDataToUpload(createTransferRequest1).observe(this, this::processAccounts);
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

    public void binValidation() {
        if (pd != null)
            pd.show();
        viewModel.authToken().observe(this,this::authResponse);
    }

    private void authResponse(String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            viewModel.binValidation(textValue.getText().toString(), authToken).observe(this, this::binValidationResponse);
        } else {
            value = "";
            if (pd != null)
                pd.dismiss();
        }
    }

    private void binValidationResponse(StorageBinResponse storageBinResponse) {
        if (pd != null)
            pd.dismiss();
        if (storageBinResponse != null) {
            value = textValue.getText().toString();
            textMessage.setText("Target Bin Number Scanned");
            imageIcon.setBackgroundResource(R.drawable.icon_tick_success);
        } else {
            value = "";
            textMessage.setText("Invalid Bin Number");
            imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);
           // Toast.makeText(getApplicationContext(), "Invalid Bin Number", Toast.LENGTH_SHORT).show();
        }
    }
}
