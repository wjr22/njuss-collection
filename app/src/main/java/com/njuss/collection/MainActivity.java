package com.njuss.collection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.njuss.collection.service.UserService;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 登录界面
 * @author weizhaoyang
 * @since v1.0.1 未设置密码
 */
public class MainActivity extends AppCompatActivity {
    private Button mBtnLogin;
    private EditText edtUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnLogin = (Button)findViewById(R.id.btn_login);
        edtUserName = (EditText)findViewById(R.id.user_name);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = edtUserName.getText().toString();
                Log.d("get msg string is ...", s +"======");
                UserService userService = new UserService(getApplicationContext());
                if(userService.checkUserByMobile(s)) {
                    App app = (App)getApplication();
                    app.setFinished(userService.finished);
                    app.setUnfinished(userService.unfinished);
                    Intent intent = new Intent(MainActivity.this, TabHostActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "查无此人", Toast.LENGTH_SHORT).show();
                }
            }
        });
        App a = (App) getApplicationContext();

    }
}
