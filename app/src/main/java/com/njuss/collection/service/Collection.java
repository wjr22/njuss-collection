package com.njuss.collection.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

/**
 * 采集信息业务类，采集员（1） -> 零售商（1），主要是将数据写入数据库
 * HOW TO USE:
 *  实例化该类：
 *      Collection c = new Collection(getApplicationContext());
 *  设置收集店铺：
 *      c.setStore(Store);
 *  更新数据库：
 *      c.update();
 * @author wangj
 * @see GridConductor
 * @see Store
 *
 * @serial 同时承包了更新的任务，依旧是数据库操作
 *      使用构造器模式，需要传入两个参数：Store， Context
 */
public class Collection {

    private GridConductor   conductor;

    private Store           store;

    private DBHelper        dbHelper;

    private Collection(Builder builder){
        this.store = builder.store;
    }

    public Collection(Context context){
        dbHelper = new DBHelper(context);

    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean update(){
        if(this.store == null){
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("storeName",store.getStoreName());
        values.put("storeAddress",store.getStoreAddress());
        values.put("GPSAddress",store.getGPSAddress());
        values.put("GPSLongitude",store.getGPSLongitude());
        values.put("GPSLatitude",store.getGPSLatitude());
        values.put("licensePic",store.getLicensePic());
        values.put("storePicM",store.getStorePicM());
        values.put("storePicL",store.getStorePicL());
        values.put("storePicR",store.getStorePicR());
        db.update("tStores", values, "license=?", new String[]{store.getLicenseID()});
        return true;
    }


    public static class Builder{
        private Store   store;

        private Context context;

        public Builder setStore(Store _store){
            this.store = _store;
            return this;
        }

        public Builder setContext(Context _context){
            this.context = _context;
            return this;
        }

        public Collection Build(){
            return new Collection(this);
        }
    }

}
