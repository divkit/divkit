// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action.dart';
import 'div_background.dart';
import 'div_border.dart';

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

  static DivFocus? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivFocus(
      background: safeParseObj(
        (json['background'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivBackground.fromJson(v),
              )!,
            )
            .toList(),
      ),
      border: safeParseObj(
        DivBorder.fromJson(json['border']),
        fallback: const DivBorder(),
      )!,
      nextFocusIds: safeParseObj(
        DivFocusNextFocusIds.fromJson(json['next_focus_ids']),
      ),
      onBlur: safeParseObj(
        (json['on_blur'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
      onFocus: safeParseObj(
        (json['on_focus'] as List<dynamic>?)
            ?.map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      ),
    );
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

  static DivFocusNextFocusIds? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}
