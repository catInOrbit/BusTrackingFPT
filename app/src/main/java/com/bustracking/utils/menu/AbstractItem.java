package com.bustracking.utils.menu;

import com.bustracking.model.functionalmodels.SeatModel;

import java.util.List;

public abstract class AbstractItem {

    /*variable declaration*/

    public static final int TYPE_CENTER = 0;
    public static final int TYPE_EDGE = 1;
    public static final int TYPE_EMPTY = 2;
    public List<SeatModel> mSeatModelList;
    private String mLabel;

    /*constructor*/

    public AbstractItem(String aLabel) {
        this.mLabel = aLabel;
    }

    public AbstractItem(List<SeatModel> aSeatModelList) {
        this.mSeatModelList =aSeatModelList;
    }

    /*getter*/

    public String getmLabel() {
        return mLabel;
    }

    abstract public int getType();

}
