package quicktvui.support.lottie.animation;

import static quicktvui.support.lottie.utils.MiscUtils.clamp;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.LocaleList;

import android.support.annotation.NonNull;

import quicktvui.support.lottie.utils.MiscUtils;

/**
 * Custom paint that doesn't set text locale.
 * It takes ~1ms on initialization and isn't needed so removing it speeds up
 * setComposition.
 */
public class LPaint extends Paint {
  public LPaint() {
    super();
  }

  public LPaint(int flags) {
    super(flags);
  }

  public LPaint(PorterDuff.Mode porterDuffMode) {
    super();
    setXfermode(new PorterDuffXfermode(porterDuffMode));
  }

  public LPaint(int flags, PorterDuff.Mode porterDuffMode) {
    super(flags);
    setXfermode(new PorterDuffXfermode(porterDuffMode));
  }

  @Override
  public void setTextLocales(@NonNull LocaleList locales) {
    // Do nothing.
  }

  /**
   * Overrides {@link Paint#setAlpha(int)} to avoid
   * unnecessary {@link android.graphics.ColorSpace.Named ColorSpace$Named[] }
   * allocations when calling this method in Android 29 or lower.
   *
   * @param alpha set the alpha component [0..255] of the paint's color.
   */
  @Override
  public void setAlpha(int alpha) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
      int color = getColor();
      setColor((MiscUtils.clamp(alpha, 0, 255) << 24) | (color & 0xFFFFFF));
    } else {
      super.setAlpha(MiscUtils.clamp(alpha, 0, 255));
    }
  }
}
