// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_blur.dart';
import 'div_filter_rtl_mirror.dart';

class DivFilter with EquatableMixin {
  const DivFilter(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivBlur) {
      return value;
    }
    if (value is DivFilterRtlMirror) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivFilter");
  }

  T map<T>({
    required T Function(DivBlur) divBlur,
    required T Function(DivFilterRtlMirror) divFilterRtlMirror,
  }) {
    final value = _value;
    if (value is DivBlur) {
      return divBlur(value);
    }
    if (value is DivFilterRtlMirror) {
      return divFilterRtlMirror(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivFilter");
  }

  T maybeMap<T>({
    T Function(DivBlur)? divBlur,
    T Function(DivFilterRtlMirror)? divFilterRtlMirror,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivBlur && divBlur != null) {
      return divBlur(value);
    }
    if (value is DivFilterRtlMirror && divFilterRtlMirror != null) {
      return divFilterRtlMirror(value);
    }
    return orElse();
  }

  const DivFilter.divBlur(
    DivBlur value,
  ) : _value = value;

  const DivFilter.divFilterRtlMirror(
    DivFilterRtlMirror value,
  ) : _value = value;

  static DivFilter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivBlur.type:
        return DivFilter(DivBlur.fromJson(json)!);
      case DivFilterRtlMirror.type:
        return DivFilter(DivFilterRtlMirror.fromJson(json)!);
    }
    return null;
  }
}
