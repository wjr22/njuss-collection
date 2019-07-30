package com.njuss.collection;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.njuss.collection.base.Global;
import com.njuss.collection.base.User;
import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
import com.njuss.collection.tools.DBHelper;
import com.njuss.collection.tools.FileUtil;
import com.njuss.collection.tools.UploadData;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * @return dir path
     */
    public String generatePicDir() {
        String rootDir;
        FileUtil.RefStorageVolume[] volumes  = FileUtil.getVolumeList(getApplicationContext());
        Log.d("ENVIRONMENT STATE" , "==="+Environment.getExternalStorageState());
        if (volumes.length == 1) {
            // SD媒体不可用，转为存储在本地
            File data = null;
            try {
                data = volumes[0].getPathFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rootDir = data.getAbsolutePath();
        } else {
            File dir = null;
            try {
                dir = volumes[1].getPathFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rootDir = dir.getAbsolutePath();
        }
        //rootDir += "/Tobacco/Pic/";
        File dir = new File(rootDir, "Tobacco/Pic");
        if(!dir.exists())
            if(dir.mkdirs())
                Log.d("GENERATE DIR ", "generatePicDir: SUCCESS! ======");
            else Log.d("GENERATE DIR ", "generatePicDir: FAILED! ======");

        Log.d("OUTPUT PIC in :", dir.getAbsolutePath());
        return dir.getAbsolutePath();
    }

    private static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
