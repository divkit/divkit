// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_input_mask_base.dart';
import 'package:divkit/src/generated_sources/div_currency_input_mask.dart';
import 'package:divkit/src/generated_sources/div_fixed_length_input_mask.dart';
import 'package:divkit/src/generated_sources/div_phone_input_mask.dart';

class DivInputMask with EquatableMixin {
  final DivInputMaskBase value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivCurrencyInputMask) divCurrencyInputMask,
    required T Function(DivFixedLengthInputMask) divFixedLengthInputMask,
    required T Function(DivPhoneInputMask) divPhoneInputMask,
  }) {
    switch (_index) {
      case 0:
        return divCurrencyInputMask(
          value as DivCurrencyInputMask,
        );
      case 1:
        return divFixedLengthInputMask(
          value as DivFixedLengthInputMask,
        );
      case 2:
        return divPhoneInputMask(
          value as DivPhoneInputMask,
        );
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivInputMask");
  }

  T maybeMap<T>({
    T Function(DivCurrencyInputMask)? divCurrencyInputMask,
    T Function(DivFixedLengthInputMask)? divFixedLengthInputMask,
    T Function(DivPhoneInputMask)? divPhoneInputMask,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divCurrencyInputMask != null) {
          return divCurrencyInputMask(
            value as DivCurrencyInputMask,
          );
        }
        break;
      case 1:
        if (divFixedLengthInputMask != null) {
          return divFixedLengthInputMask(
            value as DivFixedLengthInputMask,
          );
        }
        break;
      case 2:
        if (divPhoneInputMask != null) {
          return divPhoneInputMask(
            value as DivPhoneInputMask,
          );
        }
        break;
    }
    return orElse();
  }

  const DivInputMask.divCurrencyInputMask(
    DivCurrencyInputMask obj,
  )   : value = obj,
        _index = 0;

  const DivInputMask.divFixedLengthInputMask(
    DivFixedLengthInputMask obj,
  )   : value = obj,
        _index = 1;

  const DivInputMask.divPhoneInputMask(
    DivPhoneInputMask obj,
  )   : value = obj,
        _index = 2;

  static DivInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivCurrencyInputMask.type:
        return DivInputMask.divCurrencyInputMask(
            DivCurrencyInputMask.fromJson(json)!);
      case DivFixedLengthInputMask.type:
        return DivInputMask.divFixedLengthInputMask(
            DivFixedLengthInputMask.fromJson(json)!);
      case DivPhoneInputMask.type:
        return DivInputMask.divPhoneInputMask(
            DivPhoneInputMask.fromJson(json)!);
    }
    return null;
  }
}
