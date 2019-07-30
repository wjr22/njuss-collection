package com.njuss.collection.base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        finished = new TreeMap<>();
        unfinished = new LinkedHashMap<>();
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

    public static void setConductor(GridConductor _conductor){
        conductor = _conductor;
    }

    public static GridConductor getConductor() {
        return conductor;
    }

    public static Map<String, Store> getFinished() {
        return finished;
    }

    public static Map<String, Store> getUnfinished() {
        return unfinished;
    }

    public static void setFinished(Map<String, Store> finished_) {
        finished = finished_;
    }

    public static void setUnfinished(Map<String, Store> unfinished_) {
        unfinished = unfinished_;
    }

    /**
     * 获取完成列表
     */
    public static List<Store> getFinishedList(){
        Collection<Store> c = finished.values();
        List<Store> val = new ArrayList<>(c);
        return val;
    }

    /**
     * @return 未完成商户列表
     */
    public static List<Store> getUnfinishedList(){
        Collection<Store> c = unfinished.values();
        List<Store> val = new ArrayList<>(c);
        return val;
    }

    public static Store getStoreByLisence(String LisenceId){
        Store s = finished.get(LisenceId);
        Store g = unfinished.get(LisenceId);
        return s == null? s : g;
    }
}
