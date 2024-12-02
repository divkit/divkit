// Generated code. Do not modify.

import 'package:divkit/src/schema/div_input_mask_base.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Mask for entering phone numbers with dynamic regional format identification.
class DivPhoneInputMask extends Resolvable
    with EquatableMixin
    implements DivInputMaskBase {
  const DivPhoneInputMask({
    required this.rawTextVariable,
  });

  static const type = "phone";

  /// Name of the variable to store the unprocessed value.
  @override
  final String rawTextVariable;

  @override
  List<Object?> get props => [
        rawTextVariable,
      ];

  DivPhoneInputMask copyWith({
    String? rawTextVariable,
  }) =>
      DivPhoneInputMask(
        rawTextVariable: rawTextVariable ?? this.rawTextVariable,
      );

  static DivPhoneInputMask? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPhoneInputMask(
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

  @override
  DivPhoneInputMask resolve(DivVariableContext context) {
    return this;
  }
}
