package com.quicktvui.support.ui.selectseries.utils;

import static com.quicktvui.hippyext.views.fastlist.TemplateCodeParser.PENDING_PROPS_EVENT;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;

import com.quicktvui.support.ui.selectseries.presenters.CustomItemViewPresenter;
import com.quicktvui.hippyext.views.TextViewController;
import com.quicktvui.hippyext.views.fastlist.FastFlexNode;
import com.quicktvui.hippyext.views.fastlist.FastFlexView;
import com.quicktvui.hippyext.views.fastlist.FastListNode;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.TemplateCodeParser;
import com.quicktvui.hippyext.views.fastlist.Utils;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.DomUpdateManager;
import com.tencent.mtt.hippy.dom.flex.FlexAlign;
import com.tencent.mtt.hippy.dom.flex.FlexCSSDirection;
import com.tencent.mtt.hippy.dom.flex.FlexJustify;
import com.tencent.mtt.hippy.dom.flex.FlexSpacing;
import com.tencent.mtt.hippy.dom.node.DomNode;
import com.tencent.mtt.hippy.dom.node.NodeProps;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.uimanager.CustomControllerHelper;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;

import java.util.Arrays;

import com.quicktvui.support.ui.selectseries.bean.TemplateItem;

/**
 * 自定义选集样式的工具类
 */
public class MyCustomHelper {
    public static final String PENDING_PROP_SHOW_ON_SELECTED = "showOnSelected";
    public static final String PENDING_PROP_SHOW_ON_FOCUSED = "showOnFocused";
    public static final String PENDING_PROP_SHOW_ON_ONLY_FOCUSED = "showOnOnlyFocused";
    public static final String PENDING_PROP_SHOW_ON_ONLY_SELECTED = "showOnOnlySelected";

    // 根据模板renderNode生成新的view
    public static CustomItemViewPresenter.MyHolder createMyHolder(Context context, HippyEngineContext engineContext,
                                                                  HippyViewController<?> controller,
                                                                  RenderNode templateNode) {
        CustomItemViewPresenter.MyHolder holder;

        // 生成新的DomNode
        StyleNode styleNode = new StyleNode();
        styleNode.setFlexDirection(FlexCSSDirection.COLUMN);
        styleNode.setAlignItems(FlexAlign.FLEX_START);
        styleNode.setJustifyContent(FlexJustify.FLEX_START);

        DomNode templateDomNode = engineContext.getDomManager().getNode(templateNode.getId());
        cloneNode(styleNode, templateDomNode);

        // 生成新的view
        View viewImpl = createViewByTemplate(context, controller, templateNode.getProps());
        onViewInit(viewImpl, templateNode, templateDomNode);

        holder = new CustomItemViewPresenter.MyHolder(viewImpl);
        holder.templateNode = templateNode;
        holder.domNode = styleNode;
        holder.context = engineContext;

        if (!(templateNode instanceof FastListNode || templateNode instanceof FastFlexNode)
                && templateNode.getChildCount() > 0) {
            for (int i = 0; i < templateNode.getChildCount(); i++) {
                RenderNode childRenderNode = templateNode.getChildAt(i);

                getViewByTemplate(context, engineContext, childRenderNode, viewImpl, styleNode);
            }
        }

        return holder;
    }

