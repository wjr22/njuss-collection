package com.njuss.collection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.njuss.collection.base.CheckPermissionsActivity;
import com.njuss.collection.base.Global;
import com.njuss.collection.base.User;
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
public class CollectActivity extends CheckPermissionsActivity implements
                                        View.OnClickListener , View.OnTouchListener {
    private static final int REQUEST_CODE_1 = 1;
    private static final int REQUEST_CODE_2 = 2;
    private static final int REQUEST_CODE_3 = 3;
    private static final int REQUEST_CODE_4 = 4;
    private static final String TAG = "录音";

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

    private TextView integrity;
    private static final String LOG_TAG = "AudioRecordTest";

    private Store store;

    //界面控件
    private ImageButton startRecord;
    private ImageButton startPlay;
    private ImageButton stopRecord;
    private ImageButton stopPlay;
    //语音操作对象

    // 音频路径
    private String FileName;
    private String mediaName;

    private CollectionService collectionService ;

    private RecordPlayer player;
    private MediaRecorder mediaRecorder;
    private File recordFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        //检测是否有录音权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "默认无录音权限");
            if (Build.VERSION.SDK_INT >= 23) {
                Log.i(TAG, "系统版本不低于android6.0 ，需要动态申请权限");
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
            }
        } else {
            Log.i(TAG, "默认有录音权限");
        }

        Intent intent = getIntent();
        store = (Store)intent.getSerializableExtra("store");
        recordFile = new File(((App)getApplication()).generatePicDir(), store.getLicenseID()+".amr");

       /* mediaName = store.getLicenseID()+".amr";
        FileName = ((App)getApplication()).generatePicDir()+mediaName;*/

        initView();
        initListener();
        collectionService = new CollectionService(getApplicationContext());
        collectionService.setStore(store);
    }
    private void initView() {
        App app = (App)getApplication();
        Log.d("info ====", store.toString());
        photoIDBtn = (ImageView) findViewById(R.id.iv_licensePic);
        photoMBtn = (ImageView) findViewById(R.id.iv_storePicM);
        photoLBtn = (ImageView) findViewById(R.id.iv_storePicL);
        photoRBtn = (ImageView) findViewById(R.id.iv_storePicR);
        finish = (Button) findViewById(R.id.btn_finish);

        etConductorID = (EditText)findViewById(R.id.et_conductorID);
        etGPSAddress = (EditText)findViewById(R.id.et_GPSAddress);
        etGPSLatitude = (EditText)findViewById(R.id.et_GPSLatitude);
        etGPSLongitute = (EditText)findViewById(R.id.et_GPSLongitude);
        etStoreName = (EditText)findViewById(R.id.et_storeName);
        etLienceID = (EditText)findViewById(R.id.et_licenseID);
        etStoreAddress = (EditText) findViewById(R.id.et_storeAddress);

        startRecord = (ImageButton) findViewById(R.id.btn_startrecord);
        stopRecord = (ImageButton) findViewById(R.id.btn_stoprecord);
        startPlay = (ImageButton) findViewById(R.id.btn_startplay);
        stopPlay = (ImageButton) findViewById(R.id.btn_stopplay);

        gps = (Button) findViewById(R.id.btn_gps);

        integrity=(TextView)findViewById(R.id.integrity);
        integrity.setText("完整度"+ String.valueOf(store.getComplete() == null ? 0: store.getComplete()));
       if(store.getStoreAddress() != null)
            etStoreAddress.setText(store.getStoreAddress());
        if(store.getLicenseID() != null) {
            etLienceID.setText(store.getLicenseID());
            Log.d("info", store.getLicenseID());
        }
        if(store.getStoreName() != null)
            etStoreName.setText(store.getStoreName());
        if(store.getGPSLongitude()!=null)
            etGPSLongitute.setText(store.getGPSLongitude());
        if(store.getGPSLatitude()!= null)
            etGPSLatitude.setText(store.getGPSLatitude());
        if(store.getGPSAddress()!= null)
            etGPSAddress.setText(store.getGPSAddress());
        etConductorID.setText(User.getConductor().getConductorName());
        if(store.getLicensePic() != null) {
            photoIDBtn.setImageBitmap(BitmapFactory.decodeFile(app.generatePicDir() + "/"+store.getLicensePic()));
            Log.d("load pic ", store.getLicensePic());
        }
        if(store.getStorePicR() != null)
            photoRBtn.setImageBitmap(BitmapFactory.decodeFile(app.generatePicDir()+"/"+store.getStorePicR()));
        if(store.getStorePicM() != null)
            photoMBtn.setImageBitmap(BitmapFactory.decodeFile(app.generatePicDir()+"/"+store.getStorePicM()));
        if(store.getStorePicL() != null)
            photoLBtn.setImageBitmap(BitmapFactory.decodeFile(app.generatePicDir()+"/"+store.getStorePicL()));

        /*startRecord = (ImageButton) findViewById(R.id.btn_startrecord);
        startRecord.setOnClickListener(new startRecordListener());
        //结束录音
        stopRecord = (ImageButton) findViewById(R.id.btn_stoprecord);
        stopRecord.setOnClickListener(new stopRecordListener());
        //开始播放
        startPlay = (ImageButton) findViewById(R.id.btn_startplay);
        startPlay.setOnClickListener(new startPlayListener());
        //结束播放
        stopPlay = (ImageButton) findViewById(R.id.btn_stopplay);
        stopPlay.setOnClickListener(new stopPlayListener());

        mediaName = store.getLicenseID()+".amr";
        FileName = ((App)getApplication()).generatePicDir()+mediaName;*/
    }

    private void initListener() {
        photoRBtn.setOnClickListener(this);
        photoIDBtn.setOnClickListener(this);
        photoMBtn.setOnClickListener(this);
        photoLBtn.setOnClickListener(this);
        finish.setOnClickListener(this);
        gps.setOnClickListener(this);

        startRecord.setOnClickListener(this);
        stopRecord.setOnClickListener(this);
        startPlay.setOnClickListener(this);
        stopPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        player = new RecordPlayer(CollectActivity.this);
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

            case R.id.btn_startrecord://录音部分
                startRecording();
                break;
            case R.id.btn_stoprecord:
                stopRecording();
                break;
            case R.id.btn_startplay:
                playRecording();
                break;
            case R.id.btn_stopplay:
                stopplayer();
                break;
            case R.id.btn_finish:
                uploadData();
                break;
            case R.id.btn_gps:
                Intent it = new Intent(CollectActivity.this, GaodeLocationActivity.class);
                startActivityForResult(it,100);
                break;
        }
    }

    public void uploadData(){
        //完成度大于80%
        collectionService.setStore(store);
        collectionService.update();
        if(store.getComplete() >= 80){
            Toast.makeText(CollectActivity.this, "完成收集！",Toast.LENGTH_SHORT).show();
            Log.d("---", "uploadData: >=80");
            Log.d("---", "uploadData: pic " +store.getLicensePic());
            Log.d("---", "uploadData: picL "+store.getStorePicL());
            Log.d("---", "uploadData: picM "+store.getStorePicM());
            Log.d("---", "uploadData: picR "+store.getStorePicR());
            Log.d("---", "uploadData: gps1"+store.getGPSLongitude());
            Log.d("---", "uploadData: gps2"+store.getGPSLatitude());
            finish();
        }else{
            Toast.makeText(CollectActivity.this, "完成度过低，请继续完善至80%", Toast.LENGTH_LONG).show();
            Log.d("---", "uploadData: <=80");
            Log.d("---", "uploadData: pic " +store.getLicensePic());
            Log.d("---", "uploadData: picL "+store.getStorePicL());
            Log.d("---", "uploadData: picM "+store.getStorePicM());
            Log.d("---", "uploadData: picR "+store.getStorePicR());
            Log.d("---", "uploadData: gps1"+store.getGPSLongitude());
            Log.d("---", "uploadData: gps2"+store.getGPSLatitude());
            finish();
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
                    store.setLicensePic(fileName);
                }
            }
        } else if (requestCode == REQUEST_CODE_2) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null) {
                    photoLBtn.setImageBitmap(result);
                    fileName = fileName + "PicL.jpg";
                    store.setStorePicL(fileName);
                }
            }
        } else if (requestCode == REQUEST_CODE_3) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null){
                    photoMBtn.setImageBitmap(result);
                    fileName = fileName + "PicM.jpg";
                    store.setStorePicM(fileName);
                }
            }
        } else if (requestCode == REQUEST_CODE_4) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getParcelableExtra("data");
                if (result != null){
                    photoRBtn.setImageBitmap(result);
                    fileName = fileName + "PicR.jpg";
                    store.setStorePicR(fileName);
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
                    Log.d("OUTPUT PIC ==", file.getAbsolutePath()+file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if(requestCode == 100 && resultCode == 101){
            String Longitute = data.getStringExtra("GPSLongitude");
            String GPSLatitude=data.getStringExtra("GPSLatitude");
            String location = data.getStringExtra("location");
            Log.d("GPS location ====", location);
            Log.d("GPS Longitute ====", Longitute);
            Log.d("GPS Latitude ====", GPSLatitude);
            store.setGPSLongitude(Longitute);
            store.setGPSLatitude(GPSLatitude);
            store.setGPSAddress(location);
            if(store.getGPSLongitude()!=null)
                etGPSLongitute.setText(store.getGPSLongitude());
            if(store.getGPSLatitude()!= null)
                etGPSLatitude.setText(store.getGPSLatitude());
            if(store.getGPSAddress()!= null)
                etGPSAddress.setText(store.getGPSAddress());
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();

        // 判断，若当前文件已存在，则删除
        if (recordFile.exists()) {
            recordFile.delete();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());

        try {
            // 准备好开始录音
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "正在录音", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void stopRecording() {
        if (recordFile != null) {
            mediaRecorder.stop();
            Toast.makeText(this, "停止录音", Toast.LENGTH_SHORT).show();
            mediaRecorder.release();
        }
    }

    private void playRecording() {
        Toast.makeText(this, "播放录音", Toast.LENGTH_SHORT).show();
        player.playRecordFile(recordFile);
    }

    private void stopplayer() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
        player.stopPalyer();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }
}





    /*class startRecordListener implements View.OnClickListener {

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

    }*/





