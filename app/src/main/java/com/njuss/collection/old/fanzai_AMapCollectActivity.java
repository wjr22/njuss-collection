package com.njuss.collection.old;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.njuss.collection.R;
import com.njuss.collection.base.CheckPermissionsActivity;
import com.njuss.collection.base.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class fanzai_AMapCollectActivity extends CheckPermissionsActivity implements OnClickListener, OnCameraChangeListener, OnGeocodeSearchListener, OnMyLocationChangeListener
{

    private static final String TAG = "fanzai_AMapCollectActivity";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private MapView mMapView;
    private AMap aMap;
    private Button btn_collect;
    private LinearLayout gpsData_layout;
    private TextView txt_posinfo;
    private TextView state_text_gps;
    private TextView location_type;
    private TextView headinfo;
    private TextView poiname;
    private TextView addressname;
    private TextView mapgps;
    
    
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

    private String location;

    private String GPSLongitude;

    private String GPSLatitude;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amapmain);
        
        Global.gpsOpen = true;
        TextView button_save = (TextView) findViewById(R.id.button_save);
        button_save.setOnClickListener(this);
        txt_posinfo = (TextView) findViewById(R.id.posinfo);
        state_text_gps = (TextView) findViewById(R.id.state_text_gps);
        location_type= (TextView) findViewById(R.id.location_type);
        headinfo= (TextView) findViewById(R.id.headinfo);
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
        //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
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
        
        //注册GPS广播接收，获取GPS坐标
        registerReceiver(gpsRefreshReceiver, gpsIntervalIntentFilter());
        
        //初始化
        Pt_Arry = new ArrayList<LatLng>();
        
        //启动传感器服务
        gpsservice = new Intent(this, GPSDataService.class);
        startService(gpsservice);
        
        //writeAllCellInfo();
        
        Timer timer = new Timer();
        timer.schedule(titask, 1000, 1000);
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
        Global.gpsOpen = false;
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
            Toast.makeText(getApplicationContext(), "GPS错误",Toast.LENGTH_LONG);
        }
    }
    
    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if((mNowTime - mPressedTime) > 2000){//比较两次按键时间差
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT);
            mPressedTime = mNowTime;
        }
        else{//退出程序
            this.finish();
            System.exit(0);
        }
    }
    
    private void init_collect()
    {
        if(Global.isCollect)
        {
            btn_collect.setText("正在采集...");
        }
        else 
        {
            btn_collect.setText("开始采集");
        }
    }
    
    private void collect_click()
    {
        Global.isCollect = !Global.isCollect;
        
        if(Global.isCollect)
        {
            Toast.makeText(getApplicationContext(), "开始采集,数据保存至：" + Global.sensorPath, Toast.LENGTH_SHORT).show();
            btn_collect.setText("正在采集...");
        }
        else 
        {
            Toast.makeText(getApplicationContext(), "停止采集",Toast.LENGTH_SHORT).show();
            btn_collect.setText("开始采集");
            Intent it = new Intent();
            it.putExtra("location", location);
            it.putExtra("GPSLatitude",GPSLatitude);
            it.putExtra("GPSLongitude", GPSLongitude);
            setResult(101, it);
            finish();
        }
    }
    
    public static String ACTION_REFRESH_GPS = "action.refresh.gps";
    
    private static IntentFilter gpsIntervalIntentFilter()
    { // 注册接收的事件
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
                        //更新坐标信息
                        String gpscoord = "经度：" + Global.decimalFormat.format(log) +
                                          "°,  纬度：" + Global.decimalFormat.format(lat) + "°" ;
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
                //HLog.toast(getApplicationContext(), "GPS接收错误");
            }
        }
    };
    
    TimerTask titask = new TimerTask() {
        @Override
        public void run() { 
            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {  
                    //txt_posinfo.setText(Global.BloothDataString);
                    String testString = "方向角：" + Global.aziMuth + "°, " +
                                        "俯仰角：" + Global.pitch + "°, " +
                                        "翻滚角：" + Global.roll + "°";
                    headinfo.setText(testString);
                    
                    testString = "光照强度：" + Global.LIGHT + "lx, 距离：" + Global.PROXIMITY + "cm, 气压：" + Global.decimal3Format.format(Global.PRESSURE) + "hPa";
                    
                    if(Global.isCollect)
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
     * 设置GPS状态
     */
    private void setGPSState()
    {
        if(Global.locationType==0)
        {
            location_type.setText("定位失败");
        }
        else  if(Global.locationType==1)
        {
            location_type.setText("GPS定位：" + Global.gpsSolution);
        }
        else  if(Global.locationType==2)
        {
            location_type.setText("前次定位");
        }
        else  if(Global.locationType==4)
        {
            location_type.setText("缓存定位");
        }
        else  if(Global.locationType==5)
        {
            location_type.setText("Wifi定位");
        }
        else  if(Global.locationType==6)
        {
            location_type.setText("基站定位");
        }
        else  if(Global.locationType==8)
        {
            location_type.setText("离线定位");
        }
        else {
            location_type.setText("网络定位");
        }
        
        if(Global.gpsState== AMapLocation.GPS_ACCURACY_UNKNOWN)
        {
            state_text_gps.setText("GPS状态未知");
            gpsData.setImageResource(R.drawable.map_gps_error);
        }
        else if(Global.gpsState== AMapLocation.GPS_ACCURACY_BAD)
        {
            state_text_gps.setText("GPS信号弱");
            gpsData.setImageResource(R.drawable.map_gps_bad);
        }
        else if(Global.gpsState== AMapLocation.GPS_ACCURACY_GOOD)
        {
            state_text_gps.setText("GPS信号强");
            gpsData.setImageResource(R.drawable.map_gps_ok);
        }
        //定位失败显示定位错误码
        if(Global.amapLocation != null && Global.amapLocation.getErrorCode() != 0)
            state_text_gps.setText(state_text_gps.getText() + ":" + Global.amapLocation.getErrorCode());
        
    }
    
    public void addPointToMap()
    {
        if(nowLatLng != null && Global.isCollect)
        {
            aMap.clear();
            //add marker
            aMap.addMarker(new MarkerOptions().position(nowLatLng).snippet("DefaultMarker").draggable(false));
            
            //中心点插入List
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
        Global.geocodeResult = result;
        String formatAddress = result.getRegeocodeAddress().getFormatAddress();
        String formatAoi = result.getRegeocodeAddress().getAois().get(0).getAoiName();
        poiname.setText("选点位置:" + formatAoi);
        addressname.setText("地址:" + formatAddress);

    }
    
}
