package quicktvui.support.lottie.model;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.PointF;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

@RestrictTo(LIBRARY)
public class DocumentData {

  public enum Justification {
    LEFT_ALIGN,
    RIGHT_ALIGN,
    CENTER
  }

  public String text;
  public String fontName;
  public float size;
  public Justification justification;
  public int tracking;
  /** Extra space in between lines. */
  public float lineHeight;
  public float baselineShift;
  @ColorInt public int color;
  @ColorInt public int strokeColor;
  public float strokeWidth;
  public boolean strokeOverFill;
  @Nullable public PointF boxPosition;
  @Nullable public PointF boxSize;


  public DocumentData(String text, String fontName, float size, Justification justification, int tracking,
      float lineHeight, float baselineShift, @ColorInt int color, @ColorInt int strokeColor,
      float strokeWidth, boolean strokeOverFill, PointF boxPosition, PointF boxSize) {
    set(text, fontName, size, justification, tracking, lineHeight, baselineShift, color, strokeColor, strokeWidth, strokeOverFill, boxPosition, boxSize);
  }

  public DocumentData() {
  }

  public void set(String text, String fontName, float size, Justification justification, int tracking,
      float lineHeight, float baselineShift, @ColorInt int color, @ColorInt int strokeColor,
      float strokeWidth, boolean strokeOverFill, PointF boxPosition, PointF boxSize) {
    this.text = text;
    this.fontName = fontName;
    this.size = size;
    this.justification = justification;
    this.tracking = tracking;
    this.lineHeight = lineHeight;
    this.baselineShift = baselineShift;
    this.color = color;
    this.strokeColor = strokeColor;
    this.strokeWidth = strokeWidth;
    this.strokeOverFill = strokeOverFill;
    this.boxPosition = boxPosition;
    this.boxSize = boxSize;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = text.hashCode();
    result = 31 * result + fontName.hashCode();
    result = (int) (31 * result + size);
    result = 31 * result + justification.ordinal();
    result = 31 * result + tracking;
    temp = Float.floatToRawIntBits(lineHeight);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + color;
    return result;
  }
}
