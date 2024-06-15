package com.tvhht.myapplication.putaway.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.putaway.model.BinQuantityConfirm;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSSharedPref;

public class BinConfirmCustomDialog extends Dialog implements
        View.OnClickListener {

    public Context c;
    public String oldBin = "";
    public String newBin = "";
    public Dialog d;
    public Button yes, no;
    public TextView textOld, textNew;

    public BinConfirmCustomDialog(Context a, String newBin, String oldBin) {
        super(a);
        this.c = a;
        this.newBin = newBin;
        this.oldBin = oldBin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_four_change_bin);
        yes = (Button) findViewById(R.id.buttonYes);
        textOld = (TextView) findViewById(R.id.old_bin_number);
        textNew = (TextView) findViewById(R.id.new_bin_no);
        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        String old_bin_val = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().getContext()).getStringValue("OLD_BIN_VAL");

        textOld.setText(old_bin_val);
        textNew.setText(newBin);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes: {
                dismiss();

                BinQuantityConfirm qtyInfo = WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().getContext()
                ).getQTYInfo();

                BinQuantityConfirm qtyInfo1 = new BinQuantityConfirm(qtyInfo.getPutawayConfirmedQty(), qtyInfo.getPutawayTotalQty(), newBin, false);


                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().getContext())
                        .clearSharedPrefs("QUANTITY_INFO_LIST");

                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().getContext()).saveQTYInfo(qtyInfo1);

                Intent myIntent = new Intent(c, QuantityCustomDialogActivity.class);
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
