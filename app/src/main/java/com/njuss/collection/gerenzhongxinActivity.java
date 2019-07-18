package com.njuss.collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class gerenzhongxinActivity extends AppCompatActivity {

    private Button mBtn_weicaiji;
    private Button mBtn_yicaiji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenzhongxin);

        mBtn_weicaiji = (Button) findViewById(R.id.bt_weicaiji);
        mBtn_weicaiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gerenzhongxinActivity.this,ListViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
