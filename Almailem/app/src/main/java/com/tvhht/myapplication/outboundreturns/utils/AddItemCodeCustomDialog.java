package com.tvhht.myapplication.outboundreturns.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

public class AddItemCodeCustomDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public String title = "";
    public String value = "";
    public Dialog d;
    public Button yes, no;
    public TextView textMessage, textValue;
    ImageView imageIcon;

    public AddItemCodeCustomDialog(Activity a, String title, String value) {
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
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        textMessage.setText(title);
        textValue.setText(value);
        imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes: {
                dismiss();
                Intent myIntent = new Intent(c, AddPickingQuantityActivity.class);
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
