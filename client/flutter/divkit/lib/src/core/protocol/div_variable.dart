import 'dart:async';

import 'package:divkit/src/core/variable/variable.dart';

typedef DivVariableContextMapper<R> = FutureOr<R> Function(
  DivVariableContext values,
);

abstract class DivVariableManager {
  /// The current snapshot of the context.
  DivVariableContext get context;

  /// Stream of context changes.
  Stream<DivVariableContext> get contextStream;

  /// Update the values of a variables.
  void updateVariables(
    Map<String, dynamic> variables, [
    bool notify = true,
  ]);

  /// Update the value of a variable.
  void updateVariable(
    String name,
    dynamic value, [
    bool notify = true,
  ]);

  /// Put the value of a variable.
  void putVariable(
    String name,
    dynamic value, [
    bool notify = true,
  ]);

  /// Creates a state stream of a variable-dependent model.
  /// Needs to create a filter that passes through only the changes of model.
  Stream<R> watch<R>(DivVariableContextMapper<R> mapper);

  /// Subscribes to the context update.
  /// Needs for logic that observe changes.
  StreamSubscription<DivVariableContext> listen(
    DivVariableContextMapper<void> callback,
  );

  /// Safely destroy storage.
  Future<void> dispose();
}

abstract class DivVariableStorage {
  /// The parent storage. There is full access to it.
  DivVariableStorage? get inheritedStorage;

  /// A list of variable names under the control of the current storage.
  Set<String> get names;

  /// Update an existing variable.
  /// If successful, it will return true
  bool update(DivVariableModel variable);

  /// Update if it exists and will create otherwise.
  void put(DivVariableModel variable);

  /// The current snapshot of the raw representation of the storage.
  Map<String, DivVariableModel> get value;

  /// The raw data stream of storage changes.
  Stream<Map<String, DivVariableModel>> get stream;

  /// Safely destroy storage.
  Future<void> dispose();
}
