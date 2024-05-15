import 'dart:async';

import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/protocol/div_context.dart';

/// Handles any div-action
abstract class DivActionHandler {
  const DivActionHandler();

  /// Returns TRUE if action can be handled.
  /// [action] — Action to handle. Maybe typed or contain a non-null url
  /// [context] — DivContext to use variables and access stateManager
  bool canHandle(DivContext context, DivAction action);

  /// Returns TRUE if action was handled.
  /// [action] — Action to handle. Maybe typed or contain a non-null url
  /// [context] — DivContext to use variables and access stateManager
  FutureOr<bool> handleAction(DivContext context, DivAction action);
}
