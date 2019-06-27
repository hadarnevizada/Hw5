package com.example.ivan.kotelmania;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FundraisingActivity extends AppCompatActivity {
    int sum = 0, sum_to_add;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fundraising);
        Button buttonname = (Button) findViewById(R.id.button) ;
        final EditText editText = (EditText) findViewById(R.id.add_heading) ;
        final TextView textView = (TextView) findViewById(R.id.textView) ;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            sum_to_add = sp.getInt("SUM", 0);
            sum += sum_to_add;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        textView.setText("Fundraising: " + sum_to_add);
        buttonname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sum_to_add = Integer.parseInt(editText.getText().toString());
                sum += sum_to_add;
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("SUM", sum);
                editor.commit();
                textView.setText("Fundraising: " + sum);


            }
        });
    }


}