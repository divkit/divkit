// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_action_array_insert_value.dart';
import 'div_action_array_remove_value.dart';
import 'div_action_array_set_value.dart';
import 'div_action_clear_focus.dart';
import 'div_action_copy_to_clipboard.dart';
import 'div_action_dict_set_value.dart';
import 'div_action_focus_element.dart';
import 'div_action_set_variable.dart';

class DivActionTyped with EquatableMixin {
  const DivActionTyped(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivActionArrayInsertValue) {
      return value;
    }
    if (value is DivActionArrayRemoveValue) {
      return value;
    }
    if (value is DivActionArraySetValue) {
      return value;
    }
    if (value is DivActionClearFocus) {
      return value;
    }
    if (value is DivActionCopyToClipboard) {
      return value;
    }
    if (value is DivActionDictSetValue) {
      return value;
    }
    if (value is DivActionFocusElement) {
      return value;
    }
    if (value is DivActionSetVariable) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivActionTyped");
  }

  T map<T>({
    required T Function(DivActionArrayInsertValue) divActionArrayInsertValue,
    required T Function(DivActionArrayRemoveValue) divActionArrayRemoveValue,
    required T Function(DivActionArraySetValue) divActionArraySetValue,
    required T Function(DivActionClearFocus) divActionClearFocus,
    required T Function(DivActionCopyToClipboard) divActionCopyToClipboard,
    required T Function(DivActionDictSetValue) divActionDictSetValue,
    required T Function(DivActionFocusElement) divActionFocusElement,
    required T Function(DivActionSetVariable) divActionSetVariable,
  }) {
    final value = _value;
    if (value is DivActionArrayInsertValue) {
      return divActionArrayInsertValue(value);
    }
    if (value is DivActionArrayRemoveValue) {
      return divActionArrayRemoveValue(value);
    }
    if (value is DivActionArraySetValue) {
      return divActionArraySetValue(value);
    }
    if (value is DivActionClearFocus) {
      return divActionClearFocus(value);
    }
    if (value is DivActionCopyToClipboard) {
      return divActionCopyToClipboard(value);
    }
    if (value is DivActionDictSetValue) {
      return divActionDictSetValue(value);
    }
    if (value is DivActionFocusElement) {
      return divActionFocusElement(value);
    }
    if (value is DivActionSetVariable) {
      return divActionSetVariable(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivActionTyped");
  }

  T maybeMap<T>({
    T Function(DivActionArrayInsertValue)? divActionArrayInsertValue,
    T Function(DivActionArrayRemoveValue)? divActionArrayRemoveValue,
    T Function(DivActionArraySetValue)? divActionArraySetValue,
    T Function(DivActionClearFocus)? divActionClearFocus,
    T Function(DivActionCopyToClipboard)? divActionCopyToClipboard,
    T Function(DivActionDictSetValue)? divActionDictSetValue,
    T Function(DivActionFocusElement)? divActionFocusElement,
    T Function(DivActionSetVariable)? divActionSetVariable,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivActionArrayInsertValue &&
        divActionArrayInsertValue != null) {
      return divActionArrayInsertValue(value);
    }
    if (value is DivActionArrayRemoveValue &&
        divActionArrayRemoveValue != null) {
      return divActionArrayRemoveValue(value);
    }
    if (value is DivActionArraySetValue && divActionArraySetValue != null) {
      return divActionArraySetValue(value);
    }
    if (value is DivActionClearFocus && divActionClearFocus != null) {
      return divActionClearFocus(value);
    }
    if (value is DivActionCopyToClipboard && divActionCopyToClipboard != null) {
      return divActionCopyToClipboard(value);
    }
    if (value is DivActionDictSetValue && divActionDictSetValue != null) {
      return divActionDictSetValue(value);
    }
    if (value is DivActionFocusElement && divActionFocusElement != null) {
      return divActionFocusElement(value);
    }
    if (value is DivActionSetVariable && divActionSetVariable != null) {
      return divActionSetVariable(value);
    }
    return orElse();
  }

  const DivActionTyped.divActionArrayInsertValue(
    DivActionArrayInsertValue value,
  ) : _value = value;

  const DivActionTyped.divActionArrayRemoveValue(
    DivActionArrayRemoveValue value,
  ) : _value = value;

  const DivActionTyped.divActionArraySetValue(
    DivActionArraySetValue value,
  ) : _value = value;

  const DivActionTyped.divActionClearFocus(
    DivActionClearFocus value,
  ) : _value = value;

  const DivActionTyped.divActionCopyToClipboard(
    DivActionCopyToClipboard value,
  ) : _value = value;

  const DivActionTyped.divActionDictSetValue(
    DivActionDictSetValue value,
  ) : _value = value;

  const DivActionTyped.divActionFocusElement(
    DivActionFocusElement value,
  ) : _value = value;

  const DivActionTyped.divActionSetVariable(
    DivActionSetVariable value,
  ) : _value = value;

  static DivActionTyped? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivActionArrayInsertValue.type:
        return DivActionTyped(DivActionArrayInsertValue.fromJson(json)!);
      case DivActionArrayRemoveValue.type:
        return DivActionTyped(DivActionArrayRemoveValue.fromJson(json)!);
      case DivActionArraySetValue.type:
        return DivActionTyped(DivActionArraySetValue.fromJson(json)!);
      case DivActionClearFocus.type:
        return DivActionTyped(DivActionClearFocus.fromJson(json)!);
      case DivActionCopyToClipboard.type:
        return DivActionTyped(DivActionCopyToClipboard.fromJson(json)!);
      case DivActionDictSetValue.type:
        return DivActionTyped(DivActionDictSetValue.fromJson(json)!);
      case DivActionFocusElement.type:
        return DivActionTyped(DivActionFocusElement.fromJson(json)!);
      case DivActionSetVariable.type:
        return DivActionTyped(DivActionSetVariable.fromJson(json)!);
    }
    return null;
  }
}
