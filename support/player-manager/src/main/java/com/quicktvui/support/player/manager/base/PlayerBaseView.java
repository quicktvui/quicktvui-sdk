package com.quicktvui.support.player.manager.base;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.log.PLog;
import com.quicktvui.support.player.manager.model.IPlayerDimension;
import com.quicktvui.support.player.manager.player.PlayerError;
import com.quicktvui.support.player.manager.player.PlayerInfo;
import com.quicktvui.support.player.manager.player.PlayerStatus;
import com.quicktvui.support.player.manager.player.PlayerStatusEnum;
import com.quicktvui.support.player.manager.player.PlayerStatusParams;
import com.quicktvui.support.player.manager.aspect.AspectRatio;
import com.quicktvui.support.player.manager.decode.Decode;
import com.quicktvui.support.player.manager.player.IPlayer;
import com.quicktvui.support.player.manager.player.IPlayerCallback;
import com.tencent.mtt.hippy.common.HippyMap;

import java.util.List;
import java.util.Map;

public class PlayerBaseView extends ESBaseFrameLayout implements IPlayerCallback, IPlayerRootView {
    protected static final String TAG = "ESPlayerBaseView";

    private IPlayerDimension playerDimension;

    private IPlayer player;

    private final IEsTraceable traceable;

    public PlayerBaseView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PlayerBaseView(Context context, IEsTraceable traceable) {
        this(context, null, 0, traceable);
    }

    public PlayerBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    public PlayerBaseView(Context context, AttributeSet attrs, int defStyleAttr, IEsTraceable traceable) {
        super(context, attrs, defStyleAttr);
        this.traceable = traceable;
        init();
    }

    private void init() {

    }


    //event
    public static final String EVENT_PROP_PLAYER_STATUS = "playerStatus";
    public static final String EVENT_PROP_CURRENT_POSITION = "currentPosition";
    public static final String EVENT_PROP_DURATION = "duration";

    public static final String EVENT_PROP_INFO_TYPE = "infoType";
    public static final String EVENT_PROP_INFO_CODE = "infoCode";
    public static final String EVENT_PROP_INFO_MESSAGE = "infoMessage";

    public static final String EVENT_PROP_ERROR_CODE = "errorCode";
    public static final String EVENT_PROP_ERROR_MESSAGE = "errorMessage";

    public static final String EVENT_PROP_PLAYER_WIDTH = "playerWidth";
    public static final String EVENT_PROP_PLAYER_HEIGHT = "playerHeight";

    public static final String EVENT_PROP_CODE = "code";
    public static final String EVENT_PROP_MESSAGE = "message";

    //播放速率列表
    public static final String EVENT_PROP_PLAY_RATE_LIST = "playRateList";
    public static final String EVENT_PROP_PLAY_RATE = "playRate";

    //清晰度
    public static final String EVENT_PROP_PLAY_DEFINITION_LIST = "definitionList";
    public static final String EVENT_PROP_PLAY_DEFINITION = "definition";

    //画面比例
    public static final String EVENT_PROP_PLAY_ASPECT_RATIO_LIST = "aspectRatioList";
    public static final String EVENT_PROP_PLAY_ASPECT_RATIO = "aspectRatio";

    //解码
    public static final String EVENT_PROP_PLAY_DECODE_LIST = "decodeList";
    public static final String EVENT_PROP_PLAY_DECODE = "decode";

    //音量
    public static final String EVENT_PROP_PLAY_LEFT_VOLUME = "leftVolume";
    public static final String EVENT_PROP_PLAY_RIGHT_VOLUME = "rightVolume";

    //
    public static final String EVENT_PROP_ANDROID_VUE_BRIDGE = "bridge";

    //是否可以点击
    public static final String EVENT_PROP_PLAYER_CLICKABLE = "playerClickable";

    public enum Events {
        EVENT_ON_PLAYER_STATUS_CHANGED("onPlayerStatusChanged"),
        EVENT_ON_PLAYER_PROGRESS_CHANGED("onPlayerProgressChanged"),
        //
        EVENT_ON_PLAYER_ERROR("onPlayerError"),
        //
        EVENT_ON_PLAYER_INFO("onPlayerInfo"),

        //播放速率
        EVENT_ON_PLAYER_RATE_LIST_CHANGED("onAllPlayRateChanged"),
        EVENT_ON_PLAYER_RATE_CHANGED("onPlayRateChanged"),

        //清晰度
        EVENT_ON_PLAYER_DEFINITION_LIST_CHANGED("onAllDefinitionChanged"),
        EVENT_ON_PLAYER_DEFINITION_CHANGED("onDefinitionChanged"),

