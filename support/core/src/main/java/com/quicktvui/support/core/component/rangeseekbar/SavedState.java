package com.quicktvui.support.core.component.rangeseekbar;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class SavedState extends View.BaseSavedState {
    public float minValue;
    public float maxValue;
    public float rangeInterval;
    public int tickNumber;
    public float currSelectedMin;
    public float currSelectedMax;

    public SavedState(Parcelable superState) {
        super(superState);
    }

    private SavedState(Parcel in) {
        super(in);
        minValue = in.readFloat();
        maxValue = in.readFloat();
        rangeInterval = in.readFloat();
        tickNumber = in.readInt();
        currSelectedMin = in.readFloat();
        currSelectedMax = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeFloat(minValue);
        out.writeFloat(maxValue);
        out.writeFloat(rangeInterval);
        out.writeInt(tickNumber);
        out.writeFloat(currSelectedMin);
        out.writeFloat(currSelectedMax);
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };
}
