// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_input_mask_base.dart';

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

  static DivPhoneInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPhoneInputMask(
      rawTextVariable: safeParseStr(
        json['raw_text_variable']?.toString(),
      )!,
    );
  }
}
