package quicktvui.support.lottie.manager;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import android.support.annotation.Nullable;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import quicktvui.support.lottie.ImageAssetDelegate;
import quicktvui.support.lottie.LottieImageAsset;
import quicktvui.support.lottie.utils.Logger;
import quicktvui.support.lottie.utils.Utils;

public class ImageAssetManager {
  private static final Object bitmapHashLock = new Object();
  @Nullable private final Context context;
  private final String imagesFolder;
  @Nullable private ImageAssetDelegate delegate;
  private final Map<String, LottieImageAsset> imageAssets;

  public ImageAssetManager(Drawable.Callback callback, String imagesFolder,
      ImageAssetDelegate delegate, Map<String, LottieImageAsset> imageAssets) {
    if (!TextUtils.isEmpty(imagesFolder) && imagesFolder.charAt(imagesFolder.length() - 1) != '/') {
      this.imagesFolder = imagesFolder + '/';
    } else {
      this.imagesFolder = imagesFolder;
    }
    this.imageAssets = imageAssets;
    setDelegate(delegate);
    if (!(callback instanceof View)) {
      context = null;
      return;
    }

    context = ((View) callback).getContext().getApplicationContext();
  }

  public void setDelegate(@Nullable ImageAssetDelegate assetDelegate) {
    this.delegate = assetDelegate;
  }

  /**
   * Returns the previously set bitmap or null.
   */
  @Nullable public Bitmap updateBitmap(String id, @Nullable Bitmap bitmap) {
    if (bitmap == null) {
      LottieImageAsset asset = imageAssets.get(id);
      Bitmap ret = asset.getBitmap();
      asset.setBitmap(null);
      return ret;
    }
    Bitmap prevBitmap = imageAssets.get(id).getBitmap();
    putBitmap(id, bitmap);
    return prevBitmap;
  }

  @Nullable public LottieImageAsset getImageAssetById(String id) {
    return imageAssets.get(id);
  }

  @Nullable public Bitmap bitmapForId(String id) {
    LottieImageAsset asset = imageAssets.get(id);
    if (asset == null) {
      return null;
    }

    Bitmap bitmap = asset.getBitmap();
    if (bitmap != null) {
      return bitmap;
    }

    if (delegate != null) {
      bitmap = delegate.fetchBitmap(asset);
      if (bitmap != null) {
        putBitmap(id, bitmap);
      }
      return bitmap;
    }
    Context context = this.context;
    if (context == null) {
      // If there is no context, the image has to be embedded or provided via
      // a delegate.
      return null;
    }

    String filename = asset.getFileName();
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inScaled = true;
    opts.inDensity = 160;

    if (filename.startsWith("data:") && filename.indexOf("base64,") > 0) {
      // Contents look like a base64 data URI, with the format data:image/png;base64,<data>.
      byte[] data;
      try {
        data = Base64.decode(filename.substring(filename.indexOf(',') + 1), Base64.DEFAULT);
      } catch (IllegalArgumentException e) {
        Logger.warning("data URL did not have correct base64 format.", e);
        return null;
      }
      bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
      return putBitmap(id, bitmap);
    }

    InputStream is;
    try {
      if (TextUtils.isEmpty(imagesFolder)) {
        throw new IllegalStateException("You must set an images folder before loading an image." +
            " Set it with LottieComposition#setImagesFolder or LottieDrawable#setImagesFolder");
      }
      is = context.getAssets().open(imagesFolder + filename);
    } catch (IOException e) {
      Logger.warning("Unable to open asset.", e);
      return null;
    }

    try {
      bitmap = BitmapFactory.decodeStream(is, null, opts);
    } catch (IllegalArgumentException e) {
      Logger.warning("Unable to decode image `" + id + "`.", e);
      return null;
    }
    if (bitmap == null) {
      Logger.warning("Decoded image `" + id + "` is null.");
      return null;
    }
    bitmap = Utils.resizeBitmapIfNeeded(bitmap, asset.getWidth(), asset.getHeight());
    return putBitmap(id, bitmap);
  }

  public boolean hasSameContext(Context context) {
    Context contextToCompare = this.context instanceof Application ? context.getApplicationContext() : context;
    return contextToCompare == this.context;
  }

  private Bitmap putBitmap(String key, @Nullable Bitmap bitmap) {
    synchronized (bitmapHashLock) {
      imageAssets.get(key).setBitmap(bitmap);
      return bitmap;
    }
  }
}
