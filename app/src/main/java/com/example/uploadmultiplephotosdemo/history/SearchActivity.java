package com.example.uploadmultiplephotosdemo.history;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uploadmultiplephotosdemo.R;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

public class SearchActivity extends AppCompatActivity {
    SearchFragment searchFragment;

    /**
     * 用了下第三方库而已。。。
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

         Button button = findViewById(R.id.testt);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 searchFragment = SearchFragment.newInstance();

                 searchFragment.setOnSearchClickListener(new IOnSearchClickListener() {
                     @Override
                     public void OnSearchClick(String keyword) {
                         Toast.makeText(SearchActivity.this,"123456",Toast.LENGTH_SHORT);
                     }
                 });

                 searchFragment.showFragment(getSupportFragmentManager(),SearchFragment.TAG);
             }
         });

    }
}
