package com.quicktvui.support.ui.image.crop;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;

import com.quicktvui.support.ui.image.canvas.ESCanvasParamsUtils;

@ESKitAutoRegister
public class ESCroppedImageViewComponent implements IEsComponent<ESCroppedImageView> {

    protected static final String OP_INIT = "init";
    protected static final String OP_LOAD = "load";
    protected static final String OP_CROP = "crop";
    protected static final String OP_DRAW = "draw";
    protected static final String OP_RELEASE = "release";

    @Override
    public ESCroppedImageView createView(Context context, EsMap params) {
        return new ESCroppedImageView(context);
    }

    @Override
    public void dispatchFunction(ESCroppedImageView view, String functionName, EsArray params, EsPromise promise) {
        switch (functionName) {
            //
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                promise.resolve(map);
                break;
            //
            case OP_INIT:
                break;
            //
            case OP_LOAD:
                try {
                    String url = params.getString(0);
                    view.load(url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //
            case OP_CROP:
                try {
                    int croppedWidth = params.getInt(0);
                    int croppedHeight = params.getInt(1);

                    int xCoordinate = params.getInt(2);
                    int yCoordinate = params.getInt(3);

                    EsArray pathArray = params.getArray(4);
                    EsArray paintArray = params.getArray(5);

                    view.crop(croppedWidth,
                            croppedHeight,
                            xCoordinate,
                            yCoordinate,
                            ESCanvasParamsUtils.pathArrayToList(pathArray),
                            ESCanvasParamsUtils.paintArrayToList(paintArray));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //
            case OP_DRAW:
                try {
                    int croppedWidth = params.getInt(0);
                    int croppedHeight = params.getInt(1);

                    EsArray pathArray = params.getArray(2);
                    EsArray paintArray = params.getArray(3);

                    view.draw(croppedWidth, croppedHeight,
                            ESCanvasParamsUtils.pathArrayToList(pathArray),
                            ESCanvasParamsUtils.paintArrayToList(paintArray));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //
            case OP_RELEASE:
                try {
                    view.release();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void destroy(ESCroppedImageView view) {
        view.release();
    }
}