    public static void getViewByTemplate(Context context, HippyEngineContext engineContext,
                                         RenderNode templateNode, View parent, StyleNode parentDomNode) {
        // 生成新的DomNode
        StyleNode styleNode = new StyleNode();
//        styleNode.setFlexDirection(FlexCSSDirection.COLUMN);
//        styleNode.setAlignItems(FlexAlign.FLEX_START);
//        styleNode.setJustifyContent(FlexJustify.FLEX_START);
        DomNode templateDomNode = engineContext.getDomManager().getNode(templateNode.getId());
        cloneNode(styleNode, templateDomNode);

        if (parentDomNode != null) {
            parentDomNode.addChildAt(styleNode, parentDomNode.getChildCount());
        }
        HippyViewController<?> controller = getControllerByRenderNode(engineContext, templateNode);
        // 生成新的view
        View viewImpl = createViewByTemplate(context, controller, templateNode.getProps());
        onViewInit(viewImpl, templateNode, templateDomNode);

        //添加view
        if (parent instanceof ViewGroup && !(parent instanceof FastListView || parent instanceof FastFlexView)) {
            ((ViewGroup) parent).addView(viewImpl);
        }

        if (!(templateNode instanceof FastListNode || templateNode instanceof FastFlexNode)
                && templateNode.getChildCount() > 0) {
            for (int i = 0; i < templateNode.getChildCount(); i++) {
                RenderNode childRenderNode = templateNode.getChildAt(i);

                getViewByTemplate(context, engineContext, childRenderNode, viewImpl, styleNode);
            }
        }
    }

    public static void onViewInit(View view, RenderNode templateNode, DomNode templateDomNode) {
        //设置Name,zhaopeng 这里如果不设置Name，会导致一些基于name而设置的api无效，比如firstSearchTarget等
        HippyMap tagObj = new HippyMap();
        tagObj.pushString(NodeProps.NAME, templateDomNode.getTotalProps().getString("name"));
        view.setTag(tagObj);

        int width = templateNode.getWidth();
        int height = templateNode.getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
//        view.setFocusable(true);
        // 处理焦点相关属性
        CustomControllerHelper.dealCustomProp(view, templateNode.getProps());
    }

    /**
     * 给新创建的view进行属性赋值操作（稍后再考虑更新问题）
     */
    public static void handlePropsValues(CustomItemViewPresenter.MyHolder holder, View view, RenderNode templateNode, StyleNode domNode, Object itemData) {
        // 先不考虑是否是首次初始化
        HippyMap props = templateNode.getProps();
        HippyMap resultMap = new HippyMap();

        HippyMap realData;
        if (itemData instanceof TemplateItem) {
            HippyMap hippyMap = ((TemplateItem) itemData).getContentData();
            realData = hippyMap == null ? new HippyMap() : hippyMap;
        } else {
            return;
        }

        for (String prop : props.keySet()) {
            Object propValue = props.get(prop);
            if (isEquationProp(propValue)) {//showIf="${detailStyle=2}"
                //将等式的值计算出来放进去
                final boolean b = TemplateCodeParser.parseBooleanFromPendingProp(prop, realData, propValue);
                resultMap.pushObject(prop, b);
            } else {//pending的属性 比如type="${itemType}"中的
                final String pendingProp = parsePlaceholderProp(prop, props);
                if (!TextUtils.isEmpty(pendingProp)) {// 获取的是pending属性或者eventClick、eventFouces
                    //用item中的数据，替换map中的数据
                    final Object dataFromValue = TemplateCodeParser.getValueFromCode(realData, pendingProp);
                    resultMap.pushObject(prop, dataFromValue);

                } else {
                    //直接复制一份
                    resultMap.pushObject(prop, propValue);
                }
            }
        }

        final HippyViewController<?> vc = getControllerByRenderNode(holder.context, templateNode);
        CustomControllerHelper.updateExtraIfNeed(vc, view, templateNode);

        doDiffProps(vc, resultMap, view, domNode, holder);

        if (!(view instanceof FastListView || view instanceof FastFlexView)) { // 感觉如果嵌套list的话，不应该继续处理子节点，而是由子list自己处理
            for (int i = 0; i < templateNode.getChildCount(); i++) {
                handlePropsValues(holder, ((ViewGroup)view).getChildAt(i), templateNode.getChildAt(i),
                        (StyleNode) domNode.getChildAt(i),itemData);
            }
        }
    }

    static void doDiffProps(HippyViewController<?> vc, HippyMap props, View view, StyleNode domNode, CustomItemViewPresenter.MyHolder holder) {
        for (String prop : props.keySet()) {
            if (prop == null) {
                continue;
            }
            if (prop.equals(NodeProps.STYLE) && props.get(prop) instanceof HippyMap) {
                doDiffProps(vc, props.getMap(prop), view, domNode, holder);
            } else {
                invokeProp(vc, props, prop, view, domNode, holder);
            }
        }
    }

