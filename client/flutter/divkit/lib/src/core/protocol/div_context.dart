import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/protocol/div_data_provider.dart';
import 'package:divkit/src/core/protocol/div_patch.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:flutter/widgets.dart';

abstract class DivContext {
  const DivContext();

  DivVariableManager get variableManager;

  DivStateManager get stateManager;

  DivActionHandler get actionHandler;

  DivVisibilityActionManager get visibilityActionManager;

  DivTimerManager get timerManager;

  DivPatchManager get patchManager;

  FocusNode? getFocusNode(String divId);
}

class DivRootContext extends DivContext {
  BuildContext? buildContext;

  DivDataProvider? dataProvider;

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

  DivPatchManager? _patchManager;

  @override
  DivPatchManager get patchManager => _patchManager!;

  set patchManager(DivPatchManager value) => _patchManager = value;

  DivVisibilityActionManager? _visibilityActionManager;

  @override
  DivVisibilityActionManager get visibilityActionManager =>
      _visibilityActionManager!;

  set visibilityActionManager(DivVisibilityActionManager value) =>
      _visibilityActionManager = value;

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

  Future<void> dispose() async {
    _stateManager?.dispose();
    _visibilityActionManager?.dispose();
    await _timerManager?.dispose();
    await _variableManager?.dispose();
    await dataProvider?.dispose();

    buildContext = null;
    _stateManager = null;
    _timerManager = null;
    _variableManager = null;
    _actionHandler = null;
    _patchManager = null;
    _visibilityActionManager = null;

    // Clear the expression resolver.
    await exprResolver.clearVariables();
  }
}
