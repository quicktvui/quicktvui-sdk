package com.quicktvui.sdk.core.internal;

import static com.quicktvui.sdk.core.EsData.ACTIVITY_FLAGS_CLEAR_TASK;
import static com.quicktvui.sdk.core.EsData.ACTIVITY_FLAGS_SINGLE_INSTANCE;
import static com.quicktvui.sdk.core.EsData.ACTIVITY_FLAGS_SINGLE_TASK;
import static com.quicktvui.sdk.core.EsData.ACTIVITY_FLAGS_SINGLE_TOP;
import static com.quicktvui.sdk.core.utils.CommonUtils.isContainsFlag;
import static com.quicktvui.sdk.core.utils.CommonUtils.isNotExitAppOnClickHomeKey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.quicktvui.base.ui.DebugCache;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.sdk.base.ITakeOverKeyEventListener;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.callback.EsAppLifeCallbackImpl;
import com.quicktvui.sdk.core.callback.EsAppLifeCallbackImplProxy;
import com.quicktvui.sdk.core.ui.BrowserBaseActivity;
import com.quicktvui.sdk.core.ui.BrowserStandardActivity;
import com.quicktvui.sdk.core.ui.BrowserStandardNoThemeActivity;
import com.quicktvui.sdk.core.ui.BrowserStandardTransparentActivity;
import com.quicktvui.sdk.core.ui.normal.single.instance.BrowserSingleInstanceActivity0;
import com.quicktvui.sdk.core.ui.normal.single.instance.BrowserSingleInstanceActivity1;
import com.quicktvui.sdk.core.ui.normal.single.instance.BrowserSingleInstanceActivity2;
import com.quicktvui.sdk.core.ui.normal.single.instance.BrowserSingleInstanceActivity3;
import com.quicktvui.sdk.core.ui.normal.single.instance.BrowserSingleInstanceActivity4;
import com.quicktvui.sdk.core.ui.normal.single.task.BrowserSingleTaskActivity0;
import com.quicktvui.sdk.core.ui.normal.single.task.BrowserSingleTaskActivity1;
import com.quicktvui.sdk.core.ui.normal.single.task.BrowserSingleTaskActivity2;
import com.quicktvui.sdk.core.ui.normal.single.task.BrowserSingleTaskActivity3;
import com.quicktvui.sdk.core.ui.normal.single.task.BrowserSingleTaskActivity4;
import com.quicktvui.sdk.core.ui.normal.single.top.BrowserSingleTopActivity0;
import com.quicktvui.sdk.core.ui.normal.single.top.BrowserSingleTopActivity1;
import com.quicktvui.sdk.core.ui.normal.single.top.BrowserSingleTopActivity2;
import com.quicktvui.sdk.core.ui.normal.single.top.BrowserSingleTopActivity3;
import com.quicktvui.sdk.core.ui.normal.single.top.BrowserSingleTopActivity4;
import com.quicktvui.sdk.core.ui.pg.IPageLoader;
import com.quicktvui.sdk.core.utils.HomeKeyReceiver;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyEngineContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by weipeng on 2022/03/01 16:52
 */
public class EsViewManager {

    // 当前显示的所有界面
    private final Stack<EsViewRecord> mViews = new Stack<>();

    // 记录特殊Task的Activity的对应关系
    private final Map<String, Class<? extends Activity>> mTagMapping = new HashMap<>(8);
    private final Stack<Class<? extends Activity>> mSingleTaskStack = new Stack<>();
    private final Stack<Class<? extends Activity>> mSingleTopStack = new Stack<>();
    private final Stack<Class<? extends Activity>> mSingleInstanceStack = new Stack<>();

    private final Stack<Class<? extends Activity>> mSingleTaskNoThemeStack = new Stack<>();
    private final Stack<Class<? extends Activity>> mSingleTopNoThemeStack = new Stack<>();
    private final Stack<Class<? extends Activity>> mSingleInstanceNoThemeStack = new Stack<>();

    private int mVisiblePageNumber;

