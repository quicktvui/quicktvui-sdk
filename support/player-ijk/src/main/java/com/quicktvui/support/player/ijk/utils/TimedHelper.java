package com.quicktvui.support.player.ijk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quicktvui.support.ijk.base.IjkMediaPlayer;
import com.quicktvui.support.ijk.base.IjkTimedBitmap;
import com.quicktvui.support.player.ijk.setting.Settings;
import com.quicktvui.support.player.manager.utils.ScreenUtils;

public class TimedHelper {
    public static final int SIZE_TYPE_NORMAL = 0;
    public static final int SIZE_TYPE_BIG = 1;
    public static final int SIZE_TYPE_SUPER_BIG = 2;

    // 0 imageview, 1 surface view
    public static int VIEW_TYPE = 0;

    private View imgContainerView;

    private boolean tSEnble = false;
    private SurfaceHolder tSHolder;

    private final int screenWidth;
    private final int screenHeight;
    private final int bottomPadding;
    private final float scale;
    private int lastHeight;

//    private static int sizeType = SIZE_TYPE_NORMAL;
    private static final Map<String, Integer> sizeTypeMap = new HashMap<>();

    public TimedHelper(Context context) {
        screenWidth = ScreenUtils.getScreenWidth(context);
        screenHeight = ScreenUtils.getScreenHeight(context);
        scale = screenWidth / 1920.0f;
        bottomPadding = (int) (30 * scale + 0.5f);
        initView(context);
    }

    private void initView(Context context) {
        if (VIEW_TYPE == 0) {
            imgContainerView = new ImageView(context);
            ImageView imageView = (ImageView) imgContainerView;
//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setPadding(0, 0, 0, bottomPadding);
        } else {
            imgContainerView = new SurfaceView(context);
            SurfaceHolder holder = ((SurfaceView) imgContainerView).getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder holder) {
                    tSHolder = holder;
                    tSEnble = true;
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                    tSEnble = false;
                }
            });
        }
    }

    public View getView() {
        return imgContainerView;
    }

    public void addView(ViewGroup parent) {

        FrameLayout.LayoutParams layoutParams_txt = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);

        imgContainerView.setLayoutParams(layoutParams_txt);

        parent.addView(imgContainerView);
    }

    public void removeView(ViewGroup parent) {
        parent.removeView(imgContainerView);
    }

    public void setBitmap(IjkTimedBitmap bitmap, String appName) {
        if (VIEW_TYPE == 0) {
            ImageView imageView = (ImageView) imgContainerView;
            // 先增加bitmap回收逻辑（不一定有效果），有时间再尝试把bitmap绘制到surfaceView上
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmapBefore = ((BitmapDrawable) drawable).getBitmap();
                if (bitmapBefore != null) {
                    bitmapBefore.recycle();
                }
            }

//            int nowHeight = (int) (bitmap.getHeight() * scale + 0.5f);

            float baseHeight = bitmap.getHeight() * scale;
            int nowHeight;
            Integer integer = sizeTypeMap.get(appName);
            int sizeType = integer == null ? SIZE_TYPE_NORMAL : integer;
            switch (sizeType) {
                case SIZE_TYPE_BIG:
                    nowHeight = (int) (baseHeight + 0.5f);
                    break;
                case SIZE_TYPE_SUPER_BIG:
                    nowHeight = (int) (baseHeight * 1.25f + 0.5f);
                    break;
                default:
                    nowHeight = (int) (baseHeight / 1.25f + 0.5f);
                    break;
            }

            if (nowHeight != lastHeight) {
                imageView.measure(View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(nowHeight + bottomPadding, View.MeasureSpec.EXACTLY));
                imageView.layout(0, screenHeight - nowHeight - bottomPadding, screenWidth, screenHeight);

                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.height = nowHeight + bottomPadding;
                imageView.setLayoutParams(layoutParams);

                lastHeight = nowHeight;
            }

            imageView.setImageBitmap(bitmap.getBitmap());

        } else {
            if (tSEnble) {
                Canvas canvas = tSHolder.lockCanvas();
//                        canvas.drawColor(Color.RED);
                canvas.drawBitmap(bitmap.getBitmap(), 0, 0, null);
                tSHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void timedEnd() {
        if (VIEW_TYPE == 0) {
            ImageView imageView = (ImageView) imgContainerView;
            // 先增加bitmap回收逻辑（不一定有效果），有时间再尝试把bitmap绘制到surfaceView上
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmapBefore = ((BitmapDrawable) drawable).getBitmap();
                if (bitmapBefore != null) {
                    bitmapBefore.recycle();
                }
            }
            imageView.setImageBitmap(null);
        } else {
            Canvas canvas = tSHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            tSHolder.unlockCanvasAndPost(canvas);
        }

    }

    public static void setIjkOption(IjkMediaPlayer ijkMediaPlayer, Settings settings) {

        int timedType = settings.getTimedType();

        // 内嵌字幕总开关
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "subtitle",
                timedType > IjkMediaPlayer.TIMED_CLOSE ? 1 : 0);
        // 图形字幕开关
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "subtitle-bitmap",
                timedType > IjkMediaPlayer.TIMED_ONLY_TEXT ? 1 : 0);
        // 文本字幕开关
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "subtitle-text",
                (timedType > IjkMediaPlayer.TIMED_CLOSE && timedType < IjkMediaPlayer.TIMED_ONLY_BITMAP) ? 1 : 0);

        // 处理字幕默认选择参数
        int subIndex = settings.getSubIndex();
        boolean subChinese = settings.getSubChinese();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "subtitle-default-index", subIndex);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "subtitle-default-chi", subChinese ? 1 : 0);

        // 处理音轨默认选择参数
        int audioIndex = settings.getAudioIndex();
        boolean audioChinese = settings.getAudioChinese();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "audio-default-index", audioIndex);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "audio-default-chi", audioChinese ? 1 : 0);

    }

    public static String formatTimedText(String input) {
        if (TextUtils.isEmpty(input))
            return input;
        String pattern = "\\{.*?\\}";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.replaceAll("");
    }

    public static void setBitmapSubSizeType(String appName, int type) {
        if (type >= 0 && type <= 2) {
            sizeTypeMap.put(appName, type);
        }
    }
}
