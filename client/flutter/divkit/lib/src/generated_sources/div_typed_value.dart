// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/array_value.dart';
import 'package:divkit/src/generated_sources/boolean_value.dart';
import 'package:divkit/src/generated_sources/color_value.dart';
import 'package:divkit/src/generated_sources/dict_value.dart';
import 'package:divkit/src/generated_sources/integer_value.dart';
import 'package:divkit/src/generated_sources/number_value.dart';
import 'package:divkit/src/generated_sources/string_value.dart';
import 'package:divkit/src/generated_sources/url_value.dart';

class DivTypedValue with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

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
    switch (_index) {
      case 0:
        return arrayValue(
          value as ArrayValue,
        );
      case 1:
        return booleanValue(
          value as BooleanValue,
        );
      case 2:
        return colorValue(
          value as ColorValue,
        );
      case 3:
        return dictValue(
          value as DictValue,
        );
      case 4:
        return integerValue(
          value as IntegerValue,
        );
      case 5:
        return numberValue(
          value as NumberValue,
        );
      case 6:
        return stringValue(
          value as StringValue,
        );
      case 7:
        return urlValue(
          value as UrlValue,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivTypedValue",
    );
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
    switch (_index) {
      case 0:
        if (arrayValue != null) {
          return arrayValue(
            value as ArrayValue,
          );
        }
        break;
      case 1:
        if (booleanValue != null) {
          return booleanValue(
            value as BooleanValue,
          );
        }
        break;
      case 2:
        if (colorValue != null) {
          return colorValue(
            value as ColorValue,
          );
        }
        break;
      case 3:
        if (dictValue != null) {
          return dictValue(
            value as DictValue,
          );
        }
        break;
      case 4:
        if (integerValue != null) {
          return integerValue(
            value as IntegerValue,
          );
        }
        break;
      case 5:
        if (numberValue != null) {
          return numberValue(
            value as NumberValue,
          );
        }
        break;
      case 6:
        if (stringValue != null) {
          return stringValue(
            value as StringValue,
          );
        }
        break;
      case 7:
        if (urlValue != null) {
          return urlValue(
            value as UrlValue,
          );
        }
        break;
    }
    return orElse();
  }

  const DivTypedValue.arrayValue(
    ArrayValue obj,
  )   : value = obj,
        _index = 0;

  const DivTypedValue.booleanValue(
    BooleanValue obj,
  )   : value = obj,
        _index = 1;

  const DivTypedValue.colorValue(
    ColorValue obj,
  )   : value = obj,
        _index = 2;

  const DivTypedValue.dictValue(
    DictValue obj,
  )   : value = obj,
        _index = 3;

  const DivTypedValue.integerValue(
    IntegerValue obj,
  )   : value = obj,
        _index = 4;

  const DivTypedValue.numberValue(
    NumberValue obj,
  )   : value = obj,
        _index = 5;

  const DivTypedValue.stringValue(
    StringValue obj,
  )   : value = obj,
        _index = 6;

  const DivTypedValue.urlValue(
    UrlValue obj,
  )   : value = obj,
        _index = 7;

  static DivTypedValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case ArrayValue.type:
          return DivTypedValue.arrayValue(ArrayValue.fromJson(json)!);
        case BooleanValue.type:
          return DivTypedValue.booleanValue(BooleanValue.fromJson(json)!);
        case ColorValue.type:
          return DivTypedValue.colorValue(ColorValue.fromJson(json)!);
        case DictValue.type:
          return DivTypedValue.dictValue(DictValue.fromJson(json)!);
        case IntegerValue.type:
          return DivTypedValue.integerValue(IntegerValue.fromJson(json)!);
        case NumberValue.type:
          return DivTypedValue.numberValue(NumberValue.fromJson(json)!);
        case StringValue.type:
          return DivTypedValue.stringValue(StringValue.fromJson(json)!);
        case UrlValue.type:
          return DivTypedValue.urlValue(UrlValue.fromJson(json)!);
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
