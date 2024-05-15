import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:flutter/widgets.dart';

abstract class DivContext {
  const DivContext();

  DivVariableManager get variableManager;

  DivStateManager get stateManager;

  DivActionHandler get actionHandler;

  DivTimerManager get timerManager;

  FocusNode? getFocusNode(String divId);

  Future<void> dispose();
}

class DivRootContext extends DivContext {
  BuildContext? buildContext;

  DivVariableManager? _variableManager;

  @override
  DivVariableManager get variableManager => _variableManager!;

  set variableManager(DivVariableManager value) => _variableManager = value;

  DivStateManager? _stateManager;

  @override
  DivStateManager get stateManager => _stateManager!;

  set stateManager(DivStateManager value) => _stateManager = value;

  DivActionHandler? _actionHandler;

  @override
  DivActionHandler get actionHandler => _actionHandler!;

  set actionHandler(DivActionHandler value) => _actionHandler = value;

  DivTimerManager? _timerManager;

  @override
  DivTimerManager get timerManager => _timerManager!;

  set timerManager(DivTimerManager value) => _timerManager = value;

  DivRootContext({
    this.buildContext,
  });

  @override
  FocusNode? getFocusNode(String divId) {
    if (buildContext?.mounted ?? false) {
      return FocusScope.of(buildContext!).getById(divId);
    }
    return null;
  }

  @override
  Future<void> dispose() async {
    _stateManager?.dispose();
    await _timerManager?.dispose();
    await _variableManager?.dispose();

    buildContext = null;
    _stateManager = null;
    _timerManager = null;
    _variableManager = null;
    _actionHandler = null;

    // Clear the expression resolver.
    await exprResolver.clearVariables();
  }
}
