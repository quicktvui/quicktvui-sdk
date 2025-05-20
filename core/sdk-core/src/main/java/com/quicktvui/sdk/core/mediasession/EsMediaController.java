package com.quicktvui.sdk.core.mediasession;

import android.os.Bundle;

import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.protocol.EsProtocolDispatcher;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

/**
 * Create by weipeng on 2022/06/19 11:27
 * Describe
 */
public class EsMediaController implements IEsRemoteEventCallback {

    public static final String ACTION_START_ES_APP = "startEsApp";

    private EsMediaPlayerService mService;

    public void setCurrentService(EsMediaPlayerService service) {
        if (L.DEBUG) L.logD("mds setCurrentService");
        mService = service;
    }

    //region MediaSession的回调

    // MediaBrowser协议, C -> S 启动应用
    // 1. 启动EsApp
    //    action: startEsApp
    //    bundle: {action, pkg, from, args, exp}
    // 2. action
    //    action: OnLinkEvent
    //    bundle: {action, etc...}

    // 将其转化成快应用识别的协议格式，并执行
    public void processActionFromSession(String action, Bundle extras) {
        try {
            JSONObject jo = MapperUtils.tryMapperBundle2JsonObject(extras);
            if (ACTION_START_ES_APP.equals(action)) {
                jo.put(EsProtocolDispatcher.K_ACTION_V2, EsProtocolDispatcher.K_ACTION_ES_APP_V2);
            } else {
                jo.put(EsProtocolDispatcher.K_ACTION_V2, action);
                jo.put(Constants.EVT_LINK_FROM_K, Constants.EVT_LINK_FROM_V_MEDIASESSION);
                JSONObject newJson = new JSONObject();
                newJson.put(EsProtocolDispatcher.K_ACTION_V2, Constants.GLOBAL_EVENT.EVT_LINK_RECEIVE);
                newJson.put(EsProtocolDispatcher.K_ARGS_V2, jo);
                if (jo.has(EsProtocolDispatcher.K_FROM)) {
                    newJson.put(EsProtocolDispatcher.K_FROM, jo.optString(EsProtocolDispatcher.K_FROM));
                } else {
                    newJson.put(EsProtocolDispatcher.K_FROM, Constants.EVT_LINK_FROM_V_MEDIASESSION);
                }
                jo = newJson;
            }
            if (L.DEBUG) L.logD("MD_SESSION 协议转换:\n" + jo.toString(1));
            EsMap from = new EsMap();
            from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_REMOTE);
            from.pushObject(Constants.Event.ES_REFERER1, Constants.Event.FROM_MEDIA_SESSION);
            EsProtocolDispatcher.tryDispatcher(from, jo, this);
        } catch (Throwable e) {
            L.logW("media control", e);
        }
    }

    /**
     *
     * @param eventName 作为action传递过来
     * @param eventData 包含errorcode等执行结果信息
     */
    @Override
    public void onReceiveEvent(String eventName, String eventData) {
        try {
            Bundle extras = MapperUtils.tryMapperJson2Bundle(eventData);
            sendSessionEvent(eventName, extras);
        } catch (Exception e) {
            L.logW("receive evt", e);
        }
    }

    //endregion

    public void attachEventCallback() {
        EsContext.get().setRemoteEventCallback(this);
    }

    private void sendSessionEvent(String event, Bundle extras) {
        if (mService == null) return;
        if (L.DEBUG) L.logD("sendSessionEvent --> evt: " + event + ",extras: " + extras);
        mService.sendEvent(event, extras);
    }

    //region 单例

    private static final class EsMediaControllerHolder {
        private static final EsMediaController INSTANCE = new EsMediaController();
    }

    public static EsMediaController get() {
        return EsMediaControllerHolder.INSTANCE;
    }

    private EsMediaController() {
    }

    //endregion

}
