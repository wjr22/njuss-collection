package com.njuss.collection;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.njuss.collection.base.Global;
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
     * @return dir path
     */
    public String generatePicDir(){
        String rootDir;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // SD媒体不可用，转为存储在本地
            File data = Environment.getDataDirectory();
            rootDir = data.getAbsolutePath();
        }else{
            File dir = Environment.getExternalStorageDirectory();
            rootDir = dir.getAbsolutePath();
        }
        rootDir += "/Pic/";
        return rootDir;
    }

    public Map<String, Store> getFinished() {
        return finished;
    }

    public Map<String, Store> getUnfinished() {
        return unfinished;
    }

    public void setFinished(Map<String, Store> finished) {
        this.finished = finished;
    }

    public void setUnfinished(Map<String, Store> unfinished) {
        this.unfinished = unfinished;
    }

    /**
     * 获取完成列表
     */
    public List<Store> getFinishedList(){
        Collection<Store> c = finished.values();
        List<Store> val = new ArrayList<>(c);
        return val;
    }

    /**
     * @return 未完成商户列表
     */
    public List<Store> getUnfinishedList(){
        Collection<Store> c = unfinished.values();
        List<Store> val = new ArrayList<>(c);
        return val;
    }

    public Store getStoreByLisence(String LisenceId){
        Store s = finished.get(LisenceId);
        Store g = unfinished.get(LisenceId);
        return s == null? s : g;
    }
}
