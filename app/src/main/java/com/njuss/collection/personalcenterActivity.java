package com.njuss.collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PersonalCenterActivity extends AppCompatActivity {

    private Button mBtn_uncollected;
    private Button mBtn_collected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalcenter);

        mBtn_uncollected = (Button) findViewById(R.id.btn_uncollected);
        mBtn_uncollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalCenterActivity.this,ListViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
