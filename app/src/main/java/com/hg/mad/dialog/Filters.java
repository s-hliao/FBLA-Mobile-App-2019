package com.hg.mad.dialog;

import android.text.TextUtils;

/**
 * Object for passing filters around.
 */
public class Filters {

    private String type = null;
    private String category = null;
    private String intro = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        return filters;
    }

    public boolean hasType() {
        return !(TextUtils.isEmpty(type));
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasIntro() {
        return !(TextUtils.isEmpty(intro));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro){
        this.intro = intro;
    }
}
