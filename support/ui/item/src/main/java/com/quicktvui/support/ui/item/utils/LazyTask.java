package com.quicktvui.support.ui.item.utils;

public class LazyTask {


    Runnable mRunnable;
    Executor mExecutor;

    int delayed = 200;

    public LazyTask(Executor node,Runnable runnable) {
        mRunnable = runnable;
        this.mExecutor = node;
    }

    public LazyTask(Runnable runnable, Executor executor, int delayed) {
        mRunnable = runnable;
        mExecutor = executor;
        this.delayed = delayed;
    }

    public LazyTask setDelayed(int delayed) {
        this.delayed = delayed;
        return this;
    }

    public void cancel(){
        this.mExecutor.removeCallbacks(mRunnable);
    }

    public void postDelayed(){
        if(delayed <= 0){
            mRunnable.run();
        }else{
            this.mExecutor.postDelayed(mRunnable,delayed);
        }
    }


    public interface Executor{
        void postDelayed(Runnable runnable, long delay);
        void removeCallbacks(Runnable runnable);
    }

}
