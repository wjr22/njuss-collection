package com.njuss.collection.tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.njuss.collection.beans.District;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 数据导入导出用
 * @author wangj
 * @since v1.0.1
 */
public class UploadData {

    private Context context;

    private DBHelper dbHelper;
    private SQLiteDatabase db ;

    public UploadData(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
        db =  dbHelper.getWritableDatabase();
    }

    private UploadData(Builder builder){
        this.dbHelper = builder.dbHelper;
        this.context = builder.context;
        this.db = builder.db;
    }

    /**
     * 测试用例
     */
    public void insertForTest(){


        ContentValues contentValues = new ContentValues();      // 插入用户
        contentValues.put("conductorMobile","18051072519");
        contentValues.put("conductorName","王家若");
        contentValues.put("conductorPwd","18051072519");
        long id = db.insert("tGridConductor",null,contentValues);
        Log.d("DB Operate", "insert into tGridConductor "+id + " success!========");
        ContentValues storeValues = new ContentValues();        //插入商户
        storeValues.put("conductorID", id);
        storeValues.put("licenseID", "111110000011111");
        storeValues.put("storeName", "王家若的小店");
        long IDl = db.insert("tStores", null, storeValues);
        Log.d("DB Operate", "insert into tStores "+ IDl + " Success!========");
    }

    /**
     * 默认读取$PROJECT_HOME$/res/raw目录下的district.xlsx
     */
    public void insertDistrict(Context context){
        try {
            AssetManager asm = context.getAssets();
            InputStream in = asm.open("district.xlsx");
            List<String[]> ls = FileUtil.readExcel("district.xlsx", in);
            Log.d("DB Operate -- ", "in insertDistrict " + ls.size());
            for (int i=1; i<ls.size(); i++){
                ContentValues cv = new ContentValues();
                String[] t = ls.get(i);
                for(int j=0; j<t.length; j++) {
                    cv.put(District.names[j], t[j]);
                }
                Log.d("DB Operate ", "insert into district "+ cv.get(District.names[0])+" Success!======");
                db.insert("tDistrict",null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void dataInit(){
        insertDistrict(this.context);
    }

    /**
     * @TODO 导出数据到excel中
     */
    public void exportToExcel(){

    }

    public static class Builder{

        private Context context;

        private DBHelper dbHelper;

        private SQLiteDatabase db;

        public Builder setContext(Context _c){
            this.context = _c;
            return this;
        }

        public Builder setDBHelper(DBHelper db){
            this.dbHelper = db;
            return this;
        }

        public Builder setDB(SQLiteDatabase _db){
            this.db = _db;
            return this;
        }

        public UploadData build(){
            return new UploadData(this);
        }

    }
}
