// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivSizeUnit implements Preloadable {
  dp('dp'),
  sp('sp'),
  px('px');

  final String value;

  const DivSizeUnit(this.value);
  bool get isDp => this == dp;

  bool get isSp => this == sp;

  bool get isPx => this == px;

  T map<T>({
    required T Function() dp,
    required T Function() sp,
    required T Function() px,
  }) {
    switch (this) {
      case DivSizeUnit.dp:
        return dp();
      case DivSizeUnit.sp:
        return sp();
      case DivSizeUnit.px:
        return px();
    }
  }

  T maybeMap<T>({
    T Function()? dp,
    T Function()? sp,
    T Function()? px,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivSizeUnit.dp:
        return dp?.call() ?? orElse();
      case DivSizeUnit.sp:
        return sp?.call() ?? orElse();
      case DivSizeUnit.px:
        return px?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivSizeUnit? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'dp':
          return DivSizeUnit.dp;
        case 'sp':
          return DivSizeUnit.sp;
        case 'px':
          return DivSizeUnit.px;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivSizeUnit?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'dp':
          return DivSizeUnit.dp;
        case 'sp':
          return DivSizeUnit.sp;
        case 'px':
          return DivSizeUnit.px;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
