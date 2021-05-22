package com.ohos.demo.uitest.adapter;

import ohos.ai.cv.text.Text;

public class SampleItem {
    private String mTitle;
    private String mString;

    public SampleItem(String title, String str) {
        super();
        mTitle = title;
        mString = str;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmString() {
        return mString;
    }

    public void setmString(String mString) {
        this.mString = mString;
    }
}
