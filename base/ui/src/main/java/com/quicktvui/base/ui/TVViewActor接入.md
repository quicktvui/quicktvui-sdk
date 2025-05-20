
## view添加焦点等TVView的方法
按需接管view中的以下方法
```
//通知尺寸变化 必须
onSizeChanged
//通知焦点变化 必须
onFocusChanged
//通知view被移除
onDetachedFromWindow
//通知view被添加
onAttachedToWindow
//添加焦点
addFocusables
//寻焦
focusSearch
//绘制焦点框、背景
draw
setFocusBorderType
drawableStateChanged
```

示例：

```java
public class TestView extends View implements TVViewActorHost{

  TVViewActor mTVActor;

  public TestView(Context context) {
    super(context);
    mTVActor = new TVViewActor(this);
  }

  @Override
  public TVViewActor getTVActor() {
    return mTVActor;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mTVActor.onSizeChanged(w, h, oldw, oldh);
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    mTVActor.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    mTVActor.draw(canvas);
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    mTVActor.drawableStateChanged();
  }
}
```


