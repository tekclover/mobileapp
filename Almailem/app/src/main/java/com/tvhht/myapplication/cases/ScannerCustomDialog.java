package com.tvhht.myapplication.cases;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tvhht.myapplication.R;

import com.tvhht.myapplication.cases.model.CaseConfirmResponse;
import com.tvhht.myapplication.cases.model.CaseDetailModel;
import com.tvhht.myapplication.cases.viewmodel.CasesLiveDataModel;

import com.tvhht.myapplication.utils.PrefConstant;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class ScannerCustomDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public String title = "";
    public String value = "";
    public Dialog d;
    public Button yes, no;
    public TextView textMessage, textValue;
    public ImageView imageIcon;
    int resImg;
    CaseDetailModel caseDetailModel;
    ProgressDialog pd;

    public ScannerCustomDialog(Activity a, String title, String value, int res, CaseDetailModel caseDetailModel) {
        super(a);
        this.c = a;
        this.title = title;
        this.value = value;
        this.resImg = res;
        this.caseDetailModel = caseDetailModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_view_one);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        yes = (Button) findViewById(R.id.buttonYes);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textValue = (TextView) findViewById(R.id.textTitle);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        textMessage.setText(title);
        textValue.setText(value);
        pd = new ProgressDialog(c);
        pd.setMessage("Loading...");
        // imageIcon.setBackground(resImg);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                submitData();
                break;
            case R.id.buttonNo:

                WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().getContext()
                ).saveBooleanValue(
                        "CASE_CODE_SCANNED",
                        false
                );
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


    public void submitData() {
        dismiss();

        CaseDetailActivity.Companion.getInstance().createCase(caseDetailModel);

        WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getContext()
        ).saveBooleanValue(
                "CASE_CODE_SCANNED",
                false
        );

    }




}
