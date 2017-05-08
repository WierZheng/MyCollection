package com.example.zw.okhttp3test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    Button bt_get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

    }

    private void initViews() {
        bt_get=(Button)findViewById(R.id.bt_get);
    }

    @Override
    public void onClick(View view) {
        
    }
}
