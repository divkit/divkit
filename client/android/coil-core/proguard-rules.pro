# SVG dependencies are compile-only. Decoder registration tolerates their absence at runtime.
-dontwarn coil3.svg.**
-dontwarn com.caverock.androidsvg.**

# Keep optional SVG references out of image-loader initialization after optimization.
-keep,allowshrinking,allowobfuscation class com.yandex.div.internal.coil.svg.SvgDecoderFactoryProvider {
    public final coil3.decode.Decoder$Factory createIfAvailable(int);
}
