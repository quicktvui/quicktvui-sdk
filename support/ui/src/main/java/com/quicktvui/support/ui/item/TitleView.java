package com.quicktvui.support.ui.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.hippyext.views.fastlist.PostHandlerView;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.quicktvui.support.ui.ScreenUtils;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.views.list.HippyRecycler;

import com.quicktvui.support.ui.R;


/**
 * Created by XingRuGeng on 2021/12/16
 * Desc:TitleView
 */
class TitleView extends HippyBaseFrameLayout implements TVListView.PostContentHolder, HippyRecycler, IEsComponentView {
    //, TVListView.PostContentHolder, HippyRecycler
    private static final String TAG = TitleView.class.getSimpleName();
    private FrameLayout titleRootView;
    private RelativeLayout titleNormalView;
    //主标题
    TextView mainTitle03;
    TextView mainTitle;
    TextView floatTitle;
    // 默认样式
    private String titleStyle;//标题样式
    private SpannableString sStr;
    private String mainColor = "#ffffff";
    private String floatColor = "#ffffff";
    private boolean isSetJson = false;
    private PostHandlerView mPostHandler;
    static int POST_CATE_HIPPY_MAP = 11;


    static int[] POST_CATEGORIES = new int[]{POST_CATE_HIPPY_MAP};
    EsMap map;

    public TitleView(Context context) {
        super(context);
        init();
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RelativeLayout getTitleNormalView() {
        return titleNormalView;
    }
    public TextView getMainTitle03(){
        return mainTitle03;
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_titles_view, this);
        titleRootView = view.findViewById(R.id.titleRootView);
        titleNormalView = view.findViewById(R.id.titleNormalView);
        mainTitle = view.findViewById(R.id.mainTitle);
        mainTitle03 = view.findViewById(R.id.mainTitle03);
        floatTitle = view.findViewById(R.id.floatTitle);
    }

    public void isShowTitle(boolean isShow) {
        if (titleRootView == null) {
            return;
        }
        if (isShow) {
            mainTitle03.setVisibility(View.VISIBLE);
            titleRootView.setVisibility(View.VISIBLE);
        } else {
            mainTitle03.setVisibility(View.GONE);
            titleRootView.setVisibility(View.GONE);
        }
    }

    public void isFocus(boolean focus) {
        if (titleNormalView == null) {
            return;
        }
        if (!TextUtils.isEmpty(titleStyle) && ("0".equals(titleStyle) || "1".equals(titleStyle) || "2".equals(titleStyle) || "3".equals(titleStyle))) {
            if (focus) {
                if (isSetJson){
                    titleNormalView.setVisibility(View.INVISIBLE);
                    mainTitle03.setVisibility(View.INVISIBLE);
                }else{
                    titleNormalView.setVisibility(View.GONE);
                    mainTitle03.setVisibility(View.GONE);
                }

            } else {
                if (!TextUtils.isEmpty(titleStyle) && !"3".equals(titleStyle)){
                    if ("0".equals(titleStyle)){
                        mainTitle03.setVisibility(View.VISIBLE);
                        if (isSetJson){
                            titleNormalView.setVisibility(View.INVISIBLE);
                        }else{
                            titleNormalView.setVisibility(View.GONE);
                        }

                    }else{
                        if (isSetJson){
                            mainTitle03.setVisibility(View.INVISIBLE);
                        }else{
                            mainTitle03.setVisibility(View.GONE);
                        }
                        titleNormalView.setVisibility(View.VISIBLE);
                    }

                }

            }
        }

    }

    /**
     * 设置主标题宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setMainTitleW_H(int width, int height) {
        if (mainTitle == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mainTitle.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mainTitle.setLayoutParams(lp);
    }


    /**
     * 设置浮动标题宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setFloatTitleW_H(int width, int height) {
        if (floatTitle == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) floatTitle.getLayoutParams();
        lp.width = width;
        lp.height = height;
        floatTitle.setLayoutParams(lp);
    }

    /**
     * 设置 标题默认颜色
     *
     * @param mainColor  主标题
     * @param floatColor 浮动标题
     */
    public void setTitleNormalColor(String mainColor, String floatColor) {
        try {
            if (!TextUtils.isEmpty(mainColor)) {
                this.mainColor = mainColor;
            } else {
                this.mainColor = "#ffffff";
            }
            int mColor = Color.parseColor(this.mainColor);
            mainTitle.setTextColor(mColor);

            if (!TextUtils.isEmpty(floatColor)) {
                this.floatColor = floatColor;
            } else {
                this.floatColor = "#ffffff";
            }
            int fColor = Color.parseColor(this.floatColor);
            floatTitle.setTextColor(fColor);
        } catch (Exception e) {
        }


    }

