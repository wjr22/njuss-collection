package com.njuss.collection.tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.njuss.collection.base.Global;
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
        contentValues.put("conductorMobile", "18051072519");
        contentValues.put("conductorName", "王家若");
        contentValues.put("conductorPwd", "18051072519");
        long id = db.insert("tGridConductor", null, contentValues);
        Log.d("DB Operate", "insert into tGridConductor " + id + " success!========");

        for(int i=0; i< 35; i++) {
            ContentValues storeValues = new ContentValues();        //插入商户
            storeValues.put("conductorID", id);
            storeValues.put("licenseID", "201800000000"+i);
            storeValues.put("storeName", "王家若的小店"+i);
            long IDl = db.insert("tStores", null, storeValues);
            Log.d("DB Operate", "insert into tStores " + IDl + " Success!========");
        }
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
                Log.d("DB Operate ", "insert into district "+ cv.get(District.names[1])+" Success!======");
                db.insert("tDistrict",null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean insertConductor(Context context, InputStream in, String fileName){
        Boolean b = false;
        try {
            List<String[]> ls = FileUtil.readExcel(fileName, in);
            // TODO 检查第一行

            for (int i=1; i<ls.size(); i++){
                String[] t = ls.get(i);
                ContentValues cv = new ContentValues();
                for (int j=1; j<t.length; j++){
                    cv.put(Global.ConductorFieldNames[j], t[j]);
                }
                db.insert("tGridConductor", null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return b;
    }

    public Boolean insertStore(Context context, InputStream in, String fileName){
        Boolean b = false;
        try {
            List<String[]> ls = FileUtil.readExcel(fileName, in);
            // TODO 检查第一行

            for (int i=1; i<ls.size(); i++){
                String[] t = ls.get(i);
                ContentValues cv = new ContentValues();
                Cursor u = db.query("tGridConductor", new String[]{Global.ConductorFieldNames[0]}, "conductorName=?", new String[]{t[1]}, null, null, null);
                if(u.getCount() == 1){
                    u.moveToFirst();
                    int id = u.getInt(u.getColumnIndex(Global.ConductorFieldNames[0]));
                    ContentValues storeValues = new ContentValues();        //插入商户
                    storeValues.put("conductorID", id);
                    storeValues.put("licenseID", t[0]);
                    storeValues.put("storeName", t[2]);
                    storeValues.put("storeAddress", t[3]);
                    long IDl = db.insert("tStores", null, storeValues);
                    Log.d("DB Operate", "insert into tStores " + IDl + " Success!========");
                    db.insert("tStores", null, cv);
                }else
                    return false;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }



    public void dataInit(){
        insertDistrict(this.context);
    }

    /**
     * @TODO 导出数据到excel中
     */
    public void exportToExcel(){

    }

    public void readFromExcel(){ }


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