    // 事件拦截
    private final Map<Integer, ITakeOverKeyEventListener> mTakeOverListenerCache = new HashMap<>(2);

    private final EsAppLifeCallbackImplProxy mAppLifeCallback = new MyAppLifeCallback(InitConfig.getDefault().getAppLifeCallback());

    void reset() {
        mObjectMapping.clear();
        mTagMapping.clear();
        mSingleTaskStack.clear();
        mSingleTaskStack.push(BrowserSingleTaskActivity4.class);
        mSingleTaskStack.push(BrowserSingleTaskActivity3.class);
        mSingleTaskStack.push(BrowserSingleTaskActivity2.class);
        mSingleTaskStack.push(BrowserSingleTaskActivity1.class);
        mSingleTaskStack.push(BrowserSingleTaskActivity0.class);

        mSingleTopStack.clear();
        mSingleTopStack.push(BrowserSingleTopActivity4.class);
        mSingleTopStack.push(BrowserSingleTopActivity3.class);
        mSingleTopStack.push(BrowserSingleTopActivity2.class);
        mSingleTopStack.push(BrowserSingleTopActivity1.class);
        mSingleTopStack.push(BrowserSingleTopActivity0.class);

        mSingleInstanceStack.clear();
        mSingleInstanceStack.push(BrowserSingleInstanceActivity4.class);
        mSingleInstanceStack.push(BrowserSingleInstanceActivity3.class);
        mSingleInstanceStack.push(BrowserSingleInstanceActivity2.class);
        mSingleInstanceStack.push(BrowserSingleInstanceActivity1.class);
        mSingleInstanceStack.push(BrowserSingleInstanceActivity0.class);


        mSingleTaskNoThemeStack.clear();
        mSingleTaskNoThemeStack.push(BrowserSingleTaskActivity4.class);
        mSingleTaskNoThemeStack.push(BrowserSingleTaskActivity3.class);
        mSingleTaskNoThemeStack.push(BrowserSingleTaskActivity2.class);
        mSingleTaskNoThemeStack.push(BrowserSingleTaskActivity1.class);
        mSingleTaskNoThemeStack.push(BrowserSingleTaskActivity0.class);

        mSingleTopNoThemeStack.clear();
        mSingleTopNoThemeStack.push(BrowserSingleTopActivity4.class);
        mSingleTopNoThemeStack.push(BrowserSingleTopActivity3.class);
        mSingleTopNoThemeStack.push(BrowserSingleTopActivity2.class);
        mSingleTopNoThemeStack.push(BrowserSingleTopActivity1.class);
        mSingleTopNoThemeStack.push(BrowserSingleTopActivity0.class);

        mSingleInstanceNoThemeStack.clear();
        mSingleInstanceNoThemeStack.push(BrowserSingleInstanceActivity4.class);
        mSingleInstanceNoThemeStack.push(BrowserSingleInstanceActivity3.class);
        mSingleInstanceNoThemeStack.push(BrowserSingleInstanceActivity2.class);
        mSingleInstanceNoThemeStack.push(BrowserSingleInstanceActivity1.class);
        mSingleInstanceNoThemeStack.push(BrowserSingleInstanceActivity0.class);
    }

    public void start(EsData data) {
        EsContext.get().printSystemInfo();
        Context context = getTopActivity();
        if (context == null) context = EsContext.get().getContext();
        if (data.isFeatureSingleActivity() && context instanceof BrowserBaseActivity) {
            ((BrowserBaseActivity) context).loadAppFromSelf(data);
            return;
        }
        beforeStart(data);
        // 启动界面
        try {
            Intent intent = makeIntentWithData(context, data);
            if (intent != null) {
                intent.putExtra("data", data);
                // Add by weipeng 没指定，优先本应用跳转
                if (TextUtils.isEmpty(intent.getPackage())) {
                    intent.setPackage(context.getPackageName());
                }
                context.startActivity(intent);
            }
        } catch (Exception e) {
            L.logW("start app", e);
        }
    }

