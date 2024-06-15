package com.tvhht.myapplication.stock.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tvhht.myapplication.R;

import com.tvhht.myapplication.stock.model.PerpetualLine;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

public class PalletIDCustomDialog extends Dialog implements
        View.OnClickListener {

    public Context c;
    public String title = "";
    public PerpetualLine value;
    public Dialog d;
    public Button yes, no;
    public TextView textMessage, textValue;

    public PalletIDCustomDialog(Context a, String title, PerpetualLine value) {
        super(a);
        this.c = a;
        this.title = title;
        this.value = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_one);
        yes = (Button) findViewById(R.id.buttonYes);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textValue = (TextView) findViewById(R.id.textTitle);
        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        textMessage.setText(title);
        textValue.setText(value.getPackBarcodes());

        WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().getApplicationContext()
        ).clearSharedPrefs("STOCKS_QUALITY_INFO_LIST");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes: {
                dismiss();
                WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().getApplicationContext()
                ).saveStocksQualityInfo(value);
                Intent myIntent = new Intent(c, QualityCustomDialogActivity.class);
                c.startActivity(myIntent);

            }
            break;
            case R.id.buttonNo:
                WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().getContext()
                ).saveBooleanValue(
                        "CASE_CODE_UPDATED",
                        false
                );
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
