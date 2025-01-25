import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/protocol/div_data_provider.dart';
import 'package:equatable/equatable.dart';
import 'package:rxdart/rxdart.dart';

class DefaultDivDataProvider with EquatableMixin implements DivDataProvider {
  final _controller = BehaviorSubject<DivData>();

  @override
  DivData get value => _controller.value;

  @override
  Stream<DivData> get stream => _controller.stream;

  DefaultDivDataProvider(DivData initial) {
    _controller.add(initial);
  }

  @override
  void update(DivData data) => _controller.add(data);

  @override
  Future<void> dispose() async => await _controller.close();

  @override
  List<Object?> get props => [_controller.value];
}
