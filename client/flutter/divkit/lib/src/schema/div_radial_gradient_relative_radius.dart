// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Relative radius of the gradient transition.
class DivRadialGradientRelativeRadius extends Resolvable with EquatableMixin {
  const DivRadialGradientRelativeRadius({
    required this.value,
  });

  static const type = "relative";

  /// Type of the relative radius of the gradient transition.
  final Expression<DivRadialGradientRelativeRadiusValue> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivRadialGradientRelativeRadius copyWith({
    Expression<DivRadialGradientRelativeRadiusValue>? value,
  }) =>
      DivRadialGradientRelativeRadius(
        value: value ?? this.value,
      );

  static DivRadialGradientRelativeRadius? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientRelativeRadius(
        value: reqVProp<DivRadialGradientRelativeRadiusValue>(
          safeParseStrEnumExpr(
            json['value'],
            parse: DivRadialGradientRelativeRadiusValue.fromJson,
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivRadialGradientRelativeRadius resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}

enum DivRadialGradientRelativeRadiusValue implements Resolvable {
  nearestCorner('nearest_corner'),
  farthestCorner('farthest_corner'),
  nearestSide('nearest_side'),
  farthestSide('farthest_side');

  final String value;

  const DivRadialGradientRelativeRadiusValue(this.value);
  bool get isNearestCorner => this == nearestCorner;

  bool get isFarthestCorner => this == farthestCorner;

  bool get isNearestSide => this == nearestSide;

  bool get isFarthestSide => this == farthestSide;

  T map<T>({
    required T Function() nearestCorner,
    required T Function() farthestCorner,
    required T Function() nearestSide,
    required T Function() farthestSide,
  }) {
    switch (this) {
      case DivRadialGradientRelativeRadiusValue.nearestCorner:
        return nearestCorner();
      case DivRadialGradientRelativeRadiusValue.farthestCorner:
        return farthestCorner();
      case DivRadialGradientRelativeRadiusValue.nearestSide:
        return nearestSide();
      case DivRadialGradientRelativeRadiusValue.farthestSide:
        return farthestSide();
    }
  }

  T maybeMap<T>({
    T Function()? nearestCorner,
    T Function()? farthestCorner,
    T Function()? nearestSide,
    T Function()? farthestSide,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivRadialGradientRelativeRadiusValue.nearestCorner:
        return nearestCorner?.call() ?? orElse();
      case DivRadialGradientRelativeRadiusValue.farthestCorner:
        return farthestCorner?.call() ?? orElse();
      case DivRadialGradientRelativeRadiusValue.nearestSide:
        return nearestSide?.call() ?? orElse();
      case DivRadialGradientRelativeRadiusValue.farthestSide:
        return farthestSide?.call() ?? orElse();
    }
  }

  static DivRadialGradientRelativeRadiusValue? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'nearest_corner':
          return DivRadialGradientRelativeRadiusValue.nearestCorner;
        case 'farthest_corner':
          return DivRadialGradientRelativeRadiusValue.farthestCorner;
        case 'nearest_side':
          return DivRadialGradientRelativeRadiusValue.nearestSide;
        case 'farthest_side':
          return DivRadialGradientRelativeRadiusValue.farthestSide;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivRadialGradientRelativeRadiusValue: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivRadialGradientRelativeRadiusValue resolve(DivVariableContext context) =>
      this;
}
