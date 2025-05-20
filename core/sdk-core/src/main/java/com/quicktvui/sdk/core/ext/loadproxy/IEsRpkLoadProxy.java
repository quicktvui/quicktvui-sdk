package com.quicktvui.sdk.core.ext.loadproxy;

import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.core.entity.InfoEntity;

import java.io.File;
import java.util.Map;

/**
 * 介入rpk下载的代理
 */
public interface IEsRpkLoadProxy {

    /** 使用自己的解密规则 **/
    int DEC_RET_USE_SELF = 0;
    /** 使用默认解密规则 **/
    int DEC_RET_USE_DEFAULT = 1;

    /**
     * 鉴权
     * @param params
     * @return authCode
     */
    EsException auth(Map<String, String> params);

    /**
     * 请求rpk信息
     *
     * @param params 参数
     * @return 实体类
     */
    InfoEntity requestRpkInfo(Map<String, String> params);

    /**
     * 解密rpk包
     *
     * @param originFile  原包路径
     * @param decryptFile 解密到的路径
     * @return 处理结果<br/>
     * {{@link IEsRpkLoadProxy#DEC_RET_USE_SELF}} {{@link IEsRpkLoadProxy#DEC_RET_USE_DEFAULT}}
     */
    int decryptRpk(File originFile, File decryptFile);
}
