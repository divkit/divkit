import 'dart:ui';

import 'package:divkit/src/core/action/handler/default_div_action_handler.dart';
import 'package:divkit/src/core/protocol/div_action.dart';
import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_custom.dart';
import 'package:divkit/src/core/protocol/div_data.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/protocol/div_trigger.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/core/state/div_state_manager.dart';
import 'package:divkit/src/core/timer/timer_converter.dart';
import 'package:divkit/src/core/timer/timer_manager.dart';
import 'package:divkit/src/core/trigger/trigger_converter.dart';
import 'package:divkit/src/core/trigger/trigger_manager.dart';
import 'package:divkit/src/core/variable/variable_converter.dart';
import 'package:divkit/src/core/variable/variable_storage.dart';
import 'package:divkit/src/core/variable/variable_storage_manager.dart';
import 'package:divkit/src/core/widgets/card_state/div_card_state_widget.dart';
import 'package:divkit/src/core/widgets/div_error_widget.dart';
import 'package:divkit/src/generated_sources/div_data.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:meta/meta.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';

/// The main widget embedding DivKit BDUI in the Flutter host application.
class DivKitView extends StatelessWidget {
  /// The data of layout that will be rendered.
  /// The process of building a DTO is quite expensive, so it is better
  /// to create it outside the widget in order to avoid frame loss.
  final DivKitData data;

  /// External storage. Used to transfer the context from the host environment.
  /// The variables will merge with those obtained at the parsing stage.
  /// Name collisions are resolved by shading global variables.
  final DivVariableStorage? variableStorage;

  /// Handler fot div-action.
  final DivActionHandler? actionHandler;

  /// Handler for div-custom.
  final DivCustomHandler? customHandler;

  /// LTR or RTL mode. If null used auto.
  final TextDirection? textDirection;

  /// [NOT_USED_FOR_SVG] Cache manager to override DefaultCacheManager if needed in DivImage.
  @experimental
  final BaseCacheManager? cacheManager;

  /// [NOT_SUPPORTED_YET] Light or dark mode. If null used auto.
  @experimental
  final Brightness? brightness;

  /// This flag allows you to highlight unsupported divs.
  @experimental
  final bool showUnsupportedDivs;

  /// This callback is triggered after the first frame,
  /// when the [DivContext] has been fully initialized.
  @experimental
  final void Function(DivContext)? onInit;

  /// Can be used for scaling ui
  @experimental
  final double viewScale;

  /// Can be used for scaling text
  @experimental
  final double textScale;

  /// Use DivKitView inside your widget tree with layout passed by param "data":
  /// ```dart
  ///     DivKitView(
  ///       data: data,
  ///     )
  /// ```
  /// Please ensure that there is Directionality widget in the tree.
  ///
  /// Optionally, you can pass customs handler, actions handler and other params
  /// to configure DivKitView behavior:
  ///
  /// ```dart
  ///     DivKitView(
  ///       data: data,
  ///       customHandler: MyCustomHandler(),
  ///       actionHandler: MyCustomActionHandler(),
  ///       variableStorage: MyOwnVariableStorage(),
  ///     )
  /// ```
  /// Important! If you wish to work with default div-actions and use your own
  /// actionHandler don't forget to inherit [DefaultDivActionHandler].
  const DivKitView({
    required this.data,
    this.variableStorage,
    this.actionHandler,
    this.customHandler,
    this.cacheManager,
    this.brightness,
    this.textDirection,
    this.showUnsupportedDivs = false,
    this.onInit,
    this.viewScale = 1,
    this.textScale = 1,
    super.key,
  });

