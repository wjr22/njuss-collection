package com.njuss.collection.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.njuss.collection.base.User;
import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 处理用户服务，加载用户对应的商铺
 * @author wangj
 * @see com.njuss.collection.base.User
 */
public class UserService {

    private Context context;

    private GridConductor conductor;

    private DBHelper dbHelper;

    private Map<String, Store>  unfinished;

    private Map<String, Store>  finished;

    public UserService(Context context){
        conductor = User.getConductor();
        dbHelper = new DBHelper(context);
    }

    /**
     *
     * @param context
     */
    public void setMap(Context context){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Store> stores = new LinkedList<>();
        Cursor cursor = db.query("tStores",null, "conductorID=?", new String[]{conductor.getConductorID().toString()},null,null,null);
        if(cursor.moveToFirst()){
            while(!cursor.isLast()){
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
                cursor.moveToNext();
            }
        }
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
                setMap(this.context);
                return true;
        }
        return cursor.getCount() != 1;
    }

    public boolean checkUserByMobile(String mobile, String pwd) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("tGridConductor", null, "conductorMobile=?", new String[]{mobile}, null, null, null);
        if (cursor.getCount() == 1)
            if (cursor.getString(cursor.getColumnIndex("conductorPwd")).equals(pwd)){
                GridConductor conductor = new GridConductor();
                conductor.setConductorID(cursor.getInt(cursor.getColumnIndex("conductorID")));
                conductor.setConductorMobile(cursor.getString(cursor.getColumnIndex("conductorMobile")));
                conductor.setConductorName(cursor.getString(cursor.getColumnIndex("conductorName")));
                conductor.setDistrictCode(cursor.getString(cursor.getColumnIndex("districtCode")));
                this.conductor = conductor;
                setMap(this.context);
                return true;
            }
        return false;
    }
}
