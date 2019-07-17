package com.njuss.collection.service;

import android.content.Context;

import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;

import java.util.Map;

/**
 * 数据初始化类，即登录后获取该用户的相关内容
 */
public class DataInit {

    private DBHelper dbHelper;

    private Map<String, Store> finished ;

    private Map<String, Store> unfinished;

    public DataInit(Context context){
        dbHelper = new DBHelper(context);
    }
}
