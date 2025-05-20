package com.quicktvui.support.ui.largelist;



import com.quicktvui.support.ui.leanback.MapPresenterSelector;
import com.quicktvui.support.ui.leanback.Presenter;

public class TemplateItemPresenterSelector extends MapPresenterSelector {

    public TemplateItemPresenterSelector() {

        super(itemValue -> {
            if(itemValue instanceof TemplateBean){
                return ((TemplateBean) itemValue).getTemplateType();
            }else{
                throw new IllegalArgumentException("findKeyWithValue error itemValue must impl TemplateItem ");
            }
        });
    }

    @Override
    public Presenter getPresenter(Object item) {
        return super.getPresenter(item);
    }

    @Override
    public MapPresenterSelector addPresenter(Object type, Presenter presenter) {

        return super.addPresenter(type, presenter);
    }

    @Override
    public Presenter[] getPresenters() {
        return super.getPresenters();
    }
}
