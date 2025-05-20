package com.quicktvui.sdk.core.ui.pg;

import android.support.v4.app.FragmentActivity;

/**
 * <br>
 * 加载器基类
 * <br>
 * <br>
 * Created by WeiPeng on 2023-08-18 11:15
 */
public abstract class BasePageLoader implements IPageLoader {

    protected FragmentActivity mActivity;

    public BasePageLoader(FragmentActivity activity) {
        this.mActivity = activity;
    }

}
