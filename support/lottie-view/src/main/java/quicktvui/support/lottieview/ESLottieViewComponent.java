package quicktvui.support.lottieview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import android.support.annotation.RequiresApi;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.sunrain.toolkit.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import quicktvui.support.lottie.LottieAnimationView;
import quicktvui.support.lottie.LottieCompositionFactory;
import quicktvui.support.lottie.LottieListener;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
@ESKitAutoRegister
public class ESLottieViewComponent implements IEsComponent<LottieAnimationView> {

    private static final String TAG = ESLottieViewComponent.class.getSimpleName();

    @Override
    public LottieAnimationView createView(Context context, EsMap params) {
        return new LottieAnimationView(context);
    }

    @Override
    public void dispatchFunction(LottieAnimationView view, String eventName, EsArray params, EsPromise promise) {
        switch (eventName) {
            case "lottie_rawRes":
            case "lottie_fileName":
                view.setAnimation(params.getString(0));
                view.playAnimation();
                break;
            case "lottie_localRes":
                String fileUrl = params.getString(0);
                dealLocalJsonFile(fileUrl, view);
                break;
            case "lottie_url":
                String url = params.getString(0);
                view.setAnimationFromUrl(url);
                view.setFailureListener(result -> {
                    ToastUtils.showShort("该链接类型不支持" + result.getLocalizedMessage());
                    Log.e(TAG, result.getLocalizedMessage());
                });
                break;
            case "lottie_cache_url":
                String cacheUrl = params.getString(0);
                String cacheKey = params.getString(1);
                view.setAnimationFromUrl(cacheUrl, cacheKey);
                view.setFailureListener(result -> {
                    ToastUtils.showShort("该链接类型不支持" + result.getLocalizedMessage());
                    Log.e(TAG, result.getLocalizedMessage());
                });
                break;
            case "lottie_fallbackRes":
                view.setFallbackResource(params.getInt(0));
                break;
            case "lottie_autoPlay":
                view.setAutoPlay(params.getBoolean(0));
                break;
            case "lottie_loop":
                if (params.getBoolean(0)) {
                    view.setLottieLoop(params.getBoolean(0));
                }
                break;
            case "lottie_repeatMode":
                view.setRepeatMode(params.getInt(0));
                break;
            case "lottie_repeatCount":
                view.setRepeatCount(params.getInt(0));
                break;
            case "lottie_speed":
                view.setSpeed(Float.parseFloat(params.getString(0)));
                break;
            case "lottie_clipToCompositionBounds":
                view.setClipToCompositionBounds(params.getBoolean(0));
                break;
            case "lottie_defaultFontFileExtension":
                view.setDefaultFontFileExtension(params.getString(0));
                break;
            case "lottie_imageAssetsFolder":
                view.setImageAssetsFolder(params.getString(0));
                break;
            case "lottie_progress":
                boolean hasProgress = false;
                String progress = params.getString(0);
                if (!TextUtils.isEmpty(progress)) {
                    hasProgress = true;
                }
                float floatValue = Float.parseFloat(params.getString(0));
                view.setProgressInternal(floatValue, hasProgress);
                break;
            case "lottie_enableMergePathsForKitKatAndAbove":
                boolean value = params.getBoolean(0);
                view.enableMergePathsForKitKatAndAbove(value);
                break;
            case "lottie_colorFilter":
                String colorRes = params.getString(0);
                view.dealColorFunction(Color.parseColor(colorRes));
                break;
            case "lottie_renderMode":
                int renderModeOrdinal = params.getInt(0);
                view.dealRenderMode(renderModeOrdinal);
                break;
            case "lottie_asyncUpdates":
                int asyncUpdatesOrdinal = params.getInt(0);
                view.dealUpdatesOrdinal(asyncUpdatesOrdinal);
                break;
            case "lottie_ignoreDisabledSystemAnimations":
                boolean valueSys = params.getBoolean(0);
                view.setIgnoreDisabledSystemAnimations(valueSys);
                break;
            case "lottie_useCompositionFrameRate":
                boolean valueFrameRate = params.getBoolean(0);
                view.setUseCompositionFrameRate(valueFrameRate);
                break;
            case "playAnimation":
                view.playAnimation();
                break;
            case "resumeAnimation"://继续动画
                view.resumeAnimation();
                break;
            case "pauseAnimation"://暂停动画
                view.pauseAnimation();
                break;
            case "cancelAnimation":
                view.cancelAnimation();
                break;
            case "cacheComposition":
                boolean cache = params.getBoolean(0);
                view.setCacheComposition(cache);
                break;
            case "removeAllListener":
                view.removeAllListener();
                break;
            case "getDuration":
                long duration = view.getDuration();
                EsMap mapDur = new EsMap();
                mapDur.pushLong("currentDuration", duration);
                promise.resolve(mapDur);
                break;
            case "getFrame":
                int frame = view.getFrame();
                EsMap mapFrame = new EsMap();
                mapFrame.pushInt("currentFrame", frame);
                promise.resolve(mapFrame);
                break;
            case "setFrame":
                view.setFrame(params.getInt(0));
                break;
            case "animationListenerState":
                boolean state = params.getBoolean(0);
                view.setAnimationListenerState(state);
                break;
            case "animationUpdateState":
                boolean stateUpdate = params.getBoolean(0);
                view.setAnimationUpdateState(stateUpdate);
                break;
            default:
                break;
        }
    }

