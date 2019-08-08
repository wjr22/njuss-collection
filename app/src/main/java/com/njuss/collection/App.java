package com.njuss.collection;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tech.oom.idealrecorder.IdealRecorder;

public class App extends Application {

    private static App instance;
    public static App getInstance() {
        return instance;
    }
    private GridConductor conductor = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Global.context = context;
        instance = App.this;
        IdealRecorder.init(App.this);
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("LOADING DDATA", dbHelper.getDatabaseName()+" SUCCESS！=====");
        User user = User.getInstance();
        Log.d("SHA1 ====", sHA1(getApplicationContext()));
    }

    public void setConductor(GridConductor conductor) {
        this.conductor = conductor;
    }

    /**
     * @return dir path
     */
    public String PicDir() {
        String rootDir = generateDir();
        //rootDir += "/Tobacco/Pic/";
        File _dir = new File(rootDir, "Tobacco/Pic");
        if(!_dir.exists())
            if(_dir.mkdirs())
                Log.d("GENERATE DIR ", "PicDir: SUCCESS! ======");
            else Log.d("GENERATE DIR ", "PicDir: FAILED! ======");
        return _dir.getAbsolutePath();
    }

    public String DataDir(){
        String rootDir = generateDir();
        //rootDir += "/Tobacco/Pic/";
        File _dir = new File(rootDir, "Tobacco/Data");
        if(!_dir.exists())
            if(_dir.mkdirs())
                Log.d("GENERATE DIR ", "getDataDir: SUCCESS! ======");
            else Log.d("GENERATE DIR ", "getDataDir: FAILED! ======");
        return _dir.getAbsolutePath();
    }

    private String generateDir(){
        String rootDir;
        FileUtil.RefStorageVolume[] volumes  = FileUtil.getVolumeList(getApplicationContext());
        Log.d("ENVIRONMENT STATE" , "==="+Environment.getExternalStorageState());
        Log.d("Storage volumes length", String.valueOf(volumes.length));
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
        return rootDir;
    }

    public boolean deleteFile(String filename) {
        if (TextUtils.isEmpty(filename)){
            return false;
        }
        File file = new File(filename);
        return  file.exists() ? file.delete() : false;
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
    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