    public void load(Context context, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        if (callback == null) {
            start(data);
            return;
        }
        EsContext.get().printSystemInfo();
        beforeStart(data);
        EsAppLoadHandlerImpl loadHandlerImpl = new EsAppLoadHandlerImpl();
        onViewCreate(loadHandlerImpl);
        loadHandlerImpl.startLoad(context, containerLayoutId, data, callback);
    }

    public void loadV2(FragmentActivity activity, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        if (callback == null) {
            start(data);
            return;
        }
        EsContext.get().printSystemInfo();
        beforeStart(data);
        EsAppLoadHandlerImplV2 loadHandlerImpl = new EsAppLoadHandlerImplV2();
        onViewCreate(loadHandlerImpl);
        loadHandlerImpl.startLoad(activity, containerLayoutId, data, callback);
    }

    private void beforeStart(EsData data) {
        // ClearTask
        if (!data.isFeatureSingleActivity() && isContainsFlag(data.getFlags(), ACTIVITY_FLAGS_CLEAR_TASK)) {
            if (L.DEBUG) L.logD("start: clearTask");
            // ClearTask的时候在长虹电视上不能先finishAll，系统会杀进程，先缓存下，delay杀。
            finishAllDelay();
            data.setFlags(data.getFlags() & ~ACTIVITY_FLAGS_CLEAR_TASK);
        }

        // 判断PageLimit
        checkPageLimit(data);
    }

    private void checkPageLimit(EsData data) {
        int pageLimit = data.getPageLimit();
        if (pageLimit <= 0) return;
        int totalSize = mViews.size();
        if (totalSize <= pageLimit) return;
        String pageTag = data.getPageTag();
        if (TextUtils.isEmpty(pageTag)) return;

        if (L.DEBUG) L.logD("PAGE_LIMIT tag:" + pageTag);
        if (L.DEBUG) L.logD("PAGE_LIMIT limit:" + pageLimit);

        int pageTagCounter = 0;
//        if(Objects.equals(mViews.get(0).getEsData().getPageTag(), pageTag)) {
//            pageTagCounter = 1;
//        }

//        if (L.DEBUG) L.logD("PAGE_LIMIT check start:" + pageTagCounter);

        List<EsViewRecord> needFinish = new ArrayList<>(5);
        for (int index = mViews.size() - 1; index >= 0; index--) {
            EsViewRecord record = mViews.get(index);
            if (Objects.equals(record.getEsData().getPageTag(), pageTag)) {
                if (++pageTagCounter >= pageLimit) {
                    EsData esData = record.getEsData();
                    if (!esData.isCard()) {
                        needFinish.add(record);
                    }
                }
            }
        }

        if (L.DEBUG) L.logD("PAGE_LIMIT need finish:" + needFinish.size());

        for (EsViewRecord r : needFinish) {
            if (L.DEBUG) L.logW("PAGE_LIMIT close:" + r.getEsData().getPageTag());
            r.finish();
        }
    }

    /**
     * 任意应用运行
     **/
    public boolean isEsRunning() {
        return !mViews.isEmpty();
    }

    /**
     * 某个应用是否运行
     **/
    public boolean isEsRunning(String pkg) {
        for (EsViewRecord r : mViews) {
            if (r.isSamePackage(pkg)) {
                return true;
            }
        }
        return false;
    }

    public List<EsData> getRunningApps() {
        List<EsData> appList = new ArrayList<>(mViews.size());
        for (EsViewRecord v : mViews) {
            EsData data = v.getEsData();
            if (data != null && !data.isCard()) {
                appList.add(data);
            }
        }
        return appList;
    }

    public int getRunningAppSize() {
        return mViews.size();
    }

    public List<EsViewRecord> getRunningAppTasks() {
        return mViews;
    }

    @Nullable
    public HippyEngineContext getEngineContext() {
        if (!mViews.isEmpty()) return mViews.peek().getEngineContext();
        return null;
    }