    @EsComponentAttribute
    public void lottie_rawRes(LottieAnimationView view, String rawRes) {
        view.setAnimation(rawRes);
        view.playAnimation();
    }

    @EsComponentAttribute
    public void lottie_fileName(LottieAnimationView view, String fileName) {
        view.setAnimation(fileName);
        view.playAnimation();
    }

    @EsComponentAttribute
    public void lottie_localRes(LottieAnimationView view, String localRes) {
        dealLocalJsonFile(localRes, view);
    }

    @EsComponentAttribute
    public void lottie_url(LottieAnimationView view, String url) {
        view.setAnimationFromUrl(url);
        view.setFailureListener(new LottieListener<Throwable>() {
            @Override
            public void onResult(Throwable result) {
                ToastUtils.showShort("该链接类型不支持" + result.getLocalizedMessage());
                Log.e(TAG, result.getLocalizedMessage());
            }
        });
    }

    @EsComponentAttribute
    public void lottie_fallbackRes(LottieAnimationView view, int res) {
        view.setFallbackResource(res);
    }

    @EsComponentAttribute
    public void lottie_autoPlay(LottieAnimationView view, boolean autoPlay) {
        view.setAutoPlay(autoPlay);
    }

    @EsComponentAttribute
    public void lottie_loop(LottieAnimationView view, boolean loop) {
        view.setLottieLoop(loop);
    }

    @EsComponentAttribute
    public void lottie_repeatMode(LottieAnimationView view, int repeatMode) {
        view.setRepeatMode(repeatMode);
    }

    @EsComponentAttribute
    public void lottie_repeatCount(LottieAnimationView view, int repeatCount) {
        view.setRepeatCount(repeatCount);
    }

    @EsComponentAttribute
    public void lottie_speed(LottieAnimationView view, String speed) {
        view.setSpeed(Float.parseFloat(speed));
    }

    @EsComponentAttribute
    public void lottie_clipToCompositionBounds(LottieAnimationView view, Boolean bound) {
        view.setClipToCompositionBounds(bound);
    }

    @EsComponentAttribute
    public void lottie_defaultFontFileExtension(LottieAnimationView view, String fontName) {
        view.setDefaultFontFileExtension(fontName);
    }

    @EsComponentAttribute
    public void lottie_imageAssetsFolder(LottieAnimationView view, String imageAssetsFolder) {
        view.setImageAssetsFolder(imageAssetsFolder);
    }

