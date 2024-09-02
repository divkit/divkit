// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivRadialGradientRelativeRadius extends Preloadable with EquatableMixin {
  const DivRadialGradientRelativeRadius({
    required this.value,
  });

  static const type = "relative";

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
        value: safeParseStrEnumExpr(
          json['value'],
          parse: DivRadialGradientRelativeRadiusValue.fromJson,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivRadialGradientRelativeRadius?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientRelativeRadius(
        value: (await safeParseStrEnumExprAsync(
          json['value'],
          parse: DivRadialGradientRelativeRadiusValue.fromJson,
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
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivRadialGradientRelativeRadiusValue implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivRadialGradientRelativeRadiusValue?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}