    /**
     * 设置单行超出省略
     */
    public void setMainTitleSingleLine(boolean singleLine) {
        mainTitle.setEllipsize(TextUtils.TruncateAt.END);
        if (singleLine) {
            mainTitle.setSingleLine(true);
        } else {
            mainTitle.setSingleLine(false);
            mainTitle.setMaxLines(2);
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void setTitleBackgroundDrawable(TextView textView) {
        if (textView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.home_item_shadow_bg));
        }
    }

    public void setData(EsMap hippyMap) {
        //是否展示标题
        if (hippyMap == null || !hippyMap.containsKey("posterTitleStyle")) {
            isShowTitle(false);
            return;
        }
        titleStyle = hippyMap.getString("posterTitleStyle");
        switch (titleStyle) {
            case "0":
            case "1":
            case "2":
            case "3":
                isShowTitle(true);
                break;
            default:
                isShowTitle(false);
                break;
        }
        //标题展示
        if (titleRootView != null && titleNormalView != null && mainTitle03 != null && titleRootView.getVisibility() == View.VISIBLE) {
            if (titleStyle.equals("3")) {
                //无焦点不显示
                mainTitle03.setVisibility(View.GONE);
                titleNormalView.setVisibility(View.GONE);
            } else {
                if (titleStyle.equals("0")){
                    mainTitle03.setVisibility(View.VISIBLE);
                    titleNormalView.setVisibility(View.GONE);
                }else{
                    mainTitle03.setVisibility(View.GONE);
                    titleNormalView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            return;
        }
        //初始化标题
        initViewData();
        //设置标题无焦点下的相关信息
        if (titleNormalView.getVisibility() == View.VISIBLE || mainTitle03.getVisibility() == VISIBLE) {
            if (mainTitle == null || floatTitle == null || mainTitle03 == null) {
                return;
            }
            //图片内 单行展示
            if ("0".equals(titleStyle)) {
                mainTitle03.setEllipsize(TextUtils.TruncateAt.END);
                mainTitle03.setSingleLine(true);

                //设置内容 主标题+浮动标题
                this.mainColor = "#ffffffff";
                this.floatColor = "#B3ffffff";
                boolean title = setMainTitle0Text(hippyMap);
                if (title) {
                    //设置阴影背景
                    setTitleBackgroundDrawable(this.mainTitle03);
                } else {
                    mainTitle03.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
                //单行多行展示
            } else if ("1".equals(titleStyle) || "2".equals(titleStyle)) {
                floatTitle.setVisibility(View.VISIBLE);
                //设置主标题背景无阴影
                mainTitle.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                //设置是否单行
                if ("1".equals(titleStyle)) {
                    setMainTitleSingleLine(true);
                } else {
                    setMainTitleSingleLine(false);
                }
                //设置标题颜色
                this.mainColor = "#b3FFFFFF";
                this.floatColor = "#ffffff";
                setTitleNormalColor(this.mainColor, this.floatColor);
                //设置内容 主标题
                boolean floatTitle = setMainTitle1_2Text(hippyMap);
                //设置浮动标题阴影
                if (floatTitle) {
                    setTitleBackgroundDrawable(this.floatTitle);
                } else {
                    this.floatTitle.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
            }
        }

    }

    /**
     * 初始化数据
     */
    public void initViewData(){
        if (mainTitle != null)mainTitle.setText("");
        if (mainTitle03 != null)mainTitle03.setText("");
        if (floatTitle != null)floatTitle.setText("");
    }

    /**
     * 设置文字在图片内的标题内容
     *
     * @param hippyMap 数据
     */
    public boolean setMainTitle0Text(EsMap hippyMap) {
        String titleM = "";
        String titleF = "";
        String result;
        if (hippyMap.containsKey("posterTitle")) {
            titleM = hippyMap.getString("posterTitle");
        } else {
            titleM = "";
        }
        if (hippyMap.containsKey("floatTitle")) {
            titleF = hippyMap.getString("floatTitle");
        } else {
            titleF = "";
        }
        if (TextUtils.isEmpty(titleM) && TextUtils.isEmpty(titleF)) {
            return false;
        }
        float screenScale = ScreenUtils.getScreenHeight(getContext().getApplicationContext())/1080.0f;
        int mainColor = Color.parseColor(this.mainColor);
        int mainSize = hippyMap.getInt("mainTitleSize");
        if (mainSize == 0){
            mainSize = 30;
        }
        mainSize = (int)(mainSize*screenScale+0.5);
        if (TextUtils.isEmpty(titleF)) {
            mainTitle03.setText(titleM);
            mainTitle03.setTextColor(mainColor);
            mainTitle03.setTextSize(TypedValue.COMPLEX_UNIT_PX,mainSize);
        } else {
            result = titleM + "  " + titleF;
            int startP = result.indexOf(titleF);
            sStr = new SpannableString(result);
            int floatSize = hippyMap.getInt("floatTitleSize");
            if (floatSize == 0){
                floatSize = 26;
            }
            floatSize = (int)(floatSize*screenScale+0.5);
            sStr.setSpan(new AbsoluteSizeSpan(mainSize), 0, titleM.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(new AbsoluteSizeSpan(floatSize), startP, result.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            int floatColor = Color.parseColor(this.floatColor);
            sStr.setSpan(new ForegroundColorSpan(mainColor), 0, titleM.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(new ForegroundColorSpan(floatColor), startP, result.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mainTitle03.setText(sStr);
        }
        return true;
    }


    /**
     * 设置文字在图片外标题内容
     *
     * @param hippyMap 数据
     */
    public boolean setMainTitle1_2Text(EsMap hippyMap) {
        String titleM = "";
        String titleF = "";
        if (hippyMap.containsKey("posterTitle")) {
            titleM = hippyMap.getString("posterTitle");
        } else {
            titleM = "";
        }
        if (hippyMap.containsKey("floatTitle")) {
            titleF = hippyMap.getString("floatTitle");
        } else {
            titleF = "";
        }
        float screenScale = ScreenUtils.getScreenHeight(getContext().getApplicationContext())/1080.0f;
        int mainSize = hippyMap.getInt("mainTitleSize");
        if (mainSize == 0){
            mainSize = 30;
        }
        mainSize = (int)(mainSize*screenScale+0.5);
        mainTitle.setText(titleM);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,mainSize);
        if (TextUtils.isEmpty(titleF)) {
            return false;
        }
        int floatSize = hippyMap.getInt("floatTitleSize");
        if (floatSize == 0){
            floatSize = 26;
        }
        floatSize = (int)(floatSize*screenScale+0.5);
        floatTitle.setText(titleF);
        floatTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,floatSize);
        return true;
    }


    public void setHippyMap(EsMap map){
        if (map == null)return;
        setMainTitleW_H(-1, -2);
        setFloatTitleW_H(-1, -2);
//        this.map = map;
        if (map != null && map.containsKey("isHideHomeTitle")) {
            isSetJson = map.getBoolean("isHideHomeTitle");
        }
//        setPostHandlerData(map);
        setData(map);
    }

    private void setPostHandlerData(final EsMap map){
        Log.d(TAG, "setPostHandlerData: usePostTask======="+usePostTask());
        if (usePostTask()){
            mPostHandler.postTask(POST_CATE_HIPPY_MAP,getType(),()->{
                if (getAlpha()<1){
                    setAlpha(1);
                }
                setData(map);
            },200);
        }
    }

    private boolean mFocused = false;
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (!isSetJson)return;
        if(isDuplicateParentStateEnabled()){
            final boolean focused = ExtendUtil.stateContainsAttribute(getDrawableState(), android.R.attr.state_focused);
            if (mFocused == focused){
                return;
            }
            mFocused = focused;
            isFocus(focused);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (!isSetJson)return;
        isFocus(gainFocus);
    }

    @Override
    public void setRootPostHandlerView(PostHandlerView pv) {
        this.mPostHandler = pv;
    }

    private boolean usePostTask(){
        Log.d(TAG, "usePostTask: this.mPostHandler != null===="+(this.mPostHandler == null));
        Log.d(TAG, "usePostTask: isSetJson===="+(isSetJson));
        if (isSetJson)
            return this.mPostHandler != null;
        else
            return false;
    }

    @Override
    public void resetProps() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void onResetBeforeCache() {
        clearAllPost();
        if (usePostTask()){
            this.setAlpha(0);
            this.initViewData();
        }
    }

    @Override
    public void notifySaveState() {
        if (usePostTask())
            onResetBeforeCache();
    }

    @Override
    public void notifyRestoreState() {
        try {
            if (usePostTask())
                setHippyMap(map);
        }catch (Exception e){

        }
    }

    void clearAllPost(){
        if(usePostTask()) {
            for (int i = 0; i < POST_CATEGORIES.length; i++) {
                mPostHandler.clearTask(POST_CATEGORIES[i], getType());
            }
        }
    }
    int getType(){
        return hashCode();
    }
}
