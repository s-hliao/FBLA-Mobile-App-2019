package com.hg.mad.dialog;

import android.text.TextUtils;

import java.util.Date;

/**
 * Object for passing filters around.
 */
public class ChapFilters {

    private Date startDate = null;
    private Date endDate = null;

    public ChapFilters() {}

    public static ChapFilters getDefault() {
        ChapFilters filters = new ChapFilters();
        return filters;
    }

    public boolean hasEndDate() {
        return !(endDate == null);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean hasStartDate() {
        return !(startDate == null);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

}