    // 处理已经拿到的数据
    static void invokeProp(HippyViewController<?> vc, HippyMap props, String prop, View view, StyleNode styleNode, CustomItemViewPresenter.MyHolder holder) {
//        if (vc instanceof PendingViewController && PendingViewController.PROP_LIST.equals(prop)) {
//            if (en.templateNode instanceof FastAdapter.ListNode) {
//                FastAdapter.ListNodeTag tag = ((FastAdapter.ListNode) en.templateNode).getBoundTag();
//                if (tag == null) {
//                    tag = new FastAdapter.ListNodeTag();
//                    ((FastAdapter.ListNode) en.templateNode).setBoundTag(tag);
//                }
//                tag.position = position;
//                tag.parent = adapter.listNode;
//            }
//            if (en.boundView instanceof FastPendingView) {
//                //这里设置RootList，用住嵌套使用
//                ((FastPendingView) en.boundView).setHandleEventNodeId(adapter.rootListNodeID);
//                ((FastPendingView) en.boundView).setRootList(adapter.getRootListView());
//                if (adapter.getRootListView() != null) {
//                    ((FastPendingView) en.boundView).getEventDeliverer().setOnEventListener(adapter.getRootListView().getEventDeliverer().onEventListener);
//                }
//            }
//            final Object dataFromValue = props.get(prop);
//            ((PendingViewController) vc).setPendingData(en.boundView, dataFromValue, en.templateNode);
//        } else { // 非嵌套list的情况处理
        switch (prop) {
            case PENDING_PROP_SHOW_ON_SELECTED:
                view.setVisibility(View.INVISIBLE);
                holder.selectViews.add(view);
                return;
            case PENDING_PROP_SHOW_ON_FOCUSED:
                view.setVisibility(View.INVISIBLE);
                holder.focusViews.add(view);
                return;
            case PENDING_PROP_SHOW_ON_ONLY_FOCUSED:
                view.setVisibility(View.INVISIBLE);
                holder.oFocusViews.add(view);
                return;
            case PENDING_PROP_SHOW_ON_ONLY_SELECTED:
                view.setVisibility(View.INVISIBLE);
                holder.oSelectViews.add(view);
                return;
        }
        if (!dispatchCustomPendingProp(prop, props, styleNode, view)) { // 一些自定义属性的处理
            final Object dataFromValue = props.get(prop);
            CustomControllerHelper.invokePropMethodForPending(vc, view, prop, dataFromValue); // 通常属性的处理
        }
//        }
    }

