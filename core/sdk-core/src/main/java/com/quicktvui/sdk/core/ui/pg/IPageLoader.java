package com.quicktvui.sdk.core.ui.pg;

import android.view.KeyEvent;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.pm.EsPageView;
import com.quicktvui.sdk.core.pm.IEsPageView;
import com.quicktvui.sdk.base.EsEmptyCallback;

/**
 * <br>
 * 页面加载器
 * <br>
 * <br>
 * Created by WeiPeng on 2023-08-18 10:55
 */
public interface IPageLoader {

    void newApp(EsData data);

    void addPage(EsPageView pv);

    void updatePage(EsData data);

    void removePage(IEsPageView pv);

    boolean dispatchKeyEvent(KeyEvent event);

    boolean onBackPressed(EsEmptyCallback callback);

    void onAppFinish(RootFragmentView view);

    boolean isAnyAppRunning();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
