//package eskit.sdk.core.fragment;
//
//import eskit.sdk.core.pm.IEsPageView;
//import com.quicktvui.sdk.base.args.EsMap;
//
///**
// * Fragment管理接口
// * <p>
// *     功能需求:
// *     1. Standard
// *     2. ClearTop
// *     3. SingleInstance
// *
// * Create by weipeng on 2022/08/22 10:51
// */
//public interface IEsFragmentMgr {
//
//    void attachSubViewContainer();
//
//    /**
//     * 添加View
//     * @param pageId 页面ID
//     * @param view       View
//     */
//    void addView(int pageId, IEsPageView view);
//
//    /** 获取注册的View
//     * @return**/
//    IEsPageView getView(int pageId);
//
//    /** 启动页面 Standard **/
//    void startPage(int pageId, EsMap params);
//
//    /** 删除页面 **/
//    IEsPageView deletePage(int pageId);
//
//    /**
//     * 释放资源
//     */
//    void release();
//}
