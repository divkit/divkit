import 'dart:ui';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/root/div_root_widget.dart';
import 'package:divkit/src/utils/configuration.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:divkit/src/utils/trace.dart';
import 'package:flutter/widgets.dart';
import 'package:meta/meta.dart';

/// The main widget embedding DivKit BDUI in the Flutter host application.
class DivKitView extends StatelessWidget {
  /// The data of layout that will be rendered.
  /// The process of building a DTO is quite expensive, so it is better
  /// to create it outside the widget in order to avoid frame loss.
  final DivKitData data;

  /// Since initialization occurs asynchronously,
  /// you need to draw something during loading.
  @experimental
  final WidgetBuilder? loadingBuilder;

  /// External storage. Used to transfer the context from the host environment.
  /// The variables will merge with those obtained at the parsing stage.
  /// Name collisions are resolved by shading global variables.
  final DivVariableStorage? variableStorage;

  /// Handler fot div-action.
  final DivActionHandler? actionHandler;

  /// Handler for div-custom.
  final DivCustomHandler? customHandler;

  /// Provider of text font resource.
  final DivFontProvider? fontProvider;

  /// LTR or RTL mode. If null used auto.
  final TextDirection? textDirection;

  /// [NOT_SUPPORTED_YET] Light or dark mode. If null used auto.
  @experimental
  final Brightness? brightness;

  /// This flag allows you to highlight unsupported divs.
  @experimental
  final bool showUnsupportedDivs;

  /// Can be used for scaling ui.
  @experimental
  final double viewScale;

  /// Can be used for scaling text.
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
    this.loadingBuilder,
    this.variableStorage,
    this.actionHandler,
    this.customHandler,
    this.fontProvider,
    this.brightness,
    this.textDirection,
    this.showUnsupportedDivs = false,
    this.viewScale = 1,
    this.textScale = 1,
    super.key,
  });

  @override
  Widget build(BuildContext context) => Directionality(
        textDirection: textDirection ?? Directionality.of(context),
        child: provide(
          DivScalingModel(
            textScale: textScale,
            viewScale: viewScale,
          ),
          child: provide(
            DivConfiguration(
              showUnsupportedDivs: showUnsupportedDivs,
            ),
            child: provide(
              fontProvider ?? DivFontProvider.direct(),
              child: FocusScope(
                child: _DivKitView(
                  data: data,
                  loadingBuilder: loadingBuilder,
                  variableStorage: variableStorage,
                  actionHandler: actionHandler,
                  customHandler: customHandler,
                ),
              ),
            ),
          ),
        ),
      );
}

class _DivKitView extends StatefulWidget {
  final DivKitData data;
  final WidgetBuilder? loadingBuilder;
  final DivVariableStorage? variableStorage;
  final DivActionHandler? actionHandler;
  final DivCustomHandler? customHandler;

  const _DivKitView({
    required this.data,
    required this.loadingBuilder,
    required this.variableStorage,
    required this.actionHandler,
    required this.customHandler,
  });

  @override
  State<_DivKitView> createState() => _DivKitViewState();
}

class _DivKitViewState extends State<_DivKitView> {
  DivRootContext? divRootContext;

  DivContext get divContext => divRootContext!;

  void updateContext() {
    if (widget.data.preloaded) {
      divRootContext = DivRootContext.initSync(
        context: context,
        data: widget.data,
        variableStorage: widget.variableStorage,
        actionHandler: widget.actionHandler,
        customHandler: widget.customHandler,
      );
    } else {
      DivRootContext.init(
        context: context,
        data: widget.data,
        variableStorage: widget.variableStorage,
        actionHandler: widget.actionHandler,
        customHandler: widget.customHandler,
      ).then(
        (value) => setState(() => divRootContext = value),
      );
    }
  }

  @override
  void initState() {
    super.initState();
    traceEvent('Init DivKitView');
    updateContext();
  }

  @override
  void didUpdateWidget(covariant _DivKitView oldWidget) {
    super.didUpdateWidget(oldWidget);

    final dataUpdated = widget.data != oldWidget.data;
    final storageUpdated = widget.variableStorage != oldWidget.variableStorage;

    if (dataUpdated || storageUpdated) {
      updateContext();
    } else {
      if (widget.actionHandler != oldWidget.actionHandler) {
        divRootContext?.actionHandler =
            widget.actionHandler ?? DefaultDivActionHandler();
      }

      if (widget.customHandler != oldWidget.customHandler) {
        divRootContext?.customHandler =
            widget.customHandler ?? DivCustomHandler.none();
      }
    }
  }

  Widget get loader =>
      widget.loadingBuilder?.call(context) ?? const SizedBox.shrink();

  @override
  Widget build(BuildContext context) => divRootContext != null
      ? DivKitProvider(
          value: divContext,
          child: DivRootWidget(divRootContext!),
        )
      : loader;

  @override
  void dispose() {
    traceEvent('Dispose DivKitView');
    divRootContext?.dispose();
    divRootContext = null;
    super.dispose();
  }
}
