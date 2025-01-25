// Generated code. Do not modify.

import 'package:divkit/src/schema/div_absolute_edge_insets.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Image in 9-patch format (https://developer.android.com/studio/write/draw9patch).
class DivNinePatchBackground extends Resolvable with EquatableMixin {
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
        imageUrl: safeParseUriExpr(json['image_url'])!,
        insets: safeParseObj(
          DivAbsoluteEdgeInsets.fromJson(json['insets']),
          fallback: const DivAbsoluteEdgeInsets(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivNinePatchBackground resolve(DivVariableContext context) {
    imageUrl.resolve(context);
    insets.resolve(context);
    return this;
  }
}
