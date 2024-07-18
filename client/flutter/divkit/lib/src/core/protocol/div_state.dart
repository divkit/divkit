import 'dart:async';

abstract class DivStateManager {
  const DivStateManager();

  /// Adds a state to the manager. Then it is available for updating.
  void registerState(String divId, [String? stateId]);

  /// Change state of div with [divId] to [stateId].
  void updateState(String divId, String? stateId);

  /// Creates a state stream of a state-dependent model.
  /// Needs to create a filter that passes through only the changes of model.
  Stream<R> watch<R>(FutureOr<R> Function(Map<String, String?> values) mapper);

  /// Removes a state from the manager. Then it is not available for updating.
  void unregisterState(String divId);

  /// Safely destroy manager.
  void dispose();
}