    @EsComponentAttribute
    public void lottie_progress(LottieAnimationView view, String progress) {
        boolean hasProgress = false;
        if (!TextUtils.isEmpty(progress)) {
            hasProgress = true;
        }
        float floatValue = Float.parseFloat(progress);
        view.setProgressInternal(floatValue, hasProgress);
    }

    @EsComponentAttribute
    public void lottie_enableMergePathsForKitKatAndAbove(LottieAnimationView view, boolean value) {
        view.enableMergePathsForKitKatAndAbove(value);
    }

    @EsComponentAttribute
    public void lottie_colorFilter(LottieAnimationView view, String colorRes) {
        view.dealColorFunction(Color.parseColor(colorRes));
    }

    @EsComponentAttribute
    public void lottie_renderMode(LottieAnimationView view, int renderModeOrdinal) {
        view.dealRenderMode(renderModeOrdinal);
    }

    @EsComponentAttribute
    public void lottie_asyncUpdates(LottieAnimationView view, int asyncUpdatesOrdinal) {
        view.dealUpdatesOrdinal(asyncUpdatesOrdinal);
    }

    @EsComponentAttribute
    public void lottie_ignoreDisabledSystemAnimations(LottieAnimationView view, boolean valueSys) {
        view.setIgnoreDisabledSystemAnimations(valueSys);
    }

    @EsComponentAttribute
    public void lottie_useCompositionFrameRate(LottieAnimationView view, boolean valueFrameRate) {
        view.setUseCompositionFrameRate(valueFrameRate);
    }

    /**
     * 是否缓存
     *
     * @param view
     * @param cacheComposition
     */
    @EsComponentAttribute
    public void cacheComposition(LottieAnimationView view, boolean cacheComposition) {
        if (view != null) {
            view.setCacheComposition(cacheComposition);
        }
    }

    @EsComponentAttribute
    public void animationListenerState(LottieAnimationView view, boolean state) {
        if (view != null) {
            view.setAnimationListenerState(state);
        }
    }

    @EsComponentAttribute
    public void animationUpdateState(LottieAnimationView view, boolean state) {
        if (view != null) {
            view.setAnimationUpdateState(state);
        }
    }

    private String DealFileUrl(String url) {
        if (url.startsWith("http:") || url.startsWith("https")) {
            return url.replaceAll(" ", "%20");
        }
        if (EsProxy.get().isDebugModel()) {
            if (url.startsWith("file://") || url.startsWith("http://127.0.0.1")) {
                int index = url.indexOf("assets");
                if (index >= 0)
                    return "http://" + EsProxy.get().getDebugServer() + "/" + url.substring(index);
            }
        } else if (url.startsWith("file://")) {
            int index = url.indexOf("assets");
            if (index >= 0) {
                File cacheDir = new File(Objects.requireNonNull(EsProxy.get().getEsAppRuntimePath(this)));
                String localFilePath = "file://" + cacheDir.getAbsolutePath() + "/" + url.substring(index);
                if (cacheDir.getPath().startsWith("/")) {
                    return localFilePath;
                }
                return localFilePath.replace("file://", "file:///android_asset");
            }
        }
        return url.replaceAll(" ", "%20");
    }

    private void dealLocalJsonFile(String fileUrl, LottieAnimationView view) {
        String finalUrl;
        finalUrl = DealFileUrl(fileUrl);
        if (finalUrl.startsWith("http:") || finalUrl.startsWith("https:")) {
            view.setAnimationFromUrl(finalUrl);
            return;
        }
        if (finalUrl.contains("file://")) {
            finalUrl = finalUrl.replaceAll("file://", "/");
        } else if (finalUrl.contains("file:///")) {
            finalUrl = finalUrl.replaceAll("file:///", "/");
        }
        File lottieDir = new File(finalUrl);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(lottieDir);
            LottieCompositionFactory.fromJsonInputStream(fileInputStream, null)
                    .addListener(result -> {
                        view.setComposition(result);
                        view.playAnimation();
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(LottieAnimationView view) {
        view.removeAllListener();
    }
}
