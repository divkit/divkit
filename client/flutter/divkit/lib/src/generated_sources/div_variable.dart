// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'array_variable.dart';
import 'boolean_variable.dart';
import 'color_variable.dart';
import 'dict_variable.dart';
import 'integer_variable.dart';
import 'number_variable.dart';
import 'string_variable.dart';
import 'url_variable.dart';

class DivVariable with EquatableMixin {
  const DivVariable(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is ArrayVariable) {
      return value;
    }
    if (value is BooleanVariable) {
      return value;
    }
    if (value is ColorVariable) {
      return value;
    }
    if (value is DictVariable) {
      return value;
    }
    if (value is IntegerVariable) {
      return value;
    }
    if (value is NumberVariable) {
      return value;
    }
    if (value is StringVariable) {
      return value;
    }
    if (value is UrlVariable) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivVariable");
  }

  T map<T>({
    required T Function(ArrayVariable) arrayVariable,
    required T Function(BooleanVariable) booleanVariable,
    required T Function(ColorVariable) colorVariable,
    required T Function(DictVariable) dictVariable,
    required T Function(IntegerVariable) integerVariable,
    required T Function(NumberVariable) numberVariable,
    required T Function(StringVariable) stringVariable,
    required T Function(UrlVariable) urlVariable,
  }) {
    final value = _value;
    if (value is ArrayVariable) {
      return arrayVariable(value);
    }
    if (value is BooleanVariable) {
      return booleanVariable(value);
    }
    if (value is ColorVariable) {
      return colorVariable(value);
    }
    if (value is DictVariable) {
      return dictVariable(value);
    }
    if (value is IntegerVariable) {
      return integerVariable(value);
    }
    if (value is NumberVariable) {
      return numberVariable(value);
    }
    if (value is StringVariable) {
      return stringVariable(value);
    }
    if (value is UrlVariable) {
      return urlVariable(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivVariable");
  }

  T maybeMap<T>({
    T Function(ArrayVariable)? arrayVariable,
    T Function(BooleanVariable)? booleanVariable,
    T Function(ColorVariable)? colorVariable,
    T Function(DictVariable)? dictVariable,
    T Function(IntegerVariable)? integerVariable,
    T Function(NumberVariable)? numberVariable,
    T Function(StringVariable)? stringVariable,
    T Function(UrlVariable)? urlVariable,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is ArrayVariable && arrayVariable != null) {
      return arrayVariable(value);
    }
    if (value is BooleanVariable && booleanVariable != null) {
      return booleanVariable(value);
    }
    if (value is ColorVariable && colorVariable != null) {
      return colorVariable(value);
    }
    if (value is DictVariable && dictVariable != null) {
      return dictVariable(value);
    }
    if (value is IntegerVariable && integerVariable != null) {
      return integerVariable(value);
    }
    if (value is NumberVariable && numberVariable != null) {
      return numberVariable(value);
    }
    if (value is StringVariable && stringVariable != null) {
      return stringVariable(value);
    }
    if (value is UrlVariable && urlVariable != null) {
      return urlVariable(value);
    }
    return orElse();
  }

  const DivVariable.arrayVariable(
    ArrayVariable value,
  ) : _value = value;

  const DivVariable.booleanVariable(
    BooleanVariable value,
  ) : _value = value;

  const DivVariable.colorVariable(
    ColorVariable value,
  ) : _value = value;

  const DivVariable.dictVariable(
    DictVariable value,
  ) : _value = value;

  const DivVariable.integerVariable(
    IntegerVariable value,
  ) : _value = value;

  const DivVariable.numberVariable(
    NumberVariable value,
  ) : _value = value;

  const DivVariable.stringVariable(
    StringVariable value,
  ) : _value = value;

  const DivVariable.urlVariable(
    UrlVariable value,
  ) : _value = value;

  static DivVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case StringVariable.type:
        return DivVariable(StringVariable.fromJson(json)!);
      case NumberVariable.type:
        return DivVariable(NumberVariable.fromJson(json)!);
      case IntegerVariable.type:
        return DivVariable(IntegerVariable.fromJson(json)!);
      case BooleanVariable.type:
        return DivVariable(BooleanVariable.fromJson(json)!);
      case ColorVariable.type:
        return DivVariable(ColorVariable.fromJson(json)!);
      case UrlVariable.type:
        return DivVariable(UrlVariable.fromJson(json)!);
      case DictVariable.type:
        return DivVariable(DictVariable.fromJson(json)!);
      case ArrayVariable.type:
        return DivVariable(ArrayVariable.fromJson(json)!);
    }
    return null;
  }
}
