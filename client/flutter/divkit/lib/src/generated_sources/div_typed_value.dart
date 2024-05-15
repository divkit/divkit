// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'array_value.dart';
import 'boolean_value.dart';
import 'color_value.dart';
import 'dict_value.dart';
import 'integer_value.dart';
import 'number_value.dart';
import 'string_value.dart';
import 'url_value.dart';

class DivTypedValue with EquatableMixin {
  const DivTypedValue(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is ArrayValue) {
      return value;
    }
    if (value is BooleanValue) {
      return value;
    }
    if (value is ColorValue) {
      return value;
    }
    if (value is DictValue) {
      return value;
    }
    if (value is IntegerValue) {
      return value;
    }
    if (value is NumberValue) {
      return value;
    }
    if (value is StringValue) {
      return value;
    }
    if (value is UrlValue) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTypedValue");
  }

  T map<T>({
    required T Function(ArrayValue) arrayValue,
    required T Function(BooleanValue) booleanValue,
    required T Function(ColorValue) colorValue,
    required T Function(DictValue) dictValue,
    required T Function(IntegerValue) integerValue,
    required T Function(NumberValue) numberValue,
    required T Function(StringValue) stringValue,
    required T Function(UrlValue) urlValue,
  }) {
    final value = _value;
    if (value is ArrayValue) {
      return arrayValue(value);
    }
    if (value is BooleanValue) {
      return booleanValue(value);
    }
    if (value is ColorValue) {
      return colorValue(value);
    }
    if (value is DictValue) {
      return dictValue(value);
    }
    if (value is IntegerValue) {
      return integerValue(value);
    }
    if (value is NumberValue) {
      return numberValue(value);
    }
    if (value is StringValue) {
      return stringValue(value);
    }
    if (value is UrlValue) {
      return urlValue(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTypedValue");
  }

  T maybeMap<T>({
    T Function(ArrayValue)? arrayValue,
    T Function(BooleanValue)? booleanValue,
    T Function(ColorValue)? colorValue,
    T Function(DictValue)? dictValue,
    T Function(IntegerValue)? integerValue,
    T Function(NumberValue)? numberValue,
    T Function(StringValue)? stringValue,
    T Function(UrlValue)? urlValue,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is ArrayValue && arrayValue != null) {
      return arrayValue(value);
    }
    if (value is BooleanValue && booleanValue != null) {
      return booleanValue(value);
    }
    if (value is ColorValue && colorValue != null) {
      return colorValue(value);
    }
    if (value is DictValue && dictValue != null) {
      return dictValue(value);
    }
    if (value is IntegerValue && integerValue != null) {
      return integerValue(value);
    }
    if (value is NumberValue && numberValue != null) {
      return numberValue(value);
    }
    if (value is StringValue && stringValue != null) {
      return stringValue(value);
    }
    if (value is UrlValue && urlValue != null) {
      return urlValue(value);
    }
    return orElse();
  }

  const DivTypedValue.arrayValue(
    ArrayValue value,
  ) : _value = value;

  const DivTypedValue.booleanValue(
    BooleanValue value,
  ) : _value = value;

  const DivTypedValue.colorValue(
    ColorValue value,
  ) : _value = value;

  const DivTypedValue.dictValue(
    DictValue value,
  ) : _value = value;

  const DivTypedValue.integerValue(
    IntegerValue value,
  ) : _value = value;

  const DivTypedValue.numberValue(
    NumberValue value,
  ) : _value = value;

  const DivTypedValue.stringValue(
    StringValue value,
  ) : _value = value;

  const DivTypedValue.urlValue(
    UrlValue value,
  ) : _value = value;

  static DivTypedValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case StringValue.type:
        return DivTypedValue(StringValue.fromJson(json)!);
      case IntegerValue.type:
        return DivTypedValue(IntegerValue.fromJson(json)!);
      case NumberValue.type:
        return DivTypedValue(NumberValue.fromJson(json)!);
      case ColorValue.type:
        return DivTypedValue(ColorValue.fromJson(json)!);
      case BooleanValue.type:
        return DivTypedValue(BooleanValue.fromJson(json)!);
      case UrlValue.type:
        return DivTypedValue(UrlValue.fromJson(json)!);
      case DictValue.type:
        return DivTypedValue(DictValue.fromJson(json)!);
      case ArrayValue.type:
        return DivTypedValue(ArrayValue.fromJson(json)!);
    }
    return null;
  }
}
