package com.quicktvui.support.core.component.qr;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.thread.Executors;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create by weipeng on 2022/03/08 20:12
 */
@ESKitAutoRegister
public class QrCodeComponent implements IEsComponent<QrCodeComponent.QrImageView> {

    @Override
    public QrImageView createView(Context context, EsMap params) {
        return new QrImageView(context);
    }

    @EsComponentAttribute
    public void content(QrImageView view, String content) {
        view.setQrContent(content);
    }

    @EsComponentAttribute
    public void optimize(QrImageView view, boolean use) {
        view.setUseOptimization(use);
    }

    @Override
    public void dispatchFunction(QrImageView view, String functionName, EsArray params, EsPromise promise) {
        switch (functionName) {
            //getVersion
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                try {
                    map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
                    map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                promise.resolve(map);
                break;
        }
    }

    @Override
    public void destroy(QrImageView view) {
        if (L.DEBUG) L.logD("destroy view: " + view);
    }

    @SuppressLint("AppCompatCustomView")
    public static final class QrImageView extends ImageView implements IEsComponentView {

        private Bitmap mQrBitmap;
        private String mText = "";
        private boolean mUseOptimization = false;

        private final Handler mHandler = new Handler();

        public QrImageView(Context context) {
            this(context, null);
        }

        public QrImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public QrImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public QrImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        private void init() {
//        PaintDrawable bgDrawable = new PaintDrawable(Color.WHITE);
//        bgDrawable.setCornerRadius(dp2px(10F));
//        setBackgroundDrawable(bgDrawable);
            setScaleType(ImageView.ScaleType.FIT_XY);
        }

        public void setUseOptimization(boolean use) {
            mUseOptimization = use;
        }

        public QrImageView setBgColor(int color) {
            setBackgroundColor(color);
            return this;
        }

        public QrImageView setInnerPadding(int padding) {
            if (L.DEBUG) L.logI("setInnerPadding padding: " + padding);
            setPadding(padding, padding, padding, padding);
            return this;
        }

        public QrImageView setQrContent(String text) {
            if (L.DEBUG) L.logI("setQrContent text: " + text);
            if (!Objects.equals(text, mText)) {
                mText = text;
                update();
            }
            return this;
        }

        private final AtomicBoolean isCreating = new AtomicBoolean(false);

        public void update() {

            if (isCreating.get() || TextUtils.isEmpty(mText)) {
                mHandler.postDelayed(this::update, 200);
                return;
            }
            final int size = mUseOptimization ? (int) (getWidth() * 0.6F) : getWidth();
            if (size < 10) {
                mHandler.postDelayed(this::update, 200);
                return;
            }

            isCreating.set(true);
            Bitmap lastBitmap = mQrBitmap;

            Executors.get().execute(() -> {
                L.logDF("create qr bitmap, size:" + size);
                mQrBitmap = QrCodeFactory.createQRImage(mText, size, size, 0, 0);
                if (mQrBitmap != null) {
                    L.logDF("prepare set qr");
                    mHandler.post(() -> {
                        L.logDF("set qr");
                        setImageBitmap(mQrBitmap);

                        if (lastBitmap != null && !lastBitmap.isRecycled()) {
                            lastBitmap.recycle();
                        }
                        isCreating.set(false);
                    });
                }
            });
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (L.DEBUG) L.logI("onAttachedToWindow");
            update();
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (L.DEBUG) L.logI("onDetachedFromWindow");
            mHandler.removeCallbacksAndMessages(null);
            setImageBitmap(null);
            if (mQrBitmap != null && !mQrBitmap.isRecycled()) mQrBitmap.recycle();
            mQrBitmap = null;
        }

    }

}