    static boolean dispatchCustomPendingProp(String prop, HippyMap props, StyleNode styleNode, View view) {
        boolean handled = true;
        switch (prop) {
            case TemplateCodeParser.PENDING_PROP_TRANSLATION: {
                final Object dataFromValue = props.get(prop);
                final HippyArray posArray = (HippyArray) dataFromValue;
                if (posArray != null && posArray.size() == 2) {
                    styleNode.setMarginLeft(posArray.getInt(0));
                    styleNode.setMarginTop(posArray.getInt(1));
                }
            }
            break;
            case TemplateCodeParser.PENDING_PROP_SIZE: {
                final Object dataFromValue = props.get(prop);
                final HippyArray posArray = (HippyArray) dataFromValue;
                if (posArray != null && posArray.size() == 2) {
                    styleNode.setStyleWidth(Utils.toPX(posArray.getInt(0)));
                    styleNode.setStyleHeight(Utils.toPX(posArray.getInt(1)));
                }
            }
            break;
            case TemplateCodeParser.PENDING_PROP_LAYOUT: {
                final Object dataFromValue = props.get(prop);
                final HippyArray posArray = (HippyArray) dataFromValue;
                if (posArray != null && posArray.size() == 4) {
                    styleNode.setMarginLeft(posArray.getInt(0));
                    styleNode.setMarginTop(posArray.getInt(1));
                    styleNode.setStyleWidth(Utils.toPX(posArray.getInt(2)));
                    styleNode.setStyleHeight(Utils.toPX(posArray.getInt(3)));
                }
            }
            break;
            case TemplateCodeParser.PENDING_PROP_FLEX_STYLE: {
                final Object dataFromValue = props.get(prop);
                final HippyMap style = (HippyMap) dataFromValue;
                new DomUpdateManager<StyleNode>().updateStyle(styleNode, style);
            }
            break;
            case TemplateCodeParser.PENDING_PROP_EVENT_CLICK: { // TODO 处理点击事件
//                final Object data = adapter.getRawObject(position);//这里不再需要clickData的形式，直接将data返回
//                final View.OnClickListener clickListener = new FastAdapter.ElementClickListener(data, adapter.rootListNodeID, en, position, adapter.eventDeliverer);
//                en.boundView.setOnClickListener(clickListener);
            }
            break;
            case TemplateCodeParser.PENDING_PROP_EVENT_FOCUS: { // TODO 处理焦点事件
//                final Object data = adapter.getRawObject(position);
//                final FastAdapter.ItemFocusListener listener = new FastAdapter.ItemFocusListener(data, adapter.rootListNodeID, en, position, adapter.eventDeliverer);
//                en.boundView.setOnFocusChangeListener(listener);
            }
            break;
            case TemplateCodeParser.PENDING_PROP_SHOW_IF: {
                final Object dataFromValue = props.get(prop);
                final boolean isEquationTrue = dataFromValue != null && (boolean) dataFromValue;
                changeViewShowIf(view, isEquationTrue);
            }
            default:
                handled = false;
                break;
        }
        if ("visibility".equals(prop)) {
            final Object dataFromValue = props.get(prop);
            if ("gone".equals(dataFromValue)) {
                clearLayout(styleNode);
            }
        }
        return handled;
    }

    static void clearLayout(StyleNode c) {
        c.setStyleWidth(0.0F);
        c.setStyleHeight(0.0F);

        for(int i = 0; i < 4; ++i) {
            c.setMargin(i, 0.0F);
            c.setPadding(i, 0.0F);
            c.setBorder(i, 0.0F);
        }

    }

    static void changeViewShowIf(View view, boolean b) {
        if (view != null) {
            view.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }
    }

    static boolean isEquationProp(Object object) {
        return object instanceof String && ((String) object).startsWith("${") && ((String) object).contains("=");
    }

    static String parsePlaceholderProp(String prop, HippyMap map) {
        Object object = map.get(prop);
        String valueKey = parsePendingProp(object);
        if (!TextUtils.isEmpty(valueKey)) {
            return valueKey;
        } else {
            int index = Arrays.binarySearch(PENDING_PROPS_EVENT, prop);
            return index > -1 ? PENDING_PROPS_EVENT[index] : null;
        }
    }

    static String parsePendingProp(Object object) {
        if (object instanceof String) {
            String value = ((String) object).trim();
            if (value.length() > 3 && value.startsWith("${")) {
                return value.substring(2, value.length() - 1);
            }
        }

        return null;
    }

    static View createViewByTemplate(Context context, HippyViewController<?> controller, HippyMap props) {
        if (controller instanceof TextViewController) {
            return ((TextViewController) controller).createViewImpl(context, props, true);
        }
        View viewImpl = CustomControllerHelper.createViewImpl(context, props, controller);
        if (viewImpl == null) {
            viewImpl = CustomControllerHelper.createViewImpl(context, controller);
        }
        return viewImpl;
    }

    public static HippyViewController<?> getControllerByRenderNode(HippyEngineContext context, RenderNode node) {
        return CustomControllerHelper.getViewController(context.getRenderManager().getControllerManager(), node);
    }

