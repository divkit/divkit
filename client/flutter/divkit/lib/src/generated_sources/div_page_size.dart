// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_percentage_size.dart';

class DivPageSize with EquatableMixin {
  const DivPageSize({
    required this.pageWidth,
  });

  static const type = "percentage";

  final DivPercentageSize pageWidth;

  @override
  List<Object?> get props => [
        pageWidth,
      ];

  DivPageSize copyWith({
    DivPercentageSize? pageWidth,
  }) =>
      DivPageSize(
        pageWidth: pageWidth ?? this.pageWidth,
      );

  static DivPageSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivPageSize(
        pageWidth: safeParseObj(
          DivPercentageSize.fromJson(json['page_width']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
