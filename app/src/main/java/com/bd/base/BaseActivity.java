package com.bd.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements View.OnClickListener {

    protected T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayout());
        init();
        initBinding();
        binding.setLifecycleOwner(this);
    }

    protected abstract int getLayout();

    protected abstract void init();

    protected abstract void initBinding();

    protected abstract void onClick(int id);

    @Override
    public void onClick(View view) {
        onClick(view.getId());
    }
}
