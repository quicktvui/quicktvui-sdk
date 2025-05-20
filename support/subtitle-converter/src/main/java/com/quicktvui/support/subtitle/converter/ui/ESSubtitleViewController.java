package com.quicktvui.support.subtitle.converter.ui;

//@HippyController(name = ESSubtitleViewController.CLASS_NAME)
public class ESSubtitleViewController {
//    public static final String CLASS_NAME = "SubtitleView";
//
//    @Override
//    protected View createViewImpl(Context context) {
//        return null;
//    }
//
//    @Override
//    protected View createViewImpl(Context context, HippyMap iniProps) {
//        return createViewImpl(context, iniProps, false);
//    }
//
//    public View createViewImpl(Context context, HippyMap iniProps, boolean createFromNative) {
//        final ESSubtitleView text = new ESSubtitleView(context);
//        if (iniProps.containsKey("backgroundColor")) {
//            String backgroundColor = iniProps.getString("backgroundColor");
//            text.setBackgroundColor(Color.parseColor(backgroundColor));
//        }
//        return text;
//    }
//
//    @Override
//    public void dispatchFunction(ESSubtitleView view, String functionName, HippyArray var) {
//        super.dispatchFunction(view, functionName, var);
//        switch (functionName) {
//            case "setText":
//                this.setText(view, var.getString(0));
//                break;
//            case "setTextSize":
//                this.setTextSize(view, var.getInt(0));
//                break;
//            case "setTextColor":
//                this.setTextColor(view, var.getString(0));
//                break;
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT, defaultType = HippyControllerProps.STRING, defaultString = "")
//    public void setText(ESSubtitleView esSubtitleView, String text) {
//        if (esSubtitleView != null) {
//            esSubtitleView.setText(text);
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.FONT_SIZE, defaultType = HippyControllerProps.NUMBER)
//    public void setFontSize(ESSubtitleView esSubtitleView, int size) {
//        if (esSubtitleView != null) {
//            esSubtitleView.setTextSize(size);
//        }
//    }
//
//    @HippyControllerProps(name = "typeface", defaultType = HippyControllerProps.STRING, defaultString = "normal")
//    public void setTypeface(ESSubtitleView esSubtitleView, String typeFace) {
//        if (esSubtitleView != null) {
//            esSubtitleView.setTypeStyle(typeFace);
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT_GRAVITY, defaultType = HippyControllerProps.STRING)
//    public void setGravityByString(TVTextView tvTextView,String gravity){
//        if(gravity.contains("|")){
//            final String[] gs = gravity.split("\\|");
//            final int gravity1 = getGravity(gs[0]);
//            final int gravity2 = getGravity(gs[1]);
//            tvTextView.setGravity(gravity1 | gravity2);
//        }else {
//            tvTextView.setGravity(getGravity(gravity));
//        }
//    }
//
//    private int getGravity(String gravity){
//        switch (gravity) {
//            case "center":
//                return Gravity.CENTER;
//            case "top":
//                return Gravity.TOP;
//            case "bottom":
//                return Gravity.BOTTOM;
//            case "end":
//                return Gravity.END;
//            case "centerHorizontal":
//                return Gravity.CENTER_HORIZONTAL;
//            case "centerVertical":
//                return Gravity.CENTER_VERTICAL;
//            case "start":
//            default:
//                return Gravity.START;
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT_SIZE, defaultType = HippyControllerProps.NUMBER)
//    public void setTextSize(ESSubtitleView esSubtitleView, int size) {
//        if (esSubtitleView != null) {
//            esSubtitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) Math.ceil(PixelUtil.dp2px(size)));
//        }
//    }
//
//    @HippyControllerProps(name = "paddingRect", defaultType = HippyControllerProps.ARRAY)
//    public void setPadding(ESSubtitleView esSubtitleView, HippyArray array) {
//        if (array == null) {
//            esSubtitleView.setPadding(0, 0, 0, 0);
//        } else {
//            esSubtitleView.setPadding(
//                    Utils.toPX(array.getInt(0)),
//                    Utils.toPX(array.getInt(1)),
//                    Utils.toPX(array.getInt(2)),
//                    Utils.toPX(array.getInt(3))
//            );
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.ELLIPSIZE_MODE, defaultType = HippyControllerProps.NUMBER, defaultNumber = 2)
//    public void setEllipsize(ESSubtitleView esSubtitleView, int where) {
//        switch (where) {
//            case 0:
//                esSubtitleView.setEllipsize(TextUtils.TruncateAt.START);
//                break;
//            case 1:
//                esSubtitleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
//                break;
//            case 2: {
//                esSubtitleView.setEllipsize(TextUtils.TruncateAt.END);
//                break;
//            }
//            case 3: {
//                esSubtitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                esSubtitleView.setSingleLine();
//                esSubtitleView.setMarqueeRepeatLimit(-1);
//                break;
//            }
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT_LINES, defaultType = HippyControllerProps.NUMBER)
//    public void setLines(ESSubtitleView esSubtitleView, int lines) {
//        if (esSubtitleView != null) {
//            esSubtitleView.setLines(lines);
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT_MAX_LINES, defaultType = HippyControllerProps.NUMBER)
//    public void setMaxLines(ESSubtitleView esSubtitleView, int lines) {
//        if (esSubtitleView != null) {
//            esSubtitleView.setMaxLines(lines);
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT_COLOR, defaultType = HippyControllerProps.STRING)
//    public void setTextColor(ESSubtitleView esSubtitleView, String color) {
//        final int colorInt = Color.parseColor(color);
//        if (esSubtitleView != null) {
//            esSubtitleView.setTextColor(ColorStateList.valueOf(colorInt));
//        }
//    }
//
//    @HippyControllerProps(name = NodeProps.TEXT_SELECT, defaultType = HippyControllerProps.BOOLEAN)
//    public void setSelect(ESSubtitleView esSubtitleView, boolean enable) {
//        esSubtitleView.setSelected(enable);
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        super.onFocusChange(v, hasFocus);
//        if (v instanceof ESSubtitleView) {
//            v.setSelected(hasFocus);
//        }
//    }
}
