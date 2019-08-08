package com.njuss.collection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import com.njuss.collection.base.CheckPermissionsActivity;
import com.njuss.collection.base.User;
import com.njuss.collection.service.UserService;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


/**
 * 登录界面
 * @author weizhaoyang
 * @since v1.0.1 未设置密码
 */
public class MainActivity extends CheckPermissionsActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private Button mBtnLogin;
    private EditText edtUserName;
    private EditText edtUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("SHA1 ====", App.sHA1(getApplicationContext()));
        mBtnLogin = (Button)findViewById(R.id.btn_login);
        edtUserName = (EditText)findViewById(R.id.user_name);
        edtUserPassword = (EditText)findViewById(R.id.user_password);
        checkPermission();
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = edtUserName.getText().toString();
                String psd = edtUserPassword.getText().toString();
                Log.d("get msg string is ...", name +"======");
                UserService userService = new UserService(getApplicationContext());
                if(userService.checkUserByMobile(name)) {
                    User.setInstance(userService.conductor);
                    User.setFinished(userService.finished);
                    User.setUnfinished(userService.unfinished);
                    Log.d("SSS", User.getUnfinished().size() +"========");
                    Intent intent = new Intent(MainActivity.this, TabHostActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "查无此人", Toast.LENGTH_SHORT).show();
                }

            }
        });
        App a = (App) getApplicationContext();


        mBtnLogin = (Button)findViewById(R.id.btn_login);

}
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("   ---", "checkPermission: 已经授权！");
        }
    }

}