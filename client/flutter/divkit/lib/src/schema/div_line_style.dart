// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';

enum DivLineStyle {
  none('none'),
  single('single');

  final String value;

  const DivLineStyle(this.value);
  bool get isNone => this == none;

  bool get isSingle => this == single;

  T map<T>({
    required T Function() none,
    required T Function() single,
  }) {
    switch (this) {
      case DivLineStyle.none:
        return none();
      case DivLineStyle.single:
        return single();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? single,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivLineStyle.none:
        return none?.call() ?? orElse();
      case DivLineStyle.single:
        return single?.call() ?? orElse();
    }
  }

  static DivLineStyle? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivLineStyle.none;
        case 'single':
          return DivLineStyle.single;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivLineStyle: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }
}