  @override
  Widget build(BuildContext context) => Directionality(
        textDirection: textDirection ?? Directionality.of(context),
        child: DivKitProvider<DivScalingModel>(
          value: DivScalingModel(
            textScale: textScale,
            viewScale: viewScale,
          ),
          child: DivKitProvider<ShowUnsupportedDivs>(
            value: ShowUnsupportedDivs(showUnsupportedDivs),
            child: FocusScope(
              child: _DivKitView(
                data: data,
                variableStorage: variableStorage,
                actionHandler: actionHandler,
                customHandler: customHandler,
                cacheManager: cacheManager,
                onInit: onInit,
              ),
            ),
          ),
        ),
      );
}

class _DivKitView extends StatefulWidget {
  final DivKitData data;
  final DivVariableStorage? variableStorage;
  final DivActionHandler? actionHandler;
  final DivCustomHandler? customHandler;
  final BaseCacheManager? cacheManager;
  final void Function(DivContext)? onInit;

  const _DivKitView({
    required this.data,
    required this.variableStorage,
    required this.actionHandler,
    required this.customHandler,
    required this.cacheManager,
    required this.onInit,
  });

  @override
  State<_DivKitView> createState() => _DivKitViewState();
}

class _DivKitViewState extends State<_DivKitView> {
  DivData? source;
  late DivLoggerContext loggerContext;
  late DivRootContext divRootContext;
  late DivTriggerManager triggerManager;
  late DivCustomHandler customHandler;

  DivContext get divContext => divRootContext;

  DivData? init(DivKitData data) {
    final source = data.source;

    loggerContext = DefaultDivLoggerContext(source?.logId);

    if (source != null) {
      loggerUse(loggerContext).debug('Init DivKitView $hashCode');
      divRootContext = DivRootContext(buildContext: context)
        ..variableManager = DefaultDivVariableManager(
          storage: DefaultDivVariableStorage(
            inheritedStorage: widget.variableStorage,
            variables: source.variables?.map((v) => v.pass).toList(),
          ),
        )
        ..stateManager = DefaultDivStateManager()
        ..actionHandler = widget.actionHandler ?? DefaultDivActionHandler();

      final timerManager = DefaultDivTimerManager(divContext: divContext);

      // Async feature initialization.
      timerManager.init(
        timers: source.timers?.map((t) => t.pass).toList(),
        onEnd: () =>
            loggerUse(loggerContext).debug('Timers initialized $hashCode'),
      );

      divRootContext.timerManager = timerManager;

      triggerManager = DefaultDivTriggerManager(
        divContext: divContext,
        triggers: source.variableTriggers?.map((t) => t.pass).toList(),
      );

      customHandler = widget.customHandler ?? DivCustomHandler.none();
    }

    return source;
  }

  @override
  void initState() {
    super.initState();
    source = init(widget.data);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      loggerUse(loggerContext).debug("First frame rendered $hashCode");
      widget.onInit?.call(divContext);
    });
  }

  @override
  void didUpdateWidget(covariant _DivKitView oldWidget) {
    super.didUpdateWidget(oldWidget);

    final dataUpdated = widget.data != oldWidget.data;
    final storageUpdated = widget.variableStorage != oldWidget.variableStorage;

    // ToDo: Optimize parameters updates
    // Full view recreation now
    if (dataUpdated || storageUpdated) {
      loggerUse(loggerContext).debug(
        "Update DivKitView $hashCode [dataUpdated:$dataUpdated storageUpdated:$storageUpdated]",
      );
      source = init(widget.data);
    }

    if (widget.actionHandler != oldWidget.actionHandler) {
      divRootContext.actionHandler =
          widget.actionHandler ?? DefaultDivActionHandler();
    }
  }

  @override
  Widget build(BuildContext context) => DivKitProvider(
        value: divContext,
        child: DivKitProvider(
          value: widget.cacheManager,
          child: DivKitProvider(
            value: customHandler,
            child: DivCardStateWidget(source!),
          ),
        ),
      );

  @override
  void dispose() {
    divContext.dispose();
    triggerManager.dispose();

    loggerUse(loggerContext).debug("Dispose DivKitView $hashCode");
    super.dispose();
  }
}
