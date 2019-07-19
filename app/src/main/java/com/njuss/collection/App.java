package com.njuss.collection;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.njuss.collection.base.Global;
import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;
import com.njuss.collection.tools.UploadData;

import java.util.Map;

public class App extends Application {

    private GridConductor conductor = null;

    private Map<String, Store> finished ;

    private Map<String, Store> unfinished;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Global.context = context;
        DBHelper dbHelper = new DBHelper(context);
    }

    public void setConductor(GridConductor conductor) {
        this.conductor = conductor;
    }

    /**
     * @TODO 存储图片的路径，先判断外存，再生成
     *
     */
    public void generateDir(){
        if(Environment.isExternalStorageEmulated())
    }
}
