package com.quicktvui.sdk.core.internal;

import static com.sunrain.toolkit.utils.log.L.DEBUG;
import static com.sunrain.toolkit.utils.log.L.logD;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import com.quicktvui.sdk.core.EsAppContext;

/**
 * Create by weipeng on 2022/03/14 20:16
 */
public class EsContextFinder {

    private IFinder mFinder;
    private final Map<String, EsAppContext> mContexts = new HashMap<>(5);

    public EsAppContext findContext(String packageName) {
        if (DEBUG) logD("findContext:" + packageName);
        EsAppContext ctx = TextUtils.isEmpty(packageName) ? null : mContexts.get(packageName);
        if (ctx == null) ctx = new EsAppContext(findRealContext(packageName));
        mContexts.put(packageName, ctx);
        return ctx;
    }

    private Context findRealContext(String packageName) {
        Context context;
        if (TextUtils.isEmpty(packageName) || mFinder == null || (context = mFinder.findContext(packageName)) == null) {
            return EsContext.get().getContext();
        }
        return context;
    }

    public void registerContextFinder(IFinder finder) {
        mFinder = finder;
    }

    public interface IFinder {
        Context findContext(String packageName);
    }

    //region 单例

    private static final class EsContextManagerHolder {
        private static final EsContextFinder INSTANCE = new EsContextFinder();
    }

    public static EsContextFinder get() {
        return EsContextManagerHolder.INSTANCE;
    }

    private EsContextFinder() {
    }

    //endregion

}
