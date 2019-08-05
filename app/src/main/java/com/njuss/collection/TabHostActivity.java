package com.njuss.collection;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.njuss.collection.base.User;

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

}
