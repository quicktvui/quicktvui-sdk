package com.quicktvui.support.ui.render;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RenderNode extends Drawable  {


    /**
     * 默认的画笔
     */
    protected Paint mDefaultPaint = new Paint();

    /**
     * 所有的渲染结点
     */
    protected RenderNodeList mRenderNodes = new RenderNodeList();

    /**
     * 父结点
     */
    protected RenderNode mParent;

    /**
     * 绘制顺序，只在同层级结点下生效
     */
    protected int mZOrder = 0;

//    boolean isSortRequested = false;

    int mFlag = 0;

    public static final boolean DEBUG = false;
    /**
     *
     * 请求重新排列ZOrder
     */
    public static final int FLAG_REQUESTED_SORT = 0x1;

    /**
     * 请求重新测量
     */
    public static final int FLAG_REQUESTED_MEASURE = 0x2;

    /**
     * 是否宽度与父结点相同
     */
    public static final int FLAG_SIZE_FILL_WIDTH = 0x4;
    /**
     * 是否高度与父结点相同
     */
    public static final int FLAG_SIZE_FILL_HEIGHT = 0x8;

    /**
     * 是否高度与父结点相同
     */
    public static final int FLAG_FORCE_MEASURE = 0x00000010;


    /**
     * 渲染的位置
     */
    protected float translateX,translateY = 0;

    final Comparator ZOrderComparator = new ZOrderComparator();


    /**
     * 结点大小为填充父结点
     */
    public final static int MATCH_PARENT = -1;

    /**
     * 包裹内容，暂不支持
     */
    final static int WRAP_CONTENT = -2;

    /**
     * 结点名称
     */
    protected String mName;


    public static final String TAG = "RenderNode";

    private boolean debugDraw = false;


    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    OnSizeChangedListener onSizeChangedListener;

    /**
     * 根结点
     */
    RootNode mRootNode;

    Paint mDebugPaint;

    RenderNode mBackGroundNode;


    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }


    int preferWidth = 0;
    int preferHeight = 0;

