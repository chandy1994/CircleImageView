package com.chandy.customwidget.circleimageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chandy.customwidget.view.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleImageView circleImageView=(CircleImageView)findViewById(R.id.testview);
        circleImageView.setOnImageClickListener(new CircleImageView.onImageClickListener() {
            @Override
            public void click(View view) {
                Toast.makeText(MainActivity.this,"点击了",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
