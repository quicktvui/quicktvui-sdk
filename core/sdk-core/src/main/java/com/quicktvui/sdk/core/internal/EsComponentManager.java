package com.quicktvui.sdk.core.internal;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.quicktvui.sdk.base.EsProvider;
import com.quicktvui.sdk.base.EsSingleCallback;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.sdk.core.component.CommonViewController;
import com.quicktvui.sdk.core.module.CommonModule;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngineContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by weipeng on 2022/03/01 14:59
 */
public class EsComponentManager {

    private final Map<String, EsProvider<IEsModule>> mModules = new ConcurrentHashMap<>();
    private final Map<String, EsProvider<IEsComponent<? extends View>>> mComponents = new ConcurrentHashMap<>();
    private final Map<Class<?>, HippyAPIProvider> mProviders = new ConcurrentHashMap<>();

    //region 预注册

    //region module

    public void registerModule(String className) {
        if (TextUtils.isEmpty(className)) return;
        if (isRegisterModule(className)) return;
        try {
            Class<IEsModule> cls = (Class<IEsModule>) Class.forName(className);
            registerModule(cls);
        } catch (Throwable e) {
            L.logEF("预注册module失败 " + Log.getStackTraceString(e));
        }
    }

    public final void registerModules(String... classNames) {
        if (classNames == null) return;
        for (String className : classNames) {
            registerModule(className);
        }
    }

    public void registerModule(Class<? extends IEsModule> clazz) {
        if (clazz == null) return;
        String className = clazz.getName();
        if (isRegisterModule(className)) return;
        mModules.put(clazz.getName(), () -> {
            try {
                return clazz.newInstance();
            } catch (Throwable e) {
                L.logEF("预注册module失败 " + Log.getStackTraceString(e));
            }
            return null;
        });
    }

    @SafeVarargs
    public final void registerModules(Class<? extends IEsModule>... clazz) {
        for (Class<? extends IEsModule> c : clazz) {
            registerModule(c);
        }
    }

    public boolean isRegisterModule(String className) {
        return !TextUtils.isEmpty(className) && mModules.get(className) != null;
    }

    private EsProvider<IEsModule> getModule(String className) {
        return mModules.get(className);
    }

    private Map<String, EsProvider<IEsModule>> getAllRegisterModules() {
        return new HashMap<>(mModules);
    }

    //endregion

    //region component

    public void registerComponent(String className) {
        if (TextUtils.isEmpty(className)) return;
        if (isRegisterComponent(className)) return;
        try {
            Class<IEsComponent<?>> cls = (Class<IEsComponent<?>>) Class.forName(className);
            registerComponent(cls);
        } catch (Throwable e) {
            L.logEF("注册component失败 0 " + Log.getStackTraceString(e));
        }
    }

    public final void registerComponents(String... classNames) {
        if (classNames == null) return;
        for (String className : classNames) {
            registerComponent(className);
        }
    }

    public void registerComponent(Class<? extends IEsComponent<?>> clazz) {
        if (clazz == null) return;
        String className = clazz.getName();
        if (isRegisterComponent(className)) return;
        mComponents.put(clazz.getName(), () -> {
            try {
                return clazz.newInstance();
            } catch (Throwable e) {
                L.logEF("注册component失败 " + Log.getStackTraceString(e));
            }
            return null;
        });
    }

    @SafeVarargs
    public final void registerComponents(Class<? extends IEsComponent<?>>... clazz) {
        for (Class<? extends IEsComponent<?>> c : clazz) {
            registerComponent(c);
        }
    }

    public boolean isRegisterComponent(String className) {
        return !TextUtils.isEmpty(className) && mComponents.get(className) != null;
    }

    private EsProvider<IEsComponent<? extends View>> getComponent(String className) {
        return mComponents.get(className);
    }

    private Map<String, EsProvider<IEsComponent<? extends View>>> getAllRegisterComponents() {
        return new HashMap<>(mComponents);
    }

    //endregion

    //region provider

    public void registerProviders(HippyAPIProvider... providers) {
        if(providers == null) return;
        for (HippyAPIProvider provider : providers) {
            Class<? extends HippyAPIProvider> providerClass = provider.getClass();
            if (mProviders.containsKey(providerClass)) {
                continue;
            }
            mProviders.put(providerClass, provider);
        }
    }

    //endregion

    //endregion

    //region 把预注册inject到hippy

    public void injectModulesAndComponents(HippyEngineContext context) {
        Set<String> moduleKeys = mModules.keySet();
        for (String key : moduleKeys) {
            injectModule(context, mModules.get(key));
        }

        Set<String> componentKeys = mComponents.keySet();
        for (String key : componentKeys) {
            injectComponent(context, mComponents.get(key));
        }

        Collection<HippyAPIProvider> providers = mProviders.values();
        for (HippyAPIProvider provider : providers) {
            injectProviders(context, provider);
        }
    }

    //endregion

    //region inject到所有hippy实例