    public Activity getTopActivity() {
        if (!mViews.isEmpty()) {
            IEsViewer viewer = mViews.peek().getViewer();
            if (viewer instanceof Activity) {
                return (Activity) viewer;
            }
            Context appContext = viewer.getAppContext();
            if (appContext instanceof Activity) {
                return (Activity) appContext;
            }
        }
        return null;
    }

    @Nullable
    public File getAppRuntimeDir() {
        if (!mViews.isEmpty()) return mViews.peek().getAppRuntimeDir();
        return null;
    }

    @Nullable
    public File getAppRuntimeDir(int engineId) {
        IEsViewer viewer = findPageWithEngineId(engineId);
        return viewer == null ? null : viewer.getAppRuntimeDir();
    }

    @Nullable
    public File getAppRuntimeDir(IEsTraceable traceable) {
        IEsViewer viewer = findPageWithObject(traceable);
        return viewer == null ? null : viewer.getAppRuntimeDir();
    }

    @Nullable
    public EsData getEsAppData() {
        if (!mViews.isEmpty()) {
            for (EsViewRecord view : mViews) {
                EsData data = view.getEsData();
                if (!data.isCard()) {
                    return data;
                }
            }
        }
        return null;
    }

    @Nullable
    public EsData getEsAppData(IEsTraceable traceable) {
        IEsViewer viewer = findPageWithObject(traceable);
        return viewer == null ? null : viewer.getEsData();
    }

    @Nullable
    public EsData getEsAppData(String pkg) {
        if (!mViews.isEmpty()) {
            for (EsViewRecord r : mViews) {
                if (Objects.equals(pkg, r.getEsData().getEsPackage())) return r.getEsData();
            }
        }
        return null;
    }

    public void sendUIEvent(int viewId, String eventName, Object params) {
        if (L.DEBUG)
            L.logD("EsFragment mViews empty?: " + mViews.isEmpty());
        if (mViews.isEmpty()) return;
        mViews.peek().sendUIEvent(viewId, eventName, params);
    }

    public void sendNativeEventTop(String eventName, Object params) {
        IEsViewer viewer = getTopViewer();
        if (viewer != null) {
            viewer.sendNativeEvent(eventName, params);
        }
    }

    public void sendNativeEventAll(String eventName, Object params) {
        if (mViews.isEmpty()) return;
        for (EsViewRecord v : mViews) {
            v.sendNativeEvent(eventName, params);
        }
    }

    public void finish() {
        if (L.DEBUG) L.logD("finish");
        if (mViews.isEmpty()) return;
        EsViewRecord r = mViews.peek();
        r.finish();
    }

    public void finish(String pkg) {
        if (L.DEBUG) L.logD("finish:" + pkg);
        if (mViews.isEmpty()) return;
        for (EsViewRecord r : mViews) {
            if (Objects.equals(pkg, r.getEsData().getEsPackage())) {
                r.finish();
                break;
            }
        }
    }

    /**
     * 关闭本应用的所有界面
     **/
    public void finishAllAppPage() {
        if (L.DEBUG) L.logD("finishAllAppPage");
        List<EsViewRecord> views = new LinkedList<>(mViews);
        int size = views.size();
        String targetPkg = null;
        for (int i = size - 1; i >= 0; i--) {
            EsViewRecord record = views.get(i);
            if (targetPkg == null) {
                targetPkg = record.getEsData().getEsPackage();
            } else if (!record.getEsData().getEsPackage().equals(targetPkg)) {
                return;
            }
            record.finish();
        }
    }

    /**
     * 关闭所有应用
     **/
    public synchronized void finishAllApp() {
        if (L.DEBUG) L.logD("finishAllApp");
        List<EsViewRecord> views = new LinkedList<>(mViews);
        for (EsViewRecord view : views) {
            EsData data = view.getEsData();
            if (data != null && !data.isCard()) {
                view.finish();
            }
        }
    }

