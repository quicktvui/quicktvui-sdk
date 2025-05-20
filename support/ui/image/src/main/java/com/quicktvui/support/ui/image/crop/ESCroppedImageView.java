package com.quicktvui.support.ui.image.crop;

import static android.graphics.Bitmap.DENSITY_NONE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.ui.image.canvas.ESPaint;
import com.quicktvui.support.ui.image.canvas.ESPaintAction;
import com.quicktvui.support.ui.image.canvas.ESPath;
import com.quicktvui.support.ui.image.canvas.ESPathAction;
import com.quicktvui.support.ui.image.canvas.PorterDuffUtils;
import com.sunrain.toolkit.utils.log.L;

import java.util.List;


public class ESCroppedImageView extends ImageView implements IEsComponentView {

    public static final String EVENT_PROP_URL = "url";
    public static final String EVENT_PROP_CROPPED_IMAGE_X = "x";
    public static final String EVENT_PROP_CROPPED_IMAGE_Y = "y";
    public static final String EVENT_PROP_CROPPED_IMAGE_LEFT = "left";
    public static final String EVENT_PROP_CROPPED_IMAGE_TOP = "top";
    public static final String EVENT_PROP_CROPPED_IMAGE_RIGHT = "right";
    public static final String EVENT_PROP_CROPPED_IMAGE_BOTTOM = "bottom";

    public static final String EVENT_PROP_CROPPED_IMAGE_WIDTH = "width";
    public static final String EVENT_PROP_CROPPED_IMAGE_HEIGHT = "height";

    public static final String EVENT_PROP_BITMAP_WIDTH = "resourceWidth";
    public static final String EVENT_PROP_BITMAP_HEIGHT = "resourceHeight";

    public enum Events {
        EVENT_ON_LOAD_BITMAP_SUCCESS("onLoadSuccess"),
        EVENT_ON_LOAD_BITMAP_ERROR("onLoadError"),
        EVENT_ON_CROP_BITMAP_SUCCESS("onCropSuccess"),
        EVENT_ON_CROP_BITMAP_ERROR("onCropError");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public Bitmap bitmap;

    public ESCroppedImageView(Context context) {
        super(context);
        setScaleType(ScaleType.CENTER_CROP);
    }

