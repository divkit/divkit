// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_input_mask_base.dart';
import 'div_currency_input_mask.dart';
import 'div_fixed_length_input_mask.dart';
import 'div_phone_input_mask.dart';

class DivInputMask with EquatableMixin {
  const DivInputMask(DivInputMaskBase value) : _value = value;

  final DivInputMaskBase _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  DivInputMaskBase get value {
    final value = _value;
    if (value is DivCurrencyInputMask) {
      return value;
    }
    if (value is DivFixedLengthInputMask) {
      return value;
    }
    if (value is DivPhoneInputMask) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivInputMask");
  }

  T map<T>({
    required T Function(DivCurrencyInputMask) divCurrencyInputMask,
    required T Function(DivFixedLengthInputMask) divFixedLengthInputMask,
    required T Function(DivPhoneInputMask) divPhoneInputMask,
  }) {
    final value = _value;
    if (value is DivCurrencyInputMask) {
      return divCurrencyInputMask(value);
    }
    if (value is DivFixedLengthInputMask) {
      return divFixedLengthInputMask(value);
    }
    if (value is DivPhoneInputMask) {
      return divPhoneInputMask(value);
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
    final value = _value;
    if (value is DivCurrencyInputMask && divCurrencyInputMask != null) {
      return divCurrencyInputMask(value);
    }
    if (value is DivFixedLengthInputMask && divFixedLengthInputMask != null) {
      return divFixedLengthInputMask(value);
    }
    if (value is DivPhoneInputMask && divPhoneInputMask != null) {
      return divPhoneInputMask(value);
    }
    return orElse();
  }

  const DivInputMask.divCurrencyInputMask(
    DivCurrencyInputMask value,
  ) : _value = value;

  const DivInputMask.divFixedLengthInputMask(
    DivFixedLengthInputMask value,
  ) : _value = value;

  const DivInputMask.divPhoneInputMask(
    DivPhoneInputMask value,
  ) : _value = value;

  static DivInputMask? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivFixedLengthInputMask.type:
        return DivInputMask(DivFixedLengthInputMask.fromJson(json)!);
      case DivCurrencyInputMask.type:
        return DivInputMask(DivCurrencyInputMask.fromJson(json)!);
      case DivPhoneInputMask.type:
        return DivInputMask(DivPhoneInputMask.fromJson(json)!);
    }
    return null;
  }
}
