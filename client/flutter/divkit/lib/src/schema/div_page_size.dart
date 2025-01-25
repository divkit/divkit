// Generated code. Do not modify.

import 'package:divkit/src/schema/div_percentage_size.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Page width (%).
class DivPageSize extends Resolvable with EquatableMixin {
  const DivPageSize({
    required this.pageWidth,
  });

  static const type = "percentage";

  /// Page width as a percentage of the parent element width.
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

  static DivPageSize? fromJson(
    Map<String, dynamic>? json,
  ) {
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

  @override
  DivPageSize resolve(DivVariableContext context) {
    pageWidth.resolve(context);
    return this;
  }
}
