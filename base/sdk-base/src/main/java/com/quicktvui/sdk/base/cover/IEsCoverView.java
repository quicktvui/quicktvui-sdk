package com.quicktvui.sdk.base.cover;

import java.io.Serializable;

import com.quicktvui.sdk.base.EsException;

/**
 * Create by weipeng on 2021/11/30 17:49
 */
public interface IEsCoverView {

    /** 初始化，传入参数 **/
    void onInit(Serializable data);

    /** 页面渲染成功 **/
    void onEsRenderSuccess();

    /** 页面渲染失败 **/
    void onEsRenderFailed(EsException exception);

    /** 页面临时挂起，暂时不要自动关闭 **/
    void suspend(String msg);

    /** 页面挂起结束，可以关闭页面 **/
    void unSuspend();
}