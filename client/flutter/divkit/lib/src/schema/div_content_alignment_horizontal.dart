// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';

enum DivContentAlignmentHorizontal {
  left('left'),
  center('center'),
  right('right'),
  start('start'),
  end('end'),
  spaceBetween('space-between'),
  spaceAround('space-around'),
  spaceEvenly('space-evenly');

  final String value;

  const DivContentAlignmentHorizontal(this.value);
  bool get isLeft => this == left;

  bool get isCenter => this == center;

  bool get isRight => this == right;

  bool get isStart => this == start;

  bool get isEnd => this == end;

  bool get isSpaceBetween => this == spaceBetween;

  bool get isSpaceAround => this == spaceAround;

  bool get isSpaceEvenly => this == spaceEvenly;

  T map<T>({
    required T Function() left,
    required T Function() center,
    required T Function() right,
    required T Function() start,
    required T Function() end,
    required T Function() spaceBetween,
    required T Function() spaceAround,
    required T Function() spaceEvenly,
  }) {
    switch (this) {
      case DivContentAlignmentHorizontal.left:
        return left();
      case DivContentAlignmentHorizontal.center:
        return center();
      case DivContentAlignmentHorizontal.right:
        return right();
      case DivContentAlignmentHorizontal.start:
        return start();
      case DivContentAlignmentHorizontal.end:
        return end();
      case DivContentAlignmentHorizontal.spaceBetween:
        return spaceBetween();
      case DivContentAlignmentHorizontal.spaceAround:
        return spaceAround();
      case DivContentAlignmentHorizontal.spaceEvenly:
        return spaceEvenly();
    }
  }

  T maybeMap<T>({
    T Function()? left,
    T Function()? center,
    T Function()? right,
    T Function()? start,
    T Function()? end,
    T Function()? spaceBetween,
    T Function()? spaceAround,
    T Function()? spaceEvenly,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivContentAlignmentHorizontal.left:
        return left?.call() ?? orElse();
      case DivContentAlignmentHorizontal.center:
        return center?.call() ?? orElse();
      case DivContentAlignmentHorizontal.right:
        return right?.call() ?? orElse();
      case DivContentAlignmentHorizontal.start:
        return start?.call() ?? orElse();
      case DivContentAlignmentHorizontal.end:
        return end?.call() ?? orElse();
      case DivContentAlignmentHorizontal.spaceBetween:
        return spaceBetween?.call() ?? orElse();
      case DivContentAlignmentHorizontal.spaceAround:
        return spaceAround?.call() ?? orElse();
      case DivContentAlignmentHorizontal.spaceEvenly:
        return spaceEvenly?.call() ?? orElse();
    }
  }

  static DivContentAlignmentHorizontal? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'left':
          return DivContentAlignmentHorizontal.left;
        case 'center':
          return DivContentAlignmentHorizontal.center;
        case 'right':
          return DivContentAlignmentHorizontal.right;
        case 'start':
          return DivContentAlignmentHorizontal.start;
        case 'end':
          return DivContentAlignmentHorizontal.end;
        case 'space-between':
          return DivContentAlignmentHorizontal.spaceBetween;
        case 'space-around':
          return DivContentAlignmentHorizontal.spaceAround;
        case 'space-evenly':
          return DivContentAlignmentHorizontal.spaceEvenly;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivContentAlignmentHorizontal: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }
}
