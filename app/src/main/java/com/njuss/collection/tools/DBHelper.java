package com.njuss.collection.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;


/**
 * SQLite DataBase 创建与更新类
 */
public class DBHelper extends SQLiteOpenHelper {

    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.

    //数据库版本
    private static final int DATABASE_VERSION=3;

    //数据库名称
    private static final String DATABASE_NAME="dbTobacco";

    private Context con;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String CREATE_TABLE_CONDDUCTOR="CREATE TABLE tGridConductor("
                +"conductorID INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +"conductorMobile TEXT, "
                +"conductorName TEXT, "
                +"conductorPwd TEXT," +
                "districtCode TEXT)";
        db.execSQL(CREATE_TABLE_CONDDUCTOR);

        String CREATE_TABLE_DISTRICT = "CREATE TABLE tDistrict(" +
                "districtLevel INTEGER," +
                "districtCode TEXT PRIMARY KEY," +
                "districtName TEXT," +
                "parentCode TEXT," +
                "districtFullname TEXT)";
        db.execSQL(CREATE_TABLE_DISTRICT);

        String CREATE_TABLE_STORES = "CREATE TABLE tStores(" +
                "licenseID INTEGER PRIMARY KEY," +
                "conductorID INTEGER, " +
                "storeName TEXT," +
                "storeAddress TEXT," +
                "GPSAddress TEXT," +
                "GPSLongitude TEXT," +
                "GPSLatitude TEXT," +
                "licensePic TEXT," +
                "storePicM TEXT," +
                "storePicL TEXT," +
                "storePicR TEXT)";
        db.execSQL(CREATE_TABLE_STORES);
        Log.d("DB Operate ==", "CREATE DATABASE dbTobacco Success! and Created three tables.========");
        UploadData up = new UploadData.Builder().setDBHelper(this).setDB(db).setContext(con).build();
        up.dataInit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果旧表存在，删除，所以数据将会消失
        db.execSQL("DROP TABLE IF EXISTS tGridConductor");

        //再次创建表
        onCreate(db);
    }

    /*
    private void importSheet() {
        try {
            // 1
            InputStream is = getResources().getAssets().open("vipdb.xls");
            // 2
            Workbook book = Workbook.getWorkbook(is);
            // 3
            Sheet sheet = book.getSheet(0);
            // 4
            for (int j = 0; j < sheet.getRows(); ++j) {
                // 5
                initDataInfo(sheet.getCell(0, j).getContents(), sheet.getCell(1, j).getContents());
            }
            book.close();
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }

    }*/
}

