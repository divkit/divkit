import 'package:divkit/divkit.dart';
import 'package:flutter/widgets.dart';

extension DivGalleryOrientationConverter on DivGalleryOrientation {
  Axis convert() {
    switch (this) {
      case DivGalleryOrientation.horizontal:
        return Axis.horizontal;
      case DivGalleryOrientation.vertical:
        return Axis.vertical;
    }
  }
}

extension DivGalleryCrossContentAlignmentConverter
    on DivGalleryCrossContentAlignment {
  CrossAxisAlignment convert() {
    switch (this) {
      case DivGalleryCrossContentAlignment.start:
        return CrossAxisAlignment.start;
      case DivGalleryCrossContentAlignment.center:
        return CrossAxisAlignment.center;
      case DivGalleryCrossContentAlignment.end:
        return CrossAxisAlignment.end;
    }
  }
}
