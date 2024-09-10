// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_animator_start.dart';
import 'package:divkit/src/schema/div_action_animator_stop.dart';
import 'package:divkit/src/schema/div_action_array_insert_value.dart';
import 'package:divkit/src/schema/div_action_array_remove_value.dart';
import 'package:divkit/src/schema/div_action_array_set_value.dart';
import 'package:divkit/src/schema/div_action_clear_focus.dart';
import 'package:divkit/src/schema/div_action_copy_to_clipboard.dart';
import 'package:divkit/src/schema/div_action_dict_set_value.dart';
import 'package:divkit/src/schema/div_action_focus_element.dart';
import 'package:divkit/src/schema/div_action_hide_tooltip.dart';
import 'package:divkit/src/schema/div_action_set_state.dart';
import 'package:divkit/src/schema/div_action_set_variable.dart';
import 'package:divkit/src/schema/div_action_show_tooltip.dart';
import 'package:divkit/src/schema/div_action_timer.dart';
import 'package:divkit/src/schema/div_action_video.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivActionTyped extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivActionAnimatorStart) divActionAnimatorStart,
    required T Function(DivActionAnimatorStop) divActionAnimatorStop,
    required T Function(DivActionArrayInsertValue) divActionArrayInsertValue,
    required T Function(DivActionArrayRemoveValue) divActionArrayRemoveValue,
    required T Function(DivActionArraySetValue) divActionArraySetValue,
    required T Function(DivActionClearFocus) divActionClearFocus,
    required T Function(DivActionCopyToClipboard) divActionCopyToClipboard,
    required T Function(DivActionDictSetValue) divActionDictSetValue,
    required T Function(DivActionFocusElement) divActionFocusElement,
    required T Function(DivActionHideTooltip) divActionHideTooltip,
    required T Function(DivActionSetState) divActionSetState,
    required T Function(DivActionSetVariable) divActionSetVariable,
    required T Function(DivActionShowTooltip) divActionShowTooltip,
    required T Function(DivActionTimer) divActionTimer,
    required T Function(DivActionVideo) divActionVideo,
  }) {
    switch (_index) {
      case 0:
        return divActionAnimatorStart(
          value as DivActionAnimatorStart,
        );
      case 1:
        return divActionAnimatorStop(
          value as DivActionAnimatorStop,
        );
      case 2:
        return divActionArrayInsertValue(
          value as DivActionArrayInsertValue,
        );
      case 3:
        return divActionArrayRemoveValue(
          value as DivActionArrayRemoveValue,
        );
      case 4:
        return divActionArraySetValue(
          value as DivActionArraySetValue,
        );
      case 5:
        return divActionClearFocus(
          value as DivActionClearFocus,
        );
      case 6:
        return divActionCopyToClipboard(
          value as DivActionCopyToClipboard,
        );
      case 7:
        return divActionDictSetValue(
          value as DivActionDictSetValue,
        );
      case 8:
        return divActionFocusElement(
          value as DivActionFocusElement,
        );
      case 9:
        return divActionHideTooltip(
          value as DivActionHideTooltip,
        );
      case 10:
        return divActionSetState(
          value as DivActionSetState,
        );
      case 11:
        return divActionSetVariable(
          value as DivActionSetVariable,
        );
      case 12:
        return divActionShowTooltip(
          value as DivActionShowTooltip,
        );
      case 13:
        return divActionTimer(
          value as DivActionTimer,
        );
      case 14:
        return divActionVideo(
          value as DivActionVideo,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivActionTyped",
    );
  }

  T maybeMap<T>({
    T Function(DivActionAnimatorStart)? divActionAnimatorStart,
    T Function(DivActionAnimatorStop)? divActionAnimatorStop,
    T Function(DivActionArrayInsertValue)? divActionArrayInsertValue,
    T Function(DivActionArrayRemoveValue)? divActionArrayRemoveValue,
    T Function(DivActionArraySetValue)? divActionArraySetValue,
    T Function(DivActionClearFocus)? divActionClearFocus,
    T Function(DivActionCopyToClipboard)? divActionCopyToClipboard,
    T Function(DivActionDictSetValue)? divActionDictSetValue,
    T Function(DivActionFocusElement)? divActionFocusElement,
    T Function(DivActionHideTooltip)? divActionHideTooltip,
    T Function(DivActionSetState)? divActionSetState,
    T Function(DivActionSetVariable)? divActionSetVariable,
    T Function(DivActionShowTooltip)? divActionShowTooltip,
    T Function(DivActionTimer)? divActionTimer,
    T Function(DivActionVideo)? divActionVideo,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divActionAnimatorStart != null) {
          return divActionAnimatorStart(
            value as DivActionAnimatorStart,
          );
        }
        break;
      case 1:
        if (divActionAnimatorStop != null) {
          return divActionAnimatorStop(
            value as DivActionAnimatorStop,
          );
        }
        break;
      case 2:
        if (divActionArrayInsertValue != null) {
          return divActionArrayInsertValue(
            value as DivActionArrayInsertValue,
          );
        }
        break;
      case 3:
        if (divActionArrayRemoveValue != null) {
          return divActionArrayRemoveValue(
            value as DivActionArrayRemoveValue,
          );
        }
        break;
      case 4:
        if (divActionArraySetValue != null) {
          return divActionArraySetValue(
            value as DivActionArraySetValue,
          );
        }
        break;
      case 5:
        if (divActionClearFocus != null) {
          return divActionClearFocus(
            value as DivActionClearFocus,
          );
        }
        break;
      case 6:
        if (divActionCopyToClipboard != null) {
          return divActionCopyToClipboard(
            value as DivActionCopyToClipboard,
          );
        }
        break;
      case 7:
        if (divActionDictSetValue != null) {
          return divActionDictSetValue(
            value as DivActionDictSetValue,
          );
        }
        break;
      case 8:
        if (divActionFocusElement != null) {
          return divActionFocusElement(
            value as DivActionFocusElement,
          );
        }
        break;
      case 9:
        if (divActionHideTooltip != null) {
          return divActionHideTooltip(
            value as DivActionHideTooltip,
          );
        }
        break;
      case 10:
        if (divActionSetState != null) {
          return divActionSetState(
            value as DivActionSetState,
          );
        }
        break;
      case 11:
        if (divActionSetVariable != null) {
          return divActionSetVariable(
            value as DivActionSetVariable,
          );
        }
        break;
      case 12:
        if (divActionShowTooltip != null) {
          return divActionShowTooltip(
            value as DivActionShowTooltip,
          );
        }
        break;
      case 13:
        if (divActionTimer != null) {
          return divActionTimer(
            value as DivActionTimer,
          );
        }
        break;
      case 14:
        if (divActionVideo != null) {
          return divActionVideo(
            value as DivActionVideo,
          );
        }
        break;
    }
    return orElse();
  }

  const DivActionTyped.divActionAnimatorStart(
    DivActionAnimatorStart obj,
  )   : value = obj,
        _index = 0;

  const DivActionTyped.divActionAnimatorStop(
    DivActionAnimatorStop obj,
  )   : value = obj,
        _index = 1;

  const DivActionTyped.divActionArrayInsertValue(
    DivActionArrayInsertValue obj,
  )   : value = obj,
        _index = 2;

  const DivActionTyped.divActionArrayRemoveValue(
    DivActionArrayRemoveValue obj,
  )   : value = obj,
        _index = 3;

  const DivActionTyped.divActionArraySetValue(
    DivActionArraySetValue obj,
  )   : value = obj,
        _index = 4;

  const DivActionTyped.divActionClearFocus(
    DivActionClearFocus obj,
  )   : value = obj,
        _index = 5;

  const DivActionTyped.divActionCopyToClipboard(
    DivActionCopyToClipboard obj,
  )   : value = obj,
        _index = 6;

  const DivActionTyped.divActionDictSetValue(
    DivActionDictSetValue obj,
  )   : value = obj,
        _index = 7;

  const DivActionTyped.divActionFocusElement(
    DivActionFocusElement obj,
  )   : value = obj,
        _index = 8;

  const DivActionTyped.divActionHideTooltip(
    DivActionHideTooltip obj,
  )   : value = obj,
        _index = 9;

  const DivActionTyped.divActionSetState(
    DivActionSetState obj,
  )   : value = obj,
        _index = 10;

  const DivActionTyped.divActionSetVariable(
    DivActionSetVariable obj,
  )   : value = obj,
        _index = 11;

  const DivActionTyped.divActionShowTooltip(
    DivActionShowTooltip obj,
  )   : value = obj,
        _index = 12;

  const DivActionTyped.divActionTimer(
    DivActionTimer obj,
  )   : value = obj,
        _index = 13;

  const DivActionTyped.divActionVideo(
    DivActionVideo obj,
  )   : value = obj,
        _index = 14;

  bool get isDivActionAnimatorStart => _index == 0;

  bool get isDivActionAnimatorStop => _index == 1;

  bool get isDivActionArrayInsertValue => _index == 2;

  bool get isDivActionArrayRemoveValue => _index == 3;

  bool get isDivActionArraySetValue => _index == 4;

  bool get isDivActionClearFocus => _index == 5;

  bool get isDivActionCopyToClipboard => _index == 6;

  bool get isDivActionDictSetValue => _index == 7;

  bool get isDivActionFocusElement => _index == 8;

  bool get isDivActionHideTooltip => _index == 9;

  bool get isDivActionSetState => _index == 10;

  bool get isDivActionSetVariable => _index == 11;

  bool get isDivActionShowTooltip => _index == 12;

  bool get isDivActionTimer => _index == 13;

  bool get isDivActionVideo => _index == 14;

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
        case DivActionAnimatorStart.type:
          return DivActionTyped.divActionAnimatorStart(
            DivActionAnimatorStart.fromJson(json)!,
          );
        case DivActionAnimatorStop.type:
          return DivActionTyped.divActionAnimatorStop(
            DivActionAnimatorStop.fromJson(json)!,
          );
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
        case DivActionHideTooltip.type:
          return DivActionTyped.divActionHideTooltip(
            DivActionHideTooltip.fromJson(json)!,
          );
        case DivActionSetState.type:
          return DivActionTyped.divActionSetState(
            DivActionSetState.fromJson(json)!,
          );
        case DivActionSetVariable.type:
          return DivActionTyped.divActionSetVariable(
            DivActionSetVariable.fromJson(json)!,
          );
        case DivActionShowTooltip.type:
          return DivActionTyped.divActionShowTooltip(
            DivActionShowTooltip.fromJson(json)!,
          );
        case DivActionTimer.type:
          return DivActionTyped.divActionTimer(
            DivActionTimer.fromJson(json)!,
          );
        case DivActionVideo.type:
          return DivActionTyped.divActionVideo(
            DivActionVideo.fromJson(json)!,
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
        case DivActionAnimatorStart.type:
          return DivActionTyped.divActionAnimatorStart(
            (await DivActionAnimatorStart.parse(json))!,
          );
        case DivActionAnimatorStop.type:
          return DivActionTyped.divActionAnimatorStop(
            (await DivActionAnimatorStop.parse(json))!,
          );
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
        case DivActionHideTooltip.type:
          return DivActionTyped.divActionHideTooltip(
            (await DivActionHideTooltip.parse(json))!,
          );
        case DivActionSetState.type:
          return DivActionTyped.divActionSetState(
            (await DivActionSetState.parse(json))!,
          );
        case DivActionSetVariable.type:
          return DivActionTyped.divActionSetVariable(
            (await DivActionSetVariable.parse(json))!,
          );
        case DivActionShowTooltip.type:
          return DivActionTyped.divActionShowTooltip(
            (await DivActionShowTooltip.parse(json))!,
          );
        case DivActionTimer.type:
          return DivActionTyped.divActionTimer(
            (await DivActionTimer.parse(json))!,
          );
        case DivActionVideo.type:
          return DivActionTyped.divActionVideo(
            (await DivActionVideo.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
