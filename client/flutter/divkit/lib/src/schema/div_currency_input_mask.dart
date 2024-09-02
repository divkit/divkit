// Generated code. Do not modify.

import 'package:divkit/src/schema/div_input_mask_base.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivCurrencyInputMask extends Preloadable
    with EquatableMixin
    implements DivInputMaskBase {
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

  static DivCurrencyInputMask? fromJson(
    Map<String, dynamic>? json,
  ) {
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

  static Future<DivCurrencyInputMask?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivCurrencyInputMask(
        locale: await safeParseStrExprAsync(
          json['locale']?.toString(),
        ),
        rawTextVariable: (await safeParseStrAsync(
          json['raw_text_variable']?.toString(),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await locale?.preload(context);
    } catch (e) {
      return;
    }
  }
}
