package com.quicktvui.support.ui.item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.hippyext.RenderUtil;
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
class TitleViewFocus extends HippyBaseFrameLayout implements TVListView.PostContentHolder, HippyRecycler, IEsComponentView {
    private FrameLayout titleRootFocusView;
    private RelativeLayout titleFocusView;
    private LinearLayout focusMainLView;
    //主标题
    private TextView focusFloatTitle;
    private TextView focusMainTitle;
    private TextView focusSubTitle;
    // 默认样式
    private String titleStyle;//标题样式
    private SpannableString sStr;

    private String focusMainColor = "#000000";
    private String focusSubColor = "#99000000";
    private boolean isSetJson = false;
    private PostHandlerView mPostHandler;
    static int POST_CATE_HIPPY_MAP = 11;


    static int[] POST_CATEGORIES = new int[]{POST_CATE_HIPPY_MAP};
    EsMap map;

    public TitleViewFocus(Context context) {
        super(context);
        init();
    }

    public TitleViewFocus(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleViewFocus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RelativeLayout getTitleFocusView() {
        return titleFocusView;
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_titles_view_focus, this);
        titleRootFocusView = view.findViewById(R.id.titleRootFocusView);
        titleFocusView = view.findViewById(R.id.titleFocusView);
        focusMainLView = view.findViewById(R.id.focusMainLView);

        focusFloatTitle = view.findViewById(R.id.focusFloatTitle);
        focusMainTitle = view.findViewById(R.id.focusMainTitle);
        focusSubTitle = view.findViewById(R.id.focusSubTitle);
    }

    public void isShowTitle(boolean isShow) {
        if (titleRootFocusView == null) {
            return;
        }
        if (isShow) {
            titleRootFocusView.setVisibility(View.VISIBLE);
        } else {
            titleRootFocusView.setVisibility(View.GONE);
        }
    }

    public void isFocus(boolean focus) {
        if (titleFocusView == null) {
            return;
        }
        if (!TextUtils.isEmpty(titleStyle) && ("0".equals(titleStyle) || "1".equals(titleStyle) || "2".equals(titleStyle) || "3".equals(titleStyle))) {
            if (focus) {
                titleFocusView.setVisibility(View.VISIBLE);
            } else {
                titleFocusView.setVisibility(View.GONE);
            }
        }

    }


    public void setFocusTitleSingleLine(String titleStyle, boolean bigWidth) {
        focusFloatTitle.setSingleLine(true);
        focusSubTitle.setSingleLine(true);
        focusFloatTitle.setEllipsize(TextUtils.TruncateAt.END);
        focusMainTitle.setEllipsize(TextUtils.TruncateAt.END);
        focusSubTitle.setEllipsize(TextUtils.TruncateAt.END);
        if (bigWidth) {
            focusMainTitle.setSingleLine(true);
        } else {
            focusMainTitle.setSingleLine(false);
            if ("2".equals(titleStyle)) {
                focusMainTitle.setMaxLines(3);
            } else {
                focusMainTitle.setMaxLines(2);
            }

        }
    }

