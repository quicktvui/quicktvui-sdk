package com.quicktvui.support.ui.item;

import android.content.Context;
import android.graphics.Color;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.quicktvui.support.ui.item.utils.DimensUtil;


public class ItemCenter {

    private static Builder builder = new Builder();

    /**
     * 默认选中焦点颜色
     */
    public static int defaultFocusBorderColor = Color.WHITE;

    /**
     * 图片加载圆角
     */
    public static int defaultCornerRadius = 0;

    /**
     * 图片存储缓存策略
     */
    public static DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.AUTOMATIC;
    /**
     * 是否弃用内存缓存
     */
    public static boolean skipMemoryCache = false;
    /**
     * 是否渲染原始图片，如果false,则会根据view大小来缩放图片
     */
    public static boolean useOriginalImageSize = true;

    /**
     * 图片加载全局请求配置
     */
//    public static Glide.RequestOptionsFactory globalRequestOptionsFactory = null;



    @Deprecated
    public static Builder getBuilder() {
        return builder;
    }

    public static void setDEBUG(boolean DEBUG) {
        Config.DEBUG = DEBUG;
    }

    private ItemCenter(Builder b){
        builder = b;
        DimensUtil.init(builder.mContext.getApplicationContext());
    }


    public static boolean isNumberIndexEnabled(){
        return builder.isNumberIndexEnabled;
    }

    public static class Builder{

        Context mContext;

        boolean isNumberIndexEnabled = true;


        public Builder numberIndexEnabled(boolean enabled) {
            isNumberIndexEnabled = enabled;
            return this;
        }

        public Builder context(Context context){
            this.mContext = context;
            return this;
        }

        public Builder() {

        }


        public ItemCenter build(){

            return new ItemCenter(this);
        }
    }




}
