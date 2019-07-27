package com.njuss.collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.njuss.collection.base.Global;
import com.njuss.collection.beans.Store;
import com.njuss.collection.service.CollectionService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @TODO 声音录制
 *
 * 信息采集页面
 * @author wangj / weizhaoyang
 *
 */
public class CollectActivity extends AppCompatActivity  implements  View.OnClickListener{
    private static final int REQUEST_CODE_1 = 1;
    private static final int REQUEST_CODE_2 = 2;
    private static final int REQUEST_CODE_3 = 3;
    private static final int REQUEST_CODE_4 = 4;

    private ImageView photoIDBtn;
    private ImageView photoLBtn;
    private ImageView photoMBtn;
    private ImageView photoRBtn;

    private Button gps;

    private EditText etLienceID;
    private EditText etStoreName;
    private EditText etGPSAddress;
    private EditText etGPSLongitute;
    private EditText etGPSLatitude;
    private EditText etConductorID;
    private EditText etStoreAddress;

    private Button finish;

    private static final String LOG_TAG = "AudioRecordTest";

    private Store store;

    //界面控件
    private ImageButton startOrStopRecord;
    private ImageButton startOrStopPlay;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    private CollectionService collectionService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        store = (Store)intent.getSerializableExtra("store");
        initView();
        initListener();
        collectionService = new CollectionService(getApplicationContext());
        collectionService.setStore(store);
    }
    private void initView() {
        photoIDBtn = (ImageView) findViewById(R.id.iv_licensePic);
        photoMBtn = (ImageView) findViewById(R.id.iv_storePicM);
        photoLBtn = (ImageView) findViewById(R.id.iv_storePicL);
        photoRBtn = (ImageView) findViewById(R.id.iv_storePicR);
        startOrStopRecord = (ImageButton) findViewById(R.id.btn_startrecord);
        startOrStopPlay = (ImageButton) findViewById(R.id.btn_startplay);
        finish = (Button) findViewById(R.id.btn_finish);

        etConductorID = (EditText)findViewById(R.id.et_conductorID);
        etGPSAddress = (EditText)findViewById(R.id.et_GPSAddress);
        etGPSLatitude = (EditText)findViewById(R.id.et_GPSLatitude);
        etGPSLongitute = (EditText)findViewById(R.id.et_GPSLongitude);
        etStoreName = (EditText)findViewById(R.id.et_storeName);
        etLienceID = (EditText)findViewById(R.id.et_licenseID);
        etStoreAddress = (EditText) findViewById(R.id.et_storeAddress);

        gps = (Button) findViewById(R.id.btn_gps);

        if(store.getStoreAddress() != null)
            etStoreAddress.setText(store.getStoreAddress());
        if(store.getConductorID() != null)
            etLienceID.setText(store.getConductorID());
        if(store.getStoreName() != null)
            etStoreName.setText(store.getStoreName());
        if(store.getGPSLongitude()!=null)
            etGPSLongitute.setText(store.getGPSLongitude());
        if(store.getGPSLatitude()!= null)
            etGPSLatitude.setText(store.getGPSLatitude());
        if(store.getGPSAddress()!= null)
            etGPSAddress.setText(store.getGPSAddress());
        if(store.getConductorID()!= null)
            etConductorID.setText(store.getConductorID());
    }

    private void initListener() {
        photoRBtn.setOnClickListener(this);
        photoIDBtn.setOnClickListener(this);
        photoMBtn.setOnClickListener(this);
        photoRBtn.setOnClickListener(this);
        startOrStopRecord.setOnClickListener(this);
        startOrStopPlay.setOnClickListener(this);
        finish.setOnClickListener(this);
        gps.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (view.getId()){
            case R.id.iv_licensePic:
                startActivityForResult(intent, REQUEST_CODE_1);
                break;
            case R.id.iv_storePicL:
                startActivityForResult(intent, REQUEST_CODE_2);
                break;
            case R.id.iv_storePicM:
                startActivityForResult(intent, REQUEST_CODE_3);
                break;
            case R.id.iv_storePicR:
                startActivityForResult(intent, REQUEST_CODE_4);
                break;
            case R.id.btn_startrecord:

                break;
            case R.id.btn_startplay:

                break;
            case R.id.btn_finish:
                uploadData();
                break;
            case R.id.btn_gps:
                Intent it = new Intent(CollectActivity.this, fanzai_AMapCollectActivity.class);
                startActivity(it);
                break;
        }
    }

    public void uploadData(){
        if(store.getComplete() >= 80){
            //完成度大于80%
            collectionService.update();
        }else{
            Toast.makeText(CollectActivity.this, "完成度过低，请继续完善至80%", Toast.LENGTH_LONG);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap result = null;
        String fileName = store.getLicenseID();
        if (requestCode == REQUEST_CODE_1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null) {
                    photoIDBtn.setImageBitmap(result);
                    fileName = fileName + "Pic.jpg";
                }
            }
        } else if (requestCode == REQUEST_CODE_2) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null) {
                    photoIDBtn.setImageBitmap(result);
                    fileName = fileName + "PicL.jpg";
                }
            }
        } else if (requestCode == REQUEST_CODE_3) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null){
                    photoIDBtn.setImageBitmap(result);
                    fileName = fileName + "PicM.jpg";
                }
            }
        } else if (requestCode == REQUEST_CODE_4) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null){
                    photoIDBtn.setImageBitmap(result);
                    fileName = fileName + "PicR.jpg";
                }
            }
        }
        if (requestCode == Activity.RESULT_CANCELED) {

            Toast.makeText(this, "你取消了拍照", Toast.LENGTH_SHORT).show();
        }
        if(result != null){
            //本地暂存
            FileOutputStream fos = null;
            App app = (App) getApplication();
            File file = new File(app.generatePicDir(), fileName);
            try {
                fos = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}