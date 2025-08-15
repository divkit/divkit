import 'dart:ui';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/root/div_root_widget.dart';
import 'package:divkit/src/utils/configuration.dart';
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

  /// Options for text and UI scaling
  final DivScale? scale;

  /// [NOT_SUPPORTED_YET] Light or dark mode. If null used auto.
  @experimental
  final Brightness? brightness;

  /// This flag allows you to highlight unsupported divs.
  @experimental
  final bool showUnsupportedDivs;

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
    this.fontProvider,
    this.brightness,
    this.textDirection,
    this.scale,
    this.showUnsupportedDivs = false,
    super.key,
  });

  @override
  Widget build(BuildContext context) => Directionality(
        textDirection: textDirection ?? Directionality.of(context),
        child: provide(
          DivConfiguration(
            showUnsupportedDivs: showUnsupportedDivs,
          ),
          child: FocusScope(
            child: _DivKitView(
              data: data,
              scale: scale,
              fontProvider: fontProvider,
              variableStorage: variableStorage,
              actionHandler: actionHandler,
              customHandler: customHandler,
            ),
          ),
        ),
      );
}

class _DivKitView extends StatefulWidget {
  final DivKitData data;
  final DivScale? scale;
  final DivFontProvider? fontProvider;
  final DivVariableStorage? variableStorage;
  final DivActionHandler? actionHandler;
  final DivCustomHandler? customHandler;

  const _DivKitView({
    required this.data,
    required this.scale,
    required this.fontProvider,
    required this.variableStorage,
    required this.actionHandler,
    required this.customHandler,
  });

  @override
  State<_DivKitView> createState() => _DivKitViewState();
}

class _DivKitViewState extends State<_DivKitView> {
  DivRootContext? divRootContext;

  @override
  void initState() {
    super.initState();
    traceEvent('Initialize DivKitView');
    initialize();
  }

  /// Updates the root context using widget data
  void initialize() {
    divRootContext = DivRootContext.initialize(
      context: context,
      data: widget.data,
      scale: widget.scale,
      fontProvider: widget.fontProvider,
      variableStorage: widget.variableStorage,
      actionHandler: widget.actionHandler,
      customHandler: widget.customHandler,
    );
  }

  @override
  void didUpdateWidget(covariant _DivKitView oldWidget) {
    super.didUpdateWidget(oldWidget);

    final dataUpdated = widget.data != oldWidget.data;
    final storageUpdated = widget.variableStorage != oldWidget.variableStorage;

    // Needs full recreation
    if (dataUpdated || storageUpdated) {
      traceEvent('Updated DivKitView');
      initialize();
    } else {
      // Allow reuse
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

  @override
  Widget build(BuildContext context) => divRootContext != null
      ? DivKitProvider(
          value: divRootContext as DivContext,
          child: DivRootWidget(divRootContext!),
        )

      /// Don't show anything if an error has occurred
      : const SizedBox.shrink();

  @override
  void dispose() {
    traceEvent('Dispose DivKitView');
    divRootContext?.dispose();
    divRootContext = null;
    super.dispose();
  }
}
