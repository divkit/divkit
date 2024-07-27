// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_input_mask_base.dart';

class DivCurrencyInputMask with EquatableMixin implements DivInputMaskBase {
  const DivCurrencyInputMask({
    this.locale,
    required this.rawTextVariable,
  });

  static const type = "currency";

  final Expression<String>? locale;

  @override
  final String rawTextVariable;

  @override
  List<Object?> get props => [
        locale,
        rawTextVariable,
      ];

  DivCurrencyInputMask copyWith({
    Expression<String>? Function()? locale,
    String? rawTextVariable,
  }) =>
      DivCurrencyInputMask(
        locale: locale != null ? locale.call() : this.locale,
        rawTextVariable: rawTextVariable ?? this.rawTextVariable,
      );

  static DivCurrencyInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivCurrencyInputMask(
        locale: safeParseStrExpr(
          json['locale']?.toString(),
        ),
        rawTextVariable: safeParseStr(
          json['raw_text_variable']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
