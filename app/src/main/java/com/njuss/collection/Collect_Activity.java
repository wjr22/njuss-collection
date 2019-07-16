package com.njuss.collection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Collect_Activity extends AppCompatActivity {
    private static final int REQUEST_CODE_1 = 1;
    private static final int REQUEST_CODE_2 = 2;
    private static final int REQUEST_CODE_3 = 3;
    private static final int REQUEST_CODE_4 = 4;
    private ImageView resultContainer1;
    private ImageView takePhotoBtn1;
    private ImageView resultContainer2;
    private ImageView takePhotoBtn2;
    private ImageView resultContainer3;
    private ImageView takePhotoBtn3;
    private ImageView resultContainer4;
    private ImageView takePhotoBtn4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

    }

    private void initListener() {
        takePhotoBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_1);
            }
        });

        takePhotoBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_2);
            }
        });

        takePhotoBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_3);
            }
        });

        takePhotoBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                startActivityForResult(intent,REQUEST_CODE_4);
            }
        });




    }

    private void initView() {

        resultContainer1 = (ImageView) this.findViewById(R.id.iv_licensePic);
        takePhotoBtn1 = (ImageView) this.findViewById(R.id.iv_licensePic);
        resultContainer2 = (ImageView) this.findViewById(R.id.iv_storePicM);
        takePhotoBtn2 = (ImageView) this.findViewById(R.id.iv_storePicM);
        resultContainer3 = (ImageView) this.findViewById(R.id.iv_storePicL);
        takePhotoBtn3 = (ImageView) this.findViewById(R.id.iv_storePicL);
        resultContainer4 = (ImageView) this.findViewById(R.id.iv_storePicR);
        takePhotoBtn4 = (ImageView) this.findViewById(R.id.iv_storePicR);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer1.setImageBitmap(result);
            }
        } else if (requestCode == REQUEST_CODE_2) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer2.setImageBitmap(result);
            }
        } else if (requestCode == REQUEST_CODE_3) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer3.setImageBitmap(result);
            }
        } else if (requestCode == REQUEST_CODE_4) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap result = data.getParcelableExtra("data");
                if (result != null)
                    resultContainer4.setImageBitmap(result);
            }
        }

        if (requestCode == Activity.RESULT_CANCELED) {

            Toast.makeText(this, "你取消了拍照", Toast.LENGTH_SHORT).show();
        }
    }}