    public void finishAllDelay() {
        if (mViews.isEmpty()) return;
        if (L.DEBUG) L.logD("finishAllDelay");
        List<EsViewRecord> tmp = new ArrayList<>(mViews.size());
        tmp.addAll(mViews);
        EsContext.get().postDelay(new DelayFinishTask(tmp, true), 1000);
    }

    public void finishAllExcludeHomePage() {
        while (!mViews.isEmpty()) {
            // TODO 理论上这里应该使用peek，在走后面的reinStack逻辑，但是发现peek不走后面的生命周期
            // TODO 改为pop可以走，怀疑是被引用的问题，故先改为pop，增加resetStack方法
            if (mViews.peek().isHomePage()) break;
            mViews.pop().finish();
        }
    }

    /**
     * 获取当前可见的界面数量
     **/
    public int getVisiblePageSize() {
        return mVisiblePageNumber;
    }

    private static final class MyAppLifeCallback extends EsAppLifeCallbackImplProxy {

        private static HomeKeyReceiver sHomeKeyReceiver;

        public MyAppLifeCallback(EsAppLifeCallbackImpl origin) {
            super(origin);
        }

        @Override
        public void onEsAppCreate(EsData data) {
            if (!EsViewManager.get().isEsRunning(data.getEsPackage())) {
                if (L.DEBUG) L.logD("onAppOpen: " + data.getEsPackage());
                IEsRemoteEventCallback callback = EsContext.get().getRemoteEventCallback();
                if (callback != null) {
                    callback.onReceiveEvent(Constants.GLOBAL_EVENT.EVT_ON_APP_OPEN, data.getEsPackage());
                }
                super.onEsAppCreate(data);
            }

            if(sHomeKeyReceiver == null) {
                if (!isNotExitAppOnClickHomeKey()) {
                    sHomeKeyReceiver = new HomeKeyReceiver();
                    sHomeKeyReceiver.register(EsContext.get().getContext());
                }
            }
        }

        @Override
        public void onEsAppDestroy(EsData data) {
            EsViewManager vm = EsViewManager.get();
            if(vm == null) return;
            if (!vm.isEsRunning(data.getEsPackage())) {
                if (L.DEBUG) L.logD("onAppClose: " + data.getEsPackage());
                IEsRemoteEventCallback callback = EsContext.get().getRemoteEventCallback();
                if (callback != null) {
                    callback.onReceiveEvent(Constants.GLOBAL_EVENT.EVT_ON_APP_CLOSE, data.getEsPackage());
                }
                super.onEsAppDestroy(data);
            }

            // 页面全部退出了
            if (!vm.isEsRunning()) {
                L.logDF("ES.ALL.EXITED");
                vm.reset();
                DebugCache.get().release();
                // 发送进入后台广播
                sendEnterBackgroundBroadcast();
                // 取消监听Home键
                if (sHomeKeyReceiver != null) {
                    sHomeKeyReceiver.unRegister(EsContext.get().getContext());
                    sHomeKeyReceiver = null;
                }

                // 标记开始检测磁盘占用
                EsDiskCacheManager.dirty();
            }
        }

        private void sendEnterBackgroundBroadcast() {
            L.logIF("enterBackground");
            Context context = EsContext.get().getContext();
            if (context == null) return;
            Intent intent = new Intent(context.getPackageName() + Constants.ACTION_ENTER_BACKGROUND);
            context.sendBroadcast(intent);
        }
    }

    public void onViewCreate(IEsViewer viewer) {
        if (L.DEBUG) L.logD("onViewCreate:" + viewer);
        EsData data = viewer.getEsData();
        if (data != null) {
            mAppLifeCallback.onEsAppCreate(data);
        }
        // 压入记录栈
        mViews.push(new EsViewRecord(viewer));
        // 向Vue告知生命周期
        postLifeEvent(viewer, Constants.GLOBAL_EVENT.LIFE_CREATE);
    }

    public void onViewStart(IEsViewer viewer) {
        if (L.DEBUG) L.logD("onViewStart:" + viewer);
        EsData data = viewer.getEsData();
        if (data != null) {
            mAppLifeCallback.onEsAppStart(data);
        }
        mVisiblePageNumber++;
        // 向Vue告知生命周期
        postLifeEvent(viewer, Constants.GLOBAL_EVENT.LIFE_START);
    }

