// Generated code. Do not modify.

import 'package:divkit/src/schema/div_cloud_background.dart';
import 'package:divkit/src/schema/div_solid_background.dart';
import 'package:equatable/equatable.dart';

class DivTextRangeBackground with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivCloudBackground) divCloudBackground,
    required T Function(DivSolidBackground) divSolidBackground,
  }) {
    switch (_index) {
      case 0:
        return divCloudBackground(
          value as DivCloudBackground,
        );
      case 1:
        return divSolidBackground(
          value as DivSolidBackground,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivTextRangeBackground",
    );
  }

  T maybeMap<T>({
    T Function(DivCloudBackground)? divCloudBackground,
    T Function(DivSolidBackground)? divSolidBackground,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divCloudBackground != null) {
          return divCloudBackground(
            value as DivCloudBackground,
          );
        }
        break;
      case 1:
        if (divSolidBackground != null) {
          return divSolidBackground(
            value as DivSolidBackground,
          );
        }
        break;
    }
    return orElse();
  }

  const DivTextRangeBackground.divCloudBackground(
    DivCloudBackground obj,
  )   : value = obj,
        _index = 0;

  const DivTextRangeBackground.divSolidBackground(
    DivSolidBackground obj,
  )   : value = obj,
        _index = 1;

  bool get isDivCloudBackground => _index == 0;

  bool get isDivSolidBackground => _index == 1;

  static DivTextRangeBackground? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivCloudBackground.type:
          return DivTextRangeBackground.divCloudBackground(
            DivCloudBackground.fromJson(json)!,
          );
        case DivSolidBackground.type:
          return DivTextRangeBackground.divSolidBackground(
            DivSolidBackground.fromJson(json)!,
          );
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}
