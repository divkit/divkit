// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'with_default.dart';
import 'without_default.dart';

class EnumWithDefaultType with EquatableMixin {
  const EnumWithDefaultType(
    Object value
  ) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if(value is WithDefault) {
      return value;
    }
    if(value is WithoutDefault) {
      return value;
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in EnumWithDefaultType");
  }

  T map<T>({
    required T Function(WithDefault) withDefault,
    required T Function(WithoutDefault) withoutDefault,
  }) {
    final value = _value;
    if(value is WithDefault) {
      return withDefault(value);
    }
    if(value is WithoutDefault) {
      return withoutDefault(value);
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in EnumWithDefaultType");
  }

  T maybeMap<T>({
    T Function(WithDefault)? withDefault,
    T Function(WithoutDefault)? withoutDefault,
    required T Function() orElse,
  }) {
    final value = _value;
    if(value is WithDefault && withDefault != null) {
     return withDefault(value);
    }
    if(value is WithoutDefault && withoutDefault != null) {
     return withoutDefault(value);
    }
    return orElse();
  }

  const EnumWithDefaultType.withDefault(
    WithDefault value,
  ) :
 _value = value;

  const EnumWithDefaultType.withoutDefault(
    WithoutDefault value,
  ) :
 _value = value;


  static EnumWithDefaultType? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case WithDefault.type :
        return EnumWithDefaultType(WithDefault.fromJson(json)!);
      case WithoutDefault.type :
        return EnumWithDefaultType(WithoutDefault.fromJson(json)!);
    }
    return null;
  }
}
