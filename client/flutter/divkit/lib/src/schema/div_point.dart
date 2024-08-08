// Generated code. Do not modify.

import 'package:divkit/src/schema/div_dimension.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivPoint extends Preloadable with EquatableMixin {
  const DivPoint({
    required this.x,
    required this.y,
  });

  final DivDimension x;

  final DivDimension y;

  @override
  List<Object?> get props => [
        x,
        y,
      ];

  DivPoint copyWith({
    DivDimension? x,
    DivDimension? y,
  }) =>
      DivPoint(
        x: x ?? this.x,
        y: y ?? this.y,
      );

  static DivPoint? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPoint(
        x: safeParseObj(
          DivDimension.fromJson(json['x']),
        )!,
        y: safeParseObj(
          DivDimension.fromJson(json['y']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPoint?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPoint(
        x: (await safeParseObjAsync(
          DivDimension.fromJson(json['x']),
        ))!,
        y: (await safeParseObjAsync(
          DivDimension.fromJson(json['y']),
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
      await x.preload(context);
      await y.preload(context);
    } catch (e) {
      return;
    }
  }
}
