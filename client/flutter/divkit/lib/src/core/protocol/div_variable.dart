import 'dart:async';

import 'package:divkit/src/core/variable/variable.dart';
import 'package:equatable/equatable.dart';

typedef DivVariableContextMapper<R> = FutureOr<R> Function(
  DivVariableContext values,
);

class DivVariableContext with EquatableMixin {
  final Map<String, dynamic>? last;
  final Map<String, dynamic> current;

  DivVariableContext({
    required this.current,
    this.last,
  });

  DivVariableContext.empty()
      : current = const {},
        last = null;

  /// Caching the last calculated update.
  Set<String>? _update;

  /// Find difference between current and last. It's updated variables names.
  Set<String> get update {
    if (_update != null) {
      return _update!;
    }

    // Create an empty set to store the names of updated variables.
    _update = {};

    // Iterate over the current map to find new or updated keys.
    current.forEach((key, value) {
      // Check if the key is not present in the last map, or its value has changed.
      if (last == null || last![key] != value) {
        // If so, add the key to the set of updated variables.
        _update!.add(key);
      }
    });

    // Check if any keys have been deleted.
    if (last != null) {
      for (var key in last!.keys) {
        if (!current.containsKey(key)) {
          _update!.add(key);
        }
      }
    }

    return _update!;
  }

  @override
  List<Object?> get props => [
        last,
        current,
      ];
}

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
  bool update(DivVariable variable);

  /// Update if it exists and will create otherwise.
  void put(DivVariable variable);

  /// The current snapshot of the raw representation of the storage.
  Map<String, DivVariable> get value;

  /// The raw data stream of storage changes.
  Stream<Map<String, DivVariable>> get stream;

  /// Safely destroy storage.
  Future<void> dispose();
}
