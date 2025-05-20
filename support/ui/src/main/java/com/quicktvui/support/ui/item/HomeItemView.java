package com.quicktvui.support.ui.item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.base.ui.TVFocusAnimHelper;
import com.quicktvui.hippyext.views.fastlist.PostHandlerView;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.quicktvui.support.ui.DensityUtils;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.views.list.HippyRecycler;

import com.quicktvui.support.ui.R;
import com.quicktvui.support.ui.ScreenUtils;
import com.quicktvui.support.ui.largelist.PendingItemView;
import com.quicktvui.support.ui.largelist.TemplateItem;
import com.quicktvui.support.ui.largelist.TemplateUtil;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.quicktvui.support.ui.item.widget.ShimmerWidget;

/**
 * Created by XingRuGeng on 2021/12/7
 * Desc:HomeItemView
 *
 * 新版本不再使用这个水波纹
 */
@Deprecated
public class HomeItemView extends HippyBaseFrameLayout implements HippyRecycler, PendingItemView,
        IEsComponentView, TVListView.PostContentHolder{


    private final String TAG = HomeItemView.class.getSimpleName();
    //展示类型 默认 custom:首页格子类型  leftRight:左图右文,topDown:上图下文, text: 纯文本,number:纯数字
    private String showType = "custom";

    private PostHandlerView mPostHandler;

    static int POST_CATE_MAIN_TEXT = 10;
    static int POST_CATE_HIPPY_MAP = 11;
    static int POST_CATE_IMG = 12;
    static int POST_CATE_SHADOW = 13;
    static int POST_CATE_FLOAT_TITLE = 14;
    static int POST_CATE_FOCUS_CHANGE = 15;
    static int POST_CATE_CORNER_TITLE = 16;


    static int[] POST_CATEGORIES = new int[]{POST_CATE_MAIN_TEXT,POST_CATE_HIPPY_MAP,POST_CATE_IMG
            ,POST_CATE_SHADOW,POST_CATE_FLOAT_TITLE,POST_CATE_FOCUS_CHANGE
            ,POST_CATE_CORNER_TITLE
    };

    private int loadImgDelay = 500;
    private boolean isSelected = false;

    private String bgUrl;
    private String shadowUrl;
    private RequestOptions options;
    private RequestOptions optionsShadow;

    protected FrameLayout rootView;

    protected ImageView borderImgView;
    private boolean showBorder = true;
    protected TextView cornerTextView;
    private String cornerContent;
    private boolean isShowTitle = true;
    private int cornerSize;
    private String cornerColor;
    private Drawable cornerDrawable;
    //背景图view
    protected ImageView imageView;
    //规则阴影背景view
    protected ImageView imgShadow;
    //不规则阴影背景view
    protected ImageView imgIrregularShadow;
    protected int imgBgWidth;
    protected int imgBgHeight;

    protected ImageView imgRipple;
    protected RippleView mRippleView;
    protected boolean showRippleView = true;
    protected TitleView titleview;
    protected TitleViewFocus titleViewFocus;

    private float mFocusScaleX = TVFocusAnimHelper.DEFAULT_SCALE;
    private float mFocusScaleY = TVFocusAnimHelper.DEFAULT_SCALE;
    private final int mDuration = TVFocusAnimHelper.DEFAULT_DURATION;

    private Handler handler;
    private Runnable runnable;
    private EsMap EsMap;

    private boolean isDetached = false;

    /*********选集******************************/
    private RelativeLayout rlView;
    //浮动标题
    private TextView floatTitle;
    private String fTitle;
    //水平主标题
    private TextView mainTitle;
    private ImageView playingImg;
    //垂直主标题
    private TextView verticalMainTitle;
    private RelativeLayout focusRlView;
    private TextView focusFloatTitle;
    private TextView focusVerticalMainTitle;
    private ImageView focusPlayingImg;
    //主标题颜色
    private ColorStateList mainTitleColor;
    private int focusMainColor;
    private int textSize = 30;
    private int floatTextSize = 20;
    //是否免费
    private boolean isFree = true;
    private boolean isHideRipple = false;
    private boolean isHideShadow = false;
    private Drawable topDownBg;

    //zhaopeng add
    private boolean enableShimmer = true;
    private boolean enablePlaceholder = true;
    private boolean stopInitTitle = false;

    // weipeng move from App
    private static Bitmap waterRippleNormalBitmap;
    private static Bitmap waterRippleVipBitmap;

    public void setEnableShimmer(boolean enableShimmer) {
        this.enableShimmer = enableShimmer;
    }

    public void setHideRipple(boolean hideRipple) {
        isHideRipple = hideRipple;
        if (isHideRipple && mRippleView != null){
            mRippleView.stopAnim();
        }
    }
    public void setHideShadow(boolean hideShadow){
        isHideShadow = hideShadow;
        if (imgShadow != null && isHideShadow){
            imgShadow.setVisibility(INVISIBLE);
        }
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
        if (!isShowTitle){
            if (titleview != null)
                titleview.setVisibility(GONE);
            if (titleViewFocus != null)
                titleViewFocus.setVisibility(GONE);
        }
    }

    public void setShadowMargin(EsMap map){
        if (map != null){
            int marginLeft = map.containsKey("marginLeft")?map.getInt("marginLeft"):0;
            int marginTop = map.containsKey("marginTop")?map.getInt("marginTop"):0;
            int marginRight = map.containsKey("marginRight")?map.getInt("marginRight"):0;
            int marginBottom = map.containsKey("marginBottom")?map.getInt("marginBottom"):0;
            if (imgShadow != null){
                LayoutParams lp = (LayoutParams) imgShadow.getLayoutParams();
                lp.width = -1;
                lp.height = -1;
                lp.leftMargin = marginLeft;
                lp.topMargin = marginTop;
                lp.rightMargin = marginRight;
                lp.bottomMargin = marginBottom;
                imgShadow.setLayoutParams(lp);
                RenderUtil.requestNodeLayout(this);
            }
        }
    }

    public void setLoadImgDelay(int loadImgDelay) {
        this.loadImgDelay = loadImgDelay;
    }

    private ShimmerWidget shimmerWidget;
    //zhaopeng add end

    /***************************************/


    public HomeItemView(Context context, EsMap iniProps) {
        super(context);
        this.showType = "custom";
        if(iniProps != null){
            if(iniProps.containsKey("disablePlaceholder")){
                this.enablePlaceholder = false;
            }
            if(iniProps.containsKey("stopInitTitle")){
                this.stopInitTitle = true;
            }

        }
        init(context);
    }

    public HomeItemView(Context context) {
        super(context);
        this.showType = "custom";
        init(context);
    }

    public HomeItemView(Context context, String type, ColorStateList titleColor, Drawable topDownBg,int textSize, int floatSize, boolean isFree) {
        super(context);
        try {
            this.showType = type;
            EsMap hmRoot = new EsMap();
            EsMap hm = new EsMap();
            hm.pushString("normal", "#80FFFFFF");
            hm.pushString("selected", "#80FFFFFF");
            if (isFree) {
                if ("topDown".equals(showType)) {
                    hm.pushString("focused", "#ff000000");
                } else {
                    hm.pushString("focused", "#ffffffff");
                }
            } else {
                if ("topDown".equals(showType)) {
                    hm.pushString("focused", "#FFD97C");
                } else {
                    hm.pushString("focused", "#603314");
                }
            }
            hmRoot.pushMap("textColor", hm);
            ColorStateList csl = TemplateUtil.createColorStateList(hmRoot, "textColor");
            this.mainTitleColor = titleColor == null ? csl : titleColor;
            this.focusMainColor = mainTitleColor.getColorForState(new int[]{android.R.attr.state_focused},Color.parseColor("#ffffffff"));
            this.textSize = textSize;
            this.floatTextSize = floatSize;
            this.isFree = isFree;
            this.topDownBg = topDownBg;
            init(context);
        }catch (Exception e){
        }

    }

    public HomeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.showType = "custom";
        init(context);
    }

    public HomeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.showType = "custom";
        init(context);
    }


    /**
     * 初始化背景view和标题view
     *
     * @param context 上下文
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void init(Context context) {
//        this.mTraceable = traceable;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        this.setClipChildren(false);
        removeAllViews();
        float screenScale = ScreenUtils.getScreenHeight(getContext().getApplicationContext())/1080.0f;
        float size = 20;
        if (screenScale < 1){
            size = screenScale * size;
        }
        if ("custom".equals(showType)) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_home_item_view, this);
            rootView = view.findViewById(R.id.homeItemRootView);

            //背景图
            imageView = view.findViewById(R.id.homeItemBgImg);
            imgShadow = view.findViewById(R.id.homeItemShadowImg);
            imgIrregularShadow = view.findViewById(R.id.homeItemShadowIrregularImg);

            //设置边框View
            borderImgView = view.findViewById(R.id.homeItemBorderImg);
            cornerTextView = view.findViewById(R.id.homeItemCorner);

            cornerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
            cornerTextView.setVisibility(INVISIBLE);
            if(enablePlaceholder) {
                options = new RequestOptions().
                        placeholder(getContext().getResources().getDrawable(R.drawable.home_place_bg))
                        .skipMemoryCache(false)
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .fitCenter()
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .transform(new RoundedCorners(8));
            }else{
                options = new RequestOptions()
                        .skipMemoryCache(false)
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .fitCenter()
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .transform(new RoundedCorners(8));
                imageView.setImageDrawable(null);
            }
            if (!stopInitTitle){
                titleview = new TitleView(getContext());
                addView(titleview);
                LayoutParams lp = (LayoutParams) titleview.getLayoutParams();
                lp.width = LayoutParams.MATCH_PARENT;
                lp.height = LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
                titleview.setLayoutParams(lp);
                titleViewFocus = new TitleViewFocus(getContext());
                addView(titleViewFocus);
                LayoutParams lpFocus = (LayoutParams) titleViewFocus.getLayoutParams();
                lpFocus.width = LayoutParams.MATCH_PARENT;
                lpFocus.height = LayoutParams.WRAP_CONTENT;
                lpFocus.gravity = Gravity.BOTTOM | Gravity.LEFT;
                titleViewFocus.setLayoutParams(lpFocus);
                initRippleView("#FF4E46", -20, -24, "assets/water_play.png");
            }
            //leftRight:左图右文,topDown:上图下文, text: 纯文本,number:纯数字
        } else if ("leftRight".equals(showType) || "topDown".equals(showType) ) {//左图右文,上图下文,纯文本,纯数字 || "text".equals(showType) || "number".equals(showType)

            View view = LayoutInflater.from(context).inflate(R.layout.activity_all_play_item_view, this);
            //边框
            borderImgView = view.findViewById(R.id.playAllItemBorderImg);
            //rootView
            rlView = view.findViewById(R.id.playAllItemRlView);
            rlView.setDuplicateParentStateEnabled(true);
            //背景
            imageView = view.findViewById(R.id.playAllItemBgImg);
            //
            cornerTextView = view.findViewById(R.id.playAllItemCorner);
            cornerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
            cornerTextView.setVisibility(INVISIBLE);
            floatTitle = view.findViewById(R.id.playAllItemFloatTitle);

            mainTitle = view.findViewById(R.id.playAllItemTitle);
            verticalMainTitle = view.findViewById(R.id.playAllItemVerticalTitle);
            focusRlView = view.findViewById(R.id.playAllFocusVerticalRlView);
            focusFloatTitle = view.findViewById(R.id.playAllFocusFloatTitle);
            focusVerticalMainTitle = view.findViewById(R.id.playAllFocusVerticalTitle);
            focusRlView.setVisibility(INVISIBLE);
            if ("topDown".equals(showType)) {
                rlView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                verticalMainTitle.setVisibility(VISIBLE);
                focusVerticalMainTitle.setDuplicateParentStateEnabled(true);
                focusRlView.setClipChildren(false);
                focusPlayingImg = new ImageView(getContext());
                addView(focusPlayingImg);
                LayoutParams lpRipple = (LayoutParams) focusPlayingImg.getLayoutParams();
                lpRipple.width = DensityUtils.dip2px(getContext(), 50);
                lpRipple.height = DensityUtils.dip2px(getContext(), 50);
                lpRipple.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                lpRipple.bottomMargin = DensityUtils.dip2px(getContext(), 42);
                focusPlayingImg.setLayoutParams(lpRipple);
                if (isFree) {
                    initRippleView("#FF4E46", 17, -25, "assets/water_play.png");
                } else {
                    initRippleView("#FFD97C", 17, -25, "assets/water_play_vip.png");
                }
                setFocusVerticalColor(mainTitleColor);
                if (topDownBg != null)
                    focusVerticalMainTitle.setBackgroundDrawable(topDownBg);
            }
            else {
                mainTitle.setDuplicateParentStateEnabled(true);
                verticalMainTitle.setVisibility(GONE);
                focusRlView.setVisibility(GONE);
                if ("leftRight".equals(showType)) {
                    playingImg = new ImageView(getContext());
                    addView(playingImg);
                    LayoutParams lpRipple = (LayoutParams) playingImg.getLayoutParams();
                    lpRipple.width = DensityUtils.dip2px(getContext(), 50);
                    lpRipple.height = DensityUtils.dip2px(getContext(), 50);
                    lpRipple.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    lpRipple.bottomMargin = DensityUtils.dip2px(getContext(), -15);
                    playingImg.setLayoutParams(lpRipple);
                    if (isFree) {
                        initRippleView("#FF4E46", -41, -26, "assets/water_play.png");
                    } else {
                        initRippleView("#FFD97C", -41, -26, "assets/water_play_vip.png");
                    }
                }
            }
            initImgParams();
            setMainTitleColor(mainTitleColor);
            setTitleSize();

        }
    }

    private void initRippleView(String color, int marginBottom, int marginRight, String imgPath) {
        FrameLayout flRippleViewRoot = new FrameLayout(getContext());
        addView(flRippleViewRoot);
        mRippleView = new RippleView(getContext());
        flRippleViewRoot.addView(mRippleView);
        LayoutParams lpRipple = (LayoutParams) mRippleView.getLayoutParams();
        lpRipple.width = DensityUtils.dip2px(getContext(), 80);
        lpRipple.height = DensityUtils.dip2px(getContext(), 80);
        mRippleView.setLayoutParams(lpRipple);
        mRippleView.init(color,imgPath);

        imgRipple = new ImageView(getContext());
        flRippleViewRoot.addView(imgRipple);
        Bitmap bitmap;
        LayoutParams lpImg = (LayoutParams) imgRipple.getLayoutParams();
        lpImg.gravity = Gravity.CENTER;
        lpImg.width = DensityUtils.dip2px(getContext(), 40);
        lpImg.height = DensityUtils.dip2px(getContext(), 40);
        imgRipple.setLayoutParams(lpImg);
        if (imgPath.contains("vip")){
            bitmap = getWaterRippleVipBitmap();
        }else{
            bitmap = getWaterRippleNormalBitmap();
        }
        imgRipple.setImageBitmap(bitmap);

        LayoutParams lpFl = (LayoutParams) flRippleViewRoot.getLayoutParams();
        lpFl.width = DensityUtils.dip2px(getContext(), 80);
        lpFl.height = DensityUtils.dip2px(getContext(), 80);
        lpFl.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lpFl.bottomMargin = DensityUtils.dip2px(getContext(), marginBottom);
        lpFl.rightMargin = DensityUtils.dip2px(getContext(), marginRight);
        flRippleViewRoot.setLayoutParams(lpFl);

        if (mRippleView != null)
            mRippleView.setVisibility(INVISIBLE);
        if (imgRipple != null)
            imgRipple.setVisibility(INVISIBLE);
        RenderUtil.requestNodeLayout(this);
    }

    /**
     * 初始化选集图片属性
     */
    private void initImgParams() {

        if ("leftRight".equals(showType)) {//左图右文
            //图片参数
            RoundedCornersTransformation rct = new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.LEFT);
            options = new RequestOptions().placeholder(R.drawable.home_place_left_bottom_bg)
                    .skipMemoryCache(false)
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .transform(rct);
            //主标题
            mainTitle.setMaxLines(3);
            mainTitle.setEllipsize(TextUtils.TruncateAt.END);

        } else if ("topDown".equals(showType)) {//上图下文
            options = new RequestOptions()
                    .skipMemoryCache(false)
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .transform(new RoundedCorners(8));
            verticalMainTitle.setSingleLine(true);
            verticalMainTitle.setEllipsize(TextUtils.TruncateAt.END);
            focusVerticalMainTitle.setMaxLines(2);
            focusVerticalMainTitle.setEllipsize(TextUtils.TruncateAt.END);

        }
    }

    public void setMainTitle(String title) {
        if (usePostTask()) {
            if ("topDown".equals(showType)) {
                if (verticalMainTitle == null) {
                    return;
                }
                verticalMainTitle.setText("");
                if (focusVerticalMainTitle == null) {
                    return;
                }
                focusVerticalMainTitle.setText("");
            } else {
                if (mainTitle == null) {
                    return;
                }
                mainTitle.setText("");
            }
            mPostHandler.postTask(POST_CATE_MAIN_TEXT, getType(), () -> {
                if ("topDown".equals(showType)) {
                    if (verticalMainTitle == null) {
                        return;
                    }
                    verticalMainTitle.setText(title);
                    if (focusVerticalMainTitle == null) {
                        return;
                    }
                    focusVerticalMainTitle.setText(title);
                } else {
                    if (mainTitle == null) {
                        return;
                    }
                    mainTitle.setText(title);
                }
            },100);
        }else {
            if ("topDown".equals(showType)) {
                if (verticalMainTitle == null) {
                    return;
                }
                verticalMainTitle.setText("");
                verticalMainTitle.setText(title);
                if (focusVerticalMainTitle == null) {
                    return;
                }
                focusVerticalMainTitle.setText("");
                focusVerticalMainTitle.setText(title);
            } else {
                if (mainTitle == null) {
                    return;
                }
                mainTitle.setText("");
                mainTitle.setText(title);
            }
        }

    }

    public void setMainTitleColor(ColorStateList color) {
        if (color == null) {
            return;
        }
        if ("topDown".equals(showType)) {
            if (verticalMainTitle != null){
                verticalMainTitle.setTextColor(color);
            }
        } else if (mainTitle != null) {
            mainTitle.setTextColor(color);
        }
    }

    public void setFocusVerticalColor(ColorStateList color) {
        if (color == null) {
            return;
        }
        if (focusVerticalMainTitle != null && "topDown".equals(showType)) {
            focusVerticalMainTitle.setTextColor(color);
        }

    }

    public void setTitleSize() {
        if ("topDown".equals(showType)) {
//            verticalMainTitle.setTextSize(textSize);
            verticalMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//            focusVerticalMainTitle.setTextSize(textSize);
            focusVerticalMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//            focusFloatTitle.setTextSize(floatTextSize);
            focusFloatTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, floatTextSize);
        } else {
            mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
//        floatTitle.setTextSize(floatTextSize);
        floatTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, floatTextSize);

    }

    public void setFloatTitle(String floatTitle) {
        this.fTitle = floatTitle;

        if (this.floatTitle == null) {
            return;
        }
        if(usePostTask()){
            mPostHandler.postTask(POST_CATE_FLOAT_TITLE, getType(), new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(floatTitle)) {
                        HomeItemView.this.floatTitle.setVisibility(INVISIBLE);
                    } else {
                        HomeItemView.this.floatTitle.setVisibility(VISIBLE);
                        HomeItemView.this.floatTitle.setText(floatTitle);
                    }
                    if ("topDown".equals(showType) && HomeItemView.this.focusFloatTitle != null) {
                        if (TextUtils.isEmpty(floatTitle)) {
                            HomeItemView.this.focusFloatTitle.setVisibility(INVISIBLE);
                        } else {
                            HomeItemView.this.focusFloatTitle.setVisibility(VISIBLE);
                            HomeItemView.this.focusFloatTitle.setText(floatTitle);
                        }
                    }
                }
            },100);
        }else{
            if (TextUtils.isEmpty(floatTitle)) {
                this.floatTitle.setVisibility(INVISIBLE);
            } else {
                this.floatTitle.setVisibility(VISIBLE);
                this.floatTitle.setText(floatTitle);
            }
            if ("topDown".equals(showType) && this.focusFloatTitle != null) {
                if (TextUtils.isEmpty(floatTitle)) {
                    this.focusFloatTitle.setVisibility(INVISIBLE);
                } else {
                    this.focusFloatTitle.setVisibility(VISIBLE);
                    this.focusFloatTitle.setText(floatTitle);
                }
            }
        }

    }

    public void setEsMap(final EsMap map) {
        if(usePostTask()){
            mPostHandler.postTask(POST_CATE_HIPPY_MAP, getType(), () -> {
                if (map != null && stopInitTitle){
                    //角标
                    if (map.containsKey("cornerContent")) {
                        String cornerContent = map.getString("cornerContent");
                        setCorner(cornerContent);
                        RenderUtil.requestNodeLayout(HomeItemView.this);
                    }
                }
                if (titleview == null || map == null || !isShowTitle) {
                    return;
                }
                if(titleview.getAlpha() < 1){
                    titleview.setAlpha(1);
                }
                HomeItemView.this.EsMap = map;
                setNormalTitleGravity(map);
                setFocusTitleGravity(map);
                titleview.setMainTitleW_H(-1, -2);
                titleview.setFloatTitleW_H(-1, -2);
                titleview.setData(map);
                titleViewFocus.setData(map);
                RenderUtil.requestNodeLayout(HomeItemView.this);
                //角标
                if (map.containsKey("cornerContent")) {
                    String cornerContent = map.getString("cornerContent");
                    setCorner(cornerContent);
                }
                //水波纹按钮是否显示
                if (map.containsKey("playLogoSwitch")) {
                    String isShow = map.getString("playLogoSwitch");
                    showRippleView = "1".equals(isShow);
                }
            },200);
        }else{
            if (map != null && stopInitTitle){
                //角标
                if (map.containsKey("cornerContent")) {
                    String cornerContent = map.getString("cornerContent");
                    setCorner(cornerContent);
                    RenderUtil.requestNodeLayout(HomeItemView.this);
                }
            }
            if (titleview == null || map == null || !isShowTitle) {
                return;
            }
            if(titleview.getAlpha() < 1){
                titleview.setAlpha(1);
            }
            this.EsMap = map;
            setNormalTitleGravity(map);
            setFocusTitleGravity(map);
            titleview.setMainTitleW_H(-1, -2);
            titleview.setFloatTitleW_H(-1, -2);
            titleview.setData(map);
            titleViewFocus.setData(map);
            RenderUtil.requestNodeLayout(this);
            //角标
            if (map.containsKey("cornerContent")) {
                String cornerContent = map.getString("cornerContent");
                setCorner(cornerContent);
            }
            //水波纹按钮是否显示
            if (map.containsKey("playLogoSwitch")) {
                String isShow = map.getString("playLogoSwitch");
                showRippleView = "1".equals(isShow);
            }
        }

    }

    /**
     * 设置角标
     *
     * @param cornerContent 角标内容
     */
    public void setCorner(String cornerContent) {
        if (cornerTextView == null) {
            return;
        }
        this.cornerContent = cornerContent;
        if(usePostTask()){
            mPostHandler.postTask(POST_CATE_CORNER_TITLE, getType(), () -> {
                if (TextUtils.isEmpty(cornerContent)) {
                    cornerTextView.setVisibility(INVISIBLE);
                } else {
                    cornerTextView.setVisibility(VISIBLE);
                    cornerTextView.setText(cornerContent);
                }
            },100);
        }else {
            if (TextUtils.isEmpty(cornerContent)) {
                cornerTextView.setVisibility(INVISIBLE);
            } else {
                cornerTextView.setVisibility(VISIBLE);
                cornerTextView.setText(cornerContent);
            }
        }
    }

    public void setCornerTextSize(int size){
        if (cornerTextView != null && size > 0){
            this.cornerSize = size;
            cornerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }
    }

    public void setCornerColor(String color) {
        if (cornerTextView == null) {
            return;
        }
        this.cornerColor = color;
        if (TextUtils.isEmpty(color)) {
            cornerTextView.setTextColor(Color.parseColor("#ffffff"));
        } else {
            cornerTextView.setTextColor(Color.parseColor(color));
        }
    }
    public void setCornerBgDrawable(Drawable bgDrawable){
        if (cornerTextView == null){
            return;
        }
        this.cornerDrawable = bgDrawable;
        if (bgDrawable != null){
            cornerTextView.setBackgroundDrawable(bgDrawable);
        }else{
            if (getContext() != null)
                cornerTextView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
    }

    private void updateLayout(View view, int x, int y, int width, int height) {
        if (view != null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            view.layout(x, y, x + width, y + height);
        }
    }

    private void setNormalTitleGravity(EsMap map) {
        if (titleview != null && map != null && map.containsKey("posterTitleStyle")) {
            String titleStyle = map.getString("posterTitleStyle");
            titleview.setClipChildren(false);
            if ("1".equals(titleStyle) || "2".equals(titleStyle)) {
                RelativeLayout rlNormal = titleview.getTitleNormalView();

                if (rlNormal != null && map.containsKey("height")) {
                    LayoutParams lp = (LayoutParams) rlNormal.getLayoutParams();
                    float screenScale = ScreenUtils.getScreenHeight(getContext().getApplicationContext())/1080.0f;
                    int height = map.getInt("height");
                    height = (int)(height * screenScale +0.5f);
                    lp.height = height;
                    int value = (int) ((height - 26 * screenScale) + 0.5f);
                    lp.bottomMargin = -value;
                    rlNormal.setLayoutParams(lp);
                }
            }else if ("0".equals(titleStyle) || "3".equals(titleStyle)) {
                TextView main03 = titleview.getMainTitle03();
                if(main03 != null && map.containsKey("height")){
                    LayoutParams lp = (LayoutParams) main03.getLayoutParams();
                    lp.height = -2;
                    lp.gravity = Gravity.BOTTOM;
                    main03.setLayoutParams(lp);
                }
            }
        }
    }

    private void setFocusTitleGravity(EsMap map) {
        if (titleViewFocus != null && map != null) {
            titleViewFocus.setClipChildren(false);
            RelativeLayout rlFocus = titleViewFocus.getTitleFocusView();
            if (rlFocus == null) {
                return;
            }
            float screenScale = ScreenUtils.getScreenHeight(getContext().getApplicationContext())/1080.0f;
            LayoutParams lp = (LayoutParams) rlFocus.getLayoutParams();
            int height = map.getInt("height");
            height = (int)(height * screenScale +0.5f);
            lp.height = height;
            int value = (int) ((height - 43 * screenScale)  + 0.5f);
            lp.bottomMargin = -value;
            rlFocus.setLayoutParams(lp);
        }
    }

    /**
     * 设置阴影
     *
     * @param shadowUrl 阴影图片地址
     */
    public void setShadowUrl(String shadowUrl) {
        this.shadowUrl = shadowUrl;
        if (imgIrregularShadow != null && imgShadow != null){
            imgIrregularShadow.setVisibility(INVISIBLE);
            imgShadow.setVisibility(INVISIBLE);
            if (TextUtils.isEmpty(shadowUrl)) {
                if (!TextUtils.isEmpty(this.bgUrl)){
                    imgShadow.setBackgroundResource(R.drawable.shadow_focus_home_item_v2);
                }
            } else {
                if (optionsShadow == null) {
                    optionsShadow = new RequestOptions()
                            .skipMemoryCache(false);
                }
                if (getGlideSafeContext() != null){
                    try {
                        Glide.with(getGlideSafeContext()).load(shadowUrl).apply(optionsShadow).into(imgIrregularShadow);
                    }catch (Exception e){

                    }

                }
            }
        }
    }

    public void setEmpty(){
//        setMainTitle("");
        if (imageView != null && getGlideSafeContext() != null) {
            try{
                Glide.with(getGlideSafeContext()).clear(imageView);
            }catch(Exception e){}
        }
    }

    public void setBgUrl(String bgUrl){
        setBgUrl(bgUrl,true,loadImgDelay);
    }

    public void setBgUrl(String bgUrl,boolean check,int delay) {

        if (check && !TextUtils.isEmpty(this.bgUrl) && this.bgUrl.equals(bgUrl)){
            return;
        }
        this.bgUrl = bgUrl;
//        if (imageView != null) imageView.setTag(bgUrl);
        if (imageView != null) imageView.setTag(R.id.home_item_img_tag, bgUrl);
        if(imgShadow != null) {
            imgShadow.setVisibility(INVISIBLE);
            if (!TextUtils.isEmpty(bgUrl) && TextUtils.isEmpty(shadowUrl)) {
                imgShadow.setBackgroundResource(R.drawable.shadow_focus_home_item_v2);
            }
        }
        setImg(delay);
    }

    /**
     * 设置背景图片宽
     *
     * @param width 宽值
     */
    public void setImageViewWidth(int width) {
        imgBgWidth = width;
        if ("custom".equals(showType)) {
            FrameLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
            lp.width = imgBgWidth;
            imageView.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            lp.width = imgBgWidth;
            imageView.setLayoutParams(lp);
        }
    }

    /**
     * 设置背景图片宽
     *
     * @param width 宽值
     */
    public void setImageViewSize(int width,int height) {

        imgBgWidth = width;
        imgBgHeight = height;
        if ("custom".equals(showType)) {
            FrameLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
            lp.width = imgBgWidth;
            lp.height = height;
            imageView.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            lp.width = imgBgWidth;
            lp.height = height;
            imageView.setLayoutParams(lp);
            if ("topDown".equals(showType)) {
                FrameLayout.LayoutParams lpBorder = (LayoutParams) borderImgView.getLayoutParams();
                lpBorder.height = imgBgHeight;
                borderImgView.setLayoutParams(lpBorder);
            }
        }
        requestLayout();
    }

    /**
     * 设置背景图片高
     *
     * @param height 高值
     */
    public void setImageViewHeight(int height) {
        imgBgHeight = height;
        if ("custom".equals(showType)) {
            FrameLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
            lp.height = imgBgHeight;
            imageView.setLayoutParams(lp);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImg(int delay) {
        try{
            if (imageView != null) {
                if(handler != null && runnable != null){
                    handler.removeCallbacks(runnable);
                }
                if (getContext() != null){
                    if ("leftRight".equals(showType)) {
                        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.home_place_left_bottom_bg));
                    } else {
                        if(enablePlaceholder) {
                            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.home_place_bg));
                        }else{
                            imageView.setImageDrawable(null);
                        }
                    }
                }

                if (bgUrl == null){
                    return;
                }
                if(delay < 1){
                    if(usePostTask()){
                        mPostHandler.postTask(POST_CATE_IMG,getType(), () -> {
                            if(getGlideSafeContext() != null)
                                Glide.with(getGlideSafeContext()).asBitmap().load(bgUrl).apply(options).into(imageView);
                        }, delay);
                    }else{
                        if(getGlideSafeContext() != null)
                            Glide.with(getGlideSafeContext()).asBitmap().load(bgUrl).apply(options).into(imageView);
                    }

                }else{
                    if (runnable == null) {
                        runnable = () ->{
                            try {
//                                String bgUrl = (String) imageView.getTag();
                                String bgUrl = (String) imageView.getTag(R.id.home_item_img_tag);
                                if(getGlideSafeContext() != null) {
                                    int width = (int) (imgBgWidth * 0.8);
                                    int height = (int) (imgBgHeight * 0.8);
                                    Glide.with(getGlideSafeContext()).asBitmap().override(width,height).load(bgUrl).apply(options).into(imageView);
//                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                }
                            }catch (Exception e){

                            }
                        };

                    }
                    if(usePostTask()){
                        mPostHandler.postTask(POST_CATE_IMG,this.hashCode(),runnable,delay);
                    }else {
                        if (handler == null) {
                            handler = new Handler(Looper.getMainLooper());
                        }
                        handler.postDelayed(runnable, delay);
                    }
                }
            }
        }catch (Exception e){

        }

    }

    int getType(){
        return hashCode();
    }
    /**
     * 设置阴影背景
     */
    private void setShadowImg(boolean isFocus) {
        if(usePostTask()){
            mPostHandler.postTask(POST_CATE_SHADOW, getType(), () -> {
                if (TextUtils.isEmpty(shadowUrl)) {
                    if (imgShadow == null) {
                        return;
                    }
                    if (isHideShadow){
                        if (imgShadow.getVisibility() == VISIBLE){
                            imgShadow.setVisibility(INVISIBLE);
                        }
                        return;
                    }
                    if (EsMap != null  && "1".equals(EsMap.getString("detailStyle"))){
                        if (imgShadow.getVisibility() == VISIBLE){
                            imgShadow.setVisibility(INVISIBLE);
                        }
                        return;
                    }
                    if (isFocus && !isHideShadow) {
                        imgShadow.setVisibility(VISIBLE);
                    } else {
                        imgShadow.setVisibility(INVISIBLE);
                    }
                } else {
                    if (imgIrregularShadow == null) {
                        return;
                    }
                    if (isFocus) {
                        imgIrregularShadow.setVisibility(VISIBLE);
                    } else {
                        imgIrregularShadow.setVisibility(INVISIBLE);
                    }
                }
            },100);
        }else {
            if (TextUtils.isEmpty(shadowUrl)) {
                if (imgShadow == null) {
                    return;
                }
                if (isHideShadow) {
                    if (imgShadow.getVisibility() == VISIBLE) {
                        imgShadow.setVisibility(INVISIBLE);
                    }
                    return;
                }
                if (EsMap != null && "1".equals(EsMap.getString("detailStyle"))) {
                    if (imgShadow.getVisibility() == VISIBLE) {
                        imgShadow.setVisibility(INVISIBLE);
                    }
                    return;
                }
                if (isFocus && !isHideShadow) {
                    imgShadow.setVisibility(VISIBLE);
                } else {
                    imgShadow.setVisibility(INVISIBLE);
                }
            } else {
                if (imgIrregularShadow == null) {
                    return;
                }
                if (isFocus) {
                    imgIrregularShadow.setVisibility(VISIBLE);
                } else {
                    imgIrregularShadow.setVisibility(INVISIBLE);
                }
            }
        }

    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //zhaopeng add
        if( shimmerWidget != null){
            shimmerWidget.draw(canvas);
        }
    }

    /**
     * zhaopeng add
     * @return
     */
    private ShimmerWidget getShimmerWidget(){
        if (shimmerWidget == null) {
            shimmerWidget = new ShimmerWidget.Builder(getContext(), this)
                    .build();
        }
        shimmerWidget.setSize(imgBgWidth, imgBgHeight);
        return shimmerWidget;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        //zhaopeng add
        invokeFocusChange(gainFocus);
    }

    boolean lastState = false;
    private void invokeFocusChange(boolean gainFocus){
        if(lastState == gainFocus){
            return;
        }
        lastState = gainFocus;
        if(enableShimmer) {
            if (gainFocus) {
                getShimmerWidget().onFocusChange(true);
            } else {
                if (shimmerWidget != null) {
                    getShimmerWidget().onFocusChange(false);
                }
            }
            postInvalidateDelayed(16);
        }
        //zhaopeng add end

        if ("custom".equals(this.showType)) {
            //缩放设置
            mFocusScaleX = 1.1f;
            mFocusScaleY = 1.1f;
            handlerFocusScale(gainFocus, mDuration);
            //阴影框设置
            setShadowImg(gainFocus);

            if (titleview != null && titleViewFocus!=null&&isShowTitle) {
                titleview.isFocus(gainFocus);
                titleViewFocus.isFocus(gainFocus);
                RenderUtil.requestNodeLayout(this);
            }
            if (gainFocus) {
                //边框
                if (borderImgView != null && showBorder) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        borderImgView.setBackground(getContext().getResources().getDrawable(R.drawable.home_item_focus));
                    }
                }
                //水波纹
                if (mRippleView != null && showRippleView && imgRipple != null) {
                    if (isHideRipple){
                        mRippleView.stopAnim();
                    }else{
                        mRippleView.setVisibility(VISIBLE);
                    }
                    imgRipple.setVisibility(VISIBLE);
                }
            } else {
                if (borderImgView != null && showBorder) {
                    borderImgView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
                if (mRippleView != null && showRippleView && imgRipple != null) {
                    mRippleView.setVisibility(INVISIBLE);
                    imgRipple.setVisibility(INVISIBLE);
                }
            }
        }
        else if ("topDown".equals(showType)) {
            if (focusRlView == null) {
                return;
            }
            if (gainFocus) {
                focusRlView.setVisibility(VISIBLE);
                verticalMainTitle.setVisibility(INVISIBLE);
                setFocusVerticalColor(ColorStateList.valueOf(focusMainColor));

                if (floatTitle != null) {
                    floatTitle.setVisibility(INVISIBLE);
                }
                if (focusPlayingImg.getVisibility() == VISIBLE) {
                    if (mRippleView != null ) {
                        mRippleView.setVisibility(INVISIBLE);
                    }
                    if (imgRipple != null){
                        imgRipple.setVisibility(INVISIBLE);
                    }
                } else {
                    if (isHideRipple){
                        mRippleView.stopAnim();
                    }else{
                        mRippleView.setVisibility(VISIBLE);
                    }
                    if (imgRipple != null){
                        imgRipple.setVisibility(VISIBLE);
                    }
                }
                if (isFree) {
                    if (borderImgView != null && showBorder) {
                        borderImgView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.home_item_focus));
                    }
                } else {
                    if (borderImgView != null && showBorder) {
                        borderImgView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.home_item_focus_vip));
                    }
                }
            } else {
                if (!isSelected){
                    setMainTitleColor(mainTitleColor);
                }
                if (mRippleView != null) {
                    mRippleView.setVisibility(INVISIBLE);
                }
                if (imgRipple != null){
                    imgRipple.setVisibility(INVISIBLE);
                }
                focusRlView.setVisibility(INVISIBLE);
                verticalMainTitle.setVisibility(VISIBLE);
                if (floatTitle != null && !TextUtils.isEmpty(fTitle)) {
                    floatTitle.setVisibility(VISIBLE);
                }
                if (borderImgView != null && showBorder) {
                    borderImgView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
            }
        }
        else if ("leftRight".equals(showType)) {
            //设置图片
            if (gainFocus) {
//                //字体颜色
                setMainTitleColor(ColorStateList.valueOf(focusMainColor));
                //边框
                if (isFree) {
                    if (borderImgView != null && showBorder) {
                        borderImgView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.home_item_focus));
                    }
                } else {
                    if (borderImgView != null && showBorder) {
                        borderImgView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.home_item_focus_vip));
                    }
                }
                if (playingImg != null && playingImg.getVisibility() == VISIBLE) {
                    if (mRippleView != null) {
                        mRippleView.setVisibility(GONE);
                    }
                    if (imgRipple != null){
                        imgRipple.setVisibility(GONE);
                    }
                } else {
                    if (isHideRipple){
                        mRippleView.stopAnim();
                    }else{
                        mRippleView.setVisibility(VISIBLE);
                    }
                    if (imgRipple != null){
                        imgRipple.setVisibility(VISIBLE);
                    }
                }
            } else {
                if (!isSelected){
                    setMainTitleColor(mainTitleColor);
                }
                if (borderImgView != null && showBorder) {
                    borderImgView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
                if (mRippleView != null) {
                    mRippleView.setVisibility(INVISIBLE);
                }
                if (imgRipple != null){
                    imgRipple.setVisibility(INVISIBLE);
                }
            }

        }
    }

    private boolean mFocused = false;
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(isDuplicateParentStateEnabled()){
            final boolean focused = ExtendUtil.stateContainsAttribute(getDrawableState(), android.R.attr.state_focused);
            if (mFocused == focused){
                return;
            }
            mFocused = focused;
            invokeFocusChange(focused);
        }
    }

    private void handlerFocusScale(boolean gainFocus, int duration) {
        if (isFocusable() && (mFocusScaleX != 1 || mFocusScaleY != 1)) {
            TVFocusAnimHelper.handleOnFocusChange(this, gainFocus, mFocusScaleX, mFocusScaleY, duration);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            if (imageView != null ){
                imageView.destroyDrawingCache();
            }
            isDetached = true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (handler != null && runnable != null && isDetached && imageView != null){

            String url = (String) imageView.getTag(R.id.home_item_img_tag);
            setBgUrl(url,false,loadImgDelay);
            isDetached = false;
        }

    }

    @Override
    public void resetProps() {
    }

    void clearAllPost(){
        if(usePostTask()) {
            for (int i = 0; i < POST_CATEGORIES.length; i++) {
                mPostHandler.clearTask(POST_CATEGORIES[i], getType());
            }
        }
    }
    @Override
    public void onResetBeforeCache() {
        clearAllPost();
        if (imageView != null && getGlideSafeContext() != null) {
            if (imageView != null ){
                imageView.destroyDrawingCache();
                imageView.setImageBitmap(null);
                imageView.setImageDrawable(null);
            }
            Glide.with(getGlideSafeContext()).clear(imageView);
            Glide.get(getContext().getApplicationContext()).clearMemory();
        }

        //标题
        if (titleview != null){
            titleview.setAlpha(0);
            titleview.initViewData();
        }
        if (titleViewFocus != null){
            titleViewFocus.initViewData();
        }

        //角标
        if(cornerTextView != null){
            cornerTextView.setText("");
            cornerTextView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
        //阴影图片
        if (!TextUtils.isEmpty(this.shadowUrl)&&imgIrregularShadow != null && getContext() != null){
            imgIrregularShadow.destroyDrawingCache();
            imgIrregularShadow.setImageBitmap(null);
            imgIrregularShadow.setImageDrawable(null);
            Glide.with(getGlideSafeContext()).clear(imgIrregularShadow);
        }
    }


    private void setCacheData(){
        try{
            //设置图片
            setBgUrl(bgUrl,false,loadImgDelay);
            //设置标题
            setEsMap(this.EsMap);

            //设置角标
            setCornerColor(this.cornerColor);
            setCornerTextSize(this.cornerSize);
            setCornerBgDrawable(this.cornerDrawable);
            setCorner(this.cornerContent);
            //设置阴影图片
            setShadowUrl(this.shadowUrl);
        }catch (Exception e){
        }
    }

    @Override
    public void clear() {

    }

    //zhaopeng add
//    private Runnable contentTask;

    @Override
    public void setContentData(Object itemData) {
        //add by zhaopeng 当选集组件中的数据变化时（比如拉取更多数据），调用此方法来更新内容
        if(itemData instanceof TemplateItem){
            final TemplateItem ti = (TemplateItem) itemData;
            setCorner(ti.obtainFlagText());
            setMainTitle(ti.obtainNormalTitle());
            setBgUrl((String) ti.getCover(),false,300);
            setFloatTitle(ti.obtainFloatText());
        }
    }


    @Override
    public void setSingleSelect(boolean selected) {
        if ("leftRight".equals(showType)) {
            isSelected = selected;
            if (selected) {
                if (mRippleView != null) {
                    mRippleView.setVisibility(INVISIBLE);
                }
                if (imgRipple != null){
                    imgRipple.setVisibility(INVISIBLE);
                }
                //展示当前播放图标
                if (playingImg != null) {
                    if (isFree) {
                        playingImg.setImageResource(R.mipmap.playing_free);
                    } else {
                        playingImg.setImageResource(R.mipmap.playing_vip);
                    }
                    playingImg.setVisibility(VISIBLE);
                }
                int color = mainTitleColor.getColorForState(new int[]{android.R.attr.state_selected, android.R.attr.state_enabled},Color.parseColor("#ffffffff"));
                mainTitle.setTextColor(color);
            } else {
                if (playingImg != null) {
                    playingImg.setVisibility(INVISIBLE);
                }
                if (!isFocused()){
                    mainTitle.setTextColor(mainTitleColor);
                }
            }
        } else if ("topDown".equals(showType)) {
            isSelected = selected;
            if (selected) {
                if (mRippleView != null) {
                    mRippleView.setVisibility(INVISIBLE);
                }
                if (imgRipple != null){
                    imgRipple.setVisibility(INVISIBLE);
                }
                if (isFree) {
                    focusPlayingImg.setImageResource(R.mipmap.playing_free);
                } else {
                    focusPlayingImg.setImageResource(R.mipmap.playing_vip);
                }
                if (focusPlayingImg != null) {
                    focusPlayingImg.setVisibility(VISIBLE);
                }
                int color = mainTitleColor.getColorForState(new int[]{android.R.attr.state_selected, android.R.attr.state_enabled},Color.parseColor("#ffffffff"));
                verticalMainTitle.setTextColor(color);
            } else {
                if (focusPlayingImg != null) {
                    focusPlayingImg.setVisibility(INVISIBLE);
                }
                if (!isFocused()){
                    verticalMainTitle.setTextColor(mainTitleColor);
                }
            }

        }
    }

    @Override
    public void notifySaveState() {
        //1. 保留url等数据，把图片置空，把文字隐藏或者置空
        onResetBeforeCache();
    }

    @Override
    public void notifyRestoreState() {
        //1. 使用url等数据，恢复图片和文字等
        setCacheData();
    }


    public void setItemDisplay(boolean enable) {
    }

    private Bitmap getWaterRippleNormalBitmap() {
        if(waterRippleNormalBitmap == null){
            String basePath = EsProxy.get().getEsAppRuntimePath(((HippyInstanceContext) getContext()).getEngineContext().getEngineId());
            if(TextUtils.isEmpty(basePath)) return null;
            String imagePath = basePath + "/assets/water_play.png";
            if(imagePath.startsWith("/main")) {
                imagePath = imagePath.substring(1);
                try {
                    waterRippleNormalBitmap = BitmapFactory.decodeStream(getContext().getAssets().open(imagePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                waterRippleNormalBitmap = BitmapFactory.decodeFile(imagePath, options);
            }
        }
        return waterRippleNormalBitmap;
    }

    private Bitmap getWaterRippleVipBitmap() {
        if(waterRippleVipBitmap == null){
            String basePath = EsProxy.get().getEsAppRuntimePath(((HippyInstanceContext) getContext()).getEngineContext().getEngineId());
            if(TextUtils.isEmpty(basePath)) return null;
            String imagePath = basePath + "/assets/water_play_vip.png";
            if(imagePath.startsWith("/main")) {
                imagePath = imagePath.substring(1);
                try {
                    waterRippleVipBitmap = BitmapFactory.decodeStream(getContext().getAssets().open(imagePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                waterRippleVipBitmap = BitmapFactory.decodeFile(imagePath, options);
            }
        }
        return waterRippleVipBitmap;
    }

    private Context getGlideSafeContext() {
        Context context = getContext();
        if(context instanceof Activity){
            if (((Activity) context).isFinishing()
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && ((Activity) context).isDestroyed())) {
                return null;
            }
        }
        return context;
    }

    @Override
    public void setRootPostHandlerView(PostHandlerView pv) {
        this.mPostHandler = pv;
    }

    private boolean usePostTask(){
        return this.mPostHandler != null;
    }

}
