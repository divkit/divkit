// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivFontWeight implements Preloadable {
  light('light'),
  medium('medium'),
  regular('regular'),
  bold('bold');

  final String value;

  const DivFontWeight(this.value);

  T map<T>({
    required T Function() light,
    required T Function() medium,
    required T Function() regular,
    required T Function() bold,
  }) {
    switch (this) {
      case DivFontWeight.light:
        return light();
      case DivFontWeight.medium:
        return medium();
      case DivFontWeight.regular:
        return regular();
      case DivFontWeight.bold:
        return bold();
    }
  }

  T maybeMap<T>({
    T Function()? light,
    T Function()? medium,
    T Function()? regular,
    T Function()? bold,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivFontWeight.light:
        return light?.call() ?? orElse();
      case DivFontWeight.medium:
        return medium?.call() ?? orElse();
      case DivFontWeight.regular:
        return regular?.call() ?? orElse();
      case DivFontWeight.bold:
        return bold?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivFontWeight? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'light':
          return DivFontWeight.light;
        case 'medium':
          return DivFontWeight.medium;
        case 'regular':
          return DivFontWeight.regular;
        case 'bold':
          return DivFontWeight.bold;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivFontWeight?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'light':
          return DivFontWeight.light;
        case 'medium':
          return DivFontWeight.medium;
        case 'regular':
          return DivFontWeight.regular;
        case 'bold':
          return DivFontWeight.bold;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
