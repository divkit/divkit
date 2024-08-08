// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_array_insert_value.dart';
import 'package:divkit/src/schema/div_action_array_remove_value.dart';
import 'package:divkit/src/schema/div_action_array_set_value.dart';
import 'package:divkit/src/schema/div_action_clear_focus.dart';
import 'package:divkit/src/schema/div_action_copy_to_clipboard.dart';
import 'package:divkit/src/schema/div_action_dict_set_value.dart';
import 'package:divkit/src/schema/div_action_focus_element.dart';
import 'package:divkit/src/schema/div_action_set_variable.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivActionTyped extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

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
    switch (_index) {
      case 0:
        return divActionArrayInsertValue(
          value as DivActionArrayInsertValue,
        );
      case 1:
        return divActionArrayRemoveValue(
          value as DivActionArrayRemoveValue,
        );
      case 2:
        return divActionArraySetValue(
          value as DivActionArraySetValue,
        );
      case 3:
        return divActionClearFocus(
          value as DivActionClearFocus,
        );
      case 4:
        return divActionCopyToClipboard(
          value as DivActionCopyToClipboard,
        );
      case 5:
        return divActionDictSetValue(
          value as DivActionDictSetValue,
        );
      case 6:
        return divActionFocusElement(
          value as DivActionFocusElement,
        );
      case 7:
        return divActionSetVariable(
          value as DivActionSetVariable,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivActionTyped",
    );
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
    switch (_index) {
      case 0:
        if (divActionArrayInsertValue != null) {
          return divActionArrayInsertValue(
            value as DivActionArrayInsertValue,
          );
        }
        break;
      case 1:
        if (divActionArrayRemoveValue != null) {
          return divActionArrayRemoveValue(
            value as DivActionArrayRemoveValue,
          );
        }
        break;
      case 2:
        if (divActionArraySetValue != null) {
          return divActionArraySetValue(
            value as DivActionArraySetValue,
          );
        }
        break;
      case 3:
        if (divActionClearFocus != null) {
          return divActionClearFocus(
            value as DivActionClearFocus,
          );
        }
        break;
      case 4:
        if (divActionCopyToClipboard != null) {
          return divActionCopyToClipboard(
            value as DivActionCopyToClipboard,
          );
        }
        break;
      case 5:
        if (divActionDictSetValue != null) {
          return divActionDictSetValue(
            value as DivActionDictSetValue,
          );
        }
        break;
      case 6:
        if (divActionFocusElement != null) {
          return divActionFocusElement(
            value as DivActionFocusElement,
          );
        }
        break;
      case 7:
        if (divActionSetVariable != null) {
          return divActionSetVariable(
            value as DivActionSetVariable,
          );
        }
        break;
    }
    return orElse();
  }

  const DivActionTyped.divActionArrayInsertValue(
    DivActionArrayInsertValue obj,
  )   : value = obj,
        _index = 0;

  const DivActionTyped.divActionArrayRemoveValue(
    DivActionArrayRemoveValue obj,
  )   : value = obj,
        _index = 1;

  const DivActionTyped.divActionArraySetValue(
    DivActionArraySetValue obj,
  )   : value = obj,
        _index = 2;

  const DivActionTyped.divActionClearFocus(
    DivActionClearFocus obj,
  )   : value = obj,
        _index = 3;

  const DivActionTyped.divActionCopyToClipboard(
    DivActionCopyToClipboard obj,
  )   : value = obj,
        _index = 4;

  const DivActionTyped.divActionDictSetValue(
    DivActionDictSetValue obj,
  )   : value = obj,
        _index = 5;

  const DivActionTyped.divActionFocusElement(
    DivActionFocusElement obj,
  )   : value = obj,
        _index = 6;

  const DivActionTyped.divActionSetVariable(
    DivActionSetVariable obj,
  )   : value = obj,
        _index = 7;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivActionTyped? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivActionArrayInsertValue.type:
          return DivActionTyped.divActionArrayInsertValue(
            DivActionArrayInsertValue.fromJson(json)!,
          );
        case DivActionArrayRemoveValue.type:
          return DivActionTyped.divActionArrayRemoveValue(
            DivActionArrayRemoveValue.fromJson(json)!,
          );
        case DivActionArraySetValue.type:
          return DivActionTyped.divActionArraySetValue(
            DivActionArraySetValue.fromJson(json)!,
          );
        case DivActionClearFocus.type:
          return DivActionTyped.divActionClearFocus(
            DivActionClearFocus.fromJson(json)!,
          );
        case DivActionCopyToClipboard.type:
          return DivActionTyped.divActionCopyToClipboard(
            DivActionCopyToClipboard.fromJson(json)!,
          );
        case DivActionDictSetValue.type:
          return DivActionTyped.divActionDictSetValue(
            DivActionDictSetValue.fromJson(json)!,
          );
        case DivActionFocusElement.type:
          return DivActionTyped.divActionFocusElement(
            DivActionFocusElement.fromJson(json)!,
          );
        case DivActionSetVariable.type:
          return DivActionTyped.divActionSetVariable(
            DivActionSetVariable.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionTyped?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivActionArrayInsertValue.type:
          return DivActionTyped.divActionArrayInsertValue(
            (await DivActionArrayInsertValue.parse(json))!,
          );
        case DivActionArrayRemoveValue.type:
          return DivActionTyped.divActionArrayRemoveValue(
            (await DivActionArrayRemoveValue.parse(json))!,
          );
        case DivActionArraySetValue.type:
          return DivActionTyped.divActionArraySetValue(
            (await DivActionArraySetValue.parse(json))!,
          );
        case DivActionClearFocus.type:
          return DivActionTyped.divActionClearFocus(
            (await DivActionClearFocus.parse(json))!,
          );
        case DivActionCopyToClipboard.type:
          return DivActionTyped.divActionCopyToClipboard(
            (await DivActionCopyToClipboard.parse(json))!,
          );
        case DivActionDictSetValue.type:
          return DivActionTyped.divActionDictSetValue(
            (await DivActionDictSetValue.parse(json))!,
          );
        case DivActionFocusElement.type:
          return DivActionTyped.divActionFocusElement(
            (await DivActionFocusElement.parse(json))!,
          );
        case DivActionSetVariable.type:
          return DivActionTyped.divActionSetVariable(
            (await DivActionSetVariable.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
