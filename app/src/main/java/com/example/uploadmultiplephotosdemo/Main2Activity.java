package com.example.uploadmultiplephotosdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.uploadmultiplephotosdemo.util.WebImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    Banner banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        banner = findViewById(R.id.banner);

        //只能模拟网络加载了
        banner.setImages(MainActivity.images).setImageLoader(new WebImageLoader());
        banner.start();
        banner.startAutoPlay();
    }
}
