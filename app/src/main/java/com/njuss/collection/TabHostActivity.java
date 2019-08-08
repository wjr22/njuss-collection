package com.njuss.collection;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.njuss.collection.base.User;
import com.njuss.collection.service.UserService;
import com.njuss.collection.tools.DBHelper;
import com.njuss.collection.tools.FileUtil;
import com.njuss.collection.tools.UploadData;

import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import static com.njuss.collection.R.color.gray;
import static com.njuss.collection.R.color.white;


/**
 * Tab host,底部，分别是已完成和未完成
 * @author weizhaoyang
 */
public class TabHostActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TabHost host = getTabHost();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_tabhost);
        //checkPermission();
        Intent intent = this.getIntent();

        /*if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =  intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
        else{
            onSearchRequested();
        }*/
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            int Listnumber=User.getFinishedList().size()+User.getUnfinishedList().size();
            actionBar.setTitle(User.getConductor().getConductorName() +"-烟草销售点信息维护:共"+Listnumber+"点");
            actionBar.setBackgroundDrawable( new ColorDrawable( Color.parseColor("#108ee9")));
        }else{
            Toast.makeText(this, "找不到标题栏", Toast.LENGTH_SHORT).show();
        }

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        LayoutInflater inflater = LayoutInflater.from(this);

        inflater.inflate(R.layout.tab1,tabHost.getTabContentView());
        inflater.inflate(R.layout.tab2,tabHost.getTabContentView());

        Intent intent1 = new Intent();
        intent1.setClass(this,ListViewActivity.class);

        Intent intent2 = new Intent();
        intent2.setClass(this,ListViewActivityr.class);

        UserService userService = new UserService(getApplicationContext());
        userService.setMap();
        User.setUnfinished(userService.unfinished);
        User.setFinished(userService.finished);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("未收集("+User.getUnfinishedList().size()+")").setContent(intent1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("已收集("+User.getFinishedList().size()+")").setContent(intent2));
       //tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("未收集").setContent(intent1));
      // tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("已收集").setContent(intent2));

        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(gray);
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(white);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                int index = getTabHost().getCurrentTab();
                //调用tabhost中的getTabWidget()方法得到TabWidget
                TabWidget tabWidget = getTabHost().getTabWidget();
                //得到选项卡的数量
                int count = tabWidget.getChildCount();
                //循环判断，只有点中的索引值改变背景颜色，其他的则恢复未选中的颜色
                for(int i=0;i<count;i++){
                    View view = tabWidget.getChildAt(i);
                    if(index == i){
                        view.setBackgroundResource(gray);
                        // tabView.setBackgroundResource(R.drawable.bg_tab_selected);
                        // text.setTextColor(this.getResources().getColorStateList(android.R.color.black));
                    }else{
                        view.setBackgroundResource(white);
                    }
                }
            }
        });
       /* //改变样式
        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i =0; i < tabWidget.getChildCount(); i++) {
            //修改显示字体大小
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(15);
            tv.setTextColor(this.getResources().getColorStateList(android.R.color.black));
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.input) {
            Toast toast = Toast.makeText(getApplicationContext(), "请导入标准模式数据", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            // TODO 文件选择
            showFileChooser();

            return true;
        }
        if (item.getItemId() == R.id.output) {
            App app = (App) getApplication();
            File file = new File(app.DataDir()+"/"+String.valueOf(User.getConductor().getConductorID())+".csv");
            UserService userService = new UserService(TabHostActivity.this);
            userService.exportToCsv(file.getAbsolutePath());

            Toast toast = Toast.makeText(getApplicationContext(), "导出数据至 Tobacco/Data/"+User.getConductor().getConductorID()+".csv完成!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doSearch(String queryStr) {
        //执行真正的查询结果处理
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
// Potentially direct the user to the Market with a Dialog
            Toast.makeText(this,"Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        UploadData uploadData = new UploadData.Builder().setContext(this).setDBHelper(dbHelper).setDB(dbHelper.getWritableDatabase()).build();
        //uploadData.insertConductor()
        String path;
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = FileUtil.getFileFromUri(uri, this);
            Log.d("file select ", file.getAbsolutePath());
            try {
                InputStream is = new FileInputStream(file);
                uploadData.insertStore(this, is, file.getName());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