    static void cloneNode(DomNode c, DomNode o) {
        c.setViewClassName(o.getViewClass());
        c.setDirection(o.getDirection());
        c.setFlexDirection(o.getFlexDirection());
        c.setJustifyContent(o.getJustifyContent());
        c.setAlignContent(o.getAlignContent());
        c.setAlignItems(o.getAlignItems());
        c.setAlignSelf(o.getAlignSelf());
        c.setPositionType(o.getPositionType());
        if (o.getPosition(FlexSpacing.TOP) != 0) {
            c.setPosition(FlexSpacing.TOP, o.getPosition(FlexSpacing.TOP));
        }
        if (o.getPosition(FlexSpacing.LEFT) != 0) {
            c.setPosition(FlexSpacing.LEFT, o.getPosition(FlexSpacing.LEFT));
        }
        if (o.getPosition(FlexSpacing.RIGHT) != 0) {
            c.setPosition(FlexSpacing.RIGHT, o.getPosition(FlexSpacing.RIGHT));
        }
        if (o.getPosition(FlexSpacing.BOTTOM) != 0) {
            c.setPosition(FlexSpacing.BOTTOM, o.getPosition(FlexSpacing.BOTTOM));
        }
        c.setWrap(o.Style().getWrap());
        c.setOverflow(o.getOverflow());
        c.setFlexGrow(o.getFlexGrow());
        c.setFlexShrink(o.getFlexShrink());
        c.setFlexBasis(o.getFlexBasis());
        if (o.getStyleWidth() > 0) {
            c.setStyleWidth(o.getStyleWidth());
        }
        if (o.getStyleHeight() > 0) {
            c.setStyleHeight(o.getStyleHeight());
        }
        for (int i = 0; i < 4; i++) {
            c.setMargin(i, o.getMargin(i));
            c.setPadding(i, o.getPadding(i));
            c.setBorder(i, o.getBorder(i));
        }
    }

    public static int[] fixItemViewSize(StyleNode itemNode) {
        int[] size = new int[2];
        if (itemNode.getChildCount() == 1) {
            size[0] = (int) itemNode.getChildAt(0).getLayoutWidth();
            size[1] = (int) itemNode.getChildAt(0).getLayoutHeight();
        } else {
            if (itemNode.getChildCount() > 1) {
                //简单的measure
                int maxWidth = 0;
                int maxHeight = 0;
                for (int i = 0; i < itemNode.getChildCount(); i++) {
                    int cw = (int) itemNode.getChildAt(i).getLayoutWidth();
                    int ch = (int) itemNode.getChildAt(i).getLayoutHeight();
                    maxWidth = Math.max(maxWidth, cw);
                    maxHeight = Math.max(maxHeight, ch);
                }
                size[0] = maxWidth;
                size[1] = maxHeight;
            }
        }
        return size;
    }

    public static void updateItemLayout(View view, DomNode node) {

        if (view != null) {
            updateLayout(view, node);
            for (int i = 0; i < node.getChildCount(); i++) {
                updateItemLayout(((ViewGroup) view).getChildAt(i), node.getChildAt(i));
            }
        }
    }

    public static void updateLayout(View view, DomNode node) {
        final int lw = (int) node.getLayoutWidth();
        final int lh = (int) node.getLayoutHeight();
        final int x = (int) node.getLayoutX();
        final int y = (int) node.getLayoutY();
        if (view.getWidth() < 1 || view.getHeight() < 1 ||
                view.getWidth() != lw || view.getHeight() != lh) {
            //尺寸有变化
            view.measure(View.MeasureSpec.makeMeasureSpec(lw, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(lh, View.MeasureSpec.EXACTLY));
            view.layout(x, y, x + lw, y + lh);
        } else if (view.getLeft() != x || view.getTop() != y) {
            view.layout(x, y, x + view.getWidth(), y + view.getHeight());
        }
    }

    public static void updateLayout(View view, int x, int y, int width, int height) {
        if (view.getWidth() < 1 || view.getHeight() < 1 ||
                view.getWidth() != width || view.getHeight() != height) {
            //尺寸有变化
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            view.layout(x, y, x + width, y + height);
        } else if (view.getLeft() != x || view.getTop() != y) {
            view.layout(x, y, x + view.getWidth(), y + view.getHeight());
        }
    }

}
