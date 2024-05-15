import 'dart:async';

import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/protocol/div_state.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/foundation.dart';
import 'package:rxdart/rxdart.dart';

class DefaultDivStateManager with EquatableMixin implements DivStateManager {
  @visibleForTesting
  final states = BehaviorSubject<Map<String, String?>>();

  DefaultDivStateManager() {
    states.add({});
  }

  @override
  void registerState(String divId, [String? stateId]) {
    final result = Map.of(states.value);
    states.add(result..addAll({divId: stateId}));
  }

  @override
  void updateState(String divId, String? stateId) {
    final result = Map.of(states.value);
    if (result.containsKey(divId)) {
      states.add(result..update(divId, (value) => stateId));
    } else {
      logger.error('Not registered state for id: $divId');
    }
  }

  @override
  Stream<R> watch<R>(
    FutureOr<R> Function(Map<String, String?> values) mapper,
  ) =>
      states.asyncMap(mapper);

  @override
  void unregisterState(String divId) {
    final result = Map.of(states.value);
    states.add(result..remove(divId));
  }

  @override
  Future<void> dispose() async => await states.close();

  @override
  List<Object?> get props => [states.value];
}
