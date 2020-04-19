package com.hg.mad.dialog;

import android.text.TextUtils;

import java.util.Date;

/**
 * Object for passing filters around.
 */
public class ChapFilters {

    private String type = null;
    private Date startDate = null;
    private Date endDate = null;

    public ChapFilters() {}

    public static ChapFilters getDefault() {
        ChapFilters filters = new ChapFilters();
        return filters;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

}