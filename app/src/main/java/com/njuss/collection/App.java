package com.njuss.collection;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.njuss.collection.base.Global;
import com.njuss.collection.base.User;
import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;
import com.njuss.collection.tools.UploadData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class App extends Application {

    private GridConductor conductor = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Global.context = context;
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("LOADING DDATA", dbHelper.getDatabaseName()+" SUCCESS！=====");
        User user = User.getInstance();
    }

    public void setConductor(GridConductor conductor) {
        this.conductor = conductor;
    }

    /**
     * @TODO 存储图片的路径，先判断外存，再生成
     *
     * @return dir path
     */
    public String generatePicDir() {
        String rootDir;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // SD媒体不可用，转为存储在本地
            File data = Environment.getDataDirectory();
            rootDir = data.getAbsolutePath();
        } else {
            File dir = Environment.getExternalStorageDirectory();
            rootDir = dir.getAbsolutePath();
        }
        rootDir += "/Pic/";
        return rootDir;
    }
}
