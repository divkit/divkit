// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

import 'with_default.dart';
import 'without_default.dart';

class EnumWithDefaultType extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(WithDefault) withDefault,
    required T Function(WithoutDefault) withoutDefault,
  }) {
    switch(_index!) {
      case 0: return withDefault(value as WithDefault,);
      case 1: return withoutDefault(value as WithoutDefault,);
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in EnumWithDefaultType",);
  }

  T maybeMap<T>({
    T Function(WithDefault)? withDefault,
    T Function(WithoutDefault)? withoutDefault,
    required T Function() orElse,
  }) {
    switch(_index!) {
    case 0:
      if (withDefault != null) {
        return withDefault(value as WithDefault,);
      }
      break;
    case 1:
      if (withoutDefault != null) {
        return withoutDefault(value as WithoutDefault,);
      }
      break;
    }
    return orElse();
  }

  const EnumWithDefaultType.withDefault(
    WithDefault obj,
  ) : value = obj,
      _index = 0;

  const EnumWithDefaultType.withoutDefault(
    WithoutDefault obj,
  ) : value = obj,
      _index = 1;

  bool get isWithDefault => _index == 0;

  bool get isWithoutDefault => _index == 1;

  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static EnumWithDefaultType? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
      case WithDefault.type :
        return EnumWithDefaultType.withDefault(WithDefault.fromJson(json)!,);
      case WithoutDefault.type :
        return EnumWithDefaultType.withoutDefault(WithoutDefault.fromJson(json)!,);
    }
      return null;
    } catch (e, st) {
      return null;
    }
  }

  static Future<EnumWithDefaultType?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
      case WithDefault.type :
        return EnumWithDefaultType.withDefault((await WithDefault.parse(json))!,);
      case WithoutDefault.type :
        return EnumWithDefaultType.withoutDefault((await WithoutDefault.parse(json))!,);
    }
      return null;
    } catch (e, st) {
      return null;
    }
  }
}