//    public <T extends RenderNode> T  findNodeByName(String name){
//        //TODO 实现使用名称实现子节点的功能。
//        return null;
//    }
//
//    public <T extends RenderNode> T findNodeByTag(Object name){
//        //TODO 实现使用tag来寻找子节点的功能
//        return null;
//    }

    /**
     * 设置名称
     * @param name
     */
    public void setName(String name) {
        mName = name;
    }

    public RenderNode(String name) {
        mName = name;
        init();
    }

    public int getPreferWidth() {
        return preferWidth;
    }

    public int getPreferHeight() {
        return preferHeight;
    }

    /**获得Node的name
     * @return
     */
    public String getName() {
        return mName;
    }

    Object tag;
    public void setTag(Object tag) {
        this.tag = tag;
    }
    public Object getTag() {
        return tag;
    }

    public RenderNode() {
        this(null);
    }


    protected void init(){ }


    /**
     * draw自身，需要子结点复写
     * @param canvas
     */
    public void onDraw(Canvas canvas){
        //draw nothing

    }

    /**
     * draw自身，需要子结点复写
     * @param canvas
     */
    public void onDrawBackGround(Canvas canvas){
        //draw nothing
        if(mBackGroundNode != null){
            mBackGroundNode.draw(canvas);
        }

    }

    public void setBackGround(@Nullable RenderNode backGround) {
        this.mBackGroundNode = backGround;

    }

    /**
     * 绘制所有子结点
     * @param canvas
     */
    protected void drawChildren(Canvas canvas){
        if((mFlag & FLAG_REQUESTED_SORT) == FLAG_REQUESTED_SORT){
            Collections.sort(mRenderNodes, ZOrderComparator);
            mFlag &= ~FLAG_REQUESTED_SORT;
        }
        for (RenderNode node : mRenderNodes){
            if(node.isVisible()) {
                drawChild(canvas,node);
            }
        }
    }


    protected void drawChild(Canvas canvas,RenderNode node){
        node.draw(canvas);
    }


    @Override
    public void setAlpha(int alpha) {
        mDefaultPaint.setAlpha(alpha);
    }


    /**
     * 相对于父结点的沉浸位置
     * @param x 横向
     * @param y 纵向
     * @return
     */
    public RenderNode setPosition(int x, int y) {
        this.translateX =  x;
        this.translateY =  y;
        invalidateSelf();
        return this;
    }

    public RenderNode setPositionF(float x,float y){
        this.translateX =  x;
        this.translateY =  y;
        invalidateSelf();
        return this;
    }


    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        return super.setVisible(visible, restart);
    }



    /**
     * 当结点大小发生改变时，此方法会被回调
     * @param bounds
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if(RenderLab.DEBUG){
            Log.v(TAG,"onBoundsChange : "+bounds+" this is "+this);
        }
        if(mBackGroundNode != null){
            mBackGroundNode.changeSizeInternal(width(),height());
        }

        if(onSizeChangedListener != null){
            onSizeChangedListener.onSizeChanged(this,width(),height());
        }
        requestMeasureChildren();
    }


    /**
     * 设置结点大小
     * @param width 宽度，可以设置成{{@link #MATCH_PARENT}}
     * @param height 高度，可以设置成{{@link #MATCH_PARENT}}
     * @return
     */
    public RenderNode  setSize(int width, int height) {
        this.preferWidth = width;
        this.preferHeight = height;
        if(width == MATCH_PARENT){
            mFlag |= FLAG_SIZE_FILL_WIDTH;
        }else{
            mFlag &= ~FLAG_SIZE_FILL_WIDTH;
        }
        if(height == MATCH_PARENT){
            mFlag |= FLAG_SIZE_FILL_HEIGHT;
        }else{
            mFlag &= ~FLAG_SIZE_FILL_HEIGHT;
        }
        return changeSizeInternal(width,height);
    }

    public RenderNode getParent() {
        return mParent;
    }


    public RenderNode setX(int x){
        return setPosition(x,getY());
    }

    public RenderNode setY(int y){
        return setPosition(getX(),y);
    }

    public RenderNode setWidth(int width){
        return setSize(width,height());
    }

    public RenderNode setHeight(int height) {
        return setSize(width(),height);
    }

    public RenderNode positionBy(int x,int y){
        return setPosition(getX() + x,getY() + y);
    }


    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
    }

    RenderNode changeSizeInternal(int width, int height){
        final Rect b = getBounds();

        if(b.width() != width || b.height() != height) {
            setBounds(b.left, b.top, b.left + width, b.top + height);
        }
        return this;
    }



    boolean changeWidthQuietly(int width){
        boolean change = false;
        final Rect b = getBounds();
        if(b.width() != width ) {
            getBounds().set(b.left, b.top, b.left + width, b.bottom);
            change = true;
        }
        return change;
    }

    boolean changeHeightQuietly(int height){
        boolean change = false;
        final Rect b = getBounds();
        if(b.height() != height ) {
            getBounds().set(b.left, b.top, b.right ,b.top + height);
            change = true;
        }
        return change;
    }

    /**
     * 获得渲染的横向坐标
     * @return
     */
    public int getX() {
        return (int)translateX;
    }

    public float getXFloat(){
        return translateX;
    }

    public float getYFloat(){
        return translateY;
    }

    /**
     * 获得渲染的纵向坐标
     * @return
     */
    public int getY() {
        return (int)translateY;
    }


    /**
     * 所有子结点请求测量
     */
    void requestMeasureChildren(){
        //大小改变，所有的子节点都要重新Measure
        for (RenderNode node : mRenderNodes) {
            node.requestMeasure();
        }
        invalidateSelf();
    }


    /**
     * 获得所有子结点的集合
     * @return
     */
    public List<RenderNode> children() {
        return mRenderNodes;
    }

    /**
     * 获得父结点
     * @return
     */
    public RenderNode parent() {
        return mParent;
    }

    /**
     * 设置绘制顺序，只针对同一级别结点生效
     * @param zOrder 数值大的在上层
     */
    public void setZOrder(int zOrder) {
        this.mZOrder = zOrder;
        if(mParent != null){
            mParent.requestSortChildrenByZOrder();
        }
    }
    

    protected void requestSortChildrenByZOrder(){

        mFlag |= FLAG_REQUESTED_SORT;

        invalidateSelf();
    }



    /**
     * 请求重新测量
     */
    void requestMeasure(){
        mFlag |= FLAG_REQUESTED_MEASURE;
        invalidateSelf();
    }

    /**
     * 强制请求重新测量
     */
    public void forceMeasure(){
        mFlag |= FLAG_FORCE_MEASURE;
        invalidateSelf();
    }


    public void setDebugDraw(boolean debugDraw,int color) {
        this.debugDraw = debugDraw;
        if( mDebugPaint == null){
            mDebugPaint = new Paint();
            mDebugPaint.setStyle(Paint.Style.STROKE);
            mDebugPaint.setStrokeWidth(1);
        }
        mDebugPaint.setColor(color);
    }

    public void setDebugDraw(int color) {
        this.debugDraw = true;
        if( mDebugPaint == null){
            mDebugPaint = new Paint();
            mDebugPaint.setStyle(Paint.Style.STROKE);
            mDebugPaint.setStrokeWidth(1);
        }
        mDebugPaint.setColor(color);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        if(isVisible()) {
            int saved = 0;
            if (translateX != 0 || translateY != 0) {
                saved = canvas.save();
                canvas.translate(translateX, translateY);
            }
            //如果有需要，进行一次Measure

            if (((mFlag & FLAG_FORCE_MEASURE) == FLAG_FORCE_MEASURE) || ((mFlag & FLAG_REQUESTED_MEASURE) == FLAG_REQUESTED_MEASURE)) {
                doMeasure();
                mFlag &= ~FLAG_REQUESTED_MEASURE;
                mFlag &= ~FLAG_FORCE_MEASURE;
//                invalidateSelfDelayed(16);
//                return;
            }

            if(width() > 0 && height() > 0 && (layout == null || layout.isApplied())) {
                onDrawBackGround(canvas);
                onDraw(canvas);
                //layout children if need
                drawChildren(canvas);
            }
            if (debugDraw && (layout == null || layout.isApplied())) {
                canvas.drawRect(getDrawBounds(), mDebugPaint);
            }
            if (saved > 0) {
                canvas.restoreToCount(saved);
            }
        }
    }





    void doMeasure(){

        if (mParent != null) {
            boolean changed = false;
            final int oldWidth = width();
            final int oldHeight = height();
            int newWidth = -1;
            int newHeight = -1;
            if (((mFlag & FLAG_SIZE_FILL_WIDTH) == FLAG_SIZE_FILL_WIDTH)
                    ) {
                newWidth = mParent.width();
            }

            if(((mFlag & FLAG_SIZE_FILL_HEIGHT) == FLAG_SIZE_FILL_HEIGHT)){
                newHeight = mParent.height();
            }

            changed |= (newWidth > 0 &&  oldWidth != newWidth) ;
            changed |= (newHeight > 0 && oldHeight != newHeight);

            if(changed || ( ( mFlag & FLAG_FORCE_MEASURE ) == FLAG_FORCE_MEASURE )){
                final int nw = newWidth > 0 ? newWidth : oldWidth;
                final int nh = newHeight > 0 ? newHeight : oldHeight;
                onMeasure(nw,nh);
            }
            doLayoutProcesses();
        }
    }

    private Layout layout;

    protected void doLayoutProcesses(){
        if(layout != null) {
            if(RenderLab.DEBUG) {
                Log.d(TAG, "doLayoutProcesses layout:" + layout);
            }
        }
        final RenderNode p = parent();
        if(layout != null && p != null){
            layout.apply(this,p.width(),p.height());
        }
    }

    public void setLayout(Layout layout){
        if(RenderLab.DEBUG){
            Log.d(TAG,"setLayout ");
        }
        if(this.layout != null){
            this.layout.layoutProcess.clear();
        }
        this.layout = layout;
        layout.setBoundNode(this);
        forceMeasure();
    }

    protected  void onMeasure(int width , int height){
        if(RenderLab.DEBUG){
            Log.d(TAG," onMeasure  width : "+width +" height is "+height+" this : "+this);
        }
        changeSizeInternal(width,height);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mDefaultPaint.setColorFilter(colorFilter);
    }

    public int width() {
        return getBounds().width();
    }

    public int height() {
        return getBounds().height();
    }


    public RenderNode add(RenderNode node) {
        mRenderNodes.add(node);
        onAddNode(node);
        node.onAddToPrent(this);
        node.mParent = this;
        requestSortChildrenByZOrder();
        requestMeasureChildren();
        return this;
    }

    protected void onAddNode(RenderNode node){

    }

    void onRemoveNode(RenderNode node){

    }

    protected void onAddToPrent(RenderNode parent){

    }

    protected void onRemoveFromParent(RenderNode parent){

    }


    /**找寻根结点
     * @return
     */
    public RootNode findRoot() {
        if(mRootNode != null){
            return mRootNode;
        }

        if(this.isRoot()){
            this.mRootNode = (RootNode) this;
            return mRootNode;
        }

        if(mParent != null){
            return mParent.findRoot();
        }
        return null;
    }


    /**是否是根结点
     * @return
     */
    boolean isRoot(){
        return this instanceof RootNode;
    }


    /**
     * 删除一个结点
     * @param node
     * @return
     */
    public boolean remove(RenderNode node) {
        final boolean b =  mRenderNodes.remove(node);
        onRemoveNode(node);
        node.mParent = null;
        node.onRemoveFromParent(this);
        requestSortChildrenByZOrder();
        return b;
    }



    /**
     * 根据位置删除结点
     * @param index 0 - count-1
     * @return
     */
    public boolean removeAt(int index){
        if(index >= 0 &&  mRenderNodes.size() > index){
            final RenderNode target = mRenderNodes.get(index);
            return  remove(target);
        }
        return false;
    }

    void removeNodeFromParent(RenderNode node){
        if(node != null && node.mParent != null){
            node.mParent.remove(node);
        }
    }

    /**
     * 销毁结点
     */
    public void destroy(){
        removeNodeFromParent(this);
        onDestroy();
    }

    /**
     * 结点销毁时,有需求可以复写此方法
     */
    protected void onDestroy(){

    }

    /**
     * 刷新自身
     */
    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        final RenderNode root = findRoot();
        if(root != null){
            root.invalidateSelf();
        }
    }

    public void invalidateSelfDelayed(long delayMilliseconds){
        if(findRoot() != null){
            findRoot().getHostView().postInvalidateDelayed(delayMilliseconds);
        }
    }

    final static class RenderNodeList extends ArrayList<RenderNode>{
        //TODO
    }

    final static class ZOrderComparator implements Comparator<RenderNode>{
        @Override
        public int compare(RenderNode o1, RenderNode o2) {
            return o1.mZOrder - o2.mZOrder;
        }
    }


    @Override
    public String toString() {
        if(mName != null){
            return super.toString()+"-"+mName;
        }else {
            return super.toString();
        }
    }


    /**延迟执行任务
     * @param action 任务
     * @param time 延迟时间
     */
    public void postDelayed(Runnable action, long time){
        if(findRoot() != null){
            final RootNode root =  findRoot();
            root.getHostView().postDelayed(action,time);
        }else{
            Log.e(TAG,"postDelayed failed , cant found rootNode");
        }
    }

    public void removeCallbacks(Runnable runnable){
        if(findRoot() != null){
            final RootNode root = findRoot();
            root.getHostView().removeCallbacks(runnable);
        }
    }


    /**
     * 获得绘制区域的Rect
     * @return
     */
    public Rect getDrawBounds(){
        return getBounds();
    }




}
