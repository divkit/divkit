// Generated code. Do not modify.

import 'package:divkit/src/schema/div_input_mask_base.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Mask for entering phone numbers with dynamic regional format identification.
class DivPhoneInputMask extends Preloadable
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
        rawTextVariable: safeParseStr(
          json['raw_text_variable']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPhoneInputMask?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPhoneInputMask(
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
    try {} catch (e) {
      return;
    }
  }
}
