package com.quicktvui.support.ui.render;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**DrawOrderBridge
 * @author zhaopeng
 * 用来联接RendreNode与AndroidView之间的绘制顺序。
 *
 */
public class DrawOrderBridge {

    public interface BridgeView extends RenderHost{
        void superDrawChild(Canvas canvas, View child);
        void superDraw(Canvas canvas);
        void superDispatchDraw(Canvas canvas);
        void invalidate();
    }

    List mPreDrawList;
    List mPostDrawList;

    MyRootNode mRootNode;


    final ZOrderComparator ZOrderComparatorInstance;

    boolean sortRequested = true;

    final static boolean DEBUG = false;


    /**
     * ZOrder的寻找器，用来指定如何从一个view中获取zOrder
     */
    public interface ZOrderFinder{
        int findZOrder(View child);
    }

    ZOrderFinder zOrderFinder;

    final static String TAG = "DrawOrderBridgeLog";

    /**
     *  设置从view里寻找ZOrder的方法。默认是利用tag
     * @param zOrderFinder
     */
    public void setzOrderFinder(ZOrderFinder zOrderFinder) {
        this.zOrderFinder = zOrderFinder;
    }

    /**
     * 利用tag寻找ZOrder
     */
    public static class TagZOrderFinder implements ZOrderFinder{

        @Override
        public int findZOrder(View child) {
            final Object tag = child.getTag();
            if(tag instanceof Integer){
                return (int) tag;
            }

            return 0;
        }
    }

    final BridgeView mBridgeView;

    public DrawOrderBridge(BridgeView mBridgeView) {
        this.mBridgeView = mBridgeView;
        this.mRootNode = new MyRootNode(this);
        zOrderFinder = new TagZOrderFinder();
        ZOrderComparatorInstance = new ZOrderComparator();
    }

    protected void onSortZOrderAll(){
//        Log.d("HostFrameLayoutLog","onSortZOrderAll");
        if(mPreDrawList != null){
            Collections.sort(mPreDrawList,ZOrderComparatorInstance);
        }
        if(mPostDrawList != null){
            Collections.sort(mPostDrawList,ZOrderComparatorInstance);
        }
        this.sortRequested = false;
        logList(mPreDrawList, "preList");
        logList(mPostDrawList, "postList");
    }

    boolean isSortRequested(){
        return sortRequested;
    }

    private void logList(List list,String prefix){
        if(DEBUG) {
            if (list == null) {
                Log.d(TAG, prefix + ":list is null");
                return;
            }
            for (Object o : list) {
                final int order = getZOrderFromDrawChild(o);
                Log.d(TAG, prefix + ":child is " + o + " zOrder：" + order);
            }
        }
    }

    void drawPreList(Canvas canvas){

        if(mPreDrawList != null) {
//            logList(list,"drawList");
            for(int i = 0;i < mPreDrawList.size(); i ++) {
                Object d = mPreDrawList.get(i);
                if (d instanceof View) {
                    //View
                    if(((View) d).getVisibility() == View.VISIBLE) {
                        mBridgeView.superDrawChild(canvas, (View) d);
                    }
                } else {
                    //Node
                    mRootNode.superDrawChild(canvas, (RenderNode) d);
                }
            }
        }
    }

    void drawPostList(Canvas canvas){

        if(mPostDrawList != null) {
            logList(mPostDrawList,"postList");
            for(int i = 0;i < mPostDrawList.size(); i ++) {
                Object d = mPostDrawList.get(i);
                if (d instanceof View ) {
                    //View
                    if(((View) d).getVisibility() == View.VISIBLE) {
                        mBridgeView.superDrawChild(canvas, (View) d);
                    }
                } else {
                    //Node
                    mRootNode.superDrawChild(canvas, (RenderNode) d);
                }
            }
        }
    }


    /**
     * 获得根节点
     * @return
     */
    public MyRootNode getRootNode() {
        return mRootNode;
    }

    /**
     * ZOrder发生变化时,通知刷新ZOrder.
     */
    public void requestSortZOrder(){
        if(DEBUG){
            Log.d(TAG,"requestSortZOrder called");
        }
        this.sortRequested = true;
        if(mPreDrawList!= null) {
            mPreDrawList.clear();
        }
        if(mPostDrawList != null) {
            mPostDrawList.clear();
        }
    }

