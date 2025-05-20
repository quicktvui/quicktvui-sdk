package com.quicktvui.support.ui.largelist;

import com.quicktvui.sdk.base.args.EsMap;

import com.quicktvui.support.ui.ScreenAdapt;


public class Template {
    public String type;
    public int width;
    public int height;
    public int textSize;
    public int floatTextSize;
    public int cornerTextSize;
    public boolean isFree;

    public boolean enablePlayIcon;
    public int itemMargin;
    public EsMap extra;
    public EsMap templateMap;

    public Template() {
    }

    public Template(EsMap map) {
        apply(map);
    }

    public void apply(EsMap props) {
        ScreenAdapt screenAdapt = ScreenAdapt.getInstance();

        type = props.getString("type");
        width = screenAdapt.transform(props.getInt("width"));
        height = screenAdapt.transform(props.getInt("height"));
        textSize = screenAdapt.transform(props.getInt("titleSize"));
        itemMargin = screenAdapt.transform(props.getInt("itemMargin"));
        floatTextSize = screenAdapt.transform(props.getInt("floatTitleSize"));
        cornerTextSize = screenAdapt.transform(props.getInt("cornerTitleSize"));
        enablePlayIcon = props.getBoolean("enablePlayIcon");
        isFree = props.getBoolean("isFree");
        extra = props.getMap("extra");
        this.templateMap = props;
    }
}
