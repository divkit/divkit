// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_input_mask_base.dart';

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

  static DivCurrencyInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivCurrencyInputMask(
      locale: safeParseStrExpr(
        json['locale']?.toString(),
      ),
      rawTextVariable: safeParseStr(
        json['raw_text_variable']?.toString(),
      )!,
    );
  }
}
