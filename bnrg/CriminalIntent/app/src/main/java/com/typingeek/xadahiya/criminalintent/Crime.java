package com.typingeek.xadahiya.criminalintent;

import java.util.UUID;

/**
 * Created by xadahiya on 8/9/16.
 */
public class Crime {

    private UUID mId;
    private String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {

        return mId;
    }

    public Crime(){
        mId = UUID.randomUUID();
    }
}
