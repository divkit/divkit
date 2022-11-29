package com.yandex.div;

import androidx.annotation.NonNull;

/**
 * A tag identifies data associated with a div view, based on this tag we can retrieve previous data state and build view accordingly
 */
public class DivDataTag {

    public static final DivDataTag INVALID = new DivDataTag("");

    @NonNull
    private final String mId;

    public DivDataTag(@NonNull String id) {
        mId = id;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DivDataTag tag = (DivDataTag) o;
        return mId.equals(tag.mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }
}
