import 'package:flutter/widgets.dart';

class _ImageConfig<T extends ImageProvider> {
  final int width;
  final int height;
  final T image;
  final Object cacheKey;

  _ImageConfig(
    this.width,
    this.height,
    this.image,
    this.cacheKey,
  );
}

Future<_ImageConfig<T>?> extractImageConfig<T extends ImageProvider>(
  ImageProvider? imageProvider,
  Element imageElement,
) async {
  final renderBox = imageElement.renderObject! as RenderBox;
  if (imageProvider is ResizeImage &&
      imageProvider.imageProvider is T &&
      imageProvider.width != null &&
      imageProvider.height != null) {
    return _ImageConfig(
      imageProvider.width!,
      imageProvider.height!,
      imageProvider.imageProvider as T,
      await imageProvider
          .obtainKey(createLocalImageConfiguration(imageElement)),
    );
  }
  if (imageProvider is T) {
    return _ImageConfig(
      renderBox.size.width.ceil(),
      renderBox.size.height.ceil(),
      imageProvider,
      await imageProvider
          .obtainKey(createLocalImageConfiguration(imageElement)),
    );
  }
  return null;
}