        //画面比例
        EVENT_ON_PLAYER_ASPECT_RATIO_LIST_CHANGED("onAllAspectRatioChanged"),
        EVENT_ON_PLAYER_ASPECT_RATIO_CHANGED("onAspectRatioChanged"),

        //解码
        EVENT_ON_PLAYER_DECODE_LIST_CHANGED("onAllDecodeChanged"),
        EVENT_ON_PLAYER_DECODE_CHANGED("onDecodeChanged"),

        //大小屏变化
        EVENT_ON_PLAYER_ENTER_FULL_SCREEN("onEnterFullScreen"),
        EVENT_ON_PLAYER_EXIT_FULL_SCREEN("onExitFullScreen"),

        //音量
        EVENT_ON_PLAYER_VOLUME_CHANGED("onPlayerVolumeChanged"),
        //
        EVENT_ON_ANDROID_INVOKE_VUE("onAndroidInvokeVue");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    @Override
    public IPlayer getPlayer() {
        return player;
    }

    public void setPlayer(IPlayer player) {
        this.player = player;
        this.player.registerPlayerCallback(this);
        this.addView(player.getPlayerView(), new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );
        this.playerDimension = player.getPlayerDimension();
    }


    @Override
    public void onPlayerStatusChanged(PlayerStatus playerStatus) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------onPlayerStatusChanged-->>>>>" + playerStatus);
        }

        //播放器add view
        if (playerStatus != null
                && playerStatus.status == PlayerStatusEnum.PLAYER_STATE_PLAYER_VIEW_CHANGED) {
            requestPlayerViewLayout();
        }

        EsMap eventMap = new EsMap();
        eventMap.pushInt(EVENT_PROP_PLAYER_STATUS, playerStatus.status.ordinal());

