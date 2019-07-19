package com.njuss.collection.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.android.lb.util.TxtUtil;
import com.njuss.collection.base.Global;
import com.fanzai.voice.datacollect.fanzai_AMapCollectActivity;
import com.medo.demo.util.GPSUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @TODO �޸�GPS��λ����
 */
public class GPSDataService extends Service
{
    protected static final String TAG = "GPSDataService";
    private static GPSDataService instance;
    public static String ACTION_SET_GPS_INTERVAL = "action.set.gps.interval";
    public static final String ACTION_GPS_ERROR = "com.wydc.action.gps.error";
    public static final String ACTION_GPS_NORMAL = "com.wydc.action.gps.normal";

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        initLocation();
        initLocation_gps();
        registerReceiver(gpsIntervalReceiver, gpsIntervalIntentFilter());
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
        unregisterReceiver(gpsIntervalReceiver);
        super.onDestroy();
    }

    /**
     * ���ص�ʵ��
     * 
     * @return
     */
    public static GPSDataService getInstance()
    {
        return instance;
    }
    /********************************��λ******************************************/
    //����AMapLocationClient�����
    public AMapLocationClient mLocationClient = null;
    private LocationManager mLocationManager;
    //����mLocationOption����
    public AMapLocationClientOption mLocationOption = null;

    private void initLocation()
    {
        //��ʼ����λ
        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(Global.gpsInterval * 1000);
        mLocationOption.setSensorEnable(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMddHHmm");
    SimpleDateFormat fileNameHour = new SimpleDateFormat("yyyyMMddHH");
    SimpleDateFormat fileNameMinute = new SimpleDateFormat("yyyyMMdd_HHmm");
    //������λ�ص�������
    public AMapLocationListener mLocationListener = new AMapLocationListener()
    {
        @Override
        public void onLocationChanged(AMapLocation amapLocation)
        {
            // TODO Auto-generated method stub
            Global.amapLocation = null;
            if (amapLocation != null)
            {
                Global.gpsState =amapLocation.getGpsAccuracyStatus();
                Global.locationType=amapLocation.getLocationType();
                Global.aoiName = amapLocation.getAoiName();
                Global.addRess = amapLocation.getAddress();
                if (amapLocation.getErrorCode() == 0)
                {
                    Global.amapLocation = amapLocation;
                    double[] gps_corct = GPSUtil.GDtrans(amapLocation.getLongitude(), amapLocation.getLatitude());
                    String lat = Global.decimalFormat.format(gps_corct[1]);
                    String lng = Global.decimalFormat.format(gps_corct[0]);
                    
                    LatLonPoint outpt = new LatLonPoint(gps_corct[1], gps_corct[0]);
                    Global.afttrans_Pt = outpt;
                    
                    if (Global.isCollect)
                    {
                        String content = dateFormat.format(new Date()) + ",";
                                content += lng + "," + lat + ",";
                                content += Global.aziMuth + "," + Global.pitch + "," + Global.roll + ",";
                                content += Global.LIGHT + "," + Global.PROXIMITY + "," + Global.PRESSURE + ",";
                                content += Global.ACCELEROMETER_X + "," + Global.ACCELEROMETER_Y + "," + Global.ACCELEROMETER_Z + ",";
                                content += Global.MAGNETIC_FIELD_X + "," + Global.MAGNETIC_FIELD_Y + "," + Global.MAGNETIC_FIELD_Z + ",";
                                content += Global.GRAVITY_X + "," + Global.GRAVITY_Y + "," + Global.GRAVITY_Z + ",";
                                content += Global.GYROSCOPE_X + "," + Global.GYROSCOPE_Y + "," + Global.GYROSCOPE_Z + ",";
                                content += Global.LINEAR_ACCELERATION_X + "," + Global.LINEAR_ACCELERATION_Y + "," + Global.LINEAR_ACCELERATION_Z + ",";
                                content += Global.AMBIENT_TEMPERATURE + "," + Global.RELATIVE_HUMIDITY;
                                
                                TxtUtil.saveTxt(content, Global.sensorPath+"/" + fileNameHour.format(new Date()) + "/", fileNameMinute.format(new Date()) + ".csv");
                    }

                    if (Global.gpsOpen)
                    {
                        Intent intent = new Intent(fanzai_AMapCollectActivity.ACTION_REFRESH_GPS);
                        intent.putExtra("lat", gps_corct[1]);
                        intent.putExtra("log", gps_corct[0]);
                        sendBroadcast(intent);
                    }
                    sendBroadcast(new Intent(ACTION_GPS_NORMAL));
                }
                else
                {
                    sendBroadcast(new Intent(ACTION_GPS_ERROR));
                }
            }
            else
            {
                Global.gpsState = AMapLocation.GPS_ACCURACY_UNKNOWN;
                Global.locationType = 0;
                sendBroadcast(new Intent(ACTION_GPS_ERROR));
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private static IntentFilter gpsIntervalIntentFilter()
    { 
        // ע����յ��¼�
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SET_GPS_INTERVAL);
        return intentFilter;
    }
    private final BroadcastReceiver gpsIntervalReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (ACTION_SET_GPS_INTERVAL.equals(action))
            {
                mLocationClient.stopLocation();
                mLocationOption.setInterval(Global.gpsInterval * 1000);
                mLocationClient.setLocationOption(mLocationOption);
                mLocationClient.startLocation();
            }
        }
    };
    
    private void initLocation_gps()
    {
        //����GPS����
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 0, mGPSLocationListener);
            mLocationManager.addNmeaListener(mNmeaListener);
        }
    }
    
    Listener mStatusChanged = new Listener() {
        @Override
        public void onGpsStatusChanged(int arg0) {
        }
    };
    
    LocationListener mGPSLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location mlocal) {
            if(mlocal == null) return;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            // TODO Auto-generated method stub
            
        }
    };
       
    NmeaListener mNmeaListener = new NmeaListener() {
        @Override
        public void onNmeaReceived(long arg0, String arg1) {
                if(arg1.contains("GNGGA") || arg1.contains("GPGGA")){
                    String[] result = arg1.split(",");
                    if(result != null && result.length >= 11){
                        Global.gpsSolution = GetnSolutionState(Integer.parseInt(result[6]));
                    }
                }
                
        }
    };
    
    private String GetnSolutionState(int nType) {
        String strSolutionState = "";
        switch (nType) {
            case 0:
                strSolutionState = "��Ч��";
                break;
            case 1:
                strSolutionState = "�����";
                break;
            case 2:
                strSolutionState = "��ֽ�";
                break;
            case 3:
                strSolutionState = "PPS��";
                break;
            case 4:
                strSolutionState = "�̶���";
                break;
            case 5:
                strSolutionState = "�����";
                break;
            case 6:
                strSolutionState = "����ֵ";
                break;    
            default:
                strSolutionState = "" + nType;
                break;
        }
        return strSolutionState;
    }
    
}
