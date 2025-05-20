## 自定义Border方式说明：

1.创建自定义Drawable类————ESBorderDrawable，继承BaseBorderDrawable类，根据个人需求实现各种效果；
```java
class ESBorderDrawable extends BaseBorderDrawable{
    @Override
    public void draw(@NonNull Canvas canvas) {
        //doSomething
    }
}
```

2.创建Border工厂类————ESBorderFactory，实现BaseBorderDrawableProvider<BaseBorderDrawable>接口，重写create方法，
返回一个自定义的drawable实例，例如ESBorderDrawable;
```java
class ESBorderFactory implements BaseBorderDrawableProvider<BaseBorderDrawable>{
    @Override
    public ESBorderFactory create() {
        return new ESBorderDrawable();
    }
}
```

3.注入ESBorderFactory，替换默认边框drawable改为自定义drawable；
示例：EsManager.get().setBorderDrawableProvider(new ESBorderFactory());

# BaseBorderDrawable相关接口方法注释说明：
```java
class ESBorderDrawable extends BaseBorderDrawable{

    //设置边框显示或隐藏
    @Override
    public void setBorderVisible(boolean visible) {}

    //设置边框颜色
    @Override
    public void setBorderColor(int borderColor) {}

    //设置边框圆角
    @Override
    public void setBorderCorner(float roundCorner) {}

    //设置边框宽度
    @Override
    public void setBorderWidth(int width) {}

    //设置默认黑色内边框显示或隐藏
    @Override
    public void setBlackRectEnable(boolean blackRectEnable) {}

    //HippyImageView的draw回调
    @Override
    public void onDraw(Canvas canvas) {}

    //HippyImageView的onSizeChanged回调
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {}

    //HippyImageView的onFocusChanged回调
    @Override
    public void onFocusChanged(View view, boolean visible) {}

    //HippyImageView的drawableStateChanged的回调
    @Override
    public void onDrawableStateChanged(View view, boolean focused) {}

    //HippyImageView的onDetachedFromWindow事件回调
    @Override
    public void onDetachedFromWindow(View view) {}
}
```
