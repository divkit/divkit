// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';

enum DivAnimationDirection implements Resolvable {
  normal('normal'),
  reverse('reverse'),
  alternate('alternate'),
  alternateReverse('alternate_reverse');

  final String value;

  const DivAnimationDirection(this.value);
  bool get isNormal => this == normal;

  bool get isReverse => this == reverse;

  bool get isAlternate => this == alternate;

  bool get isAlternateReverse => this == alternateReverse;

  T map<T>({
    required T Function() normal,
    required T Function() reverse,
    required T Function() alternate,
    required T Function() alternateReverse,
  }) {
    switch (this) {
      case DivAnimationDirection.normal:
        return normal();
      case DivAnimationDirection.reverse:
        return reverse();
      case DivAnimationDirection.alternate:
        return alternate();
      case DivAnimationDirection.alternateReverse:
        return alternateReverse();
    }
  }

  T maybeMap<T>({
    T Function()? normal,
    T Function()? reverse,
    T Function()? alternate,
    T Function()? alternateReverse,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAnimationDirection.normal:
        return normal?.call() ?? orElse();
      case DivAnimationDirection.reverse:
        return reverse?.call() ?? orElse();
      case DivAnimationDirection.alternate:
        return alternate?.call() ?? orElse();
      case DivAnimationDirection.alternateReverse:
        return alternateReverse?.call() ?? orElse();
    }
  }

  static DivAnimationDirection? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'normal':
          return DivAnimationDirection.normal;
        case 'reverse':
          return DivAnimationDirection.reverse;
        case 'alternate':
          return DivAnimationDirection.alternate;
        case 'alternate_reverse':
          return DivAnimationDirection.alternateReverse;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivAnimationDirection: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivAnimationDirection resolve(DivVariableContext context) => this;
}
