package com.quicktvui.support.core.module.toast;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 *
 */
@ESKitAutoRegister
public class ToastModule implements IEsModule, IEsInfo {

//    private ToastUtils toastUtils;
    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    public void make() {
//        toastUtils = new ToastUtils();
    }

    public void setMode(String mode) {
//        if (L.DEBUG) {
//            L.logD("#-------------setMode-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "mode:---->>>" + mode
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setMode(mode);
//        }
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
//        if (L.DEBUG) {
//            L.logD("#-------------setGravity-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "gravity:---->>>" + gravity
//                    + "xOffset:---->>>" + xOffset
//                    + "yOffset:---->>>" + yOffset
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setGravity(gravity, xOffset, yOffset);
//        }
    }

    public void setBackgroundColor(int backgroundColor) {
//        if (L.DEBUG) {
//            L.logD("#-------------setBackgroundColor-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "backgroundColor:---->>>" + backgroundColor
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setBgColor(backgroundColor);
//        }
    }

    public void setTextColor(int msgColor) {
//        if (L.DEBUG) {
//            L.logD("#-------------setTextColor-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "msgColor:---->>>" + msgColor
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setTextColor(msgColor);
//        }
    }

    public void setTextSize(int textSize) {
//        if (L.DEBUG) {
//            L.logD("#-------------setTextSize-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "textSize:---->>>" + textSize
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setTextSize(textSize);
//        }
    }

    public void setTextSize(int unit, int textSize) {
//        if (L.DEBUG) {
//            L.logD("#-------------setTextSize-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "textUnit:---->>>" + unit
//                    + "textSize:---->>>" + textSize
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setTextSize(unit, textSize);
//        }
    }

    public void setLongDuration(boolean isLong) {
//        if (L.DEBUG) {
//            L.logD("#-------------setLongDuration-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "isLong:---->>>" + isLong
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setDurationIsLong(isLong);
//        }
    }


    public void setNotUseSystemToast() {
//        if (L.DEBUG) {
//            L.logD("#-------------setNotUseSystemToast-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//            );
//        }
//        if (toastUtils != null) {
//            toastUtils.setNotUseSystemToast();
//        }
    }

    public void show(String text) {
//        if (L.DEBUG) {
//            L.logD("#-------------show-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "text:---->>>" + text
//            );
//        }
//        if (showWithSpecialPlatform(text, true)) {
//            return;
//        }
//        if (toastUtils != null) {
//            toastUtils.show(text);
//        }
        showShortToast(text);
    }

    public void cancel() {
//        if (L.DEBUG) {
//            L.logD("#-------------cancel-------------->>>");
//        }
//        if (toastUtils != null) {
//            toastUtils = null;
//        }
    }

    public void setBackground(EsMap params) {
//        if (L.DEBUG) {
//            L.logD("#-------------setBackground-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "params:---->>>" + params
//            );
//        }
//        EsProxy.get().loadImageBitmap(params, new EsCallback<Bitmap, Throwable>() {
//            @Override
//            public void onSuccess(Bitmap result) {
//                if (L.DEBUG) {
//                    L.logD("#-------------onSuccess-------------->>>"
//                            + "result:---->>>" + result
//                    );
//                }
//                if (toastUtils != null) {
//                    toastUtils.setBgDrawable(new BitmapDrawable(result));
//                }
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                throwable.printStackTrace();
//                if (L.DEBUG) {
//                    L.logD("#-------------onFailed-------------->>>"
//                            + "e:---->>>" + throwable.getMessage()
//                    );
//                }
//            }
//        });
    }

    public void setLeftIcon(EsMap params) {
//        if (L.DEBUG) {
//            L.logD("#-------------setLeftIcon-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "params:---->>>" + params
//            );
//        }
//        EsProxy.get().loadImageBitmap(params, new EsCallback<Bitmap, Throwable>() {
//            @Override
//            public void onSuccess(Bitmap result) {
//                if (L.DEBUG) {
//                    L.logD("#-------------onSuccess-------------->>>"
//                            + "result:---->>>" + result
//                    );
//                }
//                if (toastUtils != null) {
//                    toastUtils.setLeftIcon(new BitmapDrawable(result));
//                }
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                throwable.printStackTrace();
//                if (L.DEBUG) {
//                    L.logD("#-------------onFailed-------------->>>"
//                            + "e:---->>>" + throwable.getMessage()
//                    );
//                }
//            }
//        });
    }

