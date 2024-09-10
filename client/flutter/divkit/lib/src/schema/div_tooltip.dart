// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_animation.dart';
import 'package:divkit/src/schema/div_point.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Tooltip.
class DivTooltip extends Preloadable with EquatableMixin {
  const DivTooltip({
    this.animationIn,
    this.animationOut,
    required this.div,
    this.duration = const ValueExpression(5000),
    required this.id,
    this.offset,
    required this.position,
  });

  /// Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
  final DivAnimation? animationIn;

  /// Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
  final DivAnimation? animationOut;

  /// An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
  final Div div;

  /// Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
  // constraint: number >= 0; default value: 5000
  final Expression<int> duration;

  /// Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
  final String id;

  /// Shift relative to an anchor point.
  final DivPoint? offset;

  /// The position of a tooltip relative to an element it belongs to.
  final Expression<DivTooltipPosition> position;

  @override
  List<Object?> get props => [
        animationIn,
        animationOut,
        div,
        duration,
        id,
        offset,
        position,
      ];

  DivTooltip copyWith({
    DivAnimation? Function()? animationIn,
    DivAnimation? Function()? animationOut,
    Div? div,
    Expression<int>? duration,
    String? id,
    DivPoint? Function()? offset,
    Expression<DivTooltipPosition>? position,
  }) =>
      DivTooltip(
        animationIn:
            animationIn != null ? animationIn.call() : this.animationIn,
        animationOut:
            animationOut != null ? animationOut.call() : this.animationOut,
        div: div ?? this.div,
        duration: duration ?? this.duration,
        id: id ?? this.id,
        offset: offset != null ? offset.call() : this.offset,
        position: position ?? this.position,
      );

  static DivTooltip? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTooltip(
        animationIn: safeParseObj(
          DivAnimation.fromJson(json['animation_in']),
        ),
        animationOut: safeParseObj(
          DivAnimation.fromJson(json['animation_out']),
        ),
        div: safeParseObj(
          Div.fromJson(json['div']),
        )!,
        duration: safeParseIntExpr(
          json['duration'],
          fallback: 5000,
        )!,
        id: safeParseStr(
          json['id']?.toString(),
        )!,
        offset: safeParseObj(
          DivPoint.fromJson(json['offset']),
        ),
        position: safeParseStrEnumExpr(
          json['position'],
          parse: DivTooltipPosition.fromJson,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTooltip?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTooltip(
        animationIn: await safeParseObjAsync(
          DivAnimation.fromJson(json['animation_in']),
        ),
        animationOut: await safeParseObjAsync(
          DivAnimation.fromJson(json['animation_out']),
        ),
        div: (await safeParseObjAsync(
          Div.fromJson(json['div']),
        ))!,
        duration: (await safeParseIntExprAsync(
          json['duration'],
          fallback: 5000,
        ))!,
        id: (await safeParseStrAsync(
          json['id']?.toString(),
        ))!,
        offset: await safeParseObjAsync(
          DivPoint.fromJson(json['offset']),
        ),
        position: (await safeParseStrEnumExprAsync(
          json['position'],
          parse: DivTooltipPosition.fromJson,
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
      await animationIn?.preload(context);
      await animationOut?.preload(context);
      await div.preload(context);
      await duration.preload(context);
      await offset?.preload(context);
      await position.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivTooltipPosition implements Preloadable {
  left('left'),
  topLeft('top-left'),
  top('top'),
  topRight('top-right'),
  right('right'),
  bottomRight('bottom-right'),
  bottom('bottom'),
  bottomLeft('bottom-left'),
  center('center');

  final String value;

  const DivTooltipPosition(this.value);
  bool get isLeft => this == left;

  bool get isTopLeft => this == topLeft;

  bool get isTop => this == top;

  bool get isTopRight => this == topRight;

  bool get isRight => this == right;

  bool get isBottomRight => this == bottomRight;

  bool get isBottom => this == bottom;

  bool get isBottomLeft => this == bottomLeft;

  bool get isCenter => this == center;

  T map<T>({
    required T Function() left,
    required T Function() topLeft,
    required T Function() top,
    required T Function() topRight,
    required T Function() right,
    required T Function() bottomRight,
    required T Function() bottom,
    required T Function() bottomLeft,
    required T Function() center,
  }) {
    switch (this) {
      case DivTooltipPosition.left:
        return left();
      case DivTooltipPosition.topLeft:
        return topLeft();
      case DivTooltipPosition.top:
        return top();
      case DivTooltipPosition.topRight:
        return topRight();
      case DivTooltipPosition.right:
        return right();
      case DivTooltipPosition.bottomRight:
        return bottomRight();
      case DivTooltipPosition.bottom:
        return bottom();
      case DivTooltipPosition.bottomLeft:
        return bottomLeft();
      case DivTooltipPosition.center:
        return center();
    }
  }

  T maybeMap<T>({
    T Function()? left,
    T Function()? topLeft,
    T Function()? top,
    T Function()? topRight,
    T Function()? right,
    T Function()? bottomRight,
    T Function()? bottom,
    T Function()? bottomLeft,
    T Function()? center,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTooltipPosition.left:
        return left?.call() ?? orElse();
      case DivTooltipPosition.topLeft:
        return topLeft?.call() ?? orElse();
      case DivTooltipPosition.top:
        return top?.call() ?? orElse();
      case DivTooltipPosition.topRight:
        return topRight?.call() ?? orElse();
      case DivTooltipPosition.right:
        return right?.call() ?? orElse();
      case DivTooltipPosition.bottomRight:
        return bottomRight?.call() ?? orElse();
      case DivTooltipPosition.bottom:
        return bottom?.call() ?? orElse();
      case DivTooltipPosition.bottomLeft:
        return bottomLeft?.call() ?? orElse();
      case DivTooltipPosition.center:
        return center?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivTooltipPosition? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'left':
          return DivTooltipPosition.left;
        case 'top-left':
          return DivTooltipPosition.topLeft;
        case 'top':
          return DivTooltipPosition.top;
        case 'top-right':
          return DivTooltipPosition.topRight;
        case 'right':
          return DivTooltipPosition.right;
        case 'bottom-right':
          return DivTooltipPosition.bottomRight;
        case 'bottom':
          return DivTooltipPosition.bottom;
        case 'bottom-left':
          return DivTooltipPosition.bottomLeft;
        case 'center':
          return DivTooltipPosition.center;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivTooltipPosition?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'left':
          return DivTooltipPosition.left;
        case 'top-left':
          return DivTooltipPosition.topLeft;
        case 'top':
          return DivTooltipPosition.top;
        case 'top-right':
          return DivTooltipPosition.topRight;
        case 'right':
          return DivTooltipPosition.right;
        case 'bottom-right':
          return DivTooltipPosition.bottomRight;
        case 'bottom':
          return DivTooltipPosition.bottom;
        case 'bottom-left':
          return DivTooltipPosition.bottomLeft;
        case 'center':
          return DivTooltipPosition.center;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
