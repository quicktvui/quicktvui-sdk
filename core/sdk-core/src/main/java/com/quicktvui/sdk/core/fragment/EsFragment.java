//package eskit.sdk.core.fragment;
//
//import static eskit.sdk.core.internal.Constants.EVT_DISPATCH_KEY_FRAGMENT;
//import static eskit.sdk.core.internal.Constants.EVT_LIFE_CHANGE_FRAGMENT;
//import static eskit.sdk.core.internal.Constants.LIFE_CREATE;
//import static eskit.sdk.core.internal.Constants.LIFE_DESTROY;
//import static eskit.sdk.core.internal.Constants.LIFE_PAUSE;
//import static eskit.sdk.core.internal.Constants.LIFE_RESUME;
//import static eskit.sdk.core.internal.Constants.LIFE_START;
//import static eskit.sdk.core.internal.Constants.LIFE_STOP;
//
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//
//import com.sunrain.toolkit.utils.log.L;
//
//import eskit.sdk.core.internal.EsViewManager;
//import com.quicktvui.sdk.core.R;
//import eskit.sdk.core.internal.EsContext;
//import eskit.sdk.core.pm.IEsPageView;
//import com.quicktvui.sdk.base.args.EsMap;
//
//public final class EsFragment extends Fragment implements IEsPageView.EventHandler {
//
//    private int mPageId;
//    private IEsPageView mIEsView;
//
//    public void setPageId(int pageId) {
//        mPageId = pageId;
//    }
//
//    public int getPageId() {
//        return mPageId;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = null;
//        EsViewManager vm = EsViewManager.get();
//        if (vm != null) {
//            IEsFragmentMgr mgr = vm.getTopFragmentManager();
//            if (mgr != null) {
//                mIEsView = mgr.getView(mPageId);
//                if (L.DEBUG)
//                    L.logD("onCreateView:" + mPageId + " " + mIEsView + " manager:" + Integer.toHexString(System.identityHashCode(mgr)));
//                if (mIEsView == null) {
//                    view = inflater.inflate(R.layout.eskit_view_error, container, false);
//                } else {
//                    view = mIEsView.getView();
//                    mIEsView.setEventHandler(this);
//                }
//            }
//        }
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        postLifeEvent(LIFE_CREATE);
//    }
//
//    //region EventHandler
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        EsMap data = new EsMap();
//        data.pushInt("action", event.getAction());
//        data.pushInt("keyCode", event.getKeyCode());
//        data.pushInt("keyRepeat", event.getRepeatCount());
//        EsViewManager.get().sendUIEvent(getPageId(), EVT_DISPATCH_KEY_FRAGMENT, data);
//        return false;
//    }
//
//    //endregion
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (L.DEBUG) L.logD(getPageId() + " onStart");
//        postLifeEvent(LIFE_START);
//    }
//
//    public void onStartFake(){
//        if (L.DEBUG) L.logD(getPageId() + " onStartFake");
//        postLifeEvent(LIFE_START);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (L.DEBUG) L.logD(getPageId() + " onResume");
//        postLifeEvent(LIFE_RESUME);
//    }
//
//    public void onResumeFake(){
//        if (L.DEBUG) L.logD(getPageId() + " onResumeFake");
//        postLifeEvent(LIFE_RESUME);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (L.DEBUG) L.logD(getPageId() + " onPause");
//        postLifeEvent(LIFE_PAUSE);
//    }
//
//    public void onPauseFake(){
//        if (L.DEBUG) L.logD(getPageId() + " onPauseFake");
//        postLifeEvent(LIFE_PAUSE);
//    }
//
//    public void beforeHide() {
//        if (L.DEBUG) L.logD(getPageId() + " beforeHide");
//        if (mIEsView != null) mIEsView.notifyBeforeHide();
//    }
//
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
////        if (mNeedInterceptHidden && !hidden) {
////            mNeedInterceptHidden = false;
////            return;
////        }
//        if (L.DEBUG) L.logD(getPageId() + " onHiddenChanged:" + (hidden ? "hided" : "showed"));
//        if (L.DEBUG) L.logD(getPageId() + " afterShow");
//        if (!hidden) {
//            if (mIEsView != null) mIEsView.notifyAfterShow();
//        }
////        postLifeEvent(hidden ? LIFE_FRAGMENT_HIDE : LIFE_FRAGMENT_SHOW);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (L.DEBUG) L.logD(getPageId() + " onStop");
//        postLifeEvent(LIFE_STOP);
//    }
//
//    public void onStopFake(){
//        if (L.DEBUG) L.logD(getPageId() + " onStopFake");
//        postLifeEvent(LIFE_STOP);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (L.DEBUG) L.logD(getPageId() + " onDestroy");
//        postLifeEvent(LIFE_DESTROY);
//        if (mIEsView != null) {
//            mIEsView.setEventHandler(null);
//        }
//        mIEsView = null;
//    }
//
////    private EsContainerTask getContainer() {
////        FragmentActivity activity = getActivity();
////        if (activity instanceof BrowserBaseActivity) {
////            return ((BrowserBaseActivity) activity).getContainer();
////        }
////        return null;
////    }
//
//    private void postLifeEvent(String life) {
//        if (L.DEBUG) L.logD("postLifeEvent life: " + life + " " + this);
//        EsViewManager.get().sendUIEvent(getPageId(), EVT_LIFE_CHANGE_FRAGMENT, life);
//    }
//
//    @Override
//    public String toString() {
//        return "id:" + getPageId() + " hash:" + Integer.toHexString(System.identityHashCode(this));
//    }
//}