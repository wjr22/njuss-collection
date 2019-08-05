package com.njuss.collection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
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


import com.njuss.collection.base.CheckPermissionsActivity;
import com.njuss.collection.base.User;
import com.njuss.collection.beans.Store;
import com.njuss.collection.service.CollectionService;
import com.njuss.collection.tools.RecordPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @TODO 声音录制
 *
 * 信息采集页面
 * @author wangj / weizhaoyang / zhuxiaoyue
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
    private TextView conductor;
    private EditText etLienceID;
    private EditText etStoreName;
    private EditText etGPSAddress;
    private EditText etGPSLongitute;
    private EditText etGPSLatitude;
    private EditText etConductorID;
    private EditText etStoreAddress;

    private Button finish;
    private TextView integrity;

    private Store store;

    //界面控件
    private ImageButton startRecord;
    private ImageButton startPlay;
    private ImageButton stopRecord;
    private ImageButton stopPlay;
    //语音操作对象

    private CollectionService collectionService ;
    private RecordPlayer player;
    private MediaRecorder mediaRecorder;
    private File recordFile;
    private boolean isRecordDialogShow=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
     /*   //检测是否有录音权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "默认无录音权限");
            if (Build.VERSION.SDK_INT >= 23) {
                Log.i(TAG, "系统版本不低于android6.0 ，需要动态申请权限");
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
            }
        } else {
            Log.i(TAG, "默认有录音权限");
        }*/
        Intent intent = getIntent();
        store = (Store)intent.getSerializableExtra("store");
        recordFile = new File(App.getInstance().generatePicDir(), store.getLicenseID()+".mp3");

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
        startPlay = (ImageButton) findViewById(R.id.btn_startplay);
        stopPlay = (ImageButton) findViewById(R.id.btn_stopplay);

        gps = (Button) findViewById(R.id.btn_gps);

        conductor =(TextView)findViewById(R.id.conductor);
        integrity=(TextView)findViewById(R.id.integrity);

        conductor.setText(User.getConductor().getConductorName()+"烟草销售点信息采集");
        integrity.setText("完整度"+ String.valueOf(store.getComplete() == null ? 0: store.getComplete())+"%");
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

    }


    private void initListener() {
        photoRBtn.setOnClickListener(this);
        photoIDBtn.setOnClickListener(this);
        photoMBtn.setOnClickListener(this);
        photoLBtn.setOnClickListener(this);
        finish.setOnClickListener(this);
        gps.setOnClickListener(this);

        startRecord.setOnClickListener(this);

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
            case R.id.btn_startrecord:
                startRecording();
                break;
            case R.id.btn_startplay:
                playRecording();
                break;
            case R.id.btn_stopplay:
                stopplayer();
                break;
            case R.id.btn_finish:
                //uploadData();
                Intent intent1 = new Intent(CollectActivity.this, TabHostActivity.class);
                startActivity(intent1);
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
        if(isRecordDialogShow){
            return;
        }


        RecordDialogFragment dialogFragment = RecordDialogFragment.newInstance();//实现activity与dialogfragment传值
        Bundle bundle = new Bundle();
        bundle.putSerializable("store",store);

        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(), "dialog");
        isRecordDialogShow = true;
        dialogFragment.setOnCancelListener(new RecordDialogFragment.OnCancelInterface() {
            @Override
            public void onCancel() {
                isRecordDialogShow = false;
            }
        });

    }

    private void playRecording() {
        Toast.makeText(this, "播放录音"+recordFile, Toast.LENGTH_SHORT).show();
        //更新存储卡文件   没用
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(App.getInstance().generatePicDir() +'/'+ store.getLicenseID()+".mp3")));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uploadData();
    }
}






