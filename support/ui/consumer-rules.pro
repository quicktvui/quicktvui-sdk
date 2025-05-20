

-keep class * extends com.quicktvui.support.ui.item.widget.BuilderWidget {
    <init>(...);
}

-keep class * extends com.quicktvui.support.ui.item.widget.BuilderWidget$Builder {
    <init>(...);
}

-keep class com.quicktvui.support.ui.selectseries.ESSelectSeriesController {*;}

-keep class androidx.viewpager2.widget.ViewPager2{
    *** mRecyclerView;
    *** mCurrentItem;
    *** mAccessibilityProvider;
    *** mScrollEventAdapter;
}

-keep class com.extscreen.support.viewpager2.widget.ViewPager2{
    *** mRecyclerView;
    *** mCurrentItem;
    *** mAccessibilityProvider;
    *** mScrollEventAdapter;
}

-keep class android.support.v7.widget.LinearSmoothScroller {
    *** mDecelerateInterpolator;
}

-keep class androidx.recyclerview.widget.LinearSmoothScroller{
    *** mDecelerateInterpolator;
}

-keep class androidx.viewpager2.widget.ViewPager2$PageAwareAccessibilityProvider {
    *** onSetNewCurrentItem(...);
}

-keep class com.extscreen.support.viewpager2.widget.ViewPager2$AccessibilityProvider{
    *** onSetNewCurrentItem(...);
}

-keep class com.extscreen.support.viewpager2.widget.ScrollEventAdapter{
    *** getRelativeScrollPosition(...);
    *** notifyProgrammaticScroll(...);
}