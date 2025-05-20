package com.quicktvui.support.ui.item;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.support.ui.largelist.TemplateUtil;
import com.tencent.mtt.hippy.uimanager.InternalExtendViewUtil;

/**
 *
 */
@Deprecated
@ESKitAutoRegister
public class ItemViewComponent implements IEsComponent<HomeItemView> {

    @Override
    public HomeItemView createView(Context context, EsMap params) {
        if (params != null) {
            return new HomeItemView(context, params);
        }
        return new HomeItemView(context);
    }


    @EsComponentAttribute
    public void itemHomeJson(HomeItemView itemView, EsMap json) {
        itemView.setEsMap(json);
    }

    @EsComponentAttribute
    public void itemShadowUrl(HomeItemView view, String shadowUrl) {
        if (view != null) {
            view.setShadowUrl(shadowUrl);
        }
    }

    @EsComponentAttribute
    public void itemBackground(HomeItemView view, String bgUrl) {
        if (view != null) {
            view.setBgUrl(bgUrl);
        }
    }

    @EsComponentAttribute
    public void itemBackgroundWidth(HomeItemView view, int width) {
        if (view != null && width > 0) {
            view.setImageViewWidth(width);
        }
    }

    @EsComponentAttribute
    public void itemCornerTextSize(HomeItemView view, int size) {
        if (view != null) {
            view.setCornerTextSize(size);
        }
    }

    @EsComponentAttribute
    public void cornerBg(HomeItemView view, EsMap json) {
        if (view != null) {
            Drawable cornerDrawable = TemplateUtil.createGradientDrawableDrawable(json, "cornerBgColor");
            view.setCornerBgDrawable(cornerDrawable);
        }
    }

    @EsComponentAttribute
    public void loadImgDelay(HomeItemView view, int delay) {
        if (view != null) {
            view.setLoadImgDelay(delay);
        }
    }

    @EsComponentAttribute
    public void itemBackgroundHeight(HomeItemView view, int height) {
        if (view != null && height > 0) {
            view.setImageViewHeight(height);
        }
    }

    @EsComponentAttribute
    public void itemShowBorder(HomeItemView view, boolean isShow) {
        if (view != null) {
            view.setShowBorder(isShow);
        }
    }

    @EsComponentAttribute
    public void requestFocusForce(HomeItemView view, boolean b) {
        if (view != null && b) {
            InternalExtendViewUtil.unBlockRootFocus(view);
            view.requestFocus();
        }
    }

    @EsComponentAttribute
    public void itemShowShimmer(HomeItemView view, boolean enable) {
        if (view != null) {
            view.setEnableShimmer(enable);
        }
    }

    @EsComponentAttribute
    public void hideRipper(HomeItemView view, boolean enable) {
        if (view != null) {
            view.setHideRipple(enable);
        }
    }

    @EsComponentAttribute
    public void hideShadow(HomeItemView view, boolean enable) {
        if (view != null) {
            view.setHideShadow(enable);
        }
    }

    @EsComponentAttribute
    public void shadowMargin(HomeItemView view, EsMap map) {
        if (view != null) {
            view.setShadowMargin(map);
        }
    }

    @EsComponentAttribute
    public void display(HomeItemView view, boolean enable) {
        if (view != null) {
            view.setItemDisplay(enable);
        }
    }

    @EsComponentAttribute
    public void showTitle(HomeItemView view, boolean enable) {
        if (view != null) {
            view.setShowTitle(enable);
        }
    }

    @Override
    public void dispatchFunction(HomeItemView view, String eventName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(eventName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(HomeItemView view) {

    }
}
