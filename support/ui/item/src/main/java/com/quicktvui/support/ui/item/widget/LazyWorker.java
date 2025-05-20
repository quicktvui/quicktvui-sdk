package com.quicktvui.support.ui.item.widget;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 懒惰的工人
 */
public final class LazyWorker {


    private MyHandler mHandler;

//        this.mHandler = handler;
//        assert (mHandler != null);
//    }
//    LazyWorker(MyHandler handler) {

    public LazyWorker() {
        this.mHandler = new MyHandler();
    }


    public void execute(int what, final Runnable work, int delay){
        final Message msg = mHandler.obtainMessage(what);
        msg.what = what;
        msg.obj = work;
        mHandler.sendMessageDelayed(msg,delay);
        mHandler.registerMessage(msg);
    }

    void enqueueWithOldMessage(Message msg,int delay){
        mHandler.sendMessageDelayed(msg,delay);
        mHandler.registerMessage(msg);
    }


    public void cancelWork(int what){
        mHandler.removeMessages(what);
    }

    public void postponeWork(final int what,int delay){
        mHandler.removeMessages(what);
        ArrayList<Message> msgList = new ArrayList<>();
        for(Message msg : mHandler.messageQueue){
            if(msg.what == what){
                msgList.add(msg);
            }
        }
        mHandler.clearMessageQueue(what);

        for(Message msg : msgList){
            enqueueWithOldMessage(msg,delay);
        }
    }



    public void cancelAllWork(){
        mHandler.removeCallbacksAndMessages(null);
    }


    private final static class MyHandler extends Handler{
        List<Message> messageQueue;

        private MyHandler() {
            this.messageQueue = new ArrayList<>();
        }

        private void registerMessage(Message msg){
            messageQueue.add(msg);
        }

        void clearMessageQueue(final int what){
            Iterator<Message> it = messageQueue.iterator();
            while (it.hasNext()){
                Message msg = it.next();
                if(msg.what == what){
                    it.remove();
                }
            }
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Runnable runnable = (Runnable) msg.obj;
//            Log.d("LazyWorker","handleMessage what is :"+msg.what);
            if(runnable != null){
                runnable.run();
            }
            msg.obj = null;
            messageQueue.remove(msg);
        }
    }

}
