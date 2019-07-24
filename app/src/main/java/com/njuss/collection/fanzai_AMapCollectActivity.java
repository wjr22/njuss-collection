package com.fanzai.voice.datacollect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMyLocationChangeListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.CoordinateConverter.CoordType;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.lb.util.TxtUtil;
import com.fanzai.voice.collect.R;
import com.fanzai.voice.njvoice.GPSDataService;
import com.fanzai.voice.njvoice.GSensorService;
import com.fanzai.voice.njvoice.PhoneInfoView;

public class fanzai_AMapCollectActivity extends Activity implements OnClickListener, OnCameraChangeListener, OnGeocodeSearchListener, OnMyLocationChangeListener
{

    private static final String TAG = "fanzai_AMapCollectActivity";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private MapView mMapView;
    private AMap aMap;
    private Button btn_collect;
    private LinearLayout gpsData_layout;
    private TextView  txt_posinfo;
    private TextView  state_text_gps;
    private TextView  location_type;
    private TextView  headinfo;
    private TextView  otherinfo;
    private TextView  poiname;
    private TextView  addressname;
    private TextView  mapgps;
    
    
    public static List<LatLng> Pt_Arry = null;    
    private Boolean isFirstLocate = true;
    
    private Intent gpsservice;
    private Intent gsensorservice;
    private ImageView gpsData;
    private ImageView sensorData;
    private ImageView mediaData;
    private ImageView cellInfo;
    private ImageView dingwei;
    private ImageView compass;
    private long mPressedTime = 0;
    private LatLng nowLatLng = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amapmain);
        
        GVariable.gpsOpen = true;
        TextView button_save = (TextView) findViewById(R.id.button_save);
        button_save.setOnClickListener(this);
        txt_posinfo = (TextView) findViewById(R.id.posinfo);
        state_text_gps = (TextView) findViewById(R.id.state_text_gps);
        location_type= (TextView) findViewById(R.id.location_type);
        headinfo= (TextView) findViewById(R.id.headinfo);
        otherinfo= (TextView) findViewById(R.id.otherinfo);
        poiname = (TextView) findViewById(R.id.poiname);
        addressname= (TextView) findViewById(R.id.addressname);
        mapgps = (TextView) findViewById(R.id.mapgps);
        gpsData_layout = (LinearLayout) findViewById(R.id.gpsData_layout);
        
        btn_collect = (Button) findViewById(R.id.button_collect);
        btn_collect.setOnClickListener(this);
        gpsData = (ImageView) findViewById(R.id.gpsData);
        gpsData.setOnClickListener(this);
        sensorData = (ImageView) findViewById(R.id.sensorData);
        sensorData.setOnClickListener(this);
        mediaData = (ImageView) findViewById(R.id.mediaData);
        mediaData.setOnClickListener(this);
        cellInfo = (ImageView) findViewById(R.id.cellInfo);
        cellInfo.setOnClickListener(this);
        dingwei = (ImageView) findViewById(R.id.dingwei);
        dingwei.setOnClickListener(this);
        compass  = (ImageView) findViewById(R.id.compass);
        compass.setOnClickListener(this);
        
        mMapView = (MapView) findViewById(R.id.amapView);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        
        MyLocationStyle myLocationStyle= new MyLocationStyle();
        //������λ�����㲻���ƶ�����ͼ���ĵ㣬�������������豸�ƶ���
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.anchor(0.5f, 0.5f);
        myLocationStyle.interval(1000);
        myLocationStyle.strokeColor(STROKE_COLOR);
        myLocationStyle.radiusFillColor(FILL_COLOR);
        myLocationStyle.strokeWidth(1f);
        
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setMyLocationEnabled(true); 
        
        aMap.showIndoorMap(true);
        
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMyLocationChangeListener(this);
        
        //ע��GPS�㲥���գ���ȡGPS����
        registerReceiver(gpsRefreshReceiver, gpsIntervalIntentFilter());
        
        //��ʼ��
        Pt_Arry = new ArrayList<LatLng>();
        
        //��������������
        gpsservice = new Intent(this, GPSDataService.class);
        startService(gpsservice);
        
        gsensorservice = new Intent(this, GSensorService.class);
        startService(gsensorservice);
        
        writeAllCellInfo();
        
        Timer timer = new Timer();
        timer.schedule(titask, 1000, 1000);
    }

    private void writeAllCellInfo()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        
        String content = "";
        content += "Ӳ��������:" + android.os.Build.MANUFACTURER + "\n";
        content += "Ʒ������:" + android.os.Build.BRAND + "\n";
        content += "��������:" + android.os.Build.BOARD + "\n";
        content += "�豸��:" + android.os.Build.DEVICE + "\n";
        content += "�ֻ��ͺ�:" + android.os.Build.MODEL + "\n";
        content += "��Ʒ����:" + android.os.Build.PRODUCT + "\n";
        content += "�豸ʶ����:" + android.os.Build.FINGERPRINT + "\n";
        content += "Ӳ�����к�:" + android.os.Build.ID + "\n";
        content += "��׿�汾��:" + android.os.Build.VERSION.RELEASE + "\n";
        content += "ָ�����:" + android.os.Build.CPU_ABI + "\n";
        content += "�������б�:" + "\n";
        
        for (Sensor item : sensors) {
            content +=
                item.getName() + ",  " + PhoneInfoView.SensorType(item.getType()) + ",  �汾:" + 
                item.getVersion() + ",  ����:" + item.getVendor() + ",  �ֱ���:" + 
                item.getResolution() + ",  ����:" + item.getMaximumRange() + ",  �ܺ�:" + 
                item.getPower() + ",  ��С���:" + item.getMinDelay() + "\n";
        }
        
        TxtUtil.saveTxt(content, GVariable.sensorPath+"phone_sensor/", "phone_sensor_" + GVariable.dayFormat.format(new Date()) + ".txt");
    }
    
    @Override
    public void onMyLocationChange(android.location.Location location)
    {
        // TODO Auto-generated method stub
        nowLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        
        if(isFirstLocate)
        {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowLatLng, 17));
            isFirstLocate = false;
        }
    }
    
     @Override
     protected void onDestroy() {
        unregisterReceiver(gpsRefreshReceiver);
        GVariable.gpsOpen = false;
        mMapView.onDestroy();
        
        if (gpsservice != null)
        {
            stopService(gpsservice);
            gpsservice = null;
        }
        if (gsensorservice != null)
        {
            stopService(gsensorservice);
            gsensorservice = null;
        }
        
        super.onDestroy();
     }
     
     @Override
     protected void onResume() {
        super.onResume();
        mMapView.onResume();
        init_collect();
        aMap.clear();
     }
     
     @Override
     protected void onPause() {
        super.onPause();
        mMapView.onPause();
     }
     
     @Override
     protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
     }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.gpsData:
                if(gpsData_layout.getVisibility() == View.VISIBLE)
                    gpsData_layout.setVisibility(View.GONE);
                else
                    gpsData_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.sensorData:           
                intent = new Intent(this, fanzai_DataCollectActivity.class);
                startActivity(intent);
                break;
            case R.id.mediaData:
                intent = new Intent(this, fanzai_TakePhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.cellInfo:
                intent = new Intent(this, PhoneInfoView.class);
                startActivity(intent);
                break;
            case R.id.button_collect:
                collect_click();
                break;
            case R.id.dingwei:
                gotoNowPos();
                break;
            case R.id.compass:
                aMap.animateCamera(CameraUpdateFactory.changeBearing(360));
                break;
            default:
                break;
        }
    }
    
    private void gotoNowPos()
    {
        // TODO Auto-generated method stub
        if(nowLatLng != null)
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowLatLng,17));
        else {
            HLog.toast(getApplicationContext(), "GPS����");
        }
    }
    
    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        long mNowTime = System.currentTimeMillis();//��ȡ��һ�ΰ���ʱ��
        if((mNowTime - mPressedTime) > 2000){//�Ƚ����ΰ���ʱ���
            HLog.toast(this, "�ٰ�һ���˳�����");
            mPressedTime = mNowTime;
        }
        else{//�˳�����
            this.finish();
            System.exit(0);
        }
    }
    
    private void init_collect()
    {
        if(GVariable.isCollect)
        {
            btn_collect.setText("���ڲɼ�...");
        }
        else 
        {
            btn_collect.setText("��ʼ�ɼ�");
        }
    }
    
    private void collect_click()
    {
        GVariable.isCollect = !GVariable.isCollect;
        
        if(GVariable.isCollect)
        {
            HLog.toast(getApplicationContext(), "��ʼ�ɼ�,���ݱ�������" + GVariable.sensorPath);
            btn_collect.setText("���ڲɼ�...");
        }
        else 
        {
            HLog.toast(getApplicationContext(), "ֹͣ�ɼ�");
            btn_collect.setText("��ʼ�ɼ�");
        }
    }
    
    public static String ACTION_REFRESH_GPS = "action.refresh.gps";
    
    private static IntentFilter gpsIntervalIntentFilter()
    { // ע����յ��¼�
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_GPS);
        intentFilter.addAction(GPSDataService.ACTION_GPS_ERROR);
        intentFilter.addAction(GPSDataService.ACTION_GPS_NORMAL);
        return intentFilter;
    }
    private final BroadcastReceiver gpsRefreshReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            try
            {
                if (ACTION_REFRESH_GPS.equals(action))
                {
                    double lat = intent.getDoubleExtra("lat", -1);
                    double log = intent.getDoubleExtra("log", -1);
                    if (lat > 0 && log > 0)
                    {
                        //����������Ϣ
                        String gpscoord = "���ȣ�" + GVariable.decimalFormat.format(log) +
                                          "��,  γ�ȣ�" + GVariable.decimalFormat.format(lat) + "��" ;
                        txt_posinfo.setText(gpscoord);                       
                    }
                    setGPSState();
                }
                else if (GPSDataService.ACTION_GPS_ERROR.equals(action))
                {
                    setGPSState();
                }
                else if (GPSDataService.ACTION_GPS_NORMAL.equals(action))
                {
                    setGPSState();
                }
            }
            catch (Exception ex)
            {
                // TODO: handle exception
                HLog.toast(getApplicationContext(), "GPS���մ���");
            }
        }
    };
    
    TimerTask titask = new TimerTask() {  
        @Override  
        public void run() { 
            runOnUiThread(new Runnable() {      // UI thread  
                @Override  
                public void run() {  
                    //txt_posinfo.setText(GVariable.BloothDataString);
                    String testString = "����ǣ�" + GVariable.aziMuth + "��, " + 
                                        "�����ǣ�" + GVariable.pitch + "��, " + 
                                        "�����ǣ�" + GVariable.roll + "��";
                    headinfo.setText(testString);
                    
                    testString = "����ǿ�ȣ�" + GVariable.LIGHT + "lx, ���룺" + GVariable.PROXIMITY + "cm, ��ѹ��" + GVariable.decimal3Format.format(GVariable.PRESSURE) + "hPa";
                    otherinfo.setText(testString);
                    
                    if(GVariable.isCollect)
                    {
                        addPointToMap();
                    }
                    else {
                        if(Pt_Arry.size() > 0)
                        {
                            makePtToPolygon();
                            Pt_Arry.clear();
                        }
                            
                    }
                }  
            });  
        }  
    };
    
    /**
     * ����GPS״̬
     */
    private void setGPSState()
    {
        if(GVariable.locationType==0)
        {
            location_type.setText("��λʧ��");
        }
        else  if(GVariable.locationType==1)
        {
            location_type.setText("GPS��λ��" + GVariable.gpsSolution);
        }
        else  if(GVariable.locationType==2)
        {
            location_type.setText("ǰ�ζ�λ");
        }
        else  if(GVariable.locationType==4)
        {
            location_type.setText("���涨λ");
        }
        else  if(GVariable.locationType==5)
        {
            location_type.setText("Wifi��λ");
        }
        else  if(GVariable.locationType==6)
        {
            location_type.setText("��վ��λ");
        }
        else  if(GVariable.locationType==8)
        {
            location_type.setText("���߶�λ");
        }
        else {
            location_type.setText("���綨λ");
        }
        
        if(GVariable.gpsState==AMapLocation.GPS_ACCURACY_UNKNOWN)
        {
            state_text_gps.setText("GPS״̬δ֪");
            gpsData.setImageResource(R.drawable.map_gps_error);
        }
        else if(GVariable.gpsState==AMapLocation.GPS_ACCURACY_BAD)
        {
            state_text_gps.setText("GPS�ź���");
            gpsData.setImageResource(R.drawable.map_gps_bad);
        }
        else if(GVariable.gpsState==AMapLocation.GPS_ACCURACY_GOOD)
        {
            state_text_gps.setText("GPS�ź�ǿ");
            gpsData.setImageResource(R.drawable.map_gps_ok);
        }
        //��λʧ����ʾ��λ������
        if(GVariable.amapLocation != null && GVariable.amapLocation.getErrorCode() != 0)
            state_text_gps.setText(state_text_gps.getText() + ":" + GVariable.amapLocation.getErrorCode());
        
    }
    
    public void addPointToMap()
    {
        if(nowLatLng != null && GVariable.isCollect)
        {
            aMap.clear();
            //add marker
            aMap.addMarker(new MarkerOptions().position(nowLatLng).snippet("DefaultMarker").draggable(false));
            
            //���ĵ����List
            Pt_Arry.add(nowLatLng);
        }
    }

    public void makePtToPolygon()
    {
        if(Pt_Arry.size() > 2)
        {
            aMap.addPolygon(new PolygonOptions()
            .addAll(Pt_Arry)
            .fillColor(Color.LTGRAY).strokeColor(Color.RED).strokeWidth(1));
        }
    }
    
    GeocodeSearch geocoderSearch;
    
    public LatLng convertGPS(LatLng ptLatLng)
    {
        CoordinateConverter converter  = new CoordinateConverter(getApplicationContext());
        converter.from(CoordType.GPS);
        converter.coord(ptLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    private float lastBearing = 0;
    private RotateAnimation rotateAnimation;
    
    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        // TODO Auto-generated method stub
        startIvCompass(cameraPosition.bearing);
    }
    
    private void startIvCompass(float bearing) {
        bearing = 360 - bearing;
        rotateAnimation = new RotateAnimation(lastBearing, bearing, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);

        compass.startAnimation(rotateAnimation);
        lastBearing = bearing;
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition)
    {
        // TODO Auto-generated method stub
        geocoderSearch = new GeocodeSearch(getApplicationContext());
        geocoderSearch.setOnGeocodeSearchListener(this);
        LatLonPoint latLonPoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 25, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result , int rCode)
    {
        // TODO Auto-generated method stub
        GVariable.geocodeResult = result;
        String formatAddress = result.getRegeocodeAddress().getFormatAddress();
        String formatAoi = result.getRegeocodeAddress().getAois().get(0).getAoiName();
        poiname.setText("ѡ��λ��:" + formatAoi);
        addressname.setText("��ַ:" + formatAddress);
    }
    
}
