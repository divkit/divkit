// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';

enum DivAlignmentVertical implements Resolvable {
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
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivAlignmentVertical: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivAlignmentVertical resolve(DivVariableContext context) => this;
}
