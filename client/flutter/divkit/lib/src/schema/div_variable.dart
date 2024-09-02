// Generated code. Do not modify.

import 'package:divkit/src/schema/array_variable.dart';
import 'package:divkit/src/schema/boolean_variable.dart';
import 'package:divkit/src/schema/color_variable.dart';
import 'package:divkit/src/schema/dict_variable.dart';
import 'package:divkit/src/schema/integer_variable.dart';
import 'package:divkit/src/schema/number_variable.dart';
import 'package:divkit/src/schema/string_variable.dart';
import 'package:divkit/src/schema/url_variable.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivVariable extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

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
    switch (_index) {
      case 0:
        return arrayVariable(
          value as ArrayVariable,
        );
      case 1:
        return booleanVariable(
          value as BooleanVariable,
        );
      case 2:
        return colorVariable(
          value as ColorVariable,
        );
      case 3:
        return dictVariable(
          value as DictVariable,
        );
      case 4:
        return integerVariable(
          value as IntegerVariable,
        );
      case 5:
        return numberVariable(
          value as NumberVariable,
        );
      case 6:
        return stringVariable(
          value as StringVariable,
        );
      case 7:
        return urlVariable(
          value as UrlVariable,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivVariable",
    );
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
    switch (_index) {
      case 0:
        if (arrayVariable != null) {
          return arrayVariable(
            value as ArrayVariable,
          );
        }
        break;
      case 1:
        if (booleanVariable != null) {
          return booleanVariable(
            value as BooleanVariable,
          );
        }
        break;
      case 2:
        if (colorVariable != null) {
          return colorVariable(
            value as ColorVariable,
          );
        }
        break;
      case 3:
        if (dictVariable != null) {
          return dictVariable(
            value as DictVariable,
          );
        }
        break;
      case 4:
        if (integerVariable != null) {
          return integerVariable(
            value as IntegerVariable,
          );
        }
        break;
      case 5:
        if (numberVariable != null) {
          return numberVariable(
            value as NumberVariable,
          );
        }
        break;
      case 6:
        if (stringVariable != null) {
          return stringVariable(
            value as StringVariable,
          );
        }
        break;
      case 7:
        if (urlVariable != null) {
          return urlVariable(
            value as UrlVariable,
          );
        }
        break;
    }
    return orElse();
  }

  const DivVariable.arrayVariable(
    ArrayVariable obj,
  )   : value = obj,
        _index = 0;

  const DivVariable.booleanVariable(
    BooleanVariable obj,
  )   : value = obj,
        _index = 1;

  const DivVariable.colorVariable(
    ColorVariable obj,
  )   : value = obj,
        _index = 2;

  const DivVariable.dictVariable(
    DictVariable obj,
  )   : value = obj,
        _index = 3;

  const DivVariable.integerVariable(
    IntegerVariable obj,
  )   : value = obj,
        _index = 4;

  const DivVariable.numberVariable(
    NumberVariable obj,
  )   : value = obj,
        _index = 5;

  const DivVariable.stringVariable(
    StringVariable obj,
  )   : value = obj,
        _index = 6;

  const DivVariable.urlVariable(
    UrlVariable obj,
  )   : value = obj,
        _index = 7;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case ArrayVariable.type:
          return DivVariable.arrayVariable(
            ArrayVariable.fromJson(json)!,
          );
        case BooleanVariable.type:
          return DivVariable.booleanVariable(
            BooleanVariable.fromJson(json)!,
          );
        case ColorVariable.type:
          return DivVariable.colorVariable(
            ColorVariable.fromJson(json)!,
          );
        case DictVariable.type:
          return DivVariable.dictVariable(
            DictVariable.fromJson(json)!,
          );
        case IntegerVariable.type:
          return DivVariable.integerVariable(
            IntegerVariable.fromJson(json)!,
          );
        case NumberVariable.type:
          return DivVariable.numberVariable(
            NumberVariable.fromJson(json)!,
          );
        case StringVariable.type:
          return DivVariable.stringVariable(
            StringVariable.fromJson(json)!,
          );
        case UrlVariable.type:
          return DivVariable.urlVariable(
            UrlVariable.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case ArrayVariable.type:
          return DivVariable.arrayVariable(
            (await ArrayVariable.parse(json))!,
          );
        case BooleanVariable.type:
          return DivVariable.booleanVariable(
            (await BooleanVariable.parse(json))!,
          );
        case ColorVariable.type:
          return DivVariable.colorVariable(
            (await ColorVariable.parse(json))!,
          );
        case DictVariable.type:
          return DivVariable.dictVariable(
            (await DictVariable.parse(json))!,
          );
        case IntegerVariable.type:
          return DivVariable.integerVariable(
            (await IntegerVariable.parse(json))!,
          );
        case NumberVariable.type:
          return DivVariable.numberVariable(
            (await NumberVariable.parse(json))!,
          );
        case StringVariable.type:
          return DivVariable.stringVariable(
            (await StringVariable.parse(json))!,
          );
        case UrlVariable.type:
          return DivVariable.urlVariable(
            (await UrlVariable.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