    public void setTopIcon(EsMap params) {
//        if (L.DEBUG) {
//            L.logD("#-------------setTopIcon-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "params:---->>>" + params
//            );
//        }
//        EsProxy.get().loadImageBitmap(params, new EsCallback<Bitmap, Throwable>() {
//            @Override
//            public void onSuccess(Bitmap result) {
//                if (L.DEBUG) {
//                    L.logD("#-------------onSuccess-------------->>>"
//                            + "result:---->>>" + result
//                    );
//                }
//                if (toastUtils != null) {
//                    toastUtils.setTopIcon(new BitmapDrawable(result));
//                }
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                throwable.printStackTrace();
//                if (L.DEBUG) {
//                    L.logD("#-------------onFailed-------------->>>"
//                            + "e:---->>>" + throwable.getMessage()
//                    );
//                }
//            }
//        });
    }

    public void setRightIcon(EsMap params) {
//        if (L.DEBUG) {
//            L.logD("#-------------setRightIcon-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "params:---->>>" + params
//            );
//        }
//        EsProxy.get().loadImageBitmap(params, new EsCallback<Bitmap, Throwable>() {
//            @Override
//            public void onSuccess(Bitmap result) {
//                if (L.DEBUG) {
//                    L.logD("#-------------onSuccess-------------->>>"
//                            + "result:---->>>" + result
//                    );
//                }
//                if (toastUtils != null) {
//                    toastUtils.setRightIcon(new BitmapDrawable(result));
//                }
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                throwable.printStackTrace();
//                if (L.DEBUG) {
//                    L.logD("#-------------onFailed-------------->>>"
//                            + "e:---->>>" + throwable.getMessage()
//                    );
//                }
//            }
//        });
    }


    public void setBottomIcon(EsMap params) {
//        if (L.DEBUG) {
//            L.logD("#-------------setBottomIcon-------------->>>"
//                    + "toastUtils:---->>>" + toastUtils
//                    + "params:---->>>" + params
//            );
//        }
//        EsProxy.get().loadImageBitmap(params, new EsCallback<Bitmap, Throwable>() {
//            @Override
//            public void onSuccess(Bitmap result) {
//                if (L.DEBUG) {
//                    L.logD("#-------------onSuccess-------------->>>"
//                            + "result:---->>>" + result
//                    );
//                }
//                if (toastUtils != null) {
//                    toastUtils.setBottomIcon(new BitmapDrawable(result));
//                }
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                throwable.printStackTrace();
//                if (L.DEBUG) {
//                    L.logD("#-------------onFailed-------------->>>"
//                            + "e:---->>>" + throwable.getMessage()
//                    );
//                }
//            }
//        });

    }

    //-------------------------------------------------------

    public void showToast(String message) {
        showShortToast(message);
    }

    public void showLongToast(String message) {
//        if (showWithSpecialPlatform(message, false)) {
//            return;
//        }
//        ToastUtils.showLong(message);
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void showShortToast(String message) {
//        if (showWithSpecialPlatform(message, true)) {
//            return;
//        }
//        ToastUtils.showShort(message);
        // 测试内存泄漏
        // TestToastLeak.showToast(message);
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }

    @Override
    public void destroy() {
//        if (toastUtils != null) {
//            ToastUtils.cancel();
//            toastUtils = null;
//        }
        this.context = null;
    }

    private boolean showWithSpecialPlatform(String message, boolean shortToast) {
        try {
            String channel = EsProxy.get().getChannel();
            boolean isNewsmy = "newsmy".equals(channel);
            boolean isHaier = "haier".equals(Build.BRAND);
            if (isNewsmy || isHaier) {
                Toast.makeText(context, message, shortToast ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
