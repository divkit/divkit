// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivTextAlignmentVertical implements Resolvable {
  top('top'),
  center('center'),
  bottom('bottom'),
  baseline('baseline');

  final String value;

  const DivTextAlignmentVertical(this.value);
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
      case DivTextAlignmentVertical.top:
        return top();
      case DivTextAlignmentVertical.center:
        return center();
      case DivTextAlignmentVertical.bottom:
        return bottom();
      case DivTextAlignmentVertical.baseline:
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
      case DivTextAlignmentVertical.top:
        return top?.call() ?? orElse();
      case DivTextAlignmentVertical.center:
        return center?.call() ?? orElse();
      case DivTextAlignmentVertical.bottom:
        return bottom?.call() ?? orElse();
      case DivTextAlignmentVertical.baseline:
        return baseline?.call() ?? orElse();
    }
  }

  static DivTextAlignmentVertical? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'top':
          return DivTextAlignmentVertical.top;
        case 'center':
          return DivTextAlignmentVertical.center;
        case 'bottom':
          return DivTextAlignmentVertical.bottom;
        case 'baseline':
          return DivTextAlignmentVertical.baseline;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  @override
  DivTextAlignmentVertical resolve(DivVariableContext context) => this;
}
