package com.quicktvui.support.core.component.qr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sunrain.toolkit.utils.log.L;

import java.util.Hashtable;

public class QrCodeFactory {
    // 需要插图图片的大小 这里设定为40*40
//    private static final int IMAGE_HALF_WIDTH = 40;

    public static Bitmap createQRImage(String url, int widthPx, int heightPx, int marginPx, int roundPx) {
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            if (L.DEBUG)
                L.logI("start create");
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, marginPx);
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, widthPx, heightPx, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Rect bounds = new Rect();
            bounds.left = Integer.MAX_VALUE;
            bounds.top = Integer.MAX_VALUE;

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    if (bitMatrix.get(w, h)) {
                        if (bounds.left > w) bounds.left = w;
                        if (bounds.top > h) bounds.top = h;
                    }
                }
            }

            for (int h = height - 1; h >= 0; h--) {
                for (int w = width - 1; w >= 0; w--) {
                    if (bitMatrix.get(w, h)) {
                        if (bounds.right < w) bounds.right = w;
                        if (bounds.bottom < h) bounds.bottom = h;
                    }
                }
            }

//            SLog.w("目标尺寸:" + widthPx + "x" + heightPx);
//            SLog.w("生成尺寸:" + width + "x" + height);
//            SLog.w("校准尺寸:" + bounds.width() + "x" + bounds.height());
//            SLog.w("RECT:" + bounds);

            int[] pixels = new int[bounds.width() * bounds.height()];
            int x = 0;
            int y = 0;
            for (int h = bounds.top; h < bounds.bottom; h++) {
                for (int w = bounds.left; w < bounds.right; w++) {
                    pixels[y * bounds.width() + x] = bitMatrix.get(w, h) ? 0xff000000 : 0x00ffffff;
                    x++;
                }
                x = 0;
                y++;
            }

            widthPx = bounds.width();
            heightPx = bounds.height();


//            int[] pixels = new int[widthPx * heightPx];
//            //下面这里按照二维码的算法，逐个生成二维码的图片，
//            //两个for循环是图片横列扫描的结果
//            for (int y = 0; y < heightPx; y++) {
//                for (int x = 0; x < widthPx; x++) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * widthPx + x] = 0xff000000;
//                    } else {
//                        pixels[y * widthPx + x] = 0x00ffffff;
//                    }
//                }
//            }

            bitMatrix.clear();
            hints.clear();

            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPx, 0, 0, widthPx, heightPx);

            int max = pixels.length;
            for (int i = 0; i < max; i++) {
                pixels[i] = 0;
            }

            //显示到一个ImageView上面
//            sweepIV.setImageBitmap(bitmap);
            return roundPx > 0 ? toRoundCorner(bitmap, roundPx) : bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //要转换的地址或字符串,可以是中文
//    public static Bitmap createQRImage(String url, int widthPx, int heightPx) {
//        try {
//            //判断URL合法性
//            if (url == null || "".equals(url) || url.length() < 1) {
//                return null;
//            }
//            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
//            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//            //图像数据转换，使用了矩阵转换
//            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, widthPx, heightPx, hints);
//            int[] pixels = new int[widthPx * heightPx];
//            //下面这里按照二维码的算法，逐个生成二维码的图片，
//            //两个for循环是图片横列扫描的结果
//            for (int y = 0; y < heightPx; y++) {
//                for (int x = 0; x < widthPx; x++) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * widthPx + x] = 0xff000000;
//                    } else {
//                        pixels[y * widthPx + x] = 0xffffffff;
//                    }
//                }
//            }
//            //生成二维码图片的格式，使用ARGB_8888
//            Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, widthPx, 0, 0, widthPx, heightPx);
//            //显示到一个ImageView上面
////            sweepIV.setImageBitmap(bitmap);
//            return toRoundCorner(bitmap, 10);
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 圆角处理
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }
}
