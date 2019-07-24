package com.njuss.collection;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class ListViewActivityr extends AppCompatActivity {
    private List<store> data;
    private MyBaseAdapt mba;//自定义适配器
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2);
        lv = findViewById(R.id.list_view2);

        //1.数据准备
        data = new storeDao().list();
        //2.创建自定义适配器
        mba = new MyBaseAdapt((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        //3.为listView设置适配器
        lv.setAdapter(mba);
    }

    public class MyBaseAdapt extends BaseAdapter{
        public class ViewHolder{

            TextView tv_listviewitme_storename;
            TextView tv_listviewitme_address;
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
                vh.tv_listviewitme_address = v.findViewById(R.id.tv_listviewitme_address);
                vh.tv_listviewitme_licenseID = v.findViewById(R.id.tv_listviewitme_licenseID);
                vh.tv_listviewitme_locate = v.findViewById(R.id.tv_listviewitme_locate);

                v.setTag(vh);
            }

            ViewHolder vh = (ViewHolder) v.getTag();
            store store = data.get(i);

            vh.tv_listviewitme_storename.setText(store.getStorename());
            vh.tv_listviewitme_address.setText(store.getAddress());

            vh.tv_listviewitme_licenseID.setText(store.getLicenseID());
            vh.tv_listviewitme_locate.setText(store.getLocate());
            return v;
        }
    }
}