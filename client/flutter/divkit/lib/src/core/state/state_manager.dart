import 'dart:async';

import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/protocol/div_state.dart';
import 'package:equatable/equatable.dart';
import 'package:rxdart/rxdart.dart';

class DefaultDivStateManager with EquatableMixin implements DivStateManager {
  final _controller = BehaviorSubject<Map<String, String?>>();

  @override
  Map<String, String?> get states => _controller.value;

  @override
  late Stream<Map<String, String?>> statesStream;

  DefaultDivStateManager() {
    _controller.add({});
    statesStream = _controller.stream.shareValue();
  }

  @override
  void registerState(String divId, [String? stateId]) {
    final result = Map.of(states);
    _controller.add(result..addAll({divId: stateId}));
  }

  @override
  void updateState(String divId, String? stateId) {
    final result = Map.of(states);
    if (result.containsKey(divId)) {
      _controller.add(result..update(divId, (value) => stateId));
    } else {
      logger.error('Not registered state for id: $divId');
    }
  }

  @override
  Stream<R> watch<R>(
    FutureOr<R> Function(Map<String, String?> values) mapper,
  ) =>
      _controller.asyncMap(mapper).shareValue();

  @override
  void unregisterState(String divId) {
    final result = Map.of(_controller.value);
    _controller.add(result..remove(divId));
  }

  @override
  Future<void> dispose() => _controller.close();

  @override
  List<Object?> get props => [states];
}
