package com.zw.qiantu.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (savedInstanceState != null) {
            init();
        }
    }

    protected void init() {
        initView();
        initData();
    }

    protected void initData() {
    }

    protected abstract void initView();

    public abstract int getLayoutId();
}
