package com.quicktvui.support.ui.render;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class LayoutProcess {

    static boolean DEBUG = BuildConfig.DEBUG;

    List<Process> processImpls;
    private static final String TAG = "LayoutProcess";

    public void append(Process p){
        processes().add(p);
     }

    public void appendProcesses(LayoutProcess layoutProcess){
         if(layoutProcess != null && layoutProcess.processImpls != null) {
             processImpls.addAll(layoutProcess.processImpls);
         }
     }

    public void clear(){
         if(processImpls != null){
             processImpls.clear();
         }
     }

    public void remove(Process p){
         if(processImpls != null) {
             processes().remove(p);
         }
     }

    public List<Process> getProcessImpls() {
        return processImpls;
    }

    void apply(RenderNode child,int parentWidth,int parentHeight){
         if(processImpls != null){
            for(Process p : processImpls){
                p.apply(child,parentWidth,parentHeight);
            }
         }
    }

    List<Process> processes() {
         if(processImpls == null){
             processImpls = new ArrayList<>();
         }
        return processImpls;
     }


     public interface Process{
        void apply(RenderNode child, int parentWidth, int parentHeight);
     }

    public static class ProcessImpl implements Process {
        @Override
        public void apply(RenderNode child,int parentWidth, int parentHeight){
             if(DEBUG) {
                 Log.d(TAG, "ProcessImpl apply name:"+getClass().getSimpleName()+" child:"+child+" parentWidth width :"+parentWidth+" parentHeight:"+parentHeight);
             }
         }
     }


    public static class AlignParentRight extends ProcessImpl {
        @Override
        public void apply(RenderNode child,  int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setX(parentWidth - child.width());
        }
     }

    public static class AlignParentBottom extends ProcessImpl {
        @Override
        public void apply(RenderNode child,int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);

            child.setY(parentHeight - child.height());
        }
    }

//    public static class MarginParentTop extends ProcessImpl {
//        final int top;
//
//        public MarginParentTop(int top) {
//            this.top = top;
//        }
//
//        @Override
//        public void apply(RenderNode child, int parentWidth, int parentHeight) {
//            super.apply(child,parentWidth,parentHeight);
//
//            child.setY(child.getY() + top);
//
//            child.setHeight(child.height() - top);
//        }
//    }


//    public static class MarginParentLeft extends ProcessImpl {
//        final int left;
//
//        public MarginParentLeft(int left) {
//            this.left = left;
//        }
//
//        @Override
//        public void apply(RenderNode child, int parentWidth, int parentHeight) {
//            super.apply(child,parentWidth,parentHeight);
//            child.setX(left);
//            child.setWidth(parentWidth - child.width() - left);
//        }
//    }

//    public static class MarginLeft extends ProcessImpl {
//        @Override
//        public void apply(RenderNode child, int parentWidth, int parentHeight) {
//            super.apply(child,parentWidth,parentHeight);
//
//            child.setY(parentHeight - child.height());
//        }
//    }


    public static class AlignParentTop extends ProcessImpl {
        @Override
        public void apply(RenderNode child, int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setY(0);
        }
    }

    public static class AlignParentLeft extends ProcessImpl {
        @Override
        public void apply(RenderNode child, int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setX(0);
        }
    }

    public static class CenterHorizontal extends ProcessImpl {
        @Override
        public void apply(RenderNode child, int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setX((int) (parentWidth * 0.5f - child.width() * 0.5f));
        }
    }

    public static class CenterVertical extends ProcessImpl {
        @Override
        public void apply(RenderNode child, int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setY((int) (parentHeight * 0.5f - child.height() * 0.5f));
        }
    }

    public static class CenterInParent extends ProcessImpl {
        @Override
        public void apply(RenderNode child,  int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setPosition((int)(parentWidth * 0.5f - child.width() * 0.5f),(int)(parentHeight * 0.5f - child.height() * 0.5f));
        }
    }



    public static class Translate extends ProcessImpl {

         final int x;
         final int y;

        public Translate(int x,int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void apply(RenderNode child, int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.positionBy(x,y);
        }
    }



    public static class SizeBy extends ProcessImpl {

        final int byWidth;
        final int byHeight;

        public SizeBy(int byWidth,int byHeight) {
            this.byWidth = byWidth;
            this.byHeight = byHeight;
        }

        @Override
        public void apply(RenderNode child,  int parentWidth, int parentHeight) {
            super.apply(child,parentWidth,parentHeight);
            child.setSize(child.width() + byWidth,child.height() + byHeight);
        }
    }



}
