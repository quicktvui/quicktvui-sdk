package com.quicktvui.sdk.core.jsview;

import static com.quicktvui.sdk.core.utils.SlotUtils.waitPhoneWindowAdded;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.extscreen.runtime.api.ability.slotview.AttachStateCallback;
import com.sunrain.toolkit.bolts.tasks.TaskCompletionSource;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.utils.ExtendUtil;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.IEsAppLoadCallback;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;
import com.quicktvui.sdk.core.jsview.slot.SlotView;
import com.quicktvui.sdk.core.utils.ESExecutors;
import com.quicktvui.sdk.core.utils.EskitLazyInitHelper;
import com.quicktvui.sdk.core.utils.PluginUtils;
import com.quicktvui.sdk.core.utils.SlotUtils;
import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * JSView管理类
 * <br>
 */
public class JsSlotViewManager {

    private final ExecutorService mExecutor = ESExecutors.createExecutor("js-slot-view", 1, 1);

    //region 1. 加载快应用

    public void attachToActivity(Context context, EsData data) {
        attachToActivity(context, data, null);
    }

    public void attachToActivity(Context context, EsData data, AttachStateCallback callback) {
        if (data == null) return;
        EskitLazyInitHelper.initIfNeed();
        L.logDF("req load SlotView app");
        PluginUtils.assertIsInstanceOfActivity(context);
        mExecutor.execute(new LoadSlotViewAppJob(context, data, callback));
    }

    private static final class LoadSlotViewAppJob implements Runnable {

        private Context context;
        private EsData data;
        private AttachStateCallback callback;

        public LoadSlotViewAppJob(Context context, EsData data, AttachStateCallback callback) {
            this.context = context;
            this.data = data;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                L.logDF("load SlotView app");
                waitPhoneWindowAdded(context);
                View decorView = SlotUtils.getDecorView(context);
                if (decorView != null) {
                    IEsAppLoadHandler loadHandler = loadRpk(context, data);
                    decorView.setTag(R.id.es_js_view_tag1, loadHandler);
                    notifyCallback(AttachStateCallback.STATE_SUCCESS, "");
                } else {
                    notifyCallback(AttachStateCallback.STATE_ERR_WINDOW, "get window DecorView failed");
                }
            } catch (Exception e) {
                L.logEF("load SlotView", e);
                byte errorCode = e instanceof TimeoutException ? AttachStateCallback.STATE_ERR_TIMEOUT : AttachStateCallback.STATE_ERR_OTHER;
                notifyCallback(errorCode, e.getMessage());
            } finally {
                this.context = null;
                this.data = null;
                this.callback = null;
            }
        }

        private IEsAppLoadHandler loadRpk(Context context, EsData data) throws Exception {
            TaskCompletionSource<Void> wait = new TaskCompletionSource<>();

            final IEsAppLoadHandler[] loadHandler = new IEsAppLoadHandler[1];

            EsManager.get().load(context, data, new IEsAppLoadCallback() {
                @Override
                public void onStartLoad(IEsAppLoadHandler handler) {
                    loadHandler[0] = handler;
                }

                @Override
                public void onViewLoaded(HippyRootView view) {
                    L.logDF("load SlotView ok");
                    // 不add HippyRootView 也能显示JsView
//                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(1, 1);
//                    SlotUtils.addContentView(context, view, layoutParams);

                    wait.setResult(null);
                }

                @Override
                public void onLoadError(EsException e) {
                    e.printStackTrace();
                    wait.setError(e);
                }

                @Override
                public void onEsAppEvent(EsEventPacket packet) {
                    if (L.DEBUG) L.logD("onEsAppEvent packet: " + packet);
                }

                @Override
                public void requestFinish() {
                    if (L.DEBUG) L.logD("requestFinish");
                }
            });

            boolean success = wait.getTask().waitForCompletion(1, TimeUnit.MINUTES);
            if (!success) { // 超时
                throw new TimeoutException("load rpk timeout");
            }

            return loadHandler[0];
        }

