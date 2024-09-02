// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivAlignmentHorizontal implements Preloadable {
  left('left'),
  center('center'),
  right('right'),
  start('start'),
  end('end');

  final String value;

  const DivAlignmentHorizontal(this.value);
  bool get isLeft => this == left;

  bool get isCenter => this == center;

  bool get isRight => this == right;

  bool get isStart => this == start;

  bool get isEnd => this == end;

  T map<T>({
    required T Function() left,
    required T Function() center,
    required T Function() right,
    required T Function() start,
    required T Function() end,
  }) {
    switch (this) {
      case DivAlignmentHorizontal.left:
        return left();
      case DivAlignmentHorizontal.center:
        return center();
      case DivAlignmentHorizontal.right:
        return right();
      case DivAlignmentHorizontal.start:
        return start();
      case DivAlignmentHorizontal.end:
        return end();
    }
  }

  T maybeMap<T>({
    T Function()? left,
    T Function()? center,
    T Function()? right,
    T Function()? start,
    T Function()? end,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAlignmentHorizontal.left:
        return left?.call() ?? orElse();
      case DivAlignmentHorizontal.center:
        return center?.call() ?? orElse();
      case DivAlignmentHorizontal.right:
        return right?.call() ?? orElse();
      case DivAlignmentHorizontal.start:
        return start?.call() ?? orElse();
      case DivAlignmentHorizontal.end:
        return end?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivAlignmentHorizontal? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'left':
          return DivAlignmentHorizontal.left;
        case 'center':
          return DivAlignmentHorizontal.center;
        case 'right':
          return DivAlignmentHorizontal.right;
        case 'start':
          return DivAlignmentHorizontal.start;
        case 'end':
          return DivAlignmentHorizontal.end;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivAlignmentHorizontal?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'left':
          return DivAlignmentHorizontal.left;
        case 'center':
          return DivAlignmentHorizontal.center;
        case 'right':
          return DivAlignmentHorizontal.right;
        case 'start':
          return DivAlignmentHorizontal.start;
        case 'end':
          return DivAlignmentHorizontal.end;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
