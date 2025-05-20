package com.quicktvui.sdk.core.protocol;

import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_APP_V1;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_NATIVE_APP_V1;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_NATIVE_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_V1;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ARGS_V1;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ARGS_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_EXP;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_FROM;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_PACKAGE_V1;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_PACKAGE_V2;

import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

import com.quicktvui.sdk.base.args.EsMap;

/**
 * 1.0版本协议解析
 *
 * Create by weipeng on 2022/04/18 15:15
 * Describe 就写一解析
 * {
 *   "exp": {
 *     "os_ver": "11",
 *     "os": "android",
 *     "idfa": "",
 *     "es_id": "5aa2d2d241d3bdbaaab3f043c9d00a74",
 *     "app_ver_code": "920",
 *     "chainid": "713445294264852480",
 *     "app_bundle": "com.hili.mp.demo3",
 *     "appid": "demo3",
 *     "es_package": "es.zhubogou.cibn.tv",
 *     "from": "3bf61e5d4bf58dbee70a70aed6da4ecb",
 *     "mac_wifi": "a8:9c:ed:90:d5:a0",
 *     "sdk_ver": "920",
 *     "sdk_channel": "browser",
 *     "brand": "cepheus",
 *     "app_ver_name": "2.1.920"
 *   },
 *   "name": "__AC_MAIN__",
 *   "params": "{\"liveId\":274009696}"
 * }
 */
public class Protocol_1 {

    /** 1.0版本的协议解析 **/
    public static void dispatch(EsMap evtParams, JSONObject old) throws Exception{
        L.logIF("protocol 1.0 " + old);
        JSONObject jo = new JSONObject();
        String name = old.getString(K_ACTION_V1);
        switch (name){
            case K_ACTION_ES_APP_V1:
                jo.put(K_ACTION_V2, K_ACTION_ES_APP_V2);
                break;
            case K_ACTION_NATIVE_APP_V1:
                jo.put(K_ACTION_V2, K_ACTION_NATIVE_APP_V2);
                break;
        }

        if(old.has(K_ARGS_V1)){
            jo.put(K_ARGS_V2, old.get(K_ARGS_V1));
        }

        if(old.has(K_EXP)){
            JSONObject exp = old.getJSONObject(K_EXP);
            if(exp.has(K_PACKAGE_V1)) jo.put(K_PACKAGE_V2, exp.get(K_PACKAGE_V1));
            jo.put(K_FROM, exp.get(K_FROM));
        }

        Protocol_2.dispatch(evtParams, jo);
    }

}