        //PLAYER_STATE_AUTHORIZED
        if (playerStatus != null && PlayerStatusEnum.PLAYER_STATE_AUTHORIZED.equals(playerStatus.status)) {
            Map<String, Object> extraMap = playerStatus.getData();
            if (extraMap != null) {
                try {
                    Object codeObj = extraMap.get(PlayerStatus.CODE_KEY);
                    if (codeObj != null) {
                        eventMap.pushInt(EVENT_PROP_CODE, (Integer) codeObj);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                try {
                    Object messageObj = extraMap.get(PlayerStatus.CODE_MESSAGE);
                    if (messageObj != null) {
                        eventMap.pushString(EVENT_PROP_MESSAGE, (String) messageObj);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        //PLAYER_STATE_VIDEO_SIZE_CHANGED
        else if (PlayerStatusEnum.PLAYER_STATE_VIDEO_SIZE_CHANGED.equals(playerStatus.status)) {
            Map<String, Object> extraMap = playerStatus.getData();
            if (extraMap != null) {
                try {
                    Object codeObj = extraMap.get(PlayerStatusParams.PLAYER_WIDTH);
                    if (codeObj != null) {
                        eventMap.pushInt(EVENT_PROP_PLAYER_WIDTH, (Integer) codeObj);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                try {
                    Object codeObj = extraMap.get(PlayerStatusParams.PLAYER_HEIGHT);
                    if (codeObj != null) {
                        eventMap.pushInt(EVENT_PROP_PLAYER_HEIGHT, (Integer) codeObj);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        //PLAYER_STATE_PLAYER_CLICKABLE
        else if (PlayerStatusEnum.PLAYER_STATE_PLAYER_CLICKABLE.equals(playerStatus.status)) {
            Map<String, Object> extraMap = playerStatus.getData();
            if (extraMap != null) {
                try {
                    Object clickableObj = extraMap.get(PlayerStatusParams.PLAYER_CLICKABLE);
                    if (clickableObj != null) {
                        eventMap.pushBoolean(EVENT_PROP_PLAYER_CLICKABLE, (Boolean) clickableObj);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else if (PlayerStatusEnum.PLAYER_STATE_TIMED_TEXT_CHANGED.equals(playerStatus.status)) {
            eventMap.pushString(PlayerStatusParams.PLAYER_TIMED_TEXT,
                    (String) playerStatus.getData(PlayerStatusParams.PLAYER_TIMED_TEXT));

            if (playerStatus.getData(PlayerStatusParams.PLAYER_TIMED_TEXT_LEFT) != null) {
                eventMap.pushInt(PlayerStatusParams.PLAYER_TIMED_TEXT_LEFT,
                        (Integer) playerStatus.getData(PlayerStatusParams.PLAYER_TIMED_TEXT_LEFT));
                eventMap.pushInt(PlayerStatusParams.PLAYER_TIMED_TEXT_TOP,
                        (Integer) playerStatus.getData(PlayerStatusParams.PLAYER_TIMED_TEXT_TOP));
                eventMap.pushInt(PlayerStatusParams.PLAYER_TIMED_TEXT_RIGHT,
                        (Integer) playerStatus.getData(PlayerStatusParams.PLAYER_TIMED_TEXT_RIGHT));
                eventMap.pushInt(PlayerStatusParams.PLAYER_TIMED_TEXT_BOTTOM,
                        (Integer) playerStatus.getData(PlayerStatusParams.PLAYER_TIMED_TEXT_BOTTOM));
            }
        }
        //
        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), eventMap);
        }
    }

    @Override
    public void onPlayerError(PlayerError playerError) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------onPlayerError-->>>>>" + playerError);
        }
        EsMap eventMap = new EsMap();
        eventMap.pushInt(EVENT_PROP_ERROR_CODE, playerError.errorCode);
        eventMap.pushString(EVENT_PROP_ERROR_MESSAGE, playerError.errorMsg);
        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_ERROR.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_ERROR.toString(), eventMap);
        }
    }

    @Override
    public void onPlayerInfo(PlayerInfo playerInfo) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------onPlayerInfo-->>>>>" + playerInfo);
        }
        EsMap eventMap = new EsMap();
        try {
            if (playerInfo.getPlayerType() != null) {
                eventMap.pushInt(EVENT_PROP_INFO_TYPE, playerInfo.getPlayerType().ordinal());
            } else {
                eventMap.pushInt(EVENT_PROP_INFO_TYPE, -1);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        eventMap.pushInt(EVENT_PROP_INFO_CODE, playerInfo.getCode());
        eventMap.pushString(EVENT_PROP_INFO_MESSAGE, playerInfo.getMessage());
        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_INFO.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_INFO.toString(), eventMap);
        }
    }

    //-------------------------------清晰度-----------------------------------
    @Override
    public void onDefinitionChanged(Definition definition) {
        EsMap eventMap = new EsMap();
        eventMap.pushInt(EVENT_PROP_PLAY_DEFINITION, definition.getValue());

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_DEFINITION_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_DEFINITION_CHANGED.toString(), eventMap);
        }
    }

    @Override
    public void onAllDefinitionChanged(List<Definition> definitionList) {
        if (definitionList != null && definitionList.size() > 0) {
            EsArray esArray = new EsArray();
            for (Definition definition : definitionList) {
                esArray.pushInt(definition.getValue());
            }
            EsMap eventMap = new EsMap();
            eventMap.pushArray(EVENT_PROP_PLAY_DEFINITION_LIST, esArray);
            if (traceable != null) {
                EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_DEFINITION_LIST_CHANGED.toString(), eventMap);
            } else {
                EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_DEFINITION_LIST_CHANGED.toString(), eventMap);
            }
        }
    }

    //-------------------------------画面比例-----------------------------------
    @Override
    public void onAspectRatioChanged(AspectRatio aspectRatio) {
        EsMap eventMap = new EsMap();
        eventMap.pushInt(EVENT_PROP_PLAY_ASPECT_RATIO, aspectRatio.getValue());
        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_ASPECT_RATIO_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_ASPECT_RATIO_CHANGED.toString(), eventMap);
        }
    }


