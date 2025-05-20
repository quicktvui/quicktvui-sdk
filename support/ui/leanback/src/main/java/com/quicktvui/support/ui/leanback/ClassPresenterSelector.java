package com.quicktvui.support.ui.leanback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A ClassPresenterSelector selects a {@link Presenter} based on the item's
 * Java class.
 */
public final class ClassPresenterSelector extends PresenterSelector {

    private final ArrayList<Presenter> mPresenters = new ArrayList<Presenter>();

    private final HashMap<Class<?>, Object> mClassMap = new HashMap<Class<?>, Object>();

    /**
     * Sets a presenter to be used for the given class.
     * @param cls The data model class to be rendered.
     * @param presenter The presenter that renders the objects of the given class.
     * @return This ClassPresenterSelector object.
     */
    public ClassPresenterSelector addClassPresenter(Class<?> cls, Presenter presenter) {
        mClassMap.put(cls, presenter);
        if (!mPresenters.contains(presenter)) {
            mPresenters.add(presenter);
        }
        return this;
    }

    /**
     * Sets a presenter selector to be used for the given class.
     * @param cls The data model class to be rendered.
     * @param presenterSelector The presenter selector that finds the right presenter for a given
     *                          class.
     * @return This ClassPresenterSelector object.
     */
    public ClassPresenterSelector addClassPresenterSelector(Class<?> cls,
                                                            PresenterSelector presenterSelector) {
        mClassMap.put(cls, presenterSelector);
        Presenter[] innerPresenters = presenterSelector.getPresenters();
        for (int i = 0; i < innerPresenters.length; i++)
            if (!mPresenters.contains(innerPresenters[i])) {
                mPresenters.add(innerPresenters[i]);
            }
        return this;
    }

    @Override
    public Presenter getPresenter(Object item) {
        Class<?> cls = item.getClass();
        Object presenter = null;

        do {
            presenter = mClassMap.get(cls);
            if (presenter instanceof PresenterSelector) {
                Presenter innerPresenter = ((PresenterSelector) presenter).getPresenter(item);
                if (innerPresenter != null) {
                    return innerPresenter;
                }
            }
            cls = cls.getSuperclass();
        } while (presenter == null && cls != null);

        return (Presenter) presenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return mPresenters.toArray(new Presenter[mPresenters.size()]);
    }
}