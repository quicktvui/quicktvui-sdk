package com.quicktvui.support.ui.item.presenter;

import android.content.Context;
import android.graphics.Rect;

import com.quicktvui.support.ui.item.widget.AbsWidget;
import com.quicktvui.support.ui.leanback.Presenter;

import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;
import com.quicktvui.support.ui.item.widget.LazyWorker;
import com.quicktvui.support.ui.item.host.SimpleHostView;

/**
 *
 */
public abstract class BaseItemPresenter extends Presenter {


    public static final int TAG_FOCUS = 0x11111;


    protected Context context;


    public BaseItemPresenter() {

    }




    /**
     * 告知如何初始化HostView
     * @param parent
     * @return
     */
    protected abstract ItemHostView onCreateHostView(ViewGroup parent);


    /**
     * Widget被初始化时回调
     * @param widget
     * @param lwh
     */
    protected void onWidgetInitialized(AbsWidget widget, final LazyWidgetsHolder lwh){

    }

    /**
     * 通过注册widget Builder的方式，告知初始化哪些Widget
     * @param widgetsHolder
     */
    protected abstract void onRegisterWidgetBuilder(LazyWidgetsHolder widgetsHolder);

    /**
     * 当hostView的焦点改变时回调
     * @param hostView
     * @param hasFocus
     * @param widgetsHolder
     */
    protected void onHostViewFocusChanged(ItemHostView hostView,boolean hasFocus,LazyWidgetsHolder widgetsHolder){

    }

//    /**
//     * 更新指定viewHolder的焦点状态
//     * @param viewHolder
//     * @param focus
//     */
//    public void changeFocusState(ViewHolder viewHolder , boolean focus){
//        if(viewHolder instanceof LazyWidgetsHolder){
//            final LazyWidgetsHolder lh = (LazyWidgetsHolder) viewHolder;
//            onHostViewFocusChanged(lh.getItemHostView(),focus,lh);
//        }
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        this.context = parent.getContext();
        //1
        final ItemHostView hostView = onCreateHostView(parent);
        //2
        final LazyWidgetsHolder h = new LazyWidgetsHolder(hostView);

        h.setOnWidgetInitCallback(new LazyWidgetsHolder.onWidgetInitCallback() {
            @Override
            public void onWidgetInit(String name, AbsWidget widget, final LazyWidgetsHolder lwh) {
                onWidgetInitialized(widget,lwh);
            }
        });

        h.setWidgetsBuilder(new LazyWidgetsHolder.WidgetsBuilder() {
            @Override
            public void onBuildWidgets(LazyWidgetsHolder holder) {
                onRegisterWidgetBuilder(holder);
            }
        });
        h.build();

        hostView.setFocusChangeListener(new SimpleHostView.FocusChangeListener() {
                @Override
                public void onFocusChanged(View v , final boolean gainFocus, int direction, Rect previouslyFocusedRect) {
                    final LazyWorker lw = h.getLazyWorker();
                    lw.cancelWork(TAG_FOCUS);
                    if (!gainFocus ||  direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
                        onHostViewFocusChanged(hostView, gainFocus, h);
                    } else {
                        if (v instanceof ItemHostView) {
                            final Runnable newTask = new Runnable() {
                                @Override
                                public void run() {
                                    onHostViewFocusChanged(hostView, gainFocus, h);
                                }
                            };
                            lw.execute(TAG_FOCUS, newTask, 0);
                        }
                    }
                }
            });

        hostView.setOnHostViewSizeChangeListener(new ItemHostView.OnHostViewSizeChangeListener() {
            @Override
            public void onSizeChanged(ItemHostView hostView, int width, int height) {
                    onItemHostViewSizeChanged(h, hostView, width, height);
            }
        });
//        hostView.getHostView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, final boolean hasFocus) {
//
//            }
//        });
        return h;
    }

    public ViewHolder createViewHolder(Context context,final ItemHostView hostView) {
        this.context = context;
        //2
        final LazyWidgetsHolder h = new LazyWidgetsHolder(hostView);

        h.setOnWidgetInitCallback(new LazyWidgetsHolder.onWidgetInitCallback() {
            @Override
            public void onWidgetInit(String name, AbsWidget widget, final LazyWidgetsHolder lwh) {
                onWidgetInitialized(widget,lwh);
            }
        });

        h.setWidgetsBuilder(new LazyWidgetsHolder.WidgetsBuilder() {
            @Override
            public void onBuildWidgets(LazyWidgetsHolder holder) {
                onRegisterWidgetBuilder(holder);
            }
        });
        h.build();

        hostView.setFocusChangeListener(new SimpleHostView.FocusChangeListener() {
            @Override
            public void onFocusChanged(View v , final boolean gainFocus, int direction, Rect previouslyFocusedRect) {
                final LazyWorker lw = h.getLazyWorker();
                lw.cancelWork(TAG_FOCUS);
                if (!gainFocus ||  direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
                    onHostViewFocusChanged(hostView, gainFocus, h);
                } else {
                    if (v instanceof ItemHostView) {
                        final Runnable newTask = new Runnable() {
                            @Override
                            public void run() {
                                onHostViewFocusChanged(hostView, gainFocus, h);
                            }
                        };
                        lw.execute(TAG_FOCUS, newTask, 0);
                    }
                }
            }
        });

        hostView.setOnHostViewSizeChangeListener(new ItemHostView.OnHostViewSizeChangeListener() {
            @Override
            public void onSizeChanged(ItemHostView hostView, int width, int height) {
                onItemHostViewSizeChanged(h, hostView, width, height);
            }
        });

        return h;
    }


    public void bindViewHolder(ViewHolder viewHolder, Object item) {
        onBindViewHolder(viewHolder,item);
    }

    public void unbindViewHolder(ViewHolder viewHolder) {
        onUnbindViewHolder(viewHolder);
    }

    protected void onItemHostViewSizeChanged(LazyWidgetsHolder lazyWidgetsHolder, ItemHostView hostView, int width, int height){

    }

}
