import 'dart:async';

import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:rxdart/rxdart.dart';

abstract class DivTriggerManager {
  final DivContext divContext;
  StreamSubscription? _sub;

  DivTriggerManager({
    required this.divContext,
  }) {
    // Triggers listen for storage changes and make additional actions.
    _sub = divContext.variableManager.contextStream
        .delay(const Duration(milliseconds: 100))
        .listen(handleUpdate);
  }

  /// Do actions in triggers using [context].
  Future<void> handleUpdate(DivVariableContext context);

  /// Safely destroy manager.
  Future<void> dispose() async {
    _sub?.cancel();
  }
}
