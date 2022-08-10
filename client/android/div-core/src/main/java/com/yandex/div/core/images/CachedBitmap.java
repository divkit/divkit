package com.yandex.div.core.images;

import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;

/**
 * Provides cached {@link Bitmap}.
 */
@PublicApi
public class CachedBitmap {

    @NonNull private final Bitmap mBitmap;
    @Nullable private final Uri mCacheUri;
    @Nullable private final byte[] mBytes;
    @NonNull private final BitmapSource mFrom;

    public CachedBitmap(@NonNull Bitmap bitmap, @Nullable Uri cacheUri, @NonNull BitmapSource from) {
        this(bitmap, null, cacheUri, from);
    }

    public CachedBitmap(@NonNull Bitmap bitmap, @Nullable byte[] bytes, @Nullable Uri cacheUri, @NonNull BitmapSource from) {
        mBitmap = bitmap;
        mCacheUri = cacheUri;
        mBytes = bytes;
        mFrom = from;
    }

    /**
     * Returns cached bitmap.
     */
    @NonNull
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Nullable
    public byte[] getBytes() {
        return mBytes;
    }

    /**
     * Returns cached URI of the image.
     */
    @Nullable
    public Uri getCacheUri() {
        return mCacheUri;
    }

    /**
     * Returns source of the image.
     */
    @NonNull
    public BitmapSource getFrom() {
        return mFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CachedBitmap that = (CachedBitmap) o;

        if (!mBitmap.equals(that.getBitmap())) {
            return false;
        }

        if (mFrom != that.getFrom()) {
            return false;
        }

        final Uri thatCacheUri = that.getCacheUri();

        return mCacheUri != null ? mCacheUri.equals(thatCacheUri) : thatCacheUri == null;
    }

    @Override
    public int hashCode() {
        int result = mBitmap.hashCode();
        result = 31 * result + mFrom.hashCode();
        result = 31 * result + (mCacheUri != null ? mCacheUri.hashCode() : 0);
        return result;
    }
}
