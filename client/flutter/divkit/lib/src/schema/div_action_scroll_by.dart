// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Scrolls scrollable container from current position by `item_count` or by `offset`, if both provided scroll action will be combined, negative numbers associated with backward scroll.
class DivActionScrollBy extends Preloadable with EquatableMixin {
  const DivActionScrollBy({
    this.animated = const ValueExpression(true),
    required this.id,
    this.itemCount = const ValueExpression(0),
    this.offset = const ValueExpression(0),
    this.overflow = const ValueExpression(DivActionScrollByOverflow.clamp),
  });

  static const type = "scroll_by";

  /// If `true` (default value) scroll will be animated, else not.
  // default value: true
  final Expression<bool> animated;

  /// Identifier of the view that is going to be manipulated.
  final Expression<String> id;

  /// Count of container items to scroll, negative value is associated with backward scroll.
  // default value: 0
  final Expression<int> itemCount;

  /// Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
  // default value: 0
  final Expression<int> offset;

  /// Specifies how navigation will occur when the boundary elements are reached:
  /// • `clamp` — Transition will stop at the boundary element (default value);
  /// • `ring` — Transition will be to the beginning or the end depending on the current element.
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
        animated: safeParseBoolExpr(
          json['animated'],
          fallback: true,
        )!,
        id: safeParseStrExpr(
          json['id']?.toString(),
        )!,
        itemCount: safeParseIntExpr(
          json['item_count'],
          fallback: 0,
        )!,
        offset: safeParseIntExpr(
          json['offset'],
          fallback: 0,
        )!,
        overflow: safeParseStrEnumExpr(
          json['overflow'],
          parse: DivActionScrollByOverflow.fromJson,
          fallback: DivActionScrollByOverflow.clamp,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionScrollBy?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionScrollBy(
        animated: (await safeParseBoolExprAsync(
          json['animated'],
          fallback: true,
        ))!,
        id: (await safeParseStrExprAsync(
          json['id']?.toString(),
        ))!,
        itemCount: (await safeParseIntExprAsync(
          json['item_count'],
          fallback: 0,
        ))!,
        offset: (await safeParseIntExprAsync(
          json['offset'],
          fallback: 0,
        ))!,
        overflow: (await safeParseStrEnumExprAsync(
          json['overflow'],
          parse: DivActionScrollByOverflow.fromJson,
          fallback: DivActionScrollByOverflow.clamp,
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
      await animated.preload(context);
      await id.preload(context);
      await itemCount.preload(context);
      await offset.preload(context);
      await overflow.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivActionScrollByOverflow implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionScrollByOverflow?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}
