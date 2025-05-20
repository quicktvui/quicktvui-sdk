package com.quicktvui.support.ui.leanback;

import java.util.ArrayList;
import java.util.HashMap;


public class MapPresenterSelector extends PresenterSelector {

    private final ArrayList<Presenter> mPresenters = new ArrayList<Presenter>();

    private final HashMap<Object,Object> mObjectMap = new HashMap<>();


    private KeyMapper keyMapper;

    public MapPresenterSelector(KeyMapper keyMapper) {
        this.keyMapper = keyMapper;
    }

    public MapPresenterSelector() {
    }


    public MapPresenterSelector addPresenter(Object key, Presenter presenter) {
        mObjectMap.put(key, presenter);
        if (!mPresenters.contains(presenter)) {
            mPresenters.add(presenter);
        }
        return this;
    }

    public void setKeyMapper(KeyMapper keyMapper) {
        this.keyMapper = keyMapper;
    }

    public KeyMapper getKeyMapper() {
        return keyMapper;
    }

    @Override
    public Presenter getPresenter(Object item) {
        if(getKeyMapper() == null){
            throw new RuntimeException("keyMapper 不可以为空");
        }
        final Object key = getKeyMapper().findKeyWithValue(item);
        if(key == null){
            throw new RuntimeException("key 不可以为空");
        }
        final Object presenter = mObjectMap.get(key);
        if(presenter == null){
            throw new RuntimeException("presenter为空，请确保已经注册了相应的Presenter,key:"+key);
        }
        if (presenter instanceof PresenterSelector) {
            Presenter innerPresenter = ((PresenterSelector) presenter).getPresenter(item);
            if (innerPresenter != null) {
                return innerPresenter;
            }
        }
        return (Presenter) presenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return mPresenters.toArray(new Presenter[mPresenters.size()]);
    }


    public interface KeyMapper {
        Object findKeyWithValue(Object itemValue);
    }
}