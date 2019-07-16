package com.njuss.collection.service;

import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;

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

    private static GridConductor conductor = null;

    private static Map<String, Store> finished ;

    private static Map<String, Store> unfinished;

    private User(){
        finished = new LinkedHashMap<>();
        unfinished = new LinkedHashMap<>();
    }

    public void setMap(){

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
