// Generated code. Do not modify.

import 'package:divkit/src/schema/div_input_mask_base.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Mask for entering text with a fixed number of characters.
class DivFixedLengthInputMask extends Preloadable
    with EquatableMixin
    implements DivInputMaskBase {
  const DivFixedLengthInputMask({
    this.alwaysVisible = const ValueExpression(false),
    required this.pattern,
    required this.patternElements,
    required this.rawTextVariable,
  });

  static const type = "fixed_length";

  /// If this option is enabled, the text field contains the mask before being filled in.
  // default value: false
  final Expression<bool> alwaysVisible;

  /// String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
  final Expression<String> pattern;

  /// Template decoding is a description of the characters that will be replaced with user input.
  // at least 1 elements
  final List<DivFixedLengthInputMaskPatternElement> patternElements;

  /// Name of the variable to store the unprocessed value.
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

  static DivFixedLengthInputMask? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
            )!,
          ),
        )!,
        rawTextVariable: safeParseStr(
          json['raw_text_variable']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivFixedLengthInputMask?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivFixedLengthInputMask(
        alwaysVisible: (await safeParseBoolExprAsync(
          json['always_visible'],
          fallback: false,
        ))!,
        pattern: (await safeParseStrExprAsync(
          json['pattern']?.toString(),
        ))!,
        patternElements: (await safeParseObjAsync(
          await safeListMapAsync(
            json['pattern_elements'],
            (v) => safeParseObj(
              DivFixedLengthInputMaskPatternElement.fromJson(v),
            )!,
          ),
        ))!,
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
      await alwaysVisible.preload(context);
      await pattern.preload(context);
      await safeFuturesWait(patternElements, (v) => v.preload(context));
    } catch (e) {
      return;
    }
  }
}

/// Template decoding is a description of the characters that will be replaced with user input.
class DivFixedLengthInputMaskPatternElement extends Preloadable
    with EquatableMixin {
  const DivFixedLengthInputMaskPatternElement({
    required this.key,
    this.placeholder = const ValueExpression("_"),
    this.regex,
  });

  /// A character in the template that will be replaced with a user-defined character.
  // at least 1 char
  final Expression<String> key;

  /// The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
  // at least 1 char; default value: "_"
  final Expression<String> placeholder;

  /// Regular expression for validating character inputs. For example, when a mask is digit-only.
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
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivFixedLengthInputMaskPatternElement?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivFixedLengthInputMaskPatternElement(
        key: (await safeParseStrExprAsync(
          json['key']?.toString(),
        ))!,
        placeholder: (await safeParseStrExprAsync(
          json['placeholder']?.toString(),
          fallback: "_",
        ))!,
        regex: await safeParseStrExprAsync(
          json['regex']?.toString(),
        ),
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
      await key.preload(context);
      await placeholder.preload(context);
      await regex?.preload(context);
    } catch (e) {
      return;
    }
  }
}
