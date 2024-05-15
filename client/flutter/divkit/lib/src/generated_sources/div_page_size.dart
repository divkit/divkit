// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_percentage_size.dart';

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

  static DivPageSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPageSize(
      pageWidth: safeParseObj(
        DivPercentageSize.fromJson(json['page_width']),
      )!,
    );
  }
}
