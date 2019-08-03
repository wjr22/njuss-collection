package com.njuss.collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import android.content.Context;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.njuss.collection.base.User;
import com.njuss.collection.beans.Store;
import com.njuss.collection.service.UserService;

import java.io.Serializable;
import java.util.List;

/**
 * 未完成列表部分
 * @author weizhaoyang
 */
public class ListViewActivity extends AppCompatActivity {
    private List<Store> data;
    private MyBaseAdapt mba;//自定义适配器
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1);
        lv = findViewById(R.id.list_view);
        //1.数据准备
        data = User.getUnfinishedList();
        //2.创建自定义适配器
        mba = new MyBaseAdapt((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        //3.为listView设置适配器
        lv.setAdapter(mba);
    }

    /**
     * 返回刷新数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        refreshData();
    }

    public void refreshData(){
        // 更新数据
        UserService userService = new UserService(getApplicationContext());
        userService.setMap();
        User.setUnfinished(userService.unfinished);
        //1.数据准备
        data = User.getUnfinishedList();
        //2.创建自定义适配器
        mba = new ListViewActivity.MyBaseAdapt((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        //3.为listView设置适配器
        lv.setAdapter(mba);
    }

    public class MyBaseAdapt extends BaseAdapter{
        public class ViewHolder{

            TextView tv_listviewitme_storename;
            TextView tv_listviewitme_licenseID;
            TextView tv_listviewitme_locate;

        }

        private LayoutInflater inflater;

        public MyBaseAdapt(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null){
                v = inflater.inflate(R.layout.layout_list_item,null);
                ViewHolder vh = new ViewHolder();
                vh.tv_listviewitme_storename = v.findViewById(R.id.tv_listviewitme_storename);
                vh.tv_listviewitme_licenseID = v.findViewById(R.id.tv_listviewitme_licenseID);
                vh.tv_listviewitme_locate = v.findViewById(R.id.tv_listviewitme_address);
                v.setTag(vh);
            }

            ViewHolder vh = (ViewHolder) v.getTag();
            final Store store = data.get(i);

            vh.tv_listviewitme_storename.setText(store.getStoreName());
            vh.tv_listviewitme_licenseID.setText(store.getLicenseID());
            vh.tv_listviewitme_locate.setText(store.getStoreAddress());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(ListViewActivity.this, CollectActivity.class);
                    it.putExtra("store", store);
                    startActivityForResult(it, 11);
                }
            });
            return v;
        }
    }

}