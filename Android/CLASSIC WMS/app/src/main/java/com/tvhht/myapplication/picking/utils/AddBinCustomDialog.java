package com.tvhht.myapplication.picking.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.outboundreturns.ReturnPickingBinDetailsActivity;
import com.tvhht.myapplication.picking.PickingBinDetailsActivity;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

public class AddBinCustomDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public String title = "";
    public String value = "";
    public Dialog d;
    public Button yes, no;
    public TextView textMessage, textValue;

    public AddBinCustomDialog(Activity a, String title, String value) {
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
        textValue.setText(value);
        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().getContext()).saveStringValue("OLD_BIN_VAL",value);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes: {
                dismiss();
                PickingBinDetailsActivity.Companion.getInstance().loadPalletID(value);
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
