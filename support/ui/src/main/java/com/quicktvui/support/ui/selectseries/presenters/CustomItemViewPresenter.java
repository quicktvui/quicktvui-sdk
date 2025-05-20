package com.quicktvui.support.ui.selectseries.presenters;

import static com.quicktvui.support.ui.selectseries.utils.MyCustomHelper.updateLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;

import com.quicktvui.support.ui.selectseries.SelectSeriesViewGroup;
import com.quicktvui.hippyext.views.fastlist.FastAdapter;
import com.quicktvui.hippyext.views.fastlist.ListItemHolder;
import com.quicktvui.support.ui.selectseries.utils.MyCustomHelper;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.dom.node.DomNode;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;

import java.util.HashSet;
import java.util.Set;

import com.quicktvui.support.ui.leanback.Presenter;

public class CustomItemViewPresenter extends Presenter {

    private RenderNode templateNode;
    private HippyEngineContext engineContext;

    private Context context;
    private HippyViewController<?> controller;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        if (parent.getParent() instanceof SelectSeriesViewGroup) {
            SelectSeriesViewGroup listViewGroup = (SelectSeriesViewGroup) parent.getParent();
            if (templateNode == null) {
                templateNode = listViewGroup.getItemRenderNode();
                engineContext = listViewGroup.getEngineContext();
                context = parent.getContext();
                controller = MyCustomHelper.getControllerByRenderNode(engineContext, templateNode);
            }

            return MyCustomHelper.createMyHolder(context, engineContext, controller, templateNode);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        MyHolder holder = (MyHolder) viewHolder;
        StyleNode domNode = holder.domNode;
        View rootView = holder.view;

        notifyElementBeforeOnBind(domNode);
        // 先处理属性值相关 对应FastAdapter的exeBindViewHolder
//        MyCustomHelper.handlePropsValues(holder.context, rootView, holder.templateNode, domNode, item);
        MyCustomHelper.handlePropsValues(holder, rootView, holder.templateNode, domNode, item);

        // 处理样式相关
        domNode.calculateLayout();
        MyCustomHelper.updateItemLayout(rootView, domNode);

        //为item确定尺寸
        int itemWidth, itemHeight;
        itemWidth = (int) domNode.getLayoutWidth();
        itemHeight = (int) domNode.getLayoutHeight();
        if (itemWidth < 1 || itemHeight < 1) {
            int[] size = MyCustomHelper.fixItemViewSize(domNode);
            itemWidth = size[0];
            itemHeight = size[1];
            MyCustomHelper.updateLayout(rootView, 0, 0, itemWidth, itemHeight);
        }
        if (rootView.getLayoutParams() == null) {
            rootView.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, itemHeight));
        } else {
            rootView.getLayoutParams().width = itemWidth;
            rootView.getLayoutParams().height = itemHeight;
        }
    }

    void notifyElementBeforeOnBind(DomNode node) {
        if (node instanceof FastAdapter.ElementNode) {
            final View view = ((FastAdapter.ElementNode) node).boundView;
            if (view instanceof ListItemHolder) {
                //把ListItemHolder相关的生命周期执行一下
                ((ListItemHolder) view).onItemBind();
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            notifyElementBeforeOnBind(node.getChildAt(i));
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        if (viewHolder instanceof MyHolder) {
            ((MyHolder) viewHolder).clear();
        }
    }

    public Presenter getPresenter() {
        return this;
    }


    public static class MyHolder extends ViewHolder {

        public HippyEngineContext context;
        public RenderNode templateNode;
        public StyleNode domNode;

        public Set<View> selectViews = new HashSet<>();
        public Set<View> focusViews = new HashSet<>();
        public Set<View> oSelectViews = new HashSet<>();
        public Set<View> oFocusViews = new HashSet<>();

        int position = -1;
        boolean isBind = false;

        public MyHolder(View view) {
            super(view);
        }

        public void clear() {
            position = -1;
            isBind = false;
        }
    }
}
