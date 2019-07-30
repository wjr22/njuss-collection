package com.njuss.collection.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.njuss.collection.base.User;
import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

import java.util.Map;
import java.util.TreeMap;

/**
 * 处理用户服务，加载用户对应的商铺\验证身份
 * HOW TO USE:
 *      加载已完成/未完成商铺：
 *
 * @author wangj
 * @see User
 */
public class UserService {

    private Context context;

    private GridConductor conductor;

    private DBHelper dbHelper;

    public Map<String, Store>  unfinished;

    public Map<String, Store>  finished;


    public UserService(Context context){
        conductor = User.getConductor();
        dbHelper = new DBHelper(context);
        unfinished = new TreeMap<>();
        finished = new TreeMap<>();

    }

    /**
     * 设置已完成和未完成Map，采用treeMap保证原址
     */
    public void setMap(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("tStores",null, "conductorID=?", new String[]{conductor.getConductorID().toString()},null,null,null);
        Log.d("DB Operate --:", "select by conduct id sum "+ cursor.getCount());
        cursor.moveToFirst();
        do{
            String[] ss = cursor.getColumnNames();
            Store store = new Store();
            store.setStoreName(cursor.getString(cursor.getColumnIndex("storeName")));
            store.setStoreAddress(cursor.getString(cursor.getColumnIndex("storeAddress")));
            store.setLicenseID(cursor.getString(cursor.getColumnIndex("licenseID")));
            store.setGPSAddress(cursor.getString(cursor.getColumnIndex("GPSAddress")));
            store.setGPSLatitude(cursor.getString(cursor.getColumnIndex("GPSLatitude")));
            store.setStorePicL(cursor.getString(cursor.getColumnIndex("storePicL")));
            store.setLicensePic(cursor.getString(cursor.getColumnIndex("licensePic")));
            store.setGPSLongitude(cursor.getString(cursor.getColumnIndex("GPSLongitude")));
            store.setConductorID(cursor.getInt(cursor.getColumnIndex("conductorID")));
            store.setStorePicM(cursor.getString(cursor.getColumnIndex("storePicM")));
            store.setStorePicR(cursor.getString(cursor.getColumnIndex("storePicR")));
            if(store.getStoreAddress() == null){
                Log.d("unfinished stores list ", "add One "+ store.getLicenseID()+" =====");
                unfinished.put(store.getLicenseID(), store);
            }else{
                Log.d("finished stores list ", "add One "+ store.getLicenseID()+" =====");
                finished.put(store.getLicenseID(),store);
            }
        }while(cursor.moveToNext());
    }

    public boolean checkUser(String name){
        return false;
    }

    /**
     * @since v1.0.1 没有密码验证
     * @param mobile
     * @return
     */
    public boolean checkUserByMobile(String mobile){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("tGridConductor", null, "conductorMobile=?", new String[]{mobile},null,null,null);
        if (cursor.getCount() == 1) {
            GridConductor conductor = new GridConductor();
            cursor.moveToFirst();
            conductor.setConductorID(cursor.getInt(cursor.getColumnIndex("conductorID")));
            conductor.setConductorMobile(cursor.getString(cursor.getColumnIndex("conductorMobile")));
            conductor.setConductorName(cursor.getString(cursor.getColumnIndex("conductorName")));
            conductor.setDistrictCode(cursor.getString(cursor.getColumnIndex("districtCode")));
            this.conductor = conductor;
            Log.d("LOADING DATA --", "STARTING!=======");
            setMap();
            Log.d("LOADING DATA --", "END!=======");
            return true;
        }
        return false;
    }

    public boolean checkUserByMobile(String mobile, String pwd) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("tGridConductor", null, "conductorMobile=?", new String[]{mobile}, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            if (cursor.getString(cursor.getColumnIndex("conductorPwd")).equals(pwd)) {
                GridConductor conductor = new GridConductor();
                conductor.setConductorID(cursor.getInt(cursor.getColumnIndex("conductorID")));
                conductor.setConductorMobile(cursor.getString(cursor.getColumnIndex("conductorMobile")));
                conductor.setConductorName(cursor.getString(cursor.getColumnIndex("conductorName")));
                conductor.setDistrictCode(cursor.getString(cursor.getColumnIndex("districtCode")));
                this.conductor = conductor;
                setMap();
                return true;
            }
        }
        return false;
    }


}
