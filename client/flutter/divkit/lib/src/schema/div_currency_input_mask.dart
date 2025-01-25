// Generated code. Do not modify.

import 'package:divkit/src/schema/div_input_mask_base.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Mask for entering currency in the specified regional format.
class DivCurrencyInputMask with EquatableMixin implements DivInputMaskBase {
  const DivCurrencyInputMask({
    this.locale,
    required this.rawTextVariable,
  });

  static const type = "currency";

  /// Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
  final Expression<String>? locale;

  /// Name of the variable to store the unprocessed value.
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
          json['locale'],
        ),
        rawTextVariable: reqProp<String>(
          safeParseStr(
            json['raw_text_variable'],
          ),
          name: 'raw_text_variable',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
