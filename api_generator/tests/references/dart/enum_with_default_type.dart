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

  void map({
    required Function(WithDefault) withDefault,
    required Function(WithoutDefault) withoutDefault,
  }) {
    final value = _value;
    if(value is WithDefault) {
      withDefault(value);
      return;
    }
    if(value is WithoutDefault) {
      withoutDefault(value);
      return;
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in EnumWithDefaultType");
  }

  void maybeMap({
    Function(WithDefault)? withDefault,
    Function(WithoutDefault)? withoutDefault,
    required Function() orElse,
  }) {
    final value = _value;
    if(value is WithDefault && withDefault != null) {
      withDefault(value);
      return;
    }
    if(value is WithoutDefault && withoutDefault != null) {
      withoutDefault(value);
      return;
    }
    orElse();
  }

  const EnumWithDefaultType.withDefault(
    WithDefault value,
  ) : _value = value;

  const EnumWithDefaultType.withoutDefault(
    WithoutDefault value,
  ) : _value = value;


  static EnumWithDefaultType? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case WithDefault.type :
        return EnumWithDefaultType.withDefault(WithDefault.fromJson(json)!);
      case WithoutDefault.type :
        return EnumWithDefaultType.withoutDefault(WithoutDefault.fromJson(json)!);
    }
    return null;
  }
}
