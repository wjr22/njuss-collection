package com.njuss.collection;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ListViewActivity extends Activity {
    private ListView mLv1_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        mLv1_1 = (ListView) findViewById(R.id.Lv1_1);
        mLv1_1.setAdapter(new ListAdapter(ListViewActivity.this));


    }
}
