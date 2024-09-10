// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivAlignmentVertical implements Preloadable {
  top('top'),
  center('center'),
  bottom('bottom'),
  baseline('baseline');

  final String value;

  const DivAlignmentVertical(this.value);
  bool get isTop => this == top;

  bool get isCenter => this == center;

  bool get isBottom => this == bottom;

  bool get isBaseline => this == baseline;

  T map<T>({
    required T Function() top,
    required T Function() center,
    required T Function() bottom,
    required T Function() baseline,
  }) {
    switch (this) {
      case DivAlignmentVertical.top:
        return top();
      case DivAlignmentVertical.center:
        return center();
      case DivAlignmentVertical.bottom:
        return bottom();
      case DivAlignmentVertical.baseline:
        return baseline();
    }
  }

  T maybeMap<T>({
    T Function()? top,
    T Function()? center,
    T Function()? bottom,
    T Function()? baseline,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAlignmentVertical.top:
        return top?.call() ?? orElse();
      case DivAlignmentVertical.center:
        return center?.call() ?? orElse();
      case DivAlignmentVertical.bottom:
        return bottom?.call() ?? orElse();
      case DivAlignmentVertical.baseline:
        return baseline?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivAlignmentVertical? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'top':
          return DivAlignmentVertical.top;
        case 'center':
          return DivAlignmentVertical.center;
        case 'bottom':
          return DivAlignmentVertical.bottom;
        case 'baseline':
          return DivAlignmentVertical.baseline;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivAlignmentVertical?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'top':
          return DivAlignmentVertical.top;
        case 'center':
          return DivAlignmentVertical.center;
        case 'bottom':
          return DivAlignmentVertical.bottom;
        case 'baseline':
          return DivAlignmentVertical.baseline;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
