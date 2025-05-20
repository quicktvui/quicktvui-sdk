package com.quicktvui.support.ui.viewpager.utils;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.quicktvui.hippyext.views.fastlist.FastAdapterUtil;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.NodeProps;
import com.tencent.mtt.hippy.uimanager.CustomControllerHelper;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;

/**
 * @auth: njb
 * @date: 2022/8/24 16:45
 * @desc:
 */
public class RenderNodeUtils {
    /**
     * 获取当前view的子节点
     *
     * @param view
     * @return
     */
    public static RenderNode getRenderNode(View view) {
        return getHippyContext(view).getRenderManager().getRenderNode(view.getId());
    }

    public static HippyEngineContext getHippyContext(View view) {
        return ((HippyInstanceContext) view.getContext()).getEngineContext();
    }

    public static <T> T findViewController(HippyEngineContext context, RenderNode node) {
        if (node != null) {
            final HippyViewController t = CustomControllerHelper.getViewController(context.getRenderManager().getControllerManager(),
                    node);
            if (t != null) {
                return (T) t;
            }
        }
        return null;
    }

    public static <T> T findViewController(View view, RenderNode node) {
        final HippyEngineContext context = ((HippyInstanceContext) view.getContext()).getEngineContext();
        if (node != null) {
            final HippyViewController t = CustomControllerHelper.getViewController(context.getRenderManager().getControllerManager(),
                    node);
            if (t != null) {
                return (T) t;
            }
        }
        return null;
    }

    public static View findViewById(HippyEngineContext context, int id) {
        return context.getRenderManager().getControllerManager().findView(id);
    }


    public static void doDiffProps(HippyViewController vc, HippyMap props, View view) {
        for (String prop : props.keySet()) {
            if (prop == null) {
                continue;
            }
            if (prop.equals(NodeProps.STYLE) && props.get(prop) instanceof HippyMap) {
                doDiffProps(vc, props.getMap(prop), view);
            } else {
                invokeProp(vc, props, prop, view);
            }
        }
    }

    public static void replaceRootData(HippyArray array,int position,Object newData){
        if(array == null || array.size() < position){
            Log.e("ReplaceChildData","replaceChildData error on array == null || array.size() < position ,position:"+position);
            return;
        }
        array.setObject(position,newData);
    }

    public static void replaceChildData(HippyArray array,int position,Object newData,int childIndex){
        if(array == null || array.size() < position){
            Log.e("ReplaceChildData","replaceChildData error on array == null || array.size() < position ,position:"+position);
            return;
        }
        final Object rootItem = array.getObject(position);
        if(rootItem instanceof HippyArray){
            Log.i("ReplaceChildData","replaceChildData  on section position:"+position+",childIndex:"+childIndex);
            ((HippyArray) rootItem).setObject(childIndex,newData);
        }else if(rootItem instanceof HippyMap) {
            final HippyMap itemMap = (HippyMap) rootItem;
            HippyArray children = null;
            if(itemMap.containsKey("children")){
                children = itemMap.getArray("children");
            }
            if(itemMap.containsKey("itemList")){
                children = itemMap.getArray("itemList");
            }
            if (children != null) {
                Log.i("ReplaceChildData","replaceChildData2  on section position:"+position+",childIndex:"+childIndex);
                children.setObject(childIndex,newData);
            }else{
                Log.e("ReplaceChildData","replaceChildData on children is null , 请确保itemData中有key为children的或者itemList的数组,itemData:"+itemMap);
            }
        }else{
            Log.e("ReplaceChildData","replaceChildData error on rootItem is not a HippyArray rootItem:"+rootItem);
        }
    }


    public static int replaceData(HippyArray array,Object newData,Object id,String key){
        int index = -1;
        if (array == null || id == null || TextUtils.isEmpty(key)) {
            return -1;
        }
        for(int i = 0 ; i < array.size(); i ++){
            if (array.getObject(i) instanceof HippyMap) {
                final HippyMap item = array.getMap(i);
                if(item.get(key) != null && item.get(key).equals(id)){
                    array.setObject(i,newData);
//                    Log.e("DebugReplaceItem","replaceData 1 i:"+i+",item:"+item.get(key));
                    index = i;
                    break;
                }else{
//                    Log.i("DebugReplaceItem","replaceData 1 skip!!! i:"+i+",item:"+item.get(key)+",item:"+item+",key:"+key);
                    for(String ik : item.keySet()){
                        if(item.getArray(ik) != null){
//                            Log.e("DebugReplaceItem","replaceData 2 i:"+i+",item:"+item.get(key));
                            int childIndex =  replaceData(item.getArray(ik),newData,id,key);
                            if(childIndex > -1){
                                return childIndex;
                            }
                        }
                    }
                }
            }else if(array.getObject(i) instanceof HippyArray){
                final int childIndex = replaceData(array.getArray(i),newData,id,key);
                Log.i("DebugReplaceItem","replaceData 2 childIndex:"+childIndex+",index:"+index);
                if(childIndex > -1){
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public static int searchItemIndexByData(HippyArray array, Object id, String key){
        if (array == null || id == null || TextUtils.isEmpty(key)) {
            return -1;
        }
        for(int i = 0 ; i < array.size(); i ++){
            if (array.getObject(i) instanceof HippyMap) {
                final HippyMap item = array.getMap(i);
                if(item.get(key) != null && item.get(key).equals(id)){
//                    Log.e("DebugReplaceItem","searchItemIndexByData 1 i:"+i+",item id:"+item.get(key)+",id:"+id+",item:"+item);
                    return i;
                }else{
                    HippyArray children = FastAdapterUtil.findDefaultChildrenFromItem(item);
                    final int result = searchItemIndexByData(children,id,key);
                    if (result != -1) {
                        return result;
                    }else {
                        //从所有列表里遍历
                        for(String k : item.keySet()){
                            Object value = item.get(k);
                            if(value instanceof HippyArray){
//                                Log.i("DebugReplaceItem","search from item key:"+k);
                                int r = searchItemIndexByData((HippyArray) value,id,key);
                                if (r != -1) {
                                    return r;
                                }
                            }
                        }
                    }

//                    Log.i("DebugReplaceItem","searchItemIndexByData 1 skip!!! i:"+i+",item:"+item.get(key)+",item:"+item+",key:"+key);
                }
            }
            if(array.getObject(i) instanceof HippyArray){
                int indexOfChild = searchItemIndexByData(array.getArray(i),id,key);
//                Log.i("DebugReplaceItem","searchItemIndexByData 2 indexOfChild:"+indexOfChild+",index:"+indexOfChild);
                if (indexOfChild > -1) {
                    return i;
                }
            }
        }
        return -1;
    }

    static void invokeProp(HippyViewController vc, HippyMap props, String prop, View view) {
        if (view == null) {
            return;
        }
        final Object dataFromValue = props.get(prop);
        CustomControllerHelper.invokePropMethodForPending(vc, view, prop, dataFromValue);
    }

}
