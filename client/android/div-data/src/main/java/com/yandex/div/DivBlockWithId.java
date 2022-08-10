package com.yandex.div;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.util.Assert;
import com.yandex.div.json.JSONSerializable;
import com.yandex.div.json.ParsingErrorLogger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for blocks, provides unique block identification inside a div.
 * This identification is runtime. See DivView#switchToState
 * This id is like xpath, like 'tabs/0/gallery/1/button'
 */
public abstract class DivBlockWithId implements JSONSerializable {

    @NonNull
    private String mBlockId = "";

    @SuppressWarnings("unused") // this constructor required by generated inherited classes
    protected DivBlockWithId(@Nullable JSONObject jsonSource, @Nullable ParsingErrorLogger parsingErrorCallback) { }

    @NonNull
    public static String appendId(@Nullable String parentId, @Nullable String childId) {
        return parentId + "/" + childId;
    }

    @NonNull
    public String getBlockId() {
        if (TextUtils.isEmpty(mBlockId)) {
            Assert.fail("block id not initialized, call setBlockId first");
        }
        return mBlockId;
    }

    public void setBlockId(@NonNull String blockId) {
        mBlockId = blockId;
    }

    @NonNull
    @Override
    public JSONObject writeToJSON() throws JSONException { // this method is required by generated inherited classes
        return new JSONObject();
    }
}
