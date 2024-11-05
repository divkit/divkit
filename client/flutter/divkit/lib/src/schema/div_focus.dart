// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Element behavior when focusing or losing focus.
class DivFocus extends Resolvable with EquatableMixin {
  const DivFocus({
    this.background,
    this.border = const DivBorder(),
    this.nextFocusIds,
    this.onBlur,
    this.onFocus,
  });

  /// Background of an element when it is in focus. It can contain multiple layers.
  final List<DivBackground>? background;

  /// Border of an element when it's in focus.
  final DivBorder border;

  /// IDs of elements that will be next to get focus.
  final DivFocusNextFocusIds? nextFocusIds;

  /// Actions when an element loses focus.
  final List<DivAction>? onBlur;

  /// Actions when an element gets focus.
  final List<DivAction>? onFocus;

  @override
  List<Object?> get props => [
        background,
        border,
        nextFocusIds,
        onBlur,
        onFocus,
      ];

  DivFocus copyWith({
    List<DivBackground>? Function()? background,
    DivBorder? border,
    DivFocusNextFocusIds? Function()? nextFocusIds,
    List<DivAction>? Function()? onBlur,
    List<DivAction>? Function()? onFocus,
  }) =>
      DivFocus(
        background: background != null ? background.call() : this.background,
        border: border ?? this.border,
        nextFocusIds:
            nextFocusIds != null ? nextFocusIds.call() : this.nextFocusIds,
        onBlur: onBlur != null ? onBlur.call() : this.onBlur,
        onFocus: onFocus != null ? onFocus.call() : this.onFocus,
      );

  static DivFocus? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFocus(
        background: safeParseObj(
          safeListMap(
            json['background'],
            (v) => safeParseObj(
              DivBackground.fromJson(v),
            )!,
          ),
        ),
        border: safeParseObj(
          DivBorder.fromJson(json['border']),
          fallback: const DivBorder(),
        )!,
        nextFocusIds: safeParseObj(
          DivFocusNextFocusIds.fromJson(json['next_focus_ids']),
        ),
        onBlur: safeParseObj(
          safeListMap(
            json['on_blur'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        onFocus: safeParseObj(
          safeListMap(
            json['on_focus'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivFocus resolve(DivVariableContext context) {
    safeListResolve(background, (v) => v.resolve(context));
    border.resolve(context);
    nextFocusIds?.resolve(context);
    safeListResolve(onBlur, (v) => v.resolve(context));
    safeListResolve(onFocus, (v) => v.resolve(context));
    return this;
  }
}

/// IDs of elements that will be next to get focus.
class DivFocusNextFocusIds extends Resolvable with EquatableMixin {
  const DivFocusNextFocusIds({
    this.down,
    this.forward,
    this.left,
    this.right,
    this.up,
  });

  final Expression<String>? down;
  final Expression<String>? forward;
  final Expression<String>? left;
  final Expression<String>? right;
  final Expression<String>? up;

  @override
  List<Object?> get props => [
        down,
        forward,
        left,
        right,
        up,
      ];

  DivFocusNextFocusIds copyWith({
    Expression<String>? Function()? down,
    Expression<String>? Function()? forward,
    Expression<String>? Function()? left,
    Expression<String>? Function()? right,
    Expression<String>? Function()? up,
  }) =>
      DivFocusNextFocusIds(
        down: down != null ? down.call() : this.down,
        forward: forward != null ? forward.call() : this.forward,
        left: left != null ? left.call() : this.left,
        right: right != null ? right.call() : this.right,
        up: up != null ? up.call() : this.up,
      );

  static DivFocusNextFocusIds? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFocusNextFocusIds(
        down: safeParseStrExpr(
          json['down']?.toString(),
        ),
        forward: safeParseStrExpr(
          json['forward']?.toString(),
        ),
        left: safeParseStrExpr(
          json['left']?.toString(),
        ),
        right: safeParseStrExpr(
          json['right']?.toString(),
        ),
        up: safeParseStrExpr(
          json['up']?.toString(),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivFocusNextFocusIds resolve(DivVariableContext context) {
    down?.resolve(context);
    forward?.resolve(context);
    left?.resolve(context);
    right?.resolve(context);
    up?.resolve(context);
    return this;
  }
}