        private void notifyCallback(byte state, String message) {
            if (callback != null) {
                callback.onResult(state, message);
            }
        }

    }

    //endregion

    //region 2. 创建JsSlotView

    public JsSlotView createJSView(Context context, String name) {
        return createJSView(context, name, new EsMap());
    }

    public JsSlotView createJSView(Context context, String name, EsMap params) {
        JsSlotView jsView = new JsSlotView(context, name, params);
        L.logIF("req create SlotView " + name + ", sid:" + jsView.getSid());
        SlotUtils.setJSViewContainer(jsView);
        mExecutor.execute(new CreateSlotViewJob(jsView));
        return jsView;
    }

    private static final class CreateSlotViewJob implements Runnable {

        private JsSlotView jsView;

        public CreateSlotViewJob(JsSlotView jsView) {
            this.jsView = jsView;
        }

        @Override
        public void run() {
            try {
                IEsAppLoadHandler loadHandler = SlotUtils.getAppLoaderHandler(jsView.getContext());
                L.logIF("create SlotView " + (loadHandler != null) + ", sid:" + jsView.getSid());
                if (loadHandler == null) return;
                loadHandler.sendEvent(Constants.GLOBAL_EVENT.EVT_ON_REQ_SLOT_CREATE, jsView.getLoadData());
            } finally {
                jsView = null;
            }
        }
    }

    //endregion

    //region 3. 发送事件

    public void dispatchKeyEvent(JsSlotView jsView, KeyEvent event) {
        if (L.DEBUG) L.logD("req dispatchKeyEvent jsView: " + jsView + ", event: " + event);
        EsMap data = new EsMap();
        data.pushInt("action", event.getAction());
        data.pushInt("keyCode", event.getKeyCode());
        data.pushInt("keyRepeat", event.getRepeatCount());
        sendEvent(jsView, Constants.GLOBAL_EVENT.EVT_ON_SLOT_DISPATCH_KEY, data);
    }

    public void onBackPressed(JsSlotView jsView, Runnable callback) {
        if (L.DEBUG) L.logD("req onBackPressed jsView: " + jsView);
        SlotModule.sBackPressCallback = callback;
        mExecutor.execute(new SendEventJob(jsView, Constants.GLOBAL_EVENT.EVT_ON_SLOT_BACK_PRESSED, null));
    }

    public void sendEvent(JsSlotView jsView, String eventName, @Nullable Object data) {
        if (L.DEBUG)
            L.logD("req send SlotView event, eventName: " + eventName + (data == null ? "" : ",data: " + data));
        mExecutor.execute(new SendEventJob(jsView, eventName, data));
    }

    public void sendCustomEvent(JsSlotView jsView, String eventName, @Nullable Object data) {
        if (L.DEBUG)
            L.logD("req send SlotView custom event, eventName: " + eventName + (data == null ? "" : ",data: " + data));
        mExecutor.execute(new SendEventJob(jsView, eventName, data, true));
    }

    private static final class SendEventJob implements Runnable {

        private JsSlotView jsView;
        private String eventName;
        private Object data;
        private final boolean isCustomEvent;

        public SendEventJob(JsSlotView jsView, String eventName, @Nullable Object data) {
            this(jsView, eventName, data, false);
        }

        public SendEventJob(JsSlotView jsView, String eventName, @Nullable Object data, boolean isCustomEvent) {
            this.jsView = jsView;
            this.eventName = eventName;
            this.data = data;
            this.isCustomEvent = isCustomEvent;
        }

        @Override
        public void run() {
            try {
                if (isCustomEvent) {
                    if (L.DEBUG) L.logD("send custom event to SlotView: " + eventName);
                    jsView.sendCustomEvent(eventName, data);
                } else {
                    if (L.DEBUG) L.logD("send event to SlotView: " + eventName);
                    jsView.sendEvent(eventName, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.jsView = null;
                this.eventName = null;
                this.data = null;
            }
        }

    }

    //endregion

    //region 4. 销毁JsSlotView

    public void deleteJSView(JsSlotView jsView) {
        L.logIF("req delete SlotView");
        SlotModule.sBackPressCallback = null;
        removeFromParent(jsView);
        mExecutor.execute(new DeleteJSViewJob(jsView));
    }

    private void removeFromParent(JsSlotView view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private static final class DeleteJSViewJob implements Runnable {

        private JsSlotView jsView;

        public DeleteJSViewJob(JsSlotView jsView) {
            this.jsView = jsView;
        }

        @Override
        public void run() {
            try {
                IEsAppLoadHandler loadHandler = SlotUtils.getAppLoaderHandler(jsView.getContext());
                L.logIF("delete SlotView " + (loadHandler != null));
                if (loadHandler == null) return;
                loadHandler.sendEvent(Constants.GLOBAL_EVENT.EVT_ON_REQ_SLOT_DESTROY, jsView.getLoadData());
            } finally {
                jsView = null;
            }
        }
    }

    //endregion

    //region 5. 销毁快应用

    public void detachFromActivity(Context context) {
        L.logDF("req destroy SlotView app");
        PluginUtils.assertIsInstanceOfActivity(context);
        mExecutor.execute(new DestroySlotViewAppJob(context));
    }

    private static final class DestroySlotViewAppJob implements Runnable {

        private final Context context;

        public DestroySlotViewAppJob(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            IEsAppLoadHandler loadHandler = SlotUtils.getAppLoaderHandler(context);
            SlotUtils.clearTags(context);
            L.logDF("destroy SlotView app " + (loadHandler != null));
            if (loadHandler == null) return;
            loadHandler.onDestroy();
        }

    }

    //endregion

    //region Controller回调

    public void onViewCreated(SlotView view) {
        if (L.DEBUG) L.logD("on SlotView create");
        String sid = ExtendUtil.getViewSID(view);
        JsSlotView jsView = SlotUtils.getSViewContainer(view.getContext(), sid);
        if (jsView != null) {
            if (L.DEBUG) L.logD("on SlotView create " + jsView.getName() + ", sid:" + sid);
            jsView.addSlotView(view);
        }
    }

    public void onViewDelete(SlotView view) {
        if (L.DEBUG) L.logD("on SlotView delete");
        view.release();
    }

    //endregion

    public void setEnableMouse(JsSlotView view) {
        String sid = ExtendUtil.getViewSID(view);
        JsSlotView jsView = SlotUtils.getSViewContainer(view.getContext(), sid);
        if (jsView != null) {
            jsView.setEnableMouse();
        }
    }

    private static final class JSViewManagerHolder {
        private static final JsSlotViewManager INSTANCE = new JsSlotViewManager();
    }

    public static JsSlotViewManager get() {
        return JSViewManagerHolder.INSTANCE;
    }

    private JsSlotViewManager() {
    }

}
