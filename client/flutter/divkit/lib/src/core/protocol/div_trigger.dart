import 'dart:async';

import 'package:divkit/divkit.dart';

abstract class DivTriggerManager {
  final DivContext divContext;
  StreamSubscription? _sub;

  DivTriggerManager({
    required this.divContext,
  }) {
    // Triggers listen for storage changes and make additional actions.
    _sub = divContext.variableManager.contextStream
        .distinct()
        .listen(handleUpdate);
  }

  /// Do actions in triggers using [context].
  Future<void> handleUpdate(DivVariableContext context);

  /// Safely destroy manager.
  Future<void> dispose() async {
    _sub?.cancel();
  }
}