    @Override
    public void onAllAspectRatioChanged(List<AspectRatio> aspectRatioList) {
        if (aspectRatioList != null && aspectRatioList.size() > 0) {
            EsArray esArray = new EsArray();
            for (AspectRatio aspectRatio : aspectRatioList) {
                esArray.pushInt(aspectRatio.getValue());
            }
            EsMap eventMap = new EsMap();
            eventMap.pushArray(EVENT_PROP_PLAY_ASPECT_RATIO_LIST, esArray);

            if (traceable != null) {
                EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_ASPECT_RATIO_LIST_CHANGED.toString(), eventMap);
            } else {
                EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_ASPECT_RATIO_LIST_CHANGED.toString(), eventMap);
            }
        }
    }

    //------------------------------倍率------------------------------------
    @Override
    public void onPlayRateChanged(float rate) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#----播放速率--onPlayRateChanged-->>>>>" + rate);
        }
        EsMap eventMap = new EsMap();
        eventMap.pushString(EVENT_PROP_PLAY_RATE, rate + "");

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_RATE_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_RATE_CHANGED.toString(), eventMap);
        }
    }

    /**
     * 播放速率
     *
     * @param rateList
     */
    @Override
    public void onAllPlayRateChanged(List<Float> rateList) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#---播放速率---onAllPlayRateChanged-->>>>>" + rateList);
        }
        if (rateList != null && rateList.size() > 0) {
            EsArray esArray = new EsArray();
            for (float rate : rateList) {
                esArray.pushString(rate + "");
            }
            EsMap eventMap = new EsMap();
            eventMap.pushArray(EVENT_PROP_PLAY_RATE_LIST, esArray);
            if (traceable != null) {
                EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_RATE_LIST_CHANGED.toString(), eventMap);
            } else {
                EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_RATE_LIST_CHANGED.toString(), eventMap);
            }
        }
    }

    //-------------------------------播放器解码-----------------------------------
    @Override
    public void onDecodeChanged(Decode decode) {
        EsMap eventMap = new EsMap();
        eventMap.pushInt(EVENT_PROP_PLAY_DECODE, decode.getValue());

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_DECODE_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_DECODE_CHANGED.toString(), eventMap);
        }
    }


    @Override
    public void onAllDecodeChanged(List<Decode> decodeList) {
        if (decodeList != null && decodeList.size() > 0) {
            EsArray esArray = new EsArray();
            for (Decode decode : decodeList) {
                esArray.pushInt(decode.getValue());
            }
            EsMap eventMap = new EsMap();
            eventMap.pushArray(EVENT_PROP_PLAY_DECODE_LIST, esArray);

            if (traceable != null) {
                EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_DECODE_LIST_CHANGED.toString(), eventMap);
            } else {
                EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_DECODE_LIST_CHANGED.toString(), eventMap);
            }

        }
    }
    //------------------------------------------------------------------

    @Override
    public void onPlayerDimensionChanged(IPlayerDimension playerViewSize) {

    }

    @Override
    public void onPlayerProgressChanged(long currentPosition, long duration) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------onPlayerProgressChanged-->>>>>currentPosition:"
                    + currentPosition
                    + "---------->>>>duration:" + duration);
        }

        EsMap eventMap = new EsMap();
        eventMap.pushLong(EVENT_PROP_CURRENT_POSITION, currentPosition);
        eventMap.pushLong(EVENT_PROP_DURATION, duration);

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_PROGRESS_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_PROGRESS_CHANGED.toString(), eventMap);
        }
    }

    @Override
    public void onEnterFullScreen() {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------onEnterFullScreen-->>>>>");
        }
        EsMap eventMap = new EsMap();

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_ENTER_FULL_SCREEN.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_ENTER_FULL_SCREEN.toString(), eventMap);
        }
    }

    @Override
    public void onExitFullScreen() {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------onExitFullScreen-->>>>>");
        }
        EsMap eventMap = new EsMap();

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_EXIT_FULL_SCREEN.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_EXIT_FULL_SCREEN.toString(), eventMap);
        }
    }

    @Override
    public void onPlayerVolumeChanged(float leftVolume, float rightVolume) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController----Volume--onPlayerVolumeChanged-->>>>>" + leftVolume + "------>>>" + rightVolume);
        }
        EsMap eventMap = new EsMap();
        eventMap.pushString(EVENT_PROP_PLAY_LEFT_VOLUME, leftVolume + "");
        eventMap.pushString(EVENT_PROP_PLAY_RIGHT_VOLUME, rightVolume + "");

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_PLAYER_VOLUME_CHANGED.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_PLAYER_VOLUME_CHANGED.toString(), eventMap);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController---onMeasure---布局-->>>>>");
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController---onLayout---布局-->>>>>");

        }
    }

    @Override
    public void requestLayout() {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#------requestLayout---------EsPlayerBaseView------>>>>>");
        }
        super.requestLayout();
    }

    //----------------------------------------------------

    /**
     *
     */
    public void requestCustomLayout(int width, int height, int x, int y) {
        requestLayout();
    }

    /**
     *
     */
    public void requestCustomLayout() {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#---------requestLayoutFromCustom--------->>>>>");
        }
        requestLayout();
    }


    //----------------------------------------------------
    public void setPlayerDimension(IPlayerDimension playerDimension, boolean quickUpdate) {

        if (playerDimension.equals(this.playerDimension)) {
//            Log.e("TAG", "setPlayerDimension: 重复调用");
            return;
        }

        this.playerDimension = playerDimension;

        int width, height;

        if (playerDimension.isFullScreen()) {
            width = playerDimension.getFullPlayerWidth();
            height = playerDimension.getFullPlayerHeight();
        } else {
            width = playerDimension.getDefaultPlayerWidth();
            height = playerDimension.getDefaultPlayerHeight();
        }

        quickUpdateVideoLayout(width, height, quickUpdate, false);
    }

    @Override
    public void quickUpdateVideoLayout(int w, int h) {
        quickUpdateVideoLayout(w, h, true, true);
    }

    // 三部曲：
    // 1、验证效果
    // 2、vue层指定parent上限  name：player_root_
    // 3、使用task的方式调用本方法，更加快捷迅速
    public void quickUpdateVideoLayout(int w, int h, boolean quickUpdate, boolean setLayoutParams) {
        if (quickUpdate)
            layoutParent((ViewGroup) getParent(), w, h, 2, setLayoutParams);

        measureAndLayout(this, w, h, quickUpdate, true);

        if (quickUpdate)
            layoutChildren(w, h, setLayoutParams);

    }

    private void layoutParent(ViewGroup parent, int w, int h, int maxTimes, boolean setLayoutParams) {

        if (parent != null && maxTimes > 0) {
            ViewGroup temp = parent;

            String name = "";
            if (parent.getTag() instanceof HippyMap) {
                HippyMap map = (HippyMap) parent.getTag();
                name = map.getString("name");
            }

            if (name == null || !name.startsWith("player_root")) {
                parent = (ViewGroup) parent.getParent();
                if (parent != null) {
                    layoutParent(parent, w, h, --maxTimes, setLayoutParams);
                }
            }

            measureAndLayout(temp, w, h, true, setLayoutParams);
        }

    }


    private void layoutChildren(int w, int h, boolean setLayoutParams) {

        View v = getChildAt(0);
//        int a = 3;
//        while (v != null && a > 0) {
        while (v != null) {
            measureAndLayout(v, w, h, true, setLayoutParams);
            if (v instanceof ViewGroup) {
                v = ((ViewGroup) v).getChildAt(0);
            } else {
                v = null;
            }
//            a--;
        }
    }

    private void measureAndLayout(View view, int w, int h, boolean quickUpdate, boolean setLayoutParams) {

        if (quickUpdate) {
            int l = 0, t = 0;
            if (view instanceof SurfaceView || view instanceof TextureView) {
                float setAspectRatio = (float) w / (float) h;
                float videoAspectRatio = (float) view.getWidth() / (float) view.getHeight();
                if (setAspectRatio > videoAspectRatio) {
                    int wT = (int) (h * videoAspectRatio);
                    l = (w - wT) / 2;
                    w = wT;
                } else if (setAspectRatio < videoAspectRatio){
                    int hT = (int) (w / videoAspectRatio);
                    t = (h - hT) / 2;
                    h = hT;
                }
            }

            view.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));

            view.layout(l, t, l + w, t + h);
        }

        if (setLayoutParams){
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = w;
            layoutParams.height = h;
            view.setLayoutParams(layoutParams);
        }
    }

    public void setPlayerSize(int width, int height) {
        try {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.requestLayout();
    }


    //----------------------------------------------------

    public void setPlayerTranslationX(float translationX) {
        this.setTranslationX(translationX);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#TRANSLATION------setPlayerTranslationX-->>>>>" + translationX);
        }
    }

    public void setPlayerTranslationY(float translationY) {
        this.setTranslationY(translationY);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#TRANSLATION------setPlayerTranslationY-->>>>>" + translationY);
        }
    }

    public void setPlayerTranslationZ(float translationZ) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#TRANSLATION------setPlayerTranslationZ-->>>>>" + translationZ);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setTranslationZ(translationZ);
        }
    }

    public void setPlayerTranslationXY(float translationX, float translationY) {
        this.setPlayerTranslationX(translationX);
        this.setPlayerTranslationY(translationY);
    }

    public void setPlayerTranslationXYZ(float translationX, float translationY, float translationZ) {
        this.setPlayerTranslationX(translationX);
        this.setPlayerTranslationY(translationY);
        this.setPlayerTranslationZ(translationZ);
    }

    //----------------------------------------------------

    public void androidInvokeVue(String value) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController---androidInvokeVue------>>>>>" + value);
        }
        EsMap eventMap = new EsMap();
        eventMap.pushString(EVENT_PROP_ANDROID_VUE_BRIDGE, value);

        if (traceable != null) {
            EsProxy.get().sendUIEvent(traceable, getId(), Events.EVENT_ON_ANDROID_INVOKE_VUE.toString(), eventMap);
        } else {
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANDROID_INVOKE_VUE.toString(), eventMap);
        }
    }

    public void release() {
//        try {
//            if (this.player != null) {
//                this.player.unregisterPlayerCallback(this);
//            }
//            this.player = null;
//            this.eventEmitter = null;
//            removeAllViews();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }
}
