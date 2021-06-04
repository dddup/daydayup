package com.ff.mvvm.base;

import com.ff.common.arouter.ARouterActivity;
import com.ff.common.utils.LogUtil;
import com.ff.common.utils.LogUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;


public abstract class MVVMActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends ARouterActivity {


    VM viewModel;
    V dataBinding;

    @Override
    public void setContentLayout() {
    }

    @Override
    protected void initView() {
        super.initView();
        initDataBinding();
        initViewModel();
        registerViewObserver();
        initViewObserver();
    }

    protected VM getViewModel() {
        return viewModel;
    }

    private void initViewModel() {
        //获取泛型参数第二个
        Type type = getClass().getGenericSuperclass();
        Class modelClass = BaseViewModel.class;
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length > 1) {
                modelClass = (Class) actualTypeArguments[1];
            }
        }
        //这里如果没有泛型参数,viewModel转换异常
        viewModel = (VM) createViewModel(this, modelClass);
        LogUtil.log("viewModel初始化成功:" + viewModel.getClass().getName());
        dataBinding.setVariable(getViewModelId(), getViewModel());
        dataBinding.setLifecycleOwner(this);
        getLifecycle().addObserver(viewModel);
    }

    protected abstract int getViewModelId();

    private ViewModel createViewModel(FragmentActivity activity, Class viewModelClass) {
        return new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(viewModelClass);
    }

    protected void initDataBinding() {
        dataBinding = DataBindingUtil.setContentView(this, creatLayoutId());
        LogUtils.log("初始化databing");
    }

    protected abstract int creatLayoutId();

    protected abstract void initViewObserver();

    private void registerViewObserver() {
        if (viewModel == null) {
            throw new RuntimeException("registerViewObserver failed,please viewModel init first");
        }
        viewModel.getUIOB().getShowDialogEvent().observe(this, s -> {
            showToast("showdialog");
            showProgressDialog(s);
        });
        viewModel.getUIOB().getDismissDialogEvent().observe(this, aVoid -> {
            dismissProgressDialog();
        });
        viewModel.getUIOB().getFinishEvent().observe(this, aVoid -> {
            finish();
        });
    }
}
