// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_blur.dart';
import 'package:divkit/src/generated_sources/div_filter_rtl_mirror.dart';

class DivFilter with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivBlur) divBlur,
    required T Function(DivFilterRtlMirror) divFilterRtlMirror,
  }) {
    switch (_index) {
      case 0:
        return divBlur(
          value as DivBlur,
        );
      case 1:
        return divFilterRtlMirror(
          value as DivFilterRtlMirror,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivFilter",
    );
  }

  T maybeMap<T>({
    T Function(DivBlur)? divBlur,
    T Function(DivFilterRtlMirror)? divFilterRtlMirror,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divBlur != null) {
          return divBlur(
            value as DivBlur,
          );
        }
        break;
      case 1:
        if (divFilterRtlMirror != null) {
          return divFilterRtlMirror(
            value as DivFilterRtlMirror,
          );
        }
        break;
    }
    return orElse();
  }

  const DivFilter.divBlur(
    DivBlur obj,
  )   : value = obj,
        _index = 0;

  const DivFilter.divFilterRtlMirror(
    DivFilterRtlMirror obj,
  )   : value = obj,
        _index = 1;

  static DivFilter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivBlur.type:
          return DivFilter.divBlur(DivBlur.fromJson(json)!);
        case DivFilterRtlMirror.type:
          return DivFilter.divFilterRtlMirror(
            DivFilterRtlMirror.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
