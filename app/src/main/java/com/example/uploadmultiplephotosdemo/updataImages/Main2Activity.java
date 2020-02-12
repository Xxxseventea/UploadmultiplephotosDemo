package com.example.uploadmultiplephotosdemo.updataImages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.uploadmultiplephotosdemo.R;
import com.example.uploadmultiplephotosdemo.util.WebImageLoader;
import com.youth.banner.Banner;

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
