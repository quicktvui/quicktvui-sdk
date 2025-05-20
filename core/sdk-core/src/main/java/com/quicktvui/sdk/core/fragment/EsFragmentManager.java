//package eskit.sdk.core.fragment;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentContainerView;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//
//import com.sunrain.toolkit.utils.log.L;
//
//import com.quicktvui.sdk.core.R;
//import eskit.sdk.core.internal.EsContext;
//import eskit.sdk.core.internal.IEsViewer;
//import eskit.sdk.core.pm.IEsPageView;
//import com.quicktvui.sdk.base.args.EsMap;
//
///**
// * Fragment管理接口实现
// * <p>
// * Create by weipeng on 2022/08/22 10:53
// */
//public class EsFragmentManager implements IEsFragmentMgr {
//
//    private IEsViewer mViewer;
//    @Nullable
//    private FragmentManager mFragmentManager;
//    private final SparseArray<IEsPageView> mViews = new SparseArray<>();
//    private final SparseArray<EsFragment> mFragments = new SparseArray<>();
//
//    private final String NAME_FRAGMENT = EsFragment.class.getName();
//
//    private boolean isContainerAdded;
//
//    public EsFragmentManager(IEsViewer viewer, FragmentManager manager) {
//        this.mViewer = viewer;
//        this.mFragmentManager = manager;
//    }
//
//    @Override
//    public void attachSubViewContainer() {
//        int parentId = mViewer.getFragmentContainerLayoutId();
//        L.logWF("view " + mViewer);
//        L.logWF("parentId " + parentId);
//        L.logWF("aId " + R.id.es_browser_root_view);
//        // 创建子Container
//        Activity activity = (Activity) mViewer.getAppContext();
//        ViewGroup parent = activity.findViewById(parentId);
//        if(parent == null){
//            L.logEF("can not get view group with id:" + parentId);
//            return;
//        }
//
//        FragmentContainerView container = new FragmentContainerView(activity);
//        container.setId(R.id.es_subview_container);
//        if(L.DEBUG){
//            L.logDF("container add");
//            container.post(()->{
//                isContainerAdded = true;
//                L.logDF("container added");
//                checkAdded("1");
//            });
//        }
//        parent.addView(container);
//        checkAdded("0");
//    }
//
//    private void checkAdded(String tag){
//        if(L.DEBUG && mViewer instanceof Activity){
//            View v = ((Activity) mViewer).findViewById(R.id.es_subview_container);
//            if(L.DEBUG) L.logD("container " + tag + " check add " + v);
//        }
//    }
//
//    @Override
//    public void addView(int pageId, IEsPageView view) {
//        if (L.DEBUG) L.logD("addView: " + pageId + "  " + hashCode());
//        if (mViews.indexOfKey(pageId) >= 0) {
//            L.logWF("addView return");
//        }
//        mViews.put(pageId, view);
//        if (L.DEBUG) L.logD("added");
//    }
//
//    @Override
//    public IEsPageView getView(int pageId) {
//        return mViews.get(pageId);
//    }
//
//    @Override
//    public void startPage(int pageId, EsMap params) {
//        if (L.DEBUG) L.logD("startPage: " + pageId);
//        if(mFragmentManager == null) return;
//        FragmentTransaction ft = mFragmentManager.beginTransaction();
//        ft.setReorderingAllowed(true);
//
//        if(mFragments.indexOfKey(pageId) >= 0){
//            EsFragment fragment = mFragments.get(pageId);
//            mFragments.remove(pageId);
//            ft.remove(fragment);
//            if (L.DEBUG) L.logD("remove " + fragment);
//        }
//
////        if (mFragments.size() != 0) {
////            ft.setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
////        }
//
//        if (L.DEBUG) L.logD("create");
//        // 隐藏之前的Fragment
//        Fragment lastFragment = mFragmentManager.getPrimaryNavigationFragment();
//        L.logD("last " + lastFragment);
//
//        if (lastFragment instanceof EsFragment) {
//            ft.hide(lastFragment);
//            ((EsFragment) lastFragment).beforeHide();
//            if (L.DEBUG) L.logD("hide:" + lastFragment);
//        }
//        // 添加新的Fragment
//        Context context = EsContext.get().getContext();
////        L.logIF("开始实例化Fragment");
////        L.logIF("宿主CL " + mViewer.getAppContext().getClassLoader().hashCode());
////        L.logIF("插件CL " + EsFragment.class.getClassLoader().hashCode());
////        L.logIF("Fragment CL " + Fragment.class.getClassLoader().hashCode());
////        L.logIF("EsFragment CL " + EsFragment.class.getClassLoader().hashCode());
////        L.logIF("FragmentManager CL " + mFragmentManager.getClass().getClassLoader().hashCode());
////        L.logIF("getApp(Base) CL " + Utils.getApp().getClassLoader().hashCode());
////        Fragment f = mFragmentManager.getFragmentFactory()
////                .instantiate(mViewer.getAppContext().getClassLoader(), NAME_FRAGMENT);
////        L.logIF("实例化成功:" + f);
//        EsFragment currentFragment = (EsFragment) mFragmentManager.getFragmentFactory()
//                .instantiate(context.getClassLoader(), NAME_FRAGMENT);
//        currentFragment.setPageId(pageId);
//        if (L.DEBUG) L.logD("show: " + currentFragment);
//        ft.add(R.id.es_subview_container, currentFragment, String.valueOf(pageId));
//        ft.setPrimaryNavigationFragment(currentFragment);
//
////        ft.commitNow();
//        if(L.DEBUG) L.logD("container use " + isContainerAdded);
//        checkAdded("2");
//        ft.commitNowAllowingStateLoss();
//
//        // 假生命周期
//        if (lastFragment instanceof EsFragment) {
//            ((EsFragment) lastFragment).onPauseFake();
//            ((EsFragment) lastFragment).onStopFake();
//        }
//        // 记录
//        mFragments.append(pageId, currentFragment);
//        if (L.DEBUG) L.logD("startPage end");
//    }
//
//    @Override
//    public IEsPageView deletePage(int pageId) {
//        if (L.DEBUG) L.logD("deletePage: " + pageId);
//        if(mFragmentManager == null) return null;
//        if (mFragments.indexOfKey(pageId) < 0) return null;
//        if (L.DEBUG) L.logD("delete");
//        FragmentTransaction ft = mFragmentManager.beginTransaction();
//        ft.setReorderingAllowed(true);
////        ft.setCustomAnimations(R.anim.slide_in_pop, R.anim.slide_out_pop);
//        // 删除Fragment
//        EsFragment fragment = mFragments.get(pageId);
//        mFragments.remove(pageId);
//        ft.remove(fragment);
//        if (L.DEBUG) L.logD("remove " + fragment);
//
//        // 显示上一个Fragment
//        EsFragment needShow = null;
//        if (mFragments.size() > 0) {
//            int key = mFragments.keyAt(mFragments.size() - 1);
//            if (L.DEBUG) L.logD("show:" + key);
//            needShow = mFragments.get(key);
//            ft.show(needShow);
//            ft.setPrimaryNavigationFragment(needShow);
//            if (L.DEBUG) L.logD("just show " + needShow);
//        }
//
////        ft.commitNow();
//        ft.commitNowAllowingStateLoss();
//
//        // 假生命周期
//        if (needShow != null) {
//            ((EsFragment) needShow).onStartFake();
//            ((EsFragment) needShow).onResumeFake();
//        }
//
//        // 删除View
//        mViews.remove(pageId);
//        if (L.DEBUG) L.logD("deletePage end");
//
//        if (mViews.size() > 0) {
//            return mViews.valueAt(mViews.size() - 1);
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public void release() {
//        if (L.DEBUG) L.logD("release");
//        mFragments.clear();
//        mViews.clear();
//        mFragmentManager = null;
//    }
//
//    //region 单例
//
////    private static final class IEsFragmentMgrHolder {
////        private static final IEsFragmentMgr INSTANCE = new EsFragmentManager();
////    }
////
////    public static IEsFragmentMgr get() {
////        return IEsFragmentMgrHolder.INSTANCE;
////    }
////
////    private EsFragmentManager() {
////    }
//
//    //endregion
//
//
//}
