// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/schema/div_background.dart';
import 'package:divkit/src/schema/div_border.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Element behavior when focusing or losing focus.
class DivFocus with EquatableMixin {
  const DivFocus({
    this.background,
    this.border = const DivBorder(),
    this.nextFocusIds,
    this.onBlur,
    this.onFocus,
  });

  /// Background of an element when it is in focus. It can contain multiple layers.
  final Arr<DivBackground>? background;

  /// Border of an element when it's in focus.
  final DivBorder border;

  /// IDs of elements that will be next to get focus.
  final DivFocusNextFocusIds? nextFocusIds;

  /// Actions when an element loses focus.
  final Arr<DivAction>? onBlur;

  /// Actions when an element gets focus.
  final Arr<DivAction>? onFocus;

  @override
  List<Object?> get props => [
        background,
        border,
        nextFocusIds,
        onBlur,
        onFocus,
      ];

  DivFocus copyWith({
    Arr<DivBackground>? Function()? background,
    DivBorder? border,
    DivFocusNextFocusIds? Function()? nextFocusIds,
    Arr<DivAction>? Function()? onBlur,
    Arr<DivAction>? Function()? onFocus,
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
        background: safeParseObjects(
          json['background'],
          (v) => reqProp<DivBackground>(
            safeParseObject(
              v,
              parse: DivBackground.fromJson,
            ),
          ),
        ),
        border: reqProp<DivBorder>(
          safeParseObject(
            json['border'],
            parse: DivBorder.fromJson,
            fallback: const DivBorder(),
          ),
          name: 'border',
        ),
        nextFocusIds: safeParseObject(
          json['next_focus_ids'],
          parse: DivFocusNextFocusIds.fromJson,
        ),
        onBlur: safeParseObjects(
          json['on_blur'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        onFocus: safeParseObjects(
          json['on_focus'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

/// IDs of elements that will be next to get focus.
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

  static DivFocusNextFocusIds? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFocusNextFocusIds(
        down: safeParseStrExpr(
          json['down'],
        ),
        forward: safeParseStrExpr(
          json['forward'],
        ),
        left: safeParseStrExpr(
          json['left'],
        ),
        right: safeParseStrExpr(
          json['right'],
        ),
        up: safeParseStrExpr(
          json['up'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
