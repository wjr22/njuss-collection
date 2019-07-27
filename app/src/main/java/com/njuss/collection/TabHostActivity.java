package com.njuss.collection;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;

/**
 * Tab host,底部，分别是已完成和未完成
 * @author weizhaoyang
 */
public class TabHostActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TabHost host = getTabHost();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_tabhost_);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        LayoutInflater inflater = LayoutInflater.from(this);



        inflater.inflate(R.layout.tab1,tabHost.getTabContentView());
        inflater.inflate(R.layout.tab2,tabHost.getTabContentView());


        Intent intent1 = new Intent();
        intent1.setClass(this,ListViewActivity.class);


        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("未收集").setContent(intent1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("已收集").setContent(intent1));}
}