    /**
     * 加载地址
     */
    public void load(String url) {
        //
        Glide.with(getContext())
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap = resource;

                        if (resource != null) {
                            EsMap eventMap = new EsMap();
                            eventMap.pushString(EVENT_PROP_URL, url);
                            eventMap.pushInt(EVENT_PROP_BITMAP_WIDTH, resource.getWidth());
                            eventMap.pushInt(EVENT_PROP_BITMAP_HEIGHT, resource.getHeight());
                            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_LOAD_BITMAP_SUCCESS.toString(), eventMap);
                        } else {
                            EsMap eventMap = new EsMap();
                            eventMap.pushString(EVENT_PROP_URL, url);
                            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_LOAD_BITMAP_ERROR.toString(), eventMap);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        EsMap eventMap = new EsMap();
                        eventMap.pushString(EVENT_PROP_URL, url);
                        EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_LOAD_BITMAP_ERROR.toString(), eventMap);
                    }
                });
    }

    /**
     * 裁剪
     */
    public void crop(int pieceWidth, int pieceHeight, int xCoordinate, int yCoordinate, List<ESPath> pathList, List<ESPaint> paintList) {
        if (this.bitmap == null) {
            EsMap eventMap = new EsMap();
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_CROP_BITMAP_ERROR.toString(), eventMap);
            return;
        }
        cropAndDrawBitmap(this.bitmap, pieceWidth, pieceHeight, xCoordinate, yCoordinate, pathList, paintList);
    }

    /**
     * 绘制
     */
    public void draw(int width,
                     int height,
                     List<ESPath> pathList,
                     List<ESPaint> paintList) {
        Bitmap puzzlePiece = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Path path = new Path();
        Canvas canvas = new Canvas(puzzlePiece);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        drawPathList(path, pathList);
        drawPaintList(paintList, canvas, null, path);

        setImageBitmap(puzzlePiece);
    }

    private void cropAndDrawBitmap(Bitmap croppedBitmap,
                                   int pieceWidth,
                                   int pieceHeight,
                                   int xCoordinate,
                                   int yCoordinate,
                                   List<ESPath> pathList,
                                   List<ESPaint> paintList) {
        if (L.DEBUG) {
            L.logD("#-----initCroppedBitmap---->>>" + "\n"
                    + "--->>croppedBitmap:" + croppedBitmap.getDensity() + "\n"
                    + "--->>densityDpi:" + getResources().getDisplayMetrics().densityDpi + "\n"
                    + "--->>scaledDensity:" + getResources().getDisplayMetrics().scaledDensity + "\n"
                    + "--->>croppedBitmapWidth:" + croppedBitmap.getWidth() + "\n"
                    + "--->>croppedBitmapHeight:" + croppedBitmap.getHeight() + "\n"
                    + "--->>imageViewWidth:" + getWidth() + "\n"
                    + "--->>imageViewHeight:" + getHeight() + "\n"
                    + "--->>pieceWidth:" + pieceWidth + "\n"
                    + "--->>pieceHeight:" + pieceHeight + "\n"
                    + "--->>xCoordinate:" + xCoordinate + "\n"
                    + "--->>yCoordinate:" + yCoordinate + "\n"
            );
        }
        Bitmap pieceBitmap = Bitmap.createBitmap(croppedBitmap, xCoordinate, yCoordinate, pieceWidth, pieceHeight);
        pieceBitmap.setDensity(DENSITY_NONE);

        if (L.DEBUG) {
            L.logD("#-----pieceBitmap---->>>" + "\n"
                    + "--->>pieceBitmapDensity:" + pieceBitmap.getDensity() + "\n"
                    + "--->>pieceBitmapWidth:" + pieceBitmap.getWidth() + "\n"
                    + "--->>pieceBitmapHeight:" + pieceBitmap.getHeight() + "\n"
            );
        }
        if (paintList == null || paintList.size() <= 0) {
            setImageBitmap(pieceBitmap);
            sendCropSuccessEvent();
            return;
        }

        Bitmap puzzlePiece = Bitmap.createBitmap(pieceWidth, pieceHeight, Bitmap.Config.ARGB_8888);
        Path path = new Path();
        Canvas canvas = new Canvas(puzzlePiece);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        drawPathList(path, pathList);
        drawPaintList(paintList, canvas, pieceBitmap, path);
        setImageBitmap(puzzlePiece);
        //
        sendCropSuccessEvent();
    }

    private void sendCropSuccessEvent() {
        EsMap eventMap = new EsMap();
        int[] location = new int[2];
        getLocationOnScreen(location);
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_X, location[0]);
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_Y, location[1]);
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_LEFT, getLeft());
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_TOP, getTop());
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_RIGHT, getRight());
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_BOTTOM, getBottom());
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_WIDTH, getWidth());
        eventMap.pushInt(EVENT_PROP_CROPPED_IMAGE_HEIGHT, getHeight());
        if (L.DEBUG) {
            L.logD("#-----sendCropSuccessEvent---->>>\n" + eventMap);
        }

        EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_CROP_BITMAP_SUCCESS.toString(), eventMap);
    }

    private void drawPathList(Path path, List<ESPath> pathList) {
        if (pathList != null && pathList.size() > 0) {
            for (ESPath esPath : pathList) {
                switch (esPath.action) {
                    case ESPathAction.ES_PATH_ACTION_MOVE:
                        path.moveTo(esPath.x1, esPath.y1);
                        break;
                    case ESPathAction.ES_PATH_ACTION_LINE_TO:
                        path.lineTo(esPath.x1, esPath.y1);
                        break;
                    case ESPathAction.ES_PATH_ACTION_CUBIC_TO:
                        path.cubicTo(esPath.x1, esPath.y1, esPath.x2, esPath.y2, esPath.x3, esPath.y3);
                        break;
                    case ESPathAction.ES_PATH_ACTION_ARC_TO:
                        RectF rectF = new RectF(esPath.x1, esPath.y1, esPath.x2, esPath.y2);
                        path.arcTo(rectF, esPath.startAngle, esPath.sweepAngle, esPath.forceMoveTo);
                        break;
                    case ESPathAction.ES_PATH_ACTION_QUAD_TO:
                        path.quadTo(esPath.x1, esPath.y1, esPath.x2, esPath.y2);
                        break;
                    case ESPathAction.ES_PATH_ACTION_CLOSE:
                        path.close();
                        break;
                }
            }
        }
    }

    private void drawPaintList(List<ESPaint> paintList, Canvas canvas, Bitmap bitmap, Path path) {
        if (paintList != null && paintList.size() > 0) {
            for (ESPaint esPaint : paintList) {
                switch (esPaint.getAction()) {
                    case ESPaintAction.ES_PAINT_ACTION_PATH: {
                        if (path != null) {
                            Paint paint = new Paint();
                            paint.setColor(esPaint.getColor());
                            if (esPaint.getStyle() == 0) {
                                paint.setStyle(Paint.Style.FILL);
                            } else if (esPaint.getStyle() == 1) {
                                paint.setStyle(Paint.Style.STROKE);
                            } else if (esPaint.getStyle() == 2) {
                                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            }
                            paint.setStrokeWidth(esPaint.getStrokeWidth());
                            canvas.drawPath(path, paint);
                        }
                    }
                    break;
                    case ESPaintAction.ES_PAINT_ACTION_BITMAP: {
                        if (bitmap != null) {
                            Paint bitmapPaint = new Paint();
                            bitmapPaint.setColor(esPaint.getColor());
                            if (esPaint.getStyle() == 0) {
                                bitmapPaint.setStyle(Paint.Style.FILL);
                            } else if (esPaint.getStyle() == 1) {
                                bitmapPaint.setStyle(Paint.Style.STROKE);
                            } else if (esPaint.getStyle() == 2) {
                                bitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            }
                            bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuffUtils.intToMode(esPaint.getMode())));
                            canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void release() {
        try {
            bitmap = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
