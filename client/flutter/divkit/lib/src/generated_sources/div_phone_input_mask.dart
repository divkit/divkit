// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_input_mask_base.dart';

class DivPhoneInputMask with EquatableMixin implements DivInputMaskBase {
  const DivPhoneInputMask({
    required this.rawTextVariable,
  });

  static const type = "phone";

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

  static DivPhoneInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivPhoneInputMask(
        rawTextVariable: safeParseStr(
          json['raw_text_variable']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
