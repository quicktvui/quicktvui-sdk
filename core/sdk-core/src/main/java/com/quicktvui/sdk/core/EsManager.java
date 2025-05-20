package com.quicktvui.sdk.core;

import com.quicktvui.sdk.core.internal.EsManagerInner;

/**
 * <br>
 *
 * <br>
 */
public class EsManager {

    //region 单例

    private static final class EsManagerHolder{
        private static final IEsManager INSTANCE = new EsManagerInner();
    }

    public static IEsManager get(){
        return EsManagerHolder.INSTANCE;
    }

    private EsManager(){}

    //endregion

}