    public void onViewResume(IEsViewer viewer) {
        if (L.DEBUG) L.logD("onViewResume:" + viewer);
        EsData data = viewer.getEsData();
        if (data != null) {
            mAppLifeCallback.onEsAppResume(data);
        }
        postLifeEvent(viewer, Constants.GLOBAL_EVENT.LIFE_RESUME);
    }

    public void onViewPause(IEsViewer viewer) {
        if (L.DEBUG) L.logD("onViewPause:" + viewer);
        EsData data = viewer.getEsData();
        if (data != null) {
            mAppLifeCallback.onEsAppPause(data);
        }
        postLifeEvent(viewer, Constants.GLOBAL_EVENT.LIFE_PAUSE);
    }

    public void onViewStop(IEsViewer viewer) {
        if (L.DEBUG) L.logD("onViewStop:" + viewer);
        EsData data = viewer.getEsData();
        if (data != null) {
            mAppLifeCallback.onEsAppStop(data);
        }
        mVisiblePageNumber--;
        // 向Vue告知生命周期
        postLifeEvent(viewer, Constants.GLOBAL_EVENT.LIFE_STOP);
    }

    public synchronized void onViewDestroy(IEsViewer viewer) {
        if (L.DEBUG) L.logD("onViewDestroy:" + viewer);
        postLifeEvent(viewer, Constants.GLOBAL_EVENT.LIFE_DESTROY);
        if (!mViews.isEmpty()) {
            for (EsViewRecord record : mViews) {
                if (record.getViewer() == viewer) {
                    if (L.DEBUG) L.logD("remove record");
                    mViews.remove(record);
                    Context appContext = viewer.getAppContext();
                    if (appContext instanceof BrowserBaseActivity) {
                        IPageLoader pageLoader = ((BrowserBaseActivity) appContext).getPageLoader();
                        if (!pageLoader.isAnyAppRunning()) {
                            reinStack(viewer.getEsData(), (Activity) appContext);
                        }
                    }
                    break;
                }
            }
        }
        EsData data = viewer.getEsData();
        if (data != null) {
            mAppLifeCallback.onEsAppDestroy(data);
        }
    }

    private void postLifeEvent(IEsViewer viewer, String msg) {
        viewer.sendNativeEvent(Constants.GLOBAL_EVENT.EVT_LIFE_CHANGE, msg);
    }

