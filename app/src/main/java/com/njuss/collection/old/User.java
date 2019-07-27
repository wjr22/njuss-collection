package com.njuss.collection.old;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 单例类，用来控制用户唯一性
 * 登录时验证成功后使用 setInstance(GridConductor)设置用户
 * 此类全局存储该操作员所属的所有stores（Map类，key为store.lisenceID）
 * @author wangj
 * @see GridConductor
 * @see Store
 */
public class User {

    private static User instance = null;

    private static Context context;             //上下文信息

    private static GridConductor conductor = null;

    private static Map<String, Store> finished ;

    private static Map<String, Store> unfinished;

    private User(){
        finished = new LinkedHashMap<>();
        unfinished = new LinkedHashMap<>();
    }

    public void setMap(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("tStores",null, "conductorID=?", new String[]{conductor.getConductorID().toString()},null,null,null);
        if(cursor.moveToFirst()){
            while(!cursor.isLast()){
                cursor.getColumnNames();
                cursor.moveToNext();
            }
        }
    }

    public static User getInstance(){
        if(instance == null)
            return null;
        else return instance;
    }

    public static void setInstance(GridConductor _conductor){
        synchronized (User.class){
            instance = new User();
            setConductor(_conductor);
        }
    }

    private static void setConductor(GridConductor _conductor){
        conductor = _conductor;
    }

    public static GridConductor getConductor() {
        return conductor;
    }
}
