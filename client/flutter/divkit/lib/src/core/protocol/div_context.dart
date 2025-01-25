import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/data/data_provider.dart';
import 'package:divkit/src/core/patch/patch_manager.dart';
import 'package:divkit/src/core/protocol/div_data_provider.dart';
import 'package:divkit/src/core/protocol/div_trigger.dart';
import 'package:divkit/src/core/timer/timer_manager.dart';
import 'package:divkit/src/core/trigger/trigger_manager.dart';
import 'package:divkit/src/core/variable/variable_manager.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:flutter/widgets.dart';

/// Print logs with information about DivKitView lifecycle.
bool debugPrintDivKitViewLifecycle = false;

void _log(DivLoggerContext? loggerContext, String message) {
  if (debugPrintDivKitViewLifecycle) {
    loggerUse(loggerContext).debug(message);
  }
}

abstract class DivContext {
  const DivContext();

  /// Allows to get the context at the DivKitView level.
  BuildContext get buildContext;

  /// Allows to manage variables and reactively rebuild the interface.
  DivVariableManager get variableManager;

  /// Allows to direct access to variables.
  DivVariableContext get variables;

  /// Controls the div-state component and allows you to switch states.
  DivStateManager get stateManager;

  /// Allows to handle actions and events.
  DivActionHandler get actionHandler;

  /// Allows to handle the appearance of components.
  DivVisibilityActionManager get visibilityActionManager;

  /// Allows to embed new components.
  DivCustomHandler get customHandler;

  /// Allows to manage timers and generate deferred and recurring events.
  DivTimerManager get timerManager;

  /// Allows to upload partial layout changes over the network.
  DivPatchManager get patchManager;

  ///  Allows to change visual scale.
  DivScale get scale;

  ///  Allows to get custom text font.
  DivFontProvider get fontProvider;

  /// Adds meta information to the logger.
  DivLoggerContext get loggerContext;

  /// Allows to get focusNode by id.
  FocusNode? getFocusNode(String divId);
}

class DivRootContext extends DivContext {
  final BuildContext? _buildContext;

  @override
  BuildContext get buildContext => _buildContext!;

  DivDataProvider? dataProvider;

  DivVariableManager? _variableManager;

  @override
  DivVariableManager get variableManager => _variableManager!;

  set variableManager(DivVariableManager value) => _variableManager = value;

  @override
  DivVariableContext get variables => _variableManager!.context;

  DivStateManager? _stateManager;

  @override
  DivStateManager get stateManager => _stateManager!;

  set stateManager(DivStateManager value) => _stateManager = value;

  DivActionHandler? _actionHandler;

  @override
  DivActionHandler get actionHandler => _actionHandler!;

  set actionHandler(DivActionHandler value) => _actionHandler = value;

  DivCustomHandler? _customHandler;

  @override
  DivCustomHandler get customHandler => _customHandler!;

  set customHandler(DivCustomHandler value) => _customHandler = value;

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

  DivLoggerContext? _loggerContext;

  @override
  DivLoggerContext get loggerContext => _loggerContext!;

  set loggerContext(DivLoggerContext value) => _loggerContext = value;

  DivTriggerManager? triggerManager;

  DivScale? _scale;

  @override
  DivScale get scale => _scale!;

  set scale(DivScale value) => _scale = value;

  DivFontProvider? _fontProvider;

  @override
  DivFontProvider get fontProvider => _fontProvider!;

  set fontProvider(DivFontProvider value) => _fontProvider = value;

  DivRootContext([
    BuildContext? buildContext,
  ]) : _buildContext = buildContext;

  static DivRootContext? initialize({
    required BuildContext context,
    required DivKitData data,
    DivScale? scale,
    DivFontProvider? fontProvider,
    DivVariableStorage? variableStorage,
    DivActionHandler? actionHandler,
    DivCustomHandler? customHandler,
  }) {
    if (!data.hasSource) {
      data.build();
    }

    final source = data.source;
    if (source != null && context.mounted) {
      final divContext = DivRootContext(context);
      final loggerContext = DefaultDivLoggerContext(source.logId);

      _log(loggerContext, 'Init #${divContext.hashCode}');
      _log(
        loggerContext,
        'Instant rendering is enabled! #${divContext.hashCode}',
      );

      // Main initialization
      divContext
        ..loggerContext = loggerContext
        ..dataProvider = DefaultDivDataProvider(source);

      // Features initialization
      divContext
        ..visibilityActionManager = DefaultDivVisibilityActionManager()
        ..patchManager = DefaultDivPatchManager(divContext)
        ..variableManager = DefaultDivVariableManager(
          storage: DefaultDivVariableStorage(
            inheritedStorage: variableStorage,
            variables: source.variables?.map((v) => v.pass).toList(),
          ),
        )
        ..stateManager = DefaultDivStateManager()
        ..actionHandler = actionHandler ?? DefaultDivActionHandler()
        ..customHandler = customHandler ?? DivCustomHandler.none()
        ..timerManager = DefaultDivTimerManager(divContext: divContext).init(
          timers: source.timers?.map((t) => t.pass).toList(growable: false),
        )
        ..triggerManager = DefaultDivTriggerManager(
          divContext: divContext,
          triggers: source.variableTriggers
              ?.map((t) => t.pass)
              .toList(growable: false),
        )
        ..scale = const DivScale()
        ..fontProvider = const DefaultDivFontProvider();

      _log(loggerContext, 'Prepared #${divContext.hashCode}');
      return divContext;
    }

    logger.error('DivKitView has no valid data to display!');
    return null;
  }

  @override
  FocusNode? getFocusNode(String divId) {
    if (buildContext.mounted) {
      return FocusScope.of(buildContext).getById(divId);
    }
    return null;
  }

  Future<void> dispose() async {
    _log(_loggerContext, "Dispose #$hashCode");

    _stateManager?.dispose();
    _visibilityActionManager?.dispose();
    await _timerManager?.dispose();
    await _variableManager?.dispose();
    await dataProvider?.dispose();
    await triggerManager?.dispose();

    dataProvider = null;
    _loggerContext = null;
    _stateManager = null;
    _timerManager = null;
    _variableManager = null;
    _actionHandler = null;
    _patchManager = null;
    _visibilityActionManager = null;
    triggerManager = null;
  }
}
