package com.njuss.collection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CollectActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_1 = 1;
    private static final int REQUEST_CODE_2 = 2;
    private static final int REQUEST_CODE_3 = 3;
    private static final int REQUEST_CODE_4 = 4;
    private ImageView resultContainer1;
    private ImageView takePhotoBtn1;
    private ImageView resultContainer2;
    private ImageView takePhotoBtn2;
    private ImageView resultContainer3;
    private ImageView takePhotoBtn3;
    private ImageView resultContainer4;
    private ImageView takePhotoBtn4;

    private static final String LOG_TAG = "AudioRecordTest";
    //语音文件保存路径
    private String FileName = null;

    //界面控件
    private ImageButton startRecord;
    private ImageButton startPlay;
    private ImageButton stopRecord;
    private ImageButton stopPlay;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

        startRecord = (ImageButton) findViewById(R.id.btn_startrecord);


        startRecord.setOnClickListener(new startRecordListener());

        //结束录音
        stopRecord = (ImageButton) findViewById(R.id.btn_stoprecord);

        stopRecord.setOnClickListener(new stopRecordListener());

        //开始播放
        startPlay = (ImageButton) findViewById(R.id.btn_startplay);

        //绑定监听器
        startPlay.setOnClickListener(new startPlayListener());

        //结束播放
        stopPlay = (ImageButton) findViewById(R.id.btn_stopplay);

        stopPlay.setOnClickListener(new stopPlayListener());

        //设置sdcard的路径
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName += "/audiorecord.3gp";

    }


    class startRecordListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(FileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            mRecorder.start();




        }

    }
    //停止录音
    class stopRecordListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;




        }


    }
    //播放录音
    class startPlayListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            mPlayer = new MediaPlayer();
            try{
                mPlayer.setDataSource(FileName);
                mPlayer.prepare();
                mPlayer.start();
            }catch(IOException e){
                Log.e(LOG_TAG,"播放失败");
            }




        }

    }
    //停止播放录音
    class stopPlayListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            mPlayer.release();
            mPlayer = null;




        }

    }



    private void initListener() {
        takePhotoBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_1);
            }
        });

        takePhotoBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_2);
            }
        });

        takePhotoBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_3);
            }
        });

        takePhotoBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_4);
            }
        });




    }

    private void initView() {

        resultContainer1 = (ImageView) this.findViewById(R.id.iv_licensePic);
        takePhotoBtn1 = (ImageView) this.findViewById(R.id.iv_licensePic);
        resultContainer2 = (ImageView) this.findViewById(R.id.iv_storePicM);
        takePhotoBtn2 = (ImageView) this.findViewById(R.id.iv_storePicM);
        resultContainer3 = (ImageView) this.findViewById(R.id.iv_storePicL);
        takePhotoBtn3 = (ImageView) this.findViewById(R.id.iv_storePicL);
        resultContainer4 = (ImageView) this.findViewById(R.id.iv_storePicR);
        takePhotoBtn4 = (ImageView) this.findViewById(R.id.iv_storePicR);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer1.setImageBitmap(result);
            }
        } else if (requestCode == REQUEST_CODE_2) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer2.setImageBitmap(result);
            }
        } else if (requestCode == REQUEST_CODE_3) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer3.setImageBitmap(result);
            }
        } else if (requestCode == REQUEST_CODE_4) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer4.setImageBitmap(result);
            }
        }

        if (requestCode == Activity.RESULT_CANCELED) {

            Toast.makeText(this, "你取消了拍照", Toast.LENGTH_SHORT).show();
        }
    }}