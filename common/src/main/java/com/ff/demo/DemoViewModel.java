package com.ff.demo;

import android.app.Application;

import com.ff.mvvm.base.BaseViewModel;

import androidx.annotation.NonNull;

public class DemoViewModel extends BaseViewModel<DemoUIObserver> {


    public DemoViewModel(@NonNull Application application) {
        super(application);
        doTestRequest();
    }

    public void doTestRequest() {
        getUIOB().getTestEvent().postValue("我是demo测试事件");
    }

    public void loadWeb() {
        getUIOB().getLoadWebEvent().postValue("web加载完成");
    }
}