    public void setHippyMap(EsMap map){
//        this.map = map;
        if (map != null && map.containsKey("isHideHomeTitle")) {
            isSetJson = map.getBoolean("isHideHomeTitle");
        }
//        setPostHandlerData(map);
        setData(map);
    }
    private void setPostHandlerData(final EsMap map){
        if (usePostTask()){
            mPostHandler.postTask(POST_CATE_HIPPY_MAP,getType(),()->{
                if (getAlpha()<1){
                    setAlpha(1);
                }
                setData(map);
            },200);
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

        //初始化标题
        initViewData();
        //设置标题焦点状态下的相关信息
        if (titleFocusView != null) {

            if (focusMainTitle == null || focusFloatTitle == null || focusSubTitle == null) {
                titleFocusView.setVisibility(View.GONE);
                return;
            }
            //图片内 单行展示
            if ("0".equals(titleStyle) || "1".equals(titleStyle) || "2".equals(titleStyle) || "3".equals(titleStyle)) {
                if (hippyMap.containsKey("width")) {
                    int width = hippyMap.getInt("width");
                    setFocusTitleSingleLine(titleStyle, width > 520);
                    //width>520 设置主标题+副标题 单行 +浮动标题, < 520 设置主标题+副标题 分行  主标题最多2行, 副标题一行 +浮动标题
                    setFocusTitle0Text(hippyMap, width > 520, titleStyle);
                }
            }
            titleFocusView.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 初始化数据
     */
    public void initViewData() {
        if (focusFloatTitle != null) focusFloatTitle.setText("");
        if (focusMainTitle != null) focusMainTitle.setText("");
        if (focusSubTitle != null) focusSubTitle.setText("");
    }


    public void setFocusTitle0Text(EsMap hippyMap, boolean isOneLine, String titleStyle) {

        String titleF = "";
        String titleM = "";
        String titleS = "";
        StringBuilder result;
        if (hippyMap.containsKey("floatTitle")) {
            titleF = hippyMap.getString("floatTitle");
        } else {
            titleF = "";
        }
        if (hippyMap.containsKey("posterTitle")) {
            titleM = hippyMap.getString("posterTitle");
        } else {
            titleM = "";
        }
        if (!"2".equals(titleStyle)) {
            if (hippyMap.containsKey("posterSubtitle")) {
                titleS = hippyMap.getString("posterSubtitle");
            } else {
                titleS = "";
            }
        }
        float screenScale = ScreenUtils.getScreenHeight(getContext().getApplicationContext()) / 1080.0f;
        if (TextUtils.isEmpty(titleF)) {
            focusFloatTitle.setText("");
            focusFloatTitle.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
            focusFloatTitle.setVisibility(View.INVISIBLE);
        } else {
            int floatSize = hippyMap.getInt("floatTitleSize");
            if (floatSize == 0) {
                floatSize = 26;
            }
            floatSize = (int) (floatSize * screenScale + 0.5);
            focusFloatTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, floatSize);
            focusFloatTitle.setText(titleF);
            focusFloatTitle.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.home_item_focus_shadow_bg));
            focusFloatTitle.setVisibility(View.VISIBLE);
        }
        //单行 主标题 | 副标题
        int fMainColor = Color.parseColor(focusMainColor);
        int fSubColor = Color.parseColor(focusSubColor);
        if (TextUtils.isEmpty(titleM)) {
            focusMainLView.setVisibility(View.INVISIBLE);
            return;
        } else {
            focusMainLView.setVisibility(View.VISIBLE);
        }
        int mainSize = hippyMap.getInt("mainTitleSize");
        if (mainSize == 0) {
            mainSize = 30;
        }
        mainSize = (int) (mainSize * screenScale + 0.5);
        if (isOneLine) {
            focusSubTitle.setText("");
            focusSubTitle.setVisibility(View.GONE);
            if (TextUtils.isEmpty(titleS)) {
                focusMainTitle.setText(titleM);
            } else {
                result = new StringBuilder(titleM);
                result.append(" | ");
                result.append(titleS);
                int startP = titleM.length() + 1;
                sStr = new SpannableString(result);
                sStr.setSpan(new ForegroundColorSpan(fMainColor), 0, titleM.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sStr.setSpan(new ForegroundColorSpan(fSubColor), startP, result.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                focusMainTitle.setText(sStr);
            }
            focusMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainSize);
        } else {

            focusMainTitle.setTextColor(fMainColor);
            focusMainTitle.setText(titleM);
            focusMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainSize);
            if ("2".equals(titleStyle) || TextUtils.isEmpty(titleS)) {
                focusSubTitle.setVisibility(View.GONE);
            } else {
                int subSize = hippyMap.getInt("subTitleSize");
                if (subSize == 0) {
                    subSize = 24;
                }
                subSize = (int) (subSize * screenScale + 0.5);
                focusSubTitle.setVisibility(View.VISIBLE);
                focusSubTitle.setTextColor(fSubColor);
                focusSubTitle.setText(titleS);
                focusSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subSize);

            }
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
        RenderUtil.requestNodeLayout(this);
    }

    @Override
    public void setRootPostHandlerView(PostHandlerView pv) {
        this.mPostHandler = pv;
    }
    private boolean usePostTask(){
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
