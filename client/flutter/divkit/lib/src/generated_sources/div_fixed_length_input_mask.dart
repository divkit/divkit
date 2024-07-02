// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_input_mask_base.dart';

class DivFixedLengthInputMask with EquatableMixin implements DivInputMaskBase {
  const DivFixedLengthInputMask({
    this.alwaysVisible = const ValueExpression(false),
    required this.pattern,
    required this.patternElements,
    required this.rawTextVariable,
  });

  static const type = "fixed_length";
  // default value: false
  final Expression<bool> alwaysVisible;

  final Expression<String> pattern;
  // at least 1 elements
  final List<DivFixedLengthInputMaskPatternElement> patternElements;

  @override
  final String rawTextVariable;

  @override
  List<Object?> get props => [
        alwaysVisible,
        pattern,
        patternElements,
        rawTextVariable,
      ];

  DivFixedLengthInputMask copyWith({
    Expression<bool>? alwaysVisible,
    Expression<String>? pattern,
    List<DivFixedLengthInputMaskPatternElement>? patternElements,
    String? rawTextVariable,
  }) =>
      DivFixedLengthInputMask(
        alwaysVisible: alwaysVisible ?? this.alwaysVisible,
        pattern: pattern ?? this.pattern,
        patternElements: patternElements ?? this.patternElements,
        rawTextVariable: rawTextVariable ?? this.rawTextVariable,
      );

  static DivFixedLengthInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivFixedLengthInputMask(
      alwaysVisible: safeParseBoolExpr(
        json['always_visible'],
        fallback: false,
      )!,
      pattern: safeParseStrExpr(
        json['pattern']?.toString(),
      )!,
      patternElements: safeParseObj(
        safeListMap(
            json['pattern_elements'],
            (v) => safeParseObj(
                  DivFixedLengthInputMaskPatternElement.fromJson(v),
                )!),
      )!,
      rawTextVariable: safeParseStr(
        json['raw_text_variable']?.toString(),
      )!,
    );
  }
}

class DivFixedLengthInputMaskPatternElement with EquatableMixin {
  const DivFixedLengthInputMaskPatternElement({
    required this.key,
    this.placeholder = const ValueExpression("_"),
    this.regex,
  });

  // at least 1 char
  final Expression<String> key;
  // at least 1 char; default value: "_"
  final Expression<String> placeholder;

  final Expression<String>? regex;

  @override
  List<Object?> get props => [
        key,
        placeholder,
        regex,
      ];

  DivFixedLengthInputMaskPatternElement copyWith({
    Expression<String>? key,
    Expression<String>? placeholder,
    Expression<String>? Function()? regex,
  }) =>
      DivFixedLengthInputMaskPatternElement(
        key: key ?? this.key,
        placeholder: placeholder ?? this.placeholder,
        regex: regex != null ? regex.call() : this.regex,
      );

  static DivFixedLengthInputMaskPatternElement? fromJson(
      Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivFixedLengthInputMaskPatternElement(
      key: safeParseStrExpr(
        json['key']?.toString(),
      )!,
      placeholder: safeParseStrExpr(
        json['placeholder']?.toString(),
        fallback: "_",
      )!,
      regex: safeParseStrExpr(
        json['regex']?.toString(),
      ),
    );
  }
}
