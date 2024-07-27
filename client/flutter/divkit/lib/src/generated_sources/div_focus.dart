// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_background.dart';
import 'package:divkit/src/generated_sources/div_border.dart';

class DivFocus with EquatableMixin {
  const DivFocus({
    this.background,
    this.border = const DivBorder(),
    this.nextFocusIds,
    this.onBlur,
    this.onFocus,
  });

  final List<DivBackground>? background;

  final DivBorder border;

  final DivFocusNextFocusIds? nextFocusIds;

  final List<DivAction>? onBlur;

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

  static DivFocus? fromJson(Map<String, dynamic>? json) {
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
}

class DivFocusNextFocusIds with EquatableMixin {
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

  static DivFocusNextFocusIds? fromJson(Map<String, dynamic>? json) {
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
}
