package com.njuss.collection;

import android.app.Application;
import android.content.Context;

import com.njuss.collection.beans.GridConductor;
import com.njuss.collection.beans.Store;
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
        UploadData uploadData = new UploadData(context);
        //uploadData.insertForTest();
        //uploadData.insertDistrict(context);
    }

    public void setConductor(GridConductor conductor) {
        this.conductor = conductor;
    }
}
