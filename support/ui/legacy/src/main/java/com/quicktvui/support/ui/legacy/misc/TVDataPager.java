package com.quicktvui.support.ui.legacy.misc;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.quicktvui.support.ui.legacy.FConfig;


public class TVDataPager {

    final static String TAG = "TVDataPager";

    //总的page数
    protected int mPageCount;
    //总的item数
    protected int mItemCount;

    //一页的个数
    protected final int mPageSize;

    //现在的页数
    protected int mPage = 1;

    protected final IPageDataCallback mPageDataCallback;

    protected PagingTask mPagingTask;

    protected Handler mHandler;


    public TVDataPager(int itemCount, int pageSize, IPageDataCallback callback) {
        this(1,itemCount,pageSize,callback);
    }

    public TVDataPager(int startPage, int itemCount, int pageSize, IPageDataCallback callback) {

        mPageSize = pageSize;
        mPageDataCallback = callback;

        updateItemCount(itemCount);
        this.mPage = startPage;
        mHandler = new Handler(Looper.getMainLooper());
    }


    /**
     * 回调，用来告诉用户，需要对应拉取数据
     */
    public interface IPageDataCallback {
        void loadDataAsyn(int page,int pageSize);
        void displayLoading(boolean show);
    }


    public void updateItemCount(int itemCount){
        this.mItemCount = itemCount;
        mPageCount = itemCount % mPageSize == 0 ? itemCount / mPageSize : itemCount / mPageSize + 1;
    }

    public int getPageCount() {
        return mPageCount;
    }


    public int getPageSize() {
        return mPageSize;
    }

    public PagingTask getPagingTask(){
        return mPagingTask;
    }

    public int getItemCount() {
        return mItemCount;
    }

    public int getPage() {
        return mPage;
    }


    public boolean isBusy(){
        return getPagingTask() != null;
    }

    /**
     * 如果有需要请求下一页的数据，这里如果正在请求数据，不会累加执行
     */
    public boolean requestNextPageDataIfNeed(){

        return loadPageInternalIfNeed(mPage + 1);
    }

//    /**
//     * 如果有需要请求上一页的数据
//     */
//    public boolean requestPrevPageDataIfNeed(){
//        return loadPageInternalIfNeed(mPage - 1);
//    }

    /**
     * @param page
     */
    public boolean requestPageDataIfNeed(final int page){
        return loadPageInternalIfNeed(page);
    }


    /**通知数据已经拉取成功
     * @param page
     * @param success
     */
    public synchronized void notifyLoadDataResult(final int page,final boolean success){

        final PagingTask task = getPagingTask();
        if(FConfig.DEBUG){
            Log.v(TAG,"notifyLoadDataResult task is "+task+" page is "+page+" success is "+success);
        }
        if (task != null) {
            //此时证明数据已经获取到，将任务置空
            updatePagingTask(null);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPageDataCallback.displayLoading(false);
                }
            });
            if (task.targetPage == page) {
                //成功后，将page数据更新
                if (success)
                    updatePageNum(page);
            }
        }
    }



    /**如果有必要加载对应页数数据
     * @param page 对应页
     * @return 是否开始拉取数据
     */
    protected synchronized boolean loadPageInternalIfNeed(int page){
        boolean handle = false;
        final PagingTask task = getPagingTask();

        if (task != null && task.targetPage == page) {
            //此时表明数据正在拉取
            handle = false;
        } else {
            if(page > mPageCount || page < 0){
                //此时表明已经拉至数据的尽头
                if(FConfig.DEBUG){
                    Log.w(TAG,"loadPageInternalIfNeed page > mPageCount || page < 0 return page is "+page+" pageCount is "+mPageCount);
                }
                return false;
            }
            runLoadPageTask(page);
            handle = true;
        }
        if(FConfig.DEBUG){
            Log.v(TAG,"loadPageInternalIfNeed pagingTask is "+task+" handle is "+handle);
        }
        return handle;
    }

    /**更新分页拉取数据的任务
     * @param action
     */
    protected void updatePagingTask(PagingTask action){
        mPagingTask = action;
    }

    /**执行一个拉取分页数据
     * @param page
     */
    protected void runLoadPageTask(int page){
        if(FConfig.DEBUG){
            Log.v(TAG,"runLoadPageTask page is "+page);
        }
        PagingTask action = new PagingTask(page, mPageSize, mPageDataCallback);
        updatePagingTask(action);
        mHandler.post(action);
    }

    /**更新现在的page数
     * @param page
     */
    public void updatePageNum(int page){
        this.mPage = page;
    }


    public static final class PagingTask implements Runnable{

        final int targetPage;
        final int pageSize;
        final IPageDataCallback callback;

        PagingTask(int targetPage, int pageSize, IPageDataCallback callback) {
            this.targetPage = targetPage;
            this.pageSize = pageSize;
            this.callback = callback;
        }

        @Override
        public void run() {
            if(FConfig.DEBUG){
                Log.v(TAG,"PagingTask run targetPage is "+targetPage);
            }
            callback.displayLoading(true);
            callback.loadDataAsyn(targetPage,pageSize);
        }

        @Override
        public String toString() {
            return "PagingTask{" +
                    "targetPage=" + targetPage +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }




}
