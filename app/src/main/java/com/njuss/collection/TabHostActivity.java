package com.njuss.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;

public class TabHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_tabhost_);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.tab1,tabHost.getTabContentView());
        inflater.inflate(R.layout.tab2,tabHost.getTabContentView());

        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("已完成").setContent(R.id.left));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("未完成").setContent(R.id.right));

    }
}
