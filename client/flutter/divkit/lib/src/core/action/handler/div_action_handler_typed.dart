import 'dart:async';

import 'package:divkit/divkit.dart';

class DefaultDivActionHandlerTyped implements DivActionHandler {
  final focusElementHandler = DivFocusElementHandlerTyped();
  final setVariableHandler = DivSetVariableHandlerTyped();

  @override
  bool canHandle(DivContext context, DivActionModel action) =>
      action.typedAction != null;

  @override
  FutureOr<bool> handleAction(DivContext context, DivActionModel action) {
    final typedAction = action.typedAction;
    if (typedAction != null) {
      return typedAction.maybeMap(
        divActionFocusElement: (action) => focusElementHandler.handleAction(
          context,
          action,
        ),
        divActionSetVariable: (action) => setVariableHandler.handleAction(
          context,
          action,
        ),
        orElse: () => false,
      );
    }
    return false;
  }
}

class DivFocusElementHandlerTyped {
  Future<bool> handleAction(
    DivContext context,
    DivActionFocusElement action,
  ) async {
    final elementId = action.elementId.resolve(context.variables);

    final node = context.getFocusNode(elementId);
    if (node != null) {
      node.requestFocus();
      return true;
    }
    return true;
  }
}

class DivSetVariableHandlerTyped {
  Future<bool> handleAction(
    DivContext context,
    DivActionSetVariable action,
  ) async {
    final variableContext = context.variables;

    try {
      final value = action.value.map(
        arrayValue: (arrayValue) => arrayValue.value.resolve(variableContext),
        booleanValue: (booleanValue) =>
            booleanValue.value.resolve(variableContext),
        colorValue: (colorValue) => colorValue.value.resolve(variableContext),
        dictValue: (dictValue) async => dictValue.value,
        integerValue: (integerValue) =>
            integerValue.value.resolve(variableContext),
        numberValue: (numberValue) =>
            numberValue.value.resolve(variableContext),
        stringValue: (stringValue) =>
            stringValue.value.resolve(variableContext),
        urlValue: (urlValue) => urlValue.value.resolve(variableContext),
      );
      final variableName = action.variableName.resolve(context.variables);

      context.variableManager.updateVariable(
        variableName,
        value,
      );
      return true;
    } catch (e) {
      logger.error(e);
    }
    return false;
  }
}
