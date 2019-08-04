package com.njuss.collection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.njuss.collection.tools.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.amap.api.col.n3.id.a;

public class GaodeLocationActivity extends AppCompatActivity implements LocationSource,
        AMap.OnMapLongClickListener,AMap.OnMarkerDragListener,AMap.OnMapClickListener,AMapLocationListener, AMap.OnMarkerClickListener {

    private MapView mapView;

    private AMap aMap;

    private UiSettings uiSettings;

    public static AMapLocationClient mLocationClient = null;
    public static AMapLocationClientOption mLocationOption = null;

    private Button btnFinished;

    private LocationSource.OnLocationChangedListener mListener;

    // 标识首次定位
    private boolean isFirstLocation = true;
    private String location = "";
    private String GPSLatitude = "";
    private String GPSLongitude = "";
    boolean control = false;

    private TextView tv_location;
    private TextView tv_longtitude;
    private TextView tv_latitude;
    private TextView tv_percision;

    private Boolean manual = false; // 手动档
    private Boolean timeover = false; // 5秒后未成功强制手动
    private int i = 0;
    private int t=0;
    private Double accuracy = 1000.0; //精度

    @Override
    protected void onResume() {
        super.onResume();
        // 重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_location);
        tv_latitude = (TextView) findViewById(R.id.txt_latitude);
        tv_location = (TextView) findViewById(R.id.txt_location);
        tv_longtitude = (TextView) findViewById(R.id.txt_longitude);
        tv_percision = (TextView) findViewById(R.id.txt_GPS_percision);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            Toast.makeText(GaodeLocationActivity.this, "请打开WIFI进行定位！",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
        }
        initView();
        // 创建地图
        mapView.onCreate(savedInstanceState);
        initGaoDeMap();
        btnFinished = (Button)findViewById(R.id.btn_mapfinish);
        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.putExtra("location", location);
                it.putExtra("GPSLatitude",GPSLatitude);
                it.putExtra("GPSLongitude", GPSLongitude);
                setResult(101, it);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁地图
        mapView.onDestroy();
    }
    @Override
    public void onBackPressed() {
        onDestroy();
    }
    /**
     * 重写此方法，在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initView() {

        // 实例化地图控件
        mapView = (MapView) findViewById(R.id.gaode_location_map);
        if (aMap == null) {
            // 显示地图
            aMap = mapView.getMap();
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        aMap.getUiSettings().setZoomControlsEnabled(true);
        // 设置地图默认的指南针是否显示
        aMap.getUiSettings().setCompassEnabled(true);
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setMyLocationStyle(myLocationStyle.myLocationIcon((BitmapDescriptorFactory.fromResource(R.drawable.diaoyan_pos))));
        uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        aMap.setLocationSource(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerDragListener(this);
        aMap.setOnMarkerClickListener(this);

    }

    /**
     * 初始化高德地图
     */
    public void initGaoDeMap() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(1000);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setHttpTimeOut(50000);
        mLocationOption.setLocationCacheEnable(false);
        // 设置是否只定位一次，默认为false
        //mLocationOption.setOnceLocation(true);
        //mLocationOption.setOnceLocationLatest(true);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动高德地图定位
        mLocationClient.startLocation();

    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        // 解析AMapLocation对象
        // 判断AMapLocation对象不为空，当定位错误码类型为0时定位成功
        StringBuffer sb = new StringBuffer();
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                t++;
                double accuracy = aMapLocation.getAccuracy();
                if(accuracy <= 30 && t <= 8) {

                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + aMapLocation.getLocationType() + "\n");
                    sb.append("经    度    : " + aMapLocation.getLongitude() + "\n");
                    sb.append("纬    度    : " + aMapLocation.getLatitude() + "\n");
                    sb.append("精    度    : " + accuracy + "米" + "\n");
                    sb.append("提供者    : " + aMapLocation.getProvider() + "\n");
                    sb.append("速    度    : " + aMapLocation.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + aMapLocation.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + aMapLocation.getSatellites() + "\n");
                    sb.append("国    家    : " + aMapLocation.getCountry() + "\n");
                    sb.append("省          : " + aMapLocation.getProvince() + "\n");
                    sb.append("市          : " + aMapLocation.getCity() + "\n");
                    sb.append("城市编码 : " + aMapLocation.getCityCode() + "\n");
                    sb.append("区            : " + aMapLocation.getDistrict() + "\n");
                    sb.append("区域 码   : " + aMapLocation.getAdCode() + "\n");
                    sb.append("地    址    : " + aMapLocation.getAddress() + "\n");
                    sb.append("兴趣点    : " + aMapLocation.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + Utils.formatUTC(aMapLocation.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                    sb.append("***定位质量报告***").append("\n");
                    sb.append("* WIFI开关：").append(aMapLocation.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                    sb.append("* GPS状态：").append(getGPSStatusString(aMapLocation.getLocationQualityReport().getGPSStatus())).append("\n");
                    sb.append("* GPS星数：").append(aMapLocation.getLocationQualityReport().getGPSSatellites()).append("\n");
                    sb.append("* 网络类型：" + aMapLocation.getLocationQualityReport().getNetworkType()).append("\n");
                    sb.append("* 网络耗时：" + aMapLocation.getLocationQualityReport().getNetUseTime()).append("\n");
                    sb.append("****************").append("\n");
                    //定位之后的回调时间
                    sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                    //解析定位结果，
                    String result = sb.toString();
                    Log.d("info---", result);
                    StringBuffer address = new StringBuffer();
                    aMapLocation.getLocationType(); // 获取当前定位结果来源，如网络定位结果，详见定位类型表
                    GPSLatitude = String.valueOf(aMapLocation.getLatitude()); // 获取纬度
                    GPSLongitude = String.valueOf(aMapLocation.getLongitude()); // 获取经度
                    //aMapLocation.getAccuracy(); // 获取精度信息
                    aMapLocation.getAddress(); // 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    address.append(aMapLocation.getCountry()); // 国家信息
                    address.append(aMapLocation.getProvince()); // 省信息
                    address.append(aMapLocation.getCity()); // 城市信息
                    address.append(aMapLocation.getDistrict()); // 城区信息
                    address.append(aMapLocation.getStreet()); // 街道信息
                    address.append(aMapLocation.getStreetNum()); // 街道门牌号信息
                    address.append(aMapLocation.getCityCode()); // 城市编码
                    address.append(aMapLocation.getAdCode()); // 地区编码
                    address.append(aMapLocation.getAoiName()); // 获取当前定位点的AOI信息
                    address.append(aMapLocation.getBuildingId()); // 获取当前室内定位的建筑物Id
                    address.append(aMapLocation.getFloor()); // 获取当前室内定位的楼层
                    aMapLocation.getGpsAccuracyStatus(); // 获取GPS的当前状态
                    location = aMapLocation.getAddress()+aMapLocation.getPoiName();
                    if (!control && t == 8) {
                        Toast.makeText(GaodeLocationActivity.this, "定位完成", Toast.LENGTH_LONG).show();
                        updateView(GPSLatitude, GPSLongitude, location, String.valueOf(accuracy));
                        control = true;
                    }
                } else {
                    if(t>8 && !control) {
                        // 设置手动档
                        Toast.makeText(GaodeLocationActivity.this, "定位精度不够，请手动选择点！", Toast.LENGTH_LONG).show();
                        manual = true;
                        control = true;
                    }
                }
                // 如果不设置标志位，拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLocation) {
                    // 设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    // 将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    // 点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    isFirstLocation = false;
                }
            } else {
                //定位失败
                if (!timeover) {
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + aMapLocation.getErrorCode() + "\n");
                    sb.append("错误信息:" + aMapLocation.getErrorInfo() + "\n");
                    sb.append("错误描述:" + aMapLocation.getLocationDetail() + "\n");
                    // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("HLQ_Struggle", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    Toast.makeText(GaodeLocationActivity.this, "定位失败！请检查并打开相关权限！", Toast.LENGTH_LONG).show();
                    timeover = true;
                }
            }
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }
    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode){
        String str = "";
        switch (statusCode){
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // 地图点击
        Log.d("======", "LatLng:==="+latLng.toString());
        // default set disable, if accuracy >= 30m, Users must set position through this functions.
        i++;
        if(manual && i == 1) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.title("选择点").snippet("");

            markerOption.draggable(true);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.nowpos)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(true);//设置marker平贴地图效果
            final Marker marker = aMap.addMarker(markerOption);
            marker.setClickable(true);
            location = getLocation(latLng);
            GPSLongitude = String.valueOf(latLng.longitude);
            GPSLatitude = String.valueOf(latLng.latitude);
            updateView(GPSLatitude, GPSLongitude, location, "无");
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // 拖拽结束
        LatLng ll = marker.getPosition();
        getLocation(ll);
        Log.d("======", "marker drag end[==");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 点击标记
        new AlertDialog.Builder(this).setTitle("手动选点")
                .setMessage("是否选择该点？\n 地址：")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("cancel",null).show();
        return false;
    }

    public String getLocation(final LatLng ll){
        final String[] res = new String[1];
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        LatLonPoint llp = new LatLonPoint(ll.latitude,ll.longitude);
        RegeocodeQuery query = new RegeocodeQuery(llp, 30,GeocodeSearch.GPS);
        geocoderSearch.getFromLocationAsyn(query);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                // 经纬度转地址
                StringBuffer address = new StringBuffer();
                RegeocodeAddress aMapLocation = regeocodeResult.getRegeocodeAddress();
                address.append(aMapLocation.getCountry()); // 国家信息
                address.append(aMapLocation.getProvince()); // 省信息
                address.append(aMapLocation.getCity()); // 城市信息
                address.append(aMapLocation.getDistrict()); // 城区信息
               // address.append(aMapLocation.getStreet()); // 街道信息
               // address.append(aMapLocation.getStreetNum()); // 街道门牌号信息
                address.append(aMapLocation.getCityCode()); // 城市编码
                address.append(aMapLocation.getAdCode()); // 地区编码
               // address.append(aMapLocation.getAoiName()); // 获取当前定位点的AOI信息
               // address.append(aMapLocation.getBuildingId()); // 获取当前室内定位的建筑物Id
               // address.append(aMapLocation.getFloor()); // 获取当前室内定位的楼层
                res[0] = aMapLocation.getFormatAddress();
                Log.d("======", res[0]);
                updateView(String.valueOf(ll.latitude), String.valueOf(ll.longitude), res[0], "无");
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                // 地址转高德经纬度

            }
        });
        return res[0];
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    public void updateView(String GPSLatitude,String  GPSLongitude, String location, String percision){
        tv_percision.setText("精度："+percision);
        tv_longtitude.setText("经度："+GPSLatitude);
        tv_latitude.setText("纬度："+GPSLongitude);
        tv_location.setText("位置："+location);
        this.location = location;
        this.GPSLongitude = GPSLongitude;
        this.GPSLatitude = GPSLatitude;
    }
}
