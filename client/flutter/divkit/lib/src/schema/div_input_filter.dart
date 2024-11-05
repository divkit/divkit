// Generated code. Do not modify.

import 'package:divkit/src/schema/div_input_filter_expression.dart';
import 'package:divkit/src/schema/div_input_filter_regex.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivInputFilter extends Resolvable with EquatableMixin {
  final Resolvable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivInputFilterExpression) divInputFilterExpression,
    required T Function(DivInputFilterRegex) divInputFilterRegex,
  }) {
    switch (_index) {
      case 0:
        return divInputFilterExpression(
          value as DivInputFilterExpression,
        );
      case 1:
        return divInputFilterRegex(
          value as DivInputFilterRegex,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivInputFilter",
    );
  }

  T maybeMap<T>({
    T Function(DivInputFilterExpression)? divInputFilterExpression,
    T Function(DivInputFilterRegex)? divInputFilterRegex,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divInputFilterExpression != null) {
          return divInputFilterExpression(
            value as DivInputFilterExpression,
          );
        }
        break;
      case 1:
        if (divInputFilterRegex != null) {
          return divInputFilterRegex(
            value as DivInputFilterRegex,
          );
        }
        break;
    }
    return orElse();
  }

  const DivInputFilter.divInputFilterExpression(
    DivInputFilterExpression obj,
  )   : value = obj,
        _index = 0;

  const DivInputFilter.divInputFilterRegex(
    DivInputFilterRegex obj,
  )   : value = obj,
        _index = 1;

  bool get isDivInputFilterExpression => _index == 0;

  bool get isDivInputFilterRegex => _index == 1;

  static DivInputFilter? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivInputFilterExpression.type:
          return DivInputFilter.divInputFilterExpression(
            DivInputFilterExpression.fromJson(json)!,
          );
        case DivInputFilterRegex.type:
          return DivInputFilter.divInputFilterRegex(
            DivInputFilterRegex.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  @override
  DivInputFilter resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
