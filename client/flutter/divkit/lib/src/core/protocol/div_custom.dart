import 'package:divkit/src/generated_sources/div_custom.dart';
import 'package:flutter/material.dart';

/// Handles specific div-custom. Creates Flutter Widget from custom model
abstract class DivCustomHandler {
  /// Returns TRUE if custom widget can be handled.
  /// [type] — DivCustom.customType, custom alias to handle.
  bool isCustomTypeSupported(String type);

  /// Returns Widget to use for div-custom.
  /// [div] — div-custom model.
  Widget createCustom(DivCustom div);

  factory DivCustomHandler.none() => _DefaultDivCustomHandler();
}

class _DefaultDivCustomHandler implements DivCustomHandler {
  @override
  Widget createCustom(DivCustom div) {
    throw UnimplementedError();
  }

  @override
  bool isCustomTypeSupported(String type) => false;
}
