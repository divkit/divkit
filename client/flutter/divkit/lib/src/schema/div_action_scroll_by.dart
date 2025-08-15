// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Scrolls the container by `item_count` or `offset` starting from the current position. If both values are specified, the action will be combined. For scrolling back, use negative values.
class DivActionScrollBy with EquatableMixin {
  const DivActionScrollBy({
    this.animated = const ValueExpression(true),
    required this.id,
    this.itemCount = const ValueExpression(0),
    this.offset = const ValueExpression(0),
    this.overflow = const ValueExpression(DivActionScrollByOverflow.clamp),
  });

  static const type = "scroll_by";

  /// Enables scrolling animation.
  // default value: true
  final Expression<bool> animated;

  /// ID of the element where the action should be performed.
  final Expression<String> id;

  /// Number of container elements to scroll through. For scrolling back, use negative values.
  // default value: 0
  final Expression<int> itemCount;

  /// Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
  // default value: 0
  final Expression<int> offset;

  /// Defines navigation behavior at boundary elements:
  /// • `clamp`: Stop navigation at the boundary element (default)
  /// • `ring`: Navigate to the start or end, depending on the current element.
  // default value: DivActionScrollByOverflow.clamp
  final Expression<DivActionScrollByOverflow> overflow;

  @override
  List<Object?> get props => [
        animated,
        id,
        itemCount,
        offset,
        overflow,
      ];

  DivActionScrollBy copyWith({
    Expression<bool>? animated,
    Expression<String>? id,
    Expression<int>? itemCount,
    Expression<int>? offset,
    Expression<DivActionScrollByOverflow>? overflow,
  }) =>
      DivActionScrollBy(
        animated: animated ?? this.animated,
        id: id ?? this.id,
        itemCount: itemCount ?? this.itemCount,
        offset: offset ?? this.offset,
        overflow: overflow ?? this.overflow,
      );

  static DivActionScrollBy? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionScrollBy(
        animated: reqVProp<bool>(
          safeParseBoolExpr(
            json['animated'],
            fallback: true,
          ),
          name: 'animated',
        ),
        id: reqVProp<String>(
          safeParseStrExpr(
            json['id'],
          ),
          name: 'id',
        ),
        itemCount: reqVProp<int>(
          safeParseIntExpr(
            json['item_count'],
            fallback: 0,
          ),
          name: 'item_count',
        ),
        offset: reqVProp<int>(
          safeParseIntExpr(
            json['offset'],
            fallback: 0,
          ),
          name: 'offset',
        ),
        overflow: reqVProp<DivActionScrollByOverflow>(
          safeParseStrEnumExpr(
            json['overflow'],
            parse: DivActionScrollByOverflow.fromJson,
            fallback: DivActionScrollByOverflow.clamp,
          ),
          name: 'overflow',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

enum DivActionScrollByOverflow {
  clamp('clamp'),
  ring('ring');

  final String value;

  const DivActionScrollByOverflow(this.value);
  bool get isClamp => this == clamp;

  bool get isRing => this == ring;

  T map<T>({
    required T Function() clamp,
    required T Function() ring,
  }) {
    switch (this) {
      case DivActionScrollByOverflow.clamp:
        return clamp();
      case DivActionScrollByOverflow.ring:
        return ring();
    }
  }

  T maybeMap<T>({
    T Function()? clamp,
    T Function()? ring,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivActionScrollByOverflow.clamp:
        return clamp?.call() ?? orElse();
      case DivActionScrollByOverflow.ring:
        return ring?.call() ?? orElse();
    }
  }

  static DivActionScrollByOverflow? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'clamp':
          return DivActionScrollByOverflow.clamp;
        case 'ring':
          return DivActionScrollByOverflow.ring;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivActionScrollByOverflow: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }
}
