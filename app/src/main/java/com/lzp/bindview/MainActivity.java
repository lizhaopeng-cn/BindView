package com.lzp.bindview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lzp.annotation.BindView;
import com.lzp.inject.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        textView.setText("咔咔咔");
    }
}
