// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Controls given video.
class DivActionVideo extends Preloadable with EquatableMixin {
  const DivActionVideo({
    required this.action,
    required this.id,
  });

  static const type = "video";

  /// Defines video action:
  /// • `start` - play if it is ready or plans to play when video becomes ready;
  /// • `pause` - pauses video playback.
  final Expression<DivActionVideoAction> action;

  /// Video identifier.
  final Expression<String> id;

  @override
  List<Object?> get props => [
        action,
        id,
      ];

  DivActionVideo copyWith({
    Expression<DivActionVideoAction>? action,
    Expression<String>? id,
  }) =>
      DivActionVideo(
        action: action ?? this.action,
        id: id ?? this.id,
      );

  static DivActionVideo? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionVideo(
        action: safeParseStrEnumExpr(
          json['action'],
          parse: DivActionVideoAction.fromJson,
        )!,
        id: safeParseStrExpr(
          json['id']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionVideo?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionVideo(
        action: (await safeParseStrEnumExprAsync(
          json['action'],
          parse: DivActionVideoAction.fromJson,
        ))!,
        id: (await safeParseStrExprAsync(
          json['id']?.toString(),
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
      await action.preload(context);
      await id.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivActionVideoAction implements Preloadable {
  start('start'),
  pause('pause');

  final String value;

  const DivActionVideoAction(this.value);
  bool get isStart => this == start;

  bool get isPause => this == pause;

  T map<T>({
    required T Function() start,
    required T Function() pause,
  }) {
    switch (this) {
      case DivActionVideoAction.start:
        return start();
      case DivActionVideoAction.pause:
        return pause();
    }
  }

  T maybeMap<T>({
    T Function()? start,
    T Function()? pause,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivActionVideoAction.start:
        return start?.call() ?? orElse();
      case DivActionVideoAction.pause:
        return pause?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivActionVideoAction? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'start':
          return DivActionVideoAction.start;
        case 'pause':
          return DivActionVideoAction.pause;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionVideoAction?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'start':
          return DivActionVideoAction.start;
        case 'pause':
          return DivActionVideoAction.pause;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
