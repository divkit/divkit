// Generated code. Do not modify.

import 'package:divkit/src/schema/div_text_range_mask_particles.dart';
import 'package:divkit/src/schema/div_text_range_mask_solid.dart';
import 'package:equatable/equatable.dart';

class DivTextRangeMask with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivTextRangeMaskParticles) divTextRangeMaskParticles,
    required T Function(DivTextRangeMaskSolid) divTextRangeMaskSolid,
  }) {
    switch (_index) {
      case 0:
        return divTextRangeMaskParticles(
          value as DivTextRangeMaskParticles,
        );
      case 1:
        return divTextRangeMaskSolid(
          value as DivTextRangeMaskSolid,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivTextRangeMask",
    );
  }

  T maybeMap<T>({
    T Function(DivTextRangeMaskParticles)? divTextRangeMaskParticles,
    T Function(DivTextRangeMaskSolid)? divTextRangeMaskSolid,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divTextRangeMaskParticles != null) {
          return divTextRangeMaskParticles(
            value as DivTextRangeMaskParticles,
          );
        }
        break;
      case 1:
        if (divTextRangeMaskSolid != null) {
          return divTextRangeMaskSolid(
            value as DivTextRangeMaskSolid,
          );
        }
        break;
    }
    return orElse();
  }

  const DivTextRangeMask.divTextRangeMaskParticles(
    DivTextRangeMaskParticles obj,
  )   : value = obj,
        _index = 0;

  const DivTextRangeMask.divTextRangeMaskSolid(
    DivTextRangeMaskSolid obj,
  )   : value = obj,
        _index = 1;

  bool get isDivTextRangeMaskParticles => _index == 0;

  bool get isDivTextRangeMaskSolid => _index == 1;

  static DivTextRangeMask? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivTextRangeMaskParticles.type:
          return DivTextRangeMask.divTextRangeMaskParticles(
            DivTextRangeMaskParticles.fromJson(json)!,
          );
        case DivTextRangeMaskSolid.type:
          return DivTextRangeMask.divTextRangeMaskSolid(
            DivTextRangeMaskSolid.fromJson(json)!,
          );
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}
