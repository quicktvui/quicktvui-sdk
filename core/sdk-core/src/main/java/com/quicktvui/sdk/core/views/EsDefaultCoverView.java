package com.quicktvui.sdk.core.views;

import static com.quicktvui.sdk.core.internal.Constants.ERR_ASSET;
import static com.quicktvui.sdk.core.internal.Constants.ERR_BRIDGE;
import static com.quicktvui.sdk.core.internal.Constants.ERR_DECRYPT;
import static com.quicktvui.sdk.core.internal.Constants.ERR_DEVSERVER;
import static com.quicktvui.sdk.core.internal.Constants.ERR_DOWNLOAD;
import static com.quicktvui.sdk.core.internal.Constants.ERR_DOWNLOAD_INFO;
import static com.quicktvui.sdk.core.internal.Constants.ERR_INFO;
import static com.quicktvui.sdk.core.internal.Constants.ERR_INIT_EXCEPTION;
import static com.quicktvui.sdk.core.internal.Constants.ERR_MD5;
import static com.quicktvui.sdk.core.internal.Constants.ERR_OFFLINE;
import static com.quicktvui.sdk.core.internal.Constants.ERR_SERVER;
import static com.quicktvui.sdk.core.internal.Constants.ERR_TIME_OUT;
import static com.quicktvui.sdk.core.internal.Constants.ERR_UNKNOWN;
import static com.quicktvui.sdk.core.internal.Constants.ERR_UNZIP;
import static com.quicktvui.sdk.core.internal.Constants.ERR_VER_MATCH_FAIL;
import static com.quicktvui.sdk.core.internal.Constants.ERR_WRONG_STATE;
import static com.quicktvui.sdk.core.internal.loader.NexusRpkLoader.requestJson;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.extscreen.runtime.api.ability.ESAbilityProvider;
import com.extscreen.runtime.api.ability.plugin.IEsCheckPluginUpgrade;
import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.cover.IEsCoverView;
import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.internal.SampleAnimatorListener;
import com.quicktvui.sdk.core.utils.ESExecutors;
import com.quicktvui.sdk.core.utils.NetworkSpeedMonitor;
import com.quicktvui.sdk.core.utils.NexusRepo;
import com.quicktvui.sdk.core.utils.PluginUtils;
import com.quicktvui.sdk.core.utils.TaskProgressManager;
import com.sunrain.toolkit.bolts.tasks.Continuation;
import com.sunrain.toolkit.bolts.tasks.Task;
import com.sunrain.toolkit.bolts.tasks.TaskCompletionSource;
import com.sunrain.toolkit.utils.ScreenUtils;
import com.sunrain.toolkit.utils.ToastUtils;
import com.sunrain.toolkit.utils.thread.Executors;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 默认加载信息界面
 * <p>
 * Create by weipeng on 2022/06/30 15:46
 */
public class EsDefaultCoverView extends RelativeLayout implements IEsCoverView {

    private static final int LOAD_TIMEOUT = 10000; // 10秒加载超时提示
    private static final int LOAD_SUCCESS_DELAY = 500; // 加载完毕后delay关闭界面
    private static final int FADE_OUT = 1000; // 渐变退出

    public static final int SHOW_UPLOAD_LOG = 20000; // 自动上传log
    public static final int SHOW_INTERNET_SPEED = 5000;

    private static final int DESIGN_WITH = 1920;

    private int mIconSize = 120;
    private int mIconMarginTop = 380;

    private int mLoadingBgWith = 1000;
    private int mLoadingFgWith = 400;
    private int mLoadingHeight = 5;
    private int mLoadingMarginTop = 580;
    private int mProgressMarginTop = 642;

    private int mTitleFontSize = 60;
    private int mTitleFontMarginLeft = 30;

    private int mUpgradeButtonWidth = 204;
    private int mUpgradeButtonHeight = 64;

