package com.njuss.collection.base;

import android.content.Context;
import android.os.Environment;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 全局信息
 */
public class Global {

    public static String rootPath = "/Tobacco";

    public final static String DataSavePath = rootPath+"/Data";

    public final static String PicSavePath = rootPath+"/Pic";

    public final static String[] ConductorFieldNames = {"conductorID", "conductorMobile", "conductorName",
            "conductorPwd", "districtCode"};

    public final static String[] DistrictFieldNames = {"districtLevel", "districtCode", "districtName",
            "parentCode", "districtFullname"};

    public final static String[] StoreFieldNames = {"licenseID",
            "conductorID", "storeName", "storeAddress", "GPSAddress",
            "GPSLongitude", "GPSLatitude", "storePicM", "storePicL",
            "storePicR", "licensePic"};

    public static Context context;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat MildateFormat = new SimpleDateFormat("yyMMddHHmmss_SSS");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat shorttimeFormat = new SimpleDateFormat("yyMMddHHmmss");
    public static SimpleDateFormat minuteFormat = new SimpleDateFormat("yyMMddHHmm");
    public static SimpleDateFormat recordFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
    public static DecimalFormat decimalFormat = new DecimalFormat("0.000000");
    public static DecimalFormat decimal4Format = new DecimalFormat("0.0000");
    public static DecimalFormat decimal3Format = new DecimalFormat("0.0");

    public static int gpsInterval = 1;//gps间隔时间
    public static AMapLocation amapLocation;//当前GPS信息
    public static int  locationType=0;//高德定位类型
    public static int  gpsState=-1;//高德GPS信号状态
    public static String  aoiName="";//高德定位poi
    public static String  addRess="";//高德定位地址
    public static String  district="";//高德定位行政区
    public static String  gpsSolution="";//GPS定位状态:单点差分
    public static RegeocodeResult geocodeResult;//高德地图选点查询结果
    public static LatLonPoint geocodePosition;//高德地图选点查询坐标(带纠偏)
    public static LatLonPoint afttrans_Pt=null;//转换后GPS坐标
    public static boolean gpsOpen = false;//gps打开
    public static boolean isCollect = false;//是否采集记录

    //Sensor Number
    public static int aziMuth = 0;//当前方向角
    public static int pitch = 0;//当前俯仰角
    public static int roll = 0;//当前翻滚角
    public static float PRESSURE = 0;//压强
    public static float PROXIMITY = 0;//距离
    public static float LIGHT = 0;//光照强度
    public static float AMBIENT_TEMPERATURE = 0;//温度
    public static float RELATIVE_HUMIDITY = 0;//湿度
    public static float ACCELEROMETER_X = 0;//加速度X
    public static float ACCELEROMETER_Y = 0;//加速度Y
    public static float ACCELEROMETER_Z = 0;//加速度Z
    public static float MAGNETIC_FIELD_X = 0;//磁场强度X
    public static float MAGNETIC_FIELD_Y = 0;//磁场强度Y
    public static float MAGNETIC_FIELD_Z = 0;//磁场强度Z
    public static float GYROSCOPE_X = 0;//陀螺X轴角速度
    public static float GYROSCOPE_Y = 0;//陀螺Y轴角速度
    public static float GYROSCOPE_Z = 0;//陀螺Z轴角速度
    public static float GRAVITY_X = 0;//重力加速度X
    public static float GRAVITY_Y = 0;//重力加速度Y
    public static float GRAVITY_Z = 0;//重力加速度Z
    public static float LINEAR_ACCELERATION_X = 0;
    public static float LINEAR_ACCELERATION_Y = 0;
    public static float LINEAR_ACCELERATION_Z = 0;
    public static String sensorPath;
}
