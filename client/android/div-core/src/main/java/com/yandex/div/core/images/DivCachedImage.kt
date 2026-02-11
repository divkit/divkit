package com.yandex.div.core.images

public sealed class DivCachedImage(public val from: BitmapSource) {

    public class Bitmap(
        public val bitmap: android.graphics.Bitmap,
        from: BitmapSource
    ) : DivCachedImage(from)

    public class Drawable(
        public val drawable: android.graphics.drawable.Drawable,
        from: BitmapSource
    ) : DivCachedImage(from)
}
