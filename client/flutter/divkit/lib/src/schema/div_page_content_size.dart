// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Page size equals to its content size.
class DivPageContentSize extends Resolvable with EquatableMixin {
  const DivPageContentSize({
    this.alignment = const ValueExpression(DivPageContentSizeAlignment.center),
  });

  static const type = "wrap_content";

  /// Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
  // default value: DivPageContentSizeAlignment.center
  final Expression<DivPageContentSizeAlignment> alignment;

  @override
  List<Object?> get props => [
        alignment,
      ];

  DivPageContentSize copyWith({
    Expression<DivPageContentSizeAlignment>? alignment,
  }) =>
      DivPageContentSize(
        alignment: alignment ?? this.alignment,
      );

  static DivPageContentSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPageContentSize(
        alignment: reqVProp<DivPageContentSizeAlignment>(
          safeParseStrEnumExpr(
            json['alignment'],
            parse: DivPageContentSizeAlignment.fromJson,
            fallback: DivPageContentSizeAlignment.center,
          ),
          name: 'alignment',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivPageContentSize resolve(DivVariableContext context) {
    alignment.resolve(context);
    return this;
  }
}

enum DivPageContentSizeAlignment implements Resolvable {
  start('start'),
  center('center'),
  end('end');

  final String value;

  const DivPageContentSizeAlignment(this.value);
  bool get isStart => this == start;

  bool get isCenter => this == center;

  bool get isEnd => this == end;

  T map<T>({
    required T Function() start,
    required T Function() center,
    required T Function() end,
  }) {
    switch (this) {
      case DivPageContentSizeAlignment.start:
        return start();
      case DivPageContentSizeAlignment.center:
        return center();
      case DivPageContentSizeAlignment.end:
        return end();
    }
  }

  T maybeMap<T>({
    T Function()? start,
    T Function()? center,
    T Function()? end,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivPageContentSizeAlignment.start:
        return start?.call() ?? orElse();
      case DivPageContentSizeAlignment.center:
        return center?.call() ?? orElse();
      case DivPageContentSizeAlignment.end:
        return end?.call() ?? orElse();
    }
  }

  static DivPageContentSizeAlignment? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'start':
          return DivPageContentSizeAlignment.start;
        case 'center':
          return DivPageContentSizeAlignment.center;
        case 'end':
          return DivPageContentSizeAlignment.end;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivPageContentSizeAlignment: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivPageContentSizeAlignment resolve(DivVariableContext context) => this;
}
