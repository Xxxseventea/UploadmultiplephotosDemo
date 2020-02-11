package com.example.uploadmultiplephotosdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

public class Adapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<PhotoInfo> arrayList;
    public Adapter(Context context, List<PhotoInfo> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    //gridview数量
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item,null);
        ImageView imageView = convertView.findViewById(R.id.imageView1);
        Glide.with(context).load(arrayList.get(position).getPhotoPath()).into(imageView);
        return convertView;
    }
}
