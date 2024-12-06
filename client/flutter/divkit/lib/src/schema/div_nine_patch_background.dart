// Generated code. Do not modify.

import 'package:divkit/src/schema/div_absolute_edge_insets.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Image in 9-patch format (https://developer.android.com/studio/write/draw9patch).
class DivNinePatchBackground with EquatableMixin {
  const DivNinePatchBackground({
    required this.imageUrl,
    this.insets = const DivAbsoluteEdgeInsets(),
  });

  static const type = "nine_patch_image";

  /// Image URL.
  final Expression<Uri> imageUrl;

  /// Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
  final DivAbsoluteEdgeInsets insets;

  @override
  List<Object?> get props => [
        imageUrl,
        insets,
      ];

  DivNinePatchBackground copyWith({
    Expression<Uri>? imageUrl,
    DivAbsoluteEdgeInsets? insets,
  }) =>
      DivNinePatchBackground(
        imageUrl: imageUrl ?? this.imageUrl,
        insets: insets ?? this.insets,
      );

  static DivNinePatchBackground? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivNinePatchBackground(
        imageUrl: reqVProp<Uri>(
          safeParseUriExpr(
            json['image_url'],
          ),
          name: 'image_url',
        ),
        insets: reqProp<DivAbsoluteEdgeInsets>(
          safeParseObject(
            json['insets'],
            parse: DivAbsoluteEdgeInsets.fromJson,
            fallback: const DivAbsoluteEdgeInsets(),
          ),
          name: 'insets',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
