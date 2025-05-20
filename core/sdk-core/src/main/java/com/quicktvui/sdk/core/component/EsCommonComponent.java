//package eskit.sdk.core.component;
//
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.tencent.mtt.hippy.HippyRootView;
//import com.tencent.mtt.hippy.dom.node.StyleNode;
//import com.tencent.mtt.hippy.uimanager.ControllerManager;
//import com.tencent.mtt.hippy.uimanager.ControllerRegistry;
//import com.tencent.mtt.hippy.uimanager.RenderNode;
//
//import com.quicktvui.sdk.base.args.EsMap;
//import com.quicktvui.sdk.base.component.IEsComponent;
//import com.quicktvui.sdk.base.component.IEsComponentView;
//
///**
// * 更多方法的ViewController
// * <p>
// * Create by weipeng on 2022/08/18 19:38
// */
//public abstract class EsCommonComponent<V extends View & IEsComponentView> implements IEsComponent<V>, IOverrideMethods {
//
////    private IOverrideMethods mOrigin;
////
////    /** 设置源方法类 **/
////    void setOrigin(IOverrideMethods origin) {
////        this.mOrigin = origin;
////    }
//
//    @Override
//    public void onCreateViewByCache(View view, String type, EsMap props) {
////        mOrigin.onCreateViewByCache(view, type, props);
//    }
//
//    @Override
//    public void onAfterCreateView(View view, EsMap initialProps) {
////        mOrigin.onAfterCreateView(view, MapperUtils.esMap2HpMap(initialProps));
//    }
//
//    @Override
//    public void onAfterUpdateProps(View view) {
//
//    }
//
//    @Override
//    public void updateExtra(View view, Object object) {
//
//    }
//
//    @Override
//    public StyleNode createNode(boolean isVirtual, int rootId) {
//        return null;
//    }
//
//    @Override
//    public StyleNode createNode(boolean isVirtual) {
//        return null;
//    }
//
//    @Override
//    public void updateLayout(int id, int x, int y, int width, int height, ControllerRegistry componentHolder) {
//
//    }
//
//    @Override
//    public boolean shouldInterceptLayout(View view, int x, int y, int width, int height) {
//        return false;
//    }
//
//    @Override
//    public boolean handleGestureBySelf() {
//        return false;
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//
//    }
//
//    @Override
//    public void setGestureType(View view, String type, boolean flag) {
//
//    }
//
//    @Override
//    public RenderNode createRenderNode(int id, EsMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
//        return null;
//    }
//
//    @Override
//    public void onBatchComplete(View view) {
//
//    }
//
//    @Override
//    public void deleteChild(ViewGroup parentView, View childView) {
//
//    }
//
//    @Override
//    public void deleteChild(ViewGroup parentView, View childView, int childIndex) {
//
//    }
//
//    @Override
//    public void onBeforeViewDestroy(View view) {
//
//    }
//
//    @Override
//    public void addView(ViewGroup parentView, View view, int index) {
//
//    }
//
//    @Override
//    public void onManageChildComplete(View view) {
//
//    }
//
//    @Override
//    public int getChildCount(View viewGroup) {
//        return 0;
//    }
//
//    @Override
//    public View getChildAt(View viewGroup, int i) {
//        return null;
//    }
//}
