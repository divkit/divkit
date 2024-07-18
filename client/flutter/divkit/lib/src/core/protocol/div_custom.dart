import 'package:divkit/src/generated_sources/div_custom.dart';
import 'package:flutter/material.dart';
import 'package:divkit/src/core/protocol/div_context.dart';

/// Handles specific div-custom. Creates Flutter Widget from custom model
abstract class DivCustomHandler {
  const DivCustomHandler();

  /// Returns TRUE if custom widget can be handled.
  /// [type] — DivCustom.customType, custom alias to handle.
  /// [context] — DivContext to use variables, actions, timers and access stateManager
  bool isCustomTypeSupported(String type);

  /// Returns Widget to use for div-custom.
  /// [div] — div-custom model.
  /// [context] — DivContext to use variables, actions, timers and access stateManager
  Widget createCustom(DivCustom div, DivContext context);

  factory DivCustomHandler.none() => _DefaultDivCustomHandler();
}

class _DefaultDivCustomHandler implements DivCustomHandler {
  @override
  Widget createCustom(DivCustom div, DivContext context) {
    throw UnimplementedError();
  }

  @override
  bool isCustomTypeSupported(String type) => false;
}