    public void injectModulesToAllContext(String... classNames) {
        if (classNames == null || classNames.length == 0) return;
        Class<?>[] classes = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            try {
                classes[i] = Class.forName(classNames[i]);
            } catch (Exception e) {
                L.logEF("inject module err: " + Log.getStackTraceString(e));
            }
        }
        injectModulesToAllContext(classes);
    }

    public void injectModulesToAllContext(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            try {
                String className = clazz.getName();
                // STEP.0 检测是否注册过
                if (isRegisterModule(className)) {
                    continue;
                }
                // STEP.1 添加到预注册
                registerModule((Class<? extends IEsModule>) clazz);
                // STEP.2 inject到hp中
                eachAllHippyContext(context -> {
                    injectModule(context, getModule(className));
                });
            } catch (Throwable e) {
                L.logEF("inject module err: " + Log.getStackTraceString(e));
            }
        }

    }

    public void injectProvidersToAllContext(Object... providers) {
        if (providers == null || providers.length == 0) return;
        List<HippyAPIProvider> providerList = new ArrayList<>(providers.length);
        for (Object provider : providers) {
            if (provider instanceof HippyAPIProvider) {
                providerList.add((HippyAPIProvider) provider);
                registerProviders((HippyAPIProvider) provider);
            } else {
                L.logEF(provider + " is NOT APIProvider");
            }
        }
        if (!providerList.isEmpty()) {
            injectProviderToAllContext(providerList);
        }
    }

    public void injectProviderToAllContext(List<HippyAPIProvider> providerList) {
        eachAllHippyContext(context -> {
            for (HippyAPIProvider provider : providerList) {
                try {
                    injectProviders(context, provider);
                } catch (Throwable e) {
                    L.logEF("inject provider err: " + Log.getStackTraceString(e));
                }
            }
        });
    }

    public void injectComponentsToAllContext(String... classNames) {
        if (classNames == null || classNames.length == 0) return;
        Class<?>[] classes = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            try {
                classes[i] = Class.forName(classNames[i]);
            } catch (Exception e) {
                L.logEF("inject component err: " + e);
            }
        }
        injectComponentsToAllContext(classes);
    }

    public void injectComponentsToAllContext(Class<?>... classes) {
        if (classes == null || classes.length == 0) return;
        for (Class<?> clazz : classes) {
            try {
                String className = clazz.getName();
                // STEP.0 检测是否注册过
                if (isRegisterComponent(className)) {
                    continue;
                }
                // STEP.1 添加到预注册
                registerComponent((Class<? extends IEsComponent<?>>) clazz);
                // STEP.2 inject到hp中
                eachAllHippyContext(context -> {
                    injectComponent(context, getComponent(className));
                });
            } catch (Throwable e) {
                L.logEF("inject component err: " + Log.getStackTraceString(e));
            }
        }
    }

    //endregion

    private void injectModule(HippyEngineContext context, EsProvider<IEsModule> moduleProvider) {
        try {
            if (context == null || moduleProvider == null) return;
            IEsModule iModule = moduleProvider.get();
            if (iModule == null) return;
            CommonModule module = new CommonModule(context, iModule);
            String name = module.getModuleInfo().getName();
            context.getModuleManager().addCustomModule(name, module.getModuleInfo());
            if (L.DEBUG) L.logD(context.getEngineId() + " injected module: " + name);
        } catch (Throwable e) {
            L.logEF("inject module err: " + Log.getStackTraceString(e));
        }
    }

    private void injectComponent(HippyEngineContext context, EsProvider<IEsComponent<? extends View>> componentProvider) {
        try {
            if (context == null || componentProvider == null) return;
            IEsComponent<? extends View> component = componentProvider.get();
            if (component == null) return;
            String name = component.getClass().getSimpleName();
            context.getRenderManager().addCustomViewController(name, new CommonViewController(component), false);
            if (L.DEBUG) L.logD(context.getEngineId() + " injected component: " + name);
        } catch (Throwable e) {
            L.logEF("inject component err: " + Log.getStackTraceString(e));
        }
    }

    private void injectProviders(HippyEngineContext context, HippyAPIProvider provider) {
        if (context == null || provider == null) return;
        try {
            context.getRenderManager().addCustomControllers(provider);
            L.logDF(context.getEngineId() + " injected provider: " + provider.getClass());
        } catch (Throwable e) {
            L.logEF("inject provider err: " + Log.getStackTraceString(e));
        }
    }

    private void eachAllHippyContext(EsSingleCallback<HippyEngineContext> callback) {
        EsViewManager vm = EsViewManager.get();
        if (vm == null) return;
        List<EsViewRecord> appTasks = vm.getRunningAppTasks();
        for (EsViewRecord task : appTasks) {
            HippyEngineContext context = task.getEngineContext();
            if (context == null) continue;
            callback.onCallback(context);
        }
    }

    //region 单例

    private static final class MiniComponentManagerHolder {
        private static final EsComponentManager INSTANCE = new EsComponentManager();
    }

    public static EsComponentManager get() {
        return MiniComponentManagerHolder.INSTANCE;
    }

    private EsComponentManager() {
    }

    //endregion

}
