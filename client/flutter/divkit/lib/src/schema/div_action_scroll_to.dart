// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_scroll_destination.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Scrolls or switches container to given destination provided by `destination`.
class DivActionScrollTo extends Preloadable with EquatableMixin {
  const DivActionScrollTo({
    this.animated = const ValueExpression(true),
    required this.destination,
    required this.id,
  });

  static const type = "scroll_to";

  /// If `true` (default value) scroll will be animated, else not.
  // default value: true
  final Expression<bool> animated;

  /// Specifies destination of scroll:
  /// • `index` - scroll or switch to item with index provided by `value`;
  /// • `offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;
  /// • `start` - scrolls to start of container;
  /// • `end` - scrolls to end of container..
  final DivActionScrollDestination destination;

  /// Identifier of the view that is going to be manipulated.
  final Expression<String> id;

  @override
  List<Object?> get props => [
        animated,
        destination,
        id,
      ];

  DivActionScrollTo copyWith({
    Expression<bool>? animated,
    DivActionScrollDestination? destination,
    Expression<String>? id,
  }) =>
      DivActionScrollTo(
        animated: animated ?? this.animated,
        destination: destination ?? this.destination,
        id: id ?? this.id,
      );

  static DivActionScrollTo? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionScrollTo(
        animated: safeParseBoolExpr(
          json['animated'],
          fallback: true,
        )!,
        destination: safeParseObj(
          DivActionScrollDestination.fromJson(json['destination']),
        )!,
        id: safeParseStrExpr(
          json['id']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionScrollTo?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionScrollTo(
        animated: (await safeParseBoolExprAsync(
          json['animated'],
          fallback: true,
        ))!,
        destination: (await safeParseObjAsync(
          DivActionScrollDestination.fromJson(json['destination']),
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
      await animated.preload(context);
      await destination.preload(context);
      await id.preload(context);
    } catch (e) {
      return;
    }
  }
}
