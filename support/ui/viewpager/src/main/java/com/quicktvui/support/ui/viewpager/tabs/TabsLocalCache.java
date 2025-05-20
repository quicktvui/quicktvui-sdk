package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sunrain.toolkit.utils.JsonUtils;
import com.tencent.mtt.hippy.common.Callback;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TabsLocalCache {

    public static  final String TAG = "DebugTabsCache";
    public final String cacheKey;
    private Context context;
    public static final String TABS_CACHE_FILE = "TabsCache";
    public long overTime = 1000 * 60 * 60 * 24 * 7;
    ExecutorService mExecutorService;

    SharedPreferences sharedPreferences;

    public TabsLocalCache(Context context,String cacheKey){
        this.cacheKey = cacheKey;
        this.context = context;
    }

    public ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(1);
        }
        return mExecutorService;
    }

    SharedPreferences getSP(String dirName){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(TABS_CACHE_FILE+"_"+cacheKey+"_"+dirName, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    void getCacheSavedData(String tabID, Callback<TabDataCache> callback){
        getExecutorService().submit(() -> {
            TabDataCache dataCache = new TabDataCache(getSP(TabDataCache.Key).getString(tabID,""),overTime);
            callback.callback(dataCache,null);
        });
    }

    public void setOverTime(long overTime) {
        this.overTime = overTime;
    }

    void cachePageToLocal(String cacheKey,HippyArray data, HippyMap pageParams) {
        getExecutorService().submit(() -> {
            Log.i(TAG,"start cachePageToLocal cacheKey :"+cacheKey);
            long savedTime = new Date().getTime();
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject root = new JSONObject();
                jsonObject.putOpt("root",root);
                root.put("savedTime", savedTime);
                root.put("pageParams", pageParams.toJSONObject());
                root.put("dataArray", data.toJSONArray());
                final String cacheJSonString = jsonObject.toString();
                Log.i(TAG,"cachePageToLocal cacheKey:"+cacheKey+" cacheJSonString:"+cacheJSonString);
                getSP(TabDataCache.Key).edit().putString(cacheKey, cacheJSonString).apply();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static final class TabDataCache {
        public long savedTime;
        public HippyArray data;
        public HippyMap pageParams;
        public static final String Key = "TabData";
        public boolean dataValid = false;

        TabDataCache(String cacheData, long overTime) {
                    if (cacheData != null && !cacheData.isEmpty()) {
                        JSONObject root = JsonUtils.getJSONObject(cacheData, "root", null);
                            try{
                                if (root != null) {
                                    savedTime = root.getLong("savedTime");
                                    if (overTime > 0 && new Date().getTime() - savedTime > overTime) {
                                        Log.w(TAG,"TabDataCache data is out of date savedTime:"+savedTime);
                                        dataValid = false;
                                        return;
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                JSONObject pageParamsJson = root.getJSONObject("pageParams");
                                pageParams = new HippyMap();
                                pageParams.pushJSONObject(pageParamsJson);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                JSONArray jsonArray = root.getJSONArray("dataArray");
                                HippyArray array = new HippyArray();
                                array.pushJSONArray(jsonArray);
                                data = array;
                                dataValid = array.size() > 0;
                                savedTime = new Date().getTime();
                            }catch (Exception e){
                                e.printStackTrace();
                                dataValid = false;
                            }
                    }
        }

        @Override
        public String toString() {
            return "TabDataCache{" +
                    "savedTime=" + savedTime +
                    ", data=" + data +
                    ", pageParams=" + pageParams +
                    ", dataValid=" + dataValid +
                    '}';
        }
    }
}
