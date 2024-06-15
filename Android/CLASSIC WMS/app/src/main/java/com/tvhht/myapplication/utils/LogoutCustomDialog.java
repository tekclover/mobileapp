package com.tvhht.myapplication.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.tvhht.myapplication.R;
import com.tvhht.myapplication.login.LoginActivity;

public class LogoutCustomDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;


    public LogoutCustomDialog(Activity a) {
        super(a);
        this.c = a;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);



        setContentView(R.layout.dialog_view_logout);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        yes = (Button) findViewById(R.id.buttonYes);

        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                dismiss();
                WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().getApplicationContext()
                ).clearAllSharedPrefs();

                Intent i = new Intent(c, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                c.startActivity(i);
                c.finish();
            case R.id.buttonNo:

                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


}
