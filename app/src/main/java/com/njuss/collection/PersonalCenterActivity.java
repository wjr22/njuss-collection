package com.njuss.collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.njuss.collection.tools.DBHelper;
import com.njuss.collection.tools.UploadData;

/**
 * 个人中心
 */
public class PersonalCenterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLoad;
    private Button btnOutput;

    private UploadData upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalcenter);
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        upload = new UploadData.Builder()
                .setContext(getApplication())
                .setDB(dbHelper.getWritableDatabase())
                .setDBHelper(dbHelper)
                .build();
        initView();

    }

    private void initView(){
        btnLoad = (Button) findViewById(R.id.btn_load);
        btnOutput = (Button) findViewById(R.id.btn_output);

        btnLoad.setOnClickListener(this);
        btnOutput.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_load:

                break;
            case R.id.btn_output:
                upload.exportToExcel();
                break;
        }
    }
}