    int getZOrderFromChild(View child){
        return zOrderFinder.findZOrder(child);
    }

    int getZOrderFromDrawChild(Object child){
        if(child instanceof View){
            return getZOrderFromChild((View) child);
        }else if(child instanceof  RenderNode){
            return ((RenderNode) child).mZOrder;
        }
        return 0;
    }

    /**
     * 如果有需要处理ViewGroup的drawChild方法
     * @param canvas
     * @param child
     * @param drawingTime
     * @return 如果返回false执行默认行为
     */
    public boolean handleIfNeedOnDrawChild(Canvas canvas, View child, long drawingTime){
        if(getZOrderFromChild(child) == 0){
            return false;
        }else{
            return putDrawList(child);
        }
    }

    /**
     * 如果有需要处理View的draw方法
     * @param canvas
     * @return 如果返回false执行默认行为
     */
    @Deprecated
    public boolean handleDraw(Canvas canvas) {

        if(isSortRequested()){
            if(DEBUG){
                Log.v(TAG,"handleDraw onSortZOrder");
            }
            mBridgeView.superDraw(canvas);
            if(mRootNode != null){
                mRootNode.draw(canvas);
            }
           onSortZOrderAll();
           mBridgeView.invalidate();
        }else{
            if(DEBUG){
                Log.v(TAG,"handleDraw all ");
            }
            drawPreList(canvas);
            mBridgeView.superDraw(canvas);
            if(mRootNode != null){
                mRootNode.draw(canvas);
            }
            drawPostList(canvas);
        }
        return true;
    }

    /**
     * 如果有需要处理View的draw方法
     * @param canvas
     * @return 如果返回false执行默认行为
     */
    public boolean handleDispatchDraw(Canvas canvas) {

        if(isSortRequested()){
            if(DEBUG){
                Log.v(TAG,"handleDraw onSortZOrder");
            }
            mBridgeView.superDispatchDraw(canvas);
            if(mRootNode != null){
                mRootNode.draw(canvas);
            }
            onSortZOrderAll();
            mBridgeView.invalidate();
        }else{
            if(DEBUG){
                Log.v(TAG,"handleDraw all ");
            }
            drawPreList(canvas);
            mBridgeView.superDispatchDraw(canvas);
            if(mRootNode != null){
                mRootNode.draw(canvas);
            }
            drawPostList(canvas);
        }
        return true;
    }


    boolean putDrawList(Object d){
        boolean put = false;
        int order = 0;
        if(d instanceof View){
            //View
            order = getZOrderFromChild((View) d);
        }else{
            //Node
            order = ((RenderNode)d).mZOrder;
        }

        if(order < 0){
            if(mPreDrawList == null){
                mPreDrawList = new ArrayList();
            }
            if(!mPreDrawList.contains(d)) {
//                Log.d("HostFrameLayoutLog","putDrawList order is "+order);
                put = mPreDrawList.add(d);
            }
        }
        if(order > 0){
            if(mPostDrawList == null){
                mPostDrawList = new ArrayList();
            }
            if(!mPostDrawList.contains(d)) {
//                Log.d("HostFrameLayoutLog","putDrawList order is "+order);
                put = mPostDrawList.add(d);
            }
        }

        return put;

    }


    final class ZOrderComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            final int order1 = getZOrderFromDrawChild(o1);
            final int order2 = getZOrderFromDrawChild(o2);
            return order1 - order2;
        }
    }



    final static class MyRootNode extends RootNode{
        final DrawOrderBridge mHostFrameLayout;

        public MyRootNode(DrawOrderBridge hostView) {
            super(hostView.mBridgeView.getHostView());
            this.mHostFrameLayout = hostView;
        }

        @Override
        protected void onAddNode(RenderNode node) {
            super.onAddNode(node);
            if(DEBUG){
                Log.v(TAG,"MyRootNode onAddNode node: "+node);
            }
            mHostFrameLayout.requestSortZOrder();
        }


        @Override
        protected void drawChild(Canvas canvas, RenderNode node) {
            if(node.mZOrder != 0){
                mHostFrameLayout.putDrawList(node);
            }else {
                super.drawChild(canvas, node);
            }
        }

        protected void superDrawChild(Canvas canvas, RenderNode node) {
            if(DEBUG){
                Log.v(TAG,"MyRootNode superDrawChild node: "+node);
            }
            super.drawChild(canvas,node);
        }
    }
}
