package com.tvhht.myapplication.putaway;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvhht.myapplication.R;

public class InfoCustomDialog extends Dialog implements
        View.OnClickListener {

    public Context c;
    public String title = "";
    public String value1 = "";
    public String value2 = "";
    public String value3 = "";
    public Dialog d;
    public Button yes, no;
    public TextView textValue1, textValue2,textValue3;
    public ImageView imageIcon;


    public InfoCustomDialog(Context a, String title, String value1, String value2, String value3) {
        super(a);
        this.c = a;
        this.title = title;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_view_five_three_text);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        yes = (Button) findViewById(R.id.buttonYes);
        textValue1= (TextView) findViewById(R.id.txt_cell_1);
        textValue2 = (TextView) findViewById(R.id.txt_cell_2);
        textValue3 = (TextView) findViewById(R.id.txt_cell_3);

        yes.setOnClickListener(this);


        textValue1.setText(value1);
        textValue2.setText(value2);
        textValue3.setText(value3);
        // imageIcon.setBackground(resImg);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
               dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }



}