    private Intent makeIntentWithData(Context context, EsData data) {
        if (L.DEBUG) L.logW("ES.MAKE.INTENT.START");
        Intent intent;
        String pageTag = data.getPageTag();
        int flags = data.getFlags();
        if (flags > 0) {
            if (flags != ACTIVITY_FLAGS_CLEAR_TASK && TextUtils.isEmpty(pageTag)) {
                L.logEF("缺少参数 pageTag");
                return null;
            }
        }
        if (L.DEBUG) {
            Set<String> keys = mTagMapping.keySet();
            for (String key : keys) {
                L.logD("key:" + key + " " + mTagMapping.get(key).getSimpleName());
            }
        }

        boolean noTheme = data.getCoverLayoutId() == EsData.SPLASH_NONE;
        Class<? extends Activity> targetClass;
        if (mTagMapping.containsKey(pageTag)) {
            if (L.DEBUG) L.logD("from cache");
            targetClass = mTagMapping.get(pageTag);
        } else if (isContainsFlag(flags, ACTIVITY_FLAGS_SINGLE_TASK)) {
            if (L.DEBUG) L.logD("FLAG_ACTIVITY_SINGLE_TASK");
            targetClass = noTheme ? mSingleTaskNoThemeStack.pop() : mSingleTaskStack.pop();
            mTagMapping.put(pageTag, targetClass);
        } else if (isContainsFlag(flags, ACTIVITY_FLAGS_SINGLE_TOP)) {
            if (L.DEBUG) L.logD("FLAG_ACTIVITY_SINGLE_TOP");
            targetClass = noTheme ? mSingleTopNoThemeStack.pop() : mSingleTopStack.pop();
            mTagMapping.put(pageTag, targetClass);
        } else if (isContainsFlag(flags, ACTIVITY_FLAGS_SINGLE_INSTANCE)) {
            if (L.DEBUG) L.logD("FLAG_ACTIVITY_SINGLE_INSTANCE");
            targetClass = noTheme ? mSingleInstanceNoThemeStack.pop() : mSingleInstanceStack.pop();
            mTagMapping.put(pageTag, targetClass);
        } else {
            if (L.DEBUG) L.logD("standard");
            // FIXME 还没想到好的切换方式
            targetClass = data.isTransparent() ? BrowserStandardTransparentActivity.class : noTheme ? BrowserStandardNoThemeActivity.class : BrowserStandardActivity.class;
//            targetClass = BrowserFragmentActivity.class;
//            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        if (L.DEBUG) L.logD("find:" + targetClass);

        intent = new Intent(context, targetClass);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (L.DEBUG) L.logW("ES.MAKE.INTENT.END");
        return intent;
    }

    private void reinStack(EsData data, Activity activity) {
        if (data == null) return;
        if (L.DEBUG) L.logW("ES.REIN_STACK.START");
        boolean noTheme = data.getCoverLayoutId() == EsData.SPLASH_NONE;
        String pageTag = data.getPageTag();
        if (L.DEBUG) L.logD("tag:" + pageTag + " class:" + activity.getClass());
        if (TextUtils.isEmpty(pageTag)) return;
        mTagMapping.remove(pageTag);
        int flags = data.getFlags();
        if (isContainsFlag(flags, ACTIVITY_FLAGS_SINGLE_TASK)) {
            if (L.DEBUG) L.logD("FLAG_ACTIVITY_SINGLE_TASK");
            (noTheme ? mSingleTaskNoThemeStack : mSingleTaskStack).push(activity.getClass());
        } else if (isContainsFlag(flags, ACTIVITY_FLAGS_SINGLE_TOP)) {
            if (L.DEBUG) L.logD("FLAG_ACTIVITY_SINGLE_TOP");
            (noTheme ? mSingleTopNoThemeStack : mSingleTopStack).push(activity.getClass());
        } else if (isContainsFlag(flags, ACTIVITY_FLAGS_SINGLE_INSTANCE)) {
            if (L.DEBUG) L.logD("FLAG_ACTIVITY_SINGLE_INSTANCE");
            (noTheme ? mSingleInstanceNoThemeStack : mSingleInstanceStack).push(activity.getClass());
        }
        if (L.DEBUG) L.logW("ES.REIN_STACK.END");
    }

    void onAppViewRenderSuccess(EsData data) {
        mAppLifeCallback.onEsAppRenderSuccess(data);
    }

    void onAppViewRenderFailed(EsData data, int errCode, String message) {
        mAppLifeCallback.onEsAppRenderFailed(data, errCode, message);

    }

    @Nullable
    public IEsViewer getTopViewer() {
        if (!mViews.isEmpty()) {
            int size = mViews.size();
            for (int i = size - 1; i >= 0; i--) {
                EsViewRecord r = mViews.get(i);
                EsData d = r.getEsData();
                if (d != null && !d.isCard()) {
                    return r.getViewer();
                }
            }
        }
        return null;
    }

    @Nullable
    public IEsViewer getViewerWithPackage(String packageName) {
        if (mViews.isEmpty()) return null;
        for (EsViewRecord r : mViews) {
            if (r.getEsData().getEsPackage().equals(packageName)) {
                return r.getViewer();
            }
        }
        return null;
    }

    private static final class DelayFinishTask implements Runnable {

        private List<EsViewRecord> _delayFinishList;
        private boolean _keepHomePage;

        public DelayFinishTask(List<EsViewRecord> delayFinishList, boolean keepHomePage) {
            _delayFinishList = delayFinishList;
            _keepHomePage = keepHomePage;
        }

        @Override
        public void run() {
            if (_delayFinishList != null) {
                for (EsViewRecord r : _delayFinishList) {
                    if (L.DEBUG) L.logD("delay finish");
                    try {
                        if (_keepHomePage && r.isHomePage()) continue;
                        r.finish();
                    } catch (Exception e) {
                        L.logEF("" + e);
                    }
                }
                _delayFinishList.clear();
            }
            _delayFinishList = null;
        }
    }

    public void setTakeOverKeyEventListener(Integer key, ITakeOverKeyEventListener listener) {
        if (listener != null) {
            L.logIF("take_over add: " + key + " " + listener);
            mTakeOverListenerCache.put(key, listener);
        } else if (mTakeOverListenerCache.containsKey(key)) {
            L.logIF("take_over remove: " + key);
            mTakeOverListenerCache.remove(key);
        }
    }

    public ITakeOverKeyEventListener getTakeOverKeyEventListener(Integer key) {
        return mTakeOverListenerCache.get(key);
    }

    //region instance mapping

    private final Map<Integer, Integer> mObjectMapping = new ConcurrentHashMap<>();

    /**
     * 映射IEsViewer和Engine
     **/
    public void markView2Engine(IEsViewer viewer, HippyEngineContext context) {
        if (viewer == null || context == null) {
            L.logEF("mark engine relationship FAIL");
            return;
        }
        mObjectMapping.put(context.getEngineId(), viewer.hashCode());
    }

    /**
     * 取消映射IEsViewer和Engine
     **/
    public void unMarkView2Engine(IEsViewer viewer) {
        if (viewer == null) {
            L.logEF("un mark engine relationship FAIL");
            return;
        }
        HippyEngineContext engineContext = viewer.getEngineContext();
        if (engineContext != null) {
            mObjectMapping.remove(engineContext.getEngineId());
        }
    }

    /**
     * 映射Object和Engine
     **/
    public void markObject2Engine(Object obj, HippyEngineContext context) {
        if (obj == null || context == null) {
            L.logEF("mark engine relationship FAIL");
            return;
        }
        mObjectMapping.put(obj.hashCode(), context.getEngineId());
    }

    /**
     * 取消映射Object和Engine
     **/
    public void unMarkObject2Engine(Object obj) {
        // BUGFIX 注释掉了，在同一个界面存在多个播放器的时候
        // BUGFIX 第二个destroy，会导致映射失效
        // BUGFIX 在reset里统一释放 TODO 每一个Component对应单独的IComponent实例
//        if (obj == null) {
//            L.logEF("unMark engine relationship FAIL");
//            return;
//        }
//        mObjectMapping.remove(obj.hashCode());
    }

    @Nullable
    public IEsViewer findPageWithEngine(HippyEngineContext context) {
        return findPageWithEngineId(context.getEngineId());
    }

    @Nullable
    public IEsViewer findPageWithEngineId(Integer engineId) {
        if (engineId == null) return null;
        synchronized (mViews) {
            int viewId = mObjectMapping.get(engineId);
            for (EsViewRecord vr : mViews) {
                if (vr.getViewer().hashCode() == viewId) {
                    return vr.getViewer();
                }
            }
            return null;
        }
    }

    @Nullable
    public IEsViewer findPageWithObject(Object obj) {
        if (obj == null) return null;
        return findPageWithEngineId(mObjectMapping.get(obj.hashCode()));
    }

    public int findEngineIdWithObject(Object obj) {
        if (obj == null) return -1;
        Integer integer = mObjectMapping.get(obj.hashCode());
        return integer == null ? -1 : integer;
    }

    //endregion

    //region 单例

    private static final class EsViewManagerHolder {
        private static final EsViewManager INSTANCE = new EsViewManager();
    }

    public static EsViewManager get() {
        return EsViewManagerHolder.INSTANCE;
    }

    private EsViewManager() {
        reset();
    }

    //endregion

}
