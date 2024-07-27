// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_absolute_edge_insets.dart';

class DivNinePatchBackground with EquatableMixin {
  const DivNinePatchBackground({
    required this.imageUrl,
    this.insets = const DivAbsoluteEdgeInsets(),
  });

  static const type = "nine_patch_image";

  final Expression<Uri> imageUrl;

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

  static DivNinePatchBackground? fromJson(Map<String, dynamic>? json) {
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
}
