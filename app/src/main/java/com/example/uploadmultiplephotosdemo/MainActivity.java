package com.example.uploadmultiplephotosdemo;

import androidx.appcompat.app.AppCompatActivity;
import cn.finalteam.galleryfinal.BuildConfig;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.example.uploadmultiplephotosdemo.util.GlideImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private String strphoto = "";
    private List<File> fileList = new ArrayList<>();
    private GridView gridView;
    private Button button;


    public static List<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gridView1);
        button = findViewById(R.id.button1);
        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGallery();
            }
        });

    }

    /**
     * 选择图片
     */
    private void initGallery(){
        //设置主题
        //ThemeConfig.CYAN
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(0xF4, 0x7C, 0x00))
                .setFabNornalColor(Color.rgb(0xF4, 0x7C, 0x00))
                .setFabPressedColor(Color.rgb(0xF4, 0x7C, 0x00))
                .setCropControlColor(Color.rgb(0xFF, 0xFF, 0xFF))
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(false)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(false)
                .build();

        //配置imageloader
        GlideImageLoader imageloader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                .setDebug(BuildConfig.DEBUG)
                .setFunctionConfig(functionConfig).build();
        GalleryFinal.init(coreConfig);

        GalleryFinal.openGalleryMuti(1,8 ,mOnHandlerResultCallback);

    }


    private GalleryFinal.OnHanlderResultCallback mOnHandlerResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            //进行图片上传与置换
            //置换

            for(int i = 0;i<resultList.size();i++){
                images.add(resultList.get(i).getPhotoPath());
            }

            gridView.setAdapter(new Adapter(MainActivity.this,resultList));
            //上传
            for(int i =0 ; i<resultList.size();i++){
                if(i>0){
                    strphoto = strphoto+",";
                }
                fileList.add(new File(resultList.get(i).getPhotoPath()));
                strphoto = strphoto + resultList.get(i).getPhotoPath();
                Log.e("ImgTest",resultList.get(i).getPhotoPath());
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Log.e("editinfo",errorMsg);
        }
    };

}