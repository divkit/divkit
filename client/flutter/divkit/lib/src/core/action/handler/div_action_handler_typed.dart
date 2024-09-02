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
    final elementId = await action.elementId.resolveValue(
      context: context.variables,
    );

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
      final value = await action.value.map(
        arrayValue: (arrayValue) async => await arrayValue.value.resolveValue(
          context: variableContext,
        ),
        booleanValue: (booleanValue) async =>
            await booleanValue.value.resolveValue(
          context: variableContext,
        ),
        colorValue: (colorValue) async => await colorValue.value.resolveValue(
          context: variableContext,
        ),
        dictValue: (dictValue) async => dictValue.value,
        integerValue: (integerValue) async =>
            await integerValue.value.resolveValue(
          context: variableContext,
        ),
        numberValue: (numberValue) async =>
            await numberValue.value.resolveValue(
          context: variableContext,
        ),
        stringValue: (stringValue) async =>
            await stringValue.value.resolveValue(
          context: variableContext,
        ),
        urlValue: (urlValue) async => await urlValue.value.resolveValue(
          context: variableContext,
        ),
      );
      final variableName = await action.variableName.resolveValue(
        context: context.variables,
      );

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
