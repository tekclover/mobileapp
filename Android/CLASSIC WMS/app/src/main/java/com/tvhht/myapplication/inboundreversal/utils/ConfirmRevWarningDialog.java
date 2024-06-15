package com.tvhht.myapplication.inboundreversal.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.cases.model.CaseDetailModel;
import com.tvhht.myapplication.inboundreversal.InboundReversalDetailsActivity;

public class ConfirmRevWarningDialog extends Dialog implements
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

    public ConfirmRevWarningDialog(Activity a) {
        super(a);
        this.c = a;

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
        textMessage.setText("Proposed Bin to be scanned");
        textValue.setText("Do you still want to Add New Bin?");
        imageIcon.setBackgroundResource(R.drawable.icon_alert_failure);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                dismiss();
                InboundReversalDetailsActivity.Companion.getInstance().loadAddBinData();
            case R.id.buttonNo:

               dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }




}