    private int mStatusImageWidth = 423;
    private int mStatusImageHeight = 306;
    private int mStatusTextFontSize = 28;
    private int mInternetSpeedTextFontSize = 24;
    private int mProgressTextFontSize = 30;
    private int mStatusTextMarginTop = 90;

    private int mDebugVersionMargin = 8;
    private int mInternetSpeedMargin = 40;

    // app info
    private View mAppInfoContainer;
    private ImageView mAppIcon;
    private TextView mAppName;
    // loading
    private View mLoadingView;
    // tip
    private ImageView mIvTip;
    private TextView mTvTip;
    private TextView mTvTipVersion;
    private TextView mInternetSpeedView;
    private TextView mLoadingProgressView;

    private TextView mBtnUpgradeOkView;
    private TextView mBtnUpgradeCancelView;

    private boolean mSuspend = false;
    private NetworkSpeedMonitor monitor;

    private final Handler mTaskHandler = new Handler();

    public EsDefaultCoverView(Context context) {
        this(context, null);
    }

    public EsDefaultCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EsDefaultCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupCoverView(context);
    }

    @SuppressLint("NewApi")
    public EsDefaultCoverView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupCoverView(context);
    }

    //region 初始化

    private void setupCoverView(Context context) {
        setBackgroundResource(R.color.color_es_default_black_bg);
        initSize();
        setupAppInfoContainer(context);
        setupLoadingView(context);
        setupLoadingProgress(context);
        setupStatusInfoView(context);
        setupDebugVersion(context);
        setupInternetSpeed(context);
        executeDelayTask();
    }

    private final Runnable mOnLoadTimeoutTask = () -> {
        showTips(null, R.string.es_cover_tip_load_too_long, 0);
    };

    private final Runnable mShowUploadLogTipTask = () -> {
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getContext().getPackageName(), 0);
            mTvTipVersion.setText(pi.versionName);
        } catch (Exception e) {
        }

        try {
            mTvTipVersion.setText(mTvTipVersion.getText() + " - " + EsProxy.get().getEsKitVersionCode());
        } catch (Exception e) {
        }
    };

    private final Runnable mShowInternetSpeedTask = () -> mInternetSpeedView.setVisibility(VISIBLE);

    private void executeDelayTask() {
        mTaskHandler.postDelayed(mOnLoadTimeoutTask, LOAD_TIMEOUT);
        mTaskHandler.postDelayed(mShowUploadLogTipTask, SHOW_UPLOAD_LOG);
        mTaskHandler.postDelayed(mShowInternetSpeedTask, SHOW_INTERNET_SPEED);
    }

    private void cancelDelayTask() {
        monitor.stopMonitoring();
        mTaskHandler.removeCallbacksAndMessages(null);
//        removeCallbacks(mOnLoadTimeoutTask);
//        removeCallbacks(mShowUploadLogTipTask);
//        removeCallbacks(mShowInternetSpeedTask);
    }

    private void initSize() {
        int screenWidth = ScreenUtils.getScreenWidth();
        if (screenWidth != DESIGN_WITH) {
            float scale = (float) screenWidth / DESIGN_WITH;

            mIconSize *= scale;
            mIconMarginTop *= scale;

            mLoadingBgWith *= scale;
            mLoadingFgWith *= scale;
            mLoadingHeight *= scale;
            mLoadingMarginTop *= scale;
            mProgressMarginTop *= scale;

            mTitleFontSize *= scale;
            mTitleFontMarginLeft *= scale;

            mUpgradeButtonWidth *= scale;
            mUpgradeButtonHeight *= scale;

            mStatusImageWidth *= scale;
            mStatusImageHeight *= scale;
            mStatusTextFontSize *= scale;
            mInternetSpeedTextFontSize *= scale;
            mProgressTextFontSize *= scale;
            mStatusTextMarginTop *= scale;

            mDebugVersionMargin *= scale;
            mInternetSpeedMargin *= scale;

        }
    }

    private void setupAppInfoContainer(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        mAppInfoContainer = container;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = mIconMarginTop;

        setupAppInfoIcon(container);
        setupAppInfoTitle(container);

        addView(mAppInfoContainer, params);
    }

    private void setupAppInfoIcon(LinearLayout container) {
        ImageView iv = new ImageView(container.getContext());
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        mAppIcon = iv;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIconSize, mIconSize);
        params.gravity = Gravity.CENTER_VERTICAL;
        container.addView(mAppIcon, params);
    }

    private void setupAppInfoTitle(LinearLayout container) {
        TextView name = new TextView(container.getContext());
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleFontSize);
        name.setTextColor(Color.WHITE);
        mAppName = name;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = mTitleFontMarginLeft;
        container.addView(mAppName, params);
    }

    private void setupLoadingView(Context context) {
        EsProgressLoadingView view = new EsProgressLoadingView(context);

        ImageView bg = new ImageView(context);
//        bg.setImageResource(R.drawable.eskit_pic_cover_progress_bg);
        // 用这种形式是因为 R.drawable 会撑大编辑器
        int bgImgResId = getResources().getIdentifier("eskit_pic_cover_progress_bg", "drawable", context.getPackageName());
        bg.setImageResource(bgImgResId);
        view.addView(bg, new FrameLayout.LayoutParams(mLoadingBgWith, mLoadingHeight));

        ImageView fg = new ImageView(context);
//        fg.setImageResource(R.drawable.eskit_pic_cover_progress_thumb);
        // 用这种形式是因为 R.drawable 会撑大编辑器
        int fgImgResId = getResources().getIdentifier("eskit_pic_cover_progress_thumb", "drawable", context.getPackageName());

        fg.setImageResource(fgImgResId);
        fg.setAlpha(0F);
        view.addView(fg, new FrameLayout.LayoutParams(mLoadingFgWith, mLoadingHeight));

        mLoadingView = view;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = mLoadingMarginTop;
        addView(mLoadingView, params);
    }

    /**
     * 动态so库、rpk下载合并进度
     */
    private void setupLoadingProgress(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mProgressTextFontSize);
        textView.setTextColor(context.getResources().getColor(R.color.eskit_progress_text_50));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mLoadingProgressView = textView;
        params.topMargin = mProgressMarginTop;
        addView(textView, params);
        textView.setText(context.getString(R.string.es_app_loading));
        TaskProgressManager.getInstance().setTaskProgressCallback(progress -> {
            ((Activity) context).runOnUiThread(() -> {
                if (progress == 0) {
                    textView.setText(context.getString(R.string.es_app_loading));
                } else if (progress == 100) {
                    textView.setText(String.format(context.getResources().getString(R.string.es_app_uploading), 100 + "%"));
                } else {
                    textView.setText(String.format(context.getResources().getString(R.string.es_app_uploading), (int) progress + "%"));
                }
            });
        });
    }

    private void setupStatusInfoView(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        setupStatusImage(container);
        setupStatusText(container);
        setupUpgradeButton(container);

        addView(container, params);
    }

    private void setupStatusImage(LinearLayout container) {
        ImageView iv = new ImageView(container.getContext());
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        mIvTip = iv;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mStatusImageWidth, mStatusImageHeight);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        container.addView(mIvTip, params);
    }

    private void setupStatusText(LinearLayout container) {
        TextView view = new TextView(container.getContext());
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mStatusTextFontSize);
        view.setTextColor(Color.WHITE);
        mTvTip = view;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = mStatusTextMarginTop;
        container.addView(mTvTip, params);
    }

    private void setupUpgradeButton(LinearLayout container) {
        Context context = container.getContext();

        LinearLayout upgradeContainer = new LinearLayout(context);
        upgradeContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = mInternetSpeedMargin;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        container.addView(upgradeContainer, params);

        mBtnUpgradeOkView = new MyButton(context);
        mBtnUpgradeOkView.setVisibility(View.GONE);
        mBtnUpgradeOkView.setText("去升级");
        mBtnUpgradeOkView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mProgressTextFontSize);
        upgradeContainer.addView(mBtnUpgradeOkView, new LinearLayout.LayoutParams(mUpgradeButtonWidth, mUpgradeButtonHeight));

        mBtnUpgradeCancelView = new MyButton(context);
        mBtnUpgradeCancelView.setVisibility(View.GONE);
        mBtnUpgradeCancelView.setText("退出");
        mBtnUpgradeCancelView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mProgressTextFontSize);
        params = new LinearLayout.LayoutParams(mUpgradeButtonWidth, mUpgradeButtonHeight);
        params.leftMargin = mTitleFontSize;
        upgradeContainer.addView(mBtnUpgradeCancelView, params);
    }

    private void setupDebugVersion(Context context) {
        TextView view = new TextView(context);
        view.setTextColor(getResources().getColor(R.color.eskit_color_white_50));

        mTvTipVersion = view;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = mDebugVersionMargin;
        params.bottomMargin = mDebugVersionMargin;
        addView(mTvTipVersion, params);
    }

    private void setupInternetSpeed(Context context) {
        monitor = new NetworkSpeedMonitor();
        TextView textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.eskit_color_white_50));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mInternetSpeedTextFontSize);
        textView.setVisibility(View.GONE);
        mInternetSpeedView = textView;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = mInternetSpeedMargin;
        params.topMargin = mInternetSpeedMargin;
        addView(textView, params);
        monitor.startMonitoring(speed -> textView.setText(monitor.convertSpeed(speed)));
    }

    //endregion

    //region Cover

    @Override
    public void onInit(Serializable data) {
        showLoadingImage(data);
    }

    private void showLoadingImage(Serializable data) {
        if (data instanceof EsMap) {
            fetchAppInfo((EsMap) data);
        }
        post(() -> {
            if (mLoadingView != null) {
                mLoadingView.setVisibility(VISIBLE);
                if (mLoadingView instanceof ImageView) {
                    ((ImageView) mLoadingView).setImageDrawable(new LoadingDrawable(mLoadingView));
                }
            }
        });
    }

    private void fetchAppInfo(EsMap params) {

        Task.forResult(params)
                .onSuccess(GET_APP_INFO, ESExecutors.IO)
                .onSuccess(PREPARE_DATA, ESExecutors.IO)
                .continueWith((Continuation<Pair<Drawable, String>, Void>) task -> {
                    if (!task.isFaulted()) {
                        Pair<Drawable, String> result = task.getResult();
                        if (result != null) {
                            mAppIcon.setImageDrawable(result.first);
                            mAppName.setText(result.second);
                        }
                    }
                    return null;
                }, ESExecutors.MAIN);
    }

    /**
     * 请求加载小程序的信息
     */
    private final Continuation<EsMap, Pair<String, String>> GET_APP_INFO = task -> {
        EsMap params = task.getResult();

        String name = params.getString(Constants.K_APP_NAME);
        String icon = params.getString(Constants.K_APP_ICON);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(icon)) {
            return new Pair<>(name, icon);
        }

        ICoverInfo info;

        String repo = params.getString(Constants.K_APP_REPO);
        if (TextUtils.isEmpty(repo)) {
            info = ICoverInfo.FROM_API;
        } else {
            info = ICoverInfo.FROM_NEXUS;
        }

        return info.getCoverInfo(params);
    };

    private final Continuation<Pair<String, String>, Pair<Drawable, String>> PREPARE_DATA = task -> {
        Pair<String, String> info = task.getResult();
        if (info == null) return null;

        // 下载图片
        TaskCompletionSource<Drawable> iconDrawableTask = new TaskCompletionSource<>();
        EsMap data = new EsMap();
        data.pushString("url", info.second);
        EsProxy.get().loadImageBitmap(data, new EsCallback<Bitmap, Throwable>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                iconDrawableTask.setResult(new BitmapDrawable(bitmap));
            }

            @Override
            public void onFailed(Throwable throwable) {
                iconDrawableTask.setResult(ContextCompat.getDrawable(EsContext.get().getContext(),
                        R.drawable.eskit_drawable_cover_app_icon));
            }
        });

        iconDrawableTask.getTask().waitForCompletion();

        return new Pair<>(iconDrawableTask.getTask().getResult(), info.first);
    };

    private void showTipImage(int imgResId) {
        post(() -> {
            if (mAppInfoContainer != null) mAppInfoContainer.setVisibility(View.INVISIBLE);
            setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_es_default_bg));
            if (mLoadingView != null) mLoadingView.setVisibility(View.GONE);
            if (mIvTip != null) mIvTip.setImageResource(imgResId);
        });
    }

    @Override
    public void onEsRenderSuccess() {
        monitor.stopMonitoring();
        cancelDelayTask();
        postDelayed(this::fadeOut, LOAD_SUCCESS_DELAY);
    }

    private void fadeOut() {
        if (mSuspend) return;
        cancelDelayTask();
        if (getParent() == null) return;
        animate().alpha(0)
                .setDuration(FADE_OUT)
                .setListener(new SampleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (getParent() == null) return;
                        ((ViewGroup) getParent()).removeView(EsDefaultCoverView.this);
                    }
                }).start();
    }

    @Override
    public void onEsRenderFailed(EsException exception) {
        if (mLoadingProgressView != null) {
            mLoadingProgressView.setVisibility(View.GONE);
        }
        cancelDelayTask();
        int errCode = exception.getCode();
        switch (errCode) {
            case ERR_OFFLINE://无网
                showTipImage(R.drawable.eskit_pic_cover_offline);
                showTips(exception, R.string.es_cover_tip_offline, errCode);
                break;
            case ERR_TIME_OUT://服务端超时
                showTipImage(R.drawable.eskit_pic_cover_offline);
                showTips(exception, R.string.es_cover_tip_load_too_long, errCode);
                break;
            case ERR_SERVER://服务端出错
            case ERR_INFO://获取包信息失败
            case ERR_UNKNOWN://服务端其它错误
                showTipImage(R.drawable.eskit_pic_cover_other);
                int reasonCode = exception.getReasonCode();
                if (reasonCode == 0) {
                    showTips(exception, R.string.es_cover_tip_load_server_err, errCode);
                } else {
                    suspend(exception.getMessage() + " " + exception.getReasonCode());
                    ToastUtils.showLong(exception.getMessage());
                }
                break;
            case ERR_DOWNLOAD_INFO://下载信息不全
            case ERR_DOWNLOAD://下载失败
                showTipImage(R.drawable.eskit_pic_cover_other);
                showTips(exception, R.string.es_cover_tip_download_err, errCode);
                break;
            case ERR_DEVSERVER://连接DevServer失败
                showTips(exception, R.string.es_cover_tip_debug_err, errCode);
                break;
            case ERR_BRIDGE://JsBridge出错
            case ERR_WRONG_STATE://引擎状态不对
            case ERR_INIT_EXCEPTION://加载代码出错
            case ERR_DECRYPT://解密出错
            case ERR_MD5://MD5不匹配
            case ERR_UNZIP://解压失败
            case ERR_ASSET://Assets加载失败
                showTipImage(R.drawable.eskit_pic_cover_other);
                showTips(exception, R.string.es_cover_tip_start_err, errCode);
                break;
            case ERR_VER_MATCH_FAIL:
                showTipImage(R.drawable.eskit_pic_cover_other);
                if (!PluginUtils.isClientHandlePluginUpgrade()) {
                    showTips(exception, R.string.es_cover_tip_sdk_match_err, errCode);
                } else {
                    showTips("当前引擎版本过低 请点击升级");
                    showUpgradeButton(exception.getData());
                }
                break;
            default://未知错误
                showTipImage(R.drawable.eskit_pic_cover_other);
                showTips(exception, R.string.es_cover_tip_start_unknown, errCode);
                break;
        }
    }

    private void showTips(EsException exception, int tipResId, int errCode) {
        if (mTvTip == null) return;
        String tip;
        if (exception != null && exception.getMessage() != null) {
            tip = exception.getErrorMessage();
        } else {
            tip = getResources().getString(tipResId);
        }

        if (errCode != 0) {
            tip += " " + errCode;
        }
        showTips(tip);
    }

    private void showTips(final String msg) {
        if (mTvTip == null) return;
        post(() -> mTvTip.setText(msg));
    }

    private void showUpgradeButton(EsMap data) {
        post(() -> {

            View.OnClickListener onClick = v -> {
                if (v == mBtnUpgradeOkView) {
                    dealWithBaseVersionLower(data);
                } else if (v == mBtnUpgradeCancelView) {
                    finish();
                }
            };

            mBtnUpgradeOkView.setVisibility(View.VISIBLE);
            mBtnUpgradeCancelView.setVisibility(View.VISIBLE);
            mBtnUpgradeOkView.requestFocus();

            mBtnUpgradeOkView.setOnClickListener(onClick);
            mBtnUpgradeCancelView.setOnClickListener(onClick);
        });
    }

    private void dealWithBaseVersionLower(EsMap data) {
        if (!PluginUtils.IS_PLUGIN_MODE) return;
        if (data == null) return;
        IEsCheckPluginUpgrade checkPluginUpgrade = ESAbilityProvider.get().getAbility(IEsCheckPluginUpgrade.NAME);
        if (checkPluginUpgrade == null) return;
//        final double curVer = data.getDouble("sdkVer");
        final double minVer = data.getDouble("sdkRequireDouble");
        Executors.get().execute(() -> {
            try {
                requestBaseUpgrade(checkPluginUpgrade, minVer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void requestBaseUpgrade(IEsCheckPluginUpgrade checkPluginUpgrade, double minVer) {
        checkPluginUpgrade.checkUpgrade(minVer, new IEsCheckPluginUpgrade.Callback() {
            public void onFindUpgrade() {
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(String msg) {
                ToastUtils.showLong(msg);
            }
        });
    }

    private void finish() {
        post(() -> {
            Context context = getContext();
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public void suspend(String msg) {
        mSuspend = true;
        showTips(msg);
    }

    @Override
    public void unSuspend() {
        mSuspend = false;
        fadeOut();
    }

    //endregion


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mOnLoadTimeoutTask);
        removeCallbacks(mShowUploadLogTipTask);
    }
}

interface ICoverInfo {
    /**
     * 获取封面信息  name/icon
     **/
    Pair<String, String> getCoverInfo(EsMap params) throws Exception;

    ICoverInfo FROM_API = params -> EsContext.get().getEsApiAdapter().fetchAppInfo(params);

    ICoverInfo FROM_NEXUS = params -> {

        String repo = params.getString(Constants.K_APP_REPO);
        String pkg = params.getString(Constants.K_APP_PACKAGE);
        String ver = params.getString(Constants.K_APP_VERSION);

        NexusRepo nexus = NexusRepo.from(repo).withPackage(pkg);

        if (TextUtils.isEmpty(ver)) {
            JSONObject json = requestJson(nexus.getOuterMetaUrl());
            ver = json.optString(Constants.Nexus.Meta.K_LATEST);
        }

        JSONObject json = requestJson(nexus.getInnerMetaUrl(ver));

        String appName = json.has(Constants.Nexus.Meta.K_APP_NAME_V2) ? json.getString(Constants.Nexus.Meta.K_APP_NAME_V2) : json.getString(Constants.Nexus.Meta.K_APP_NAME_V1);
        return new Pair<>(appName, nexus.getInnerUrl(ver, json.optString(Constants.Nexus.Meta.K_APP_ICON)));
    };
}
