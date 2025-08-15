import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';
import 'package:rxdart/rxdart.dart';

class DefaultDivVariableStorage extends DivVariableStorage with EquatableMixin {
  final _storage = BehaviorSubject<Map<String, DivVariableModel>>();

  /// Inherited storage of reactive variables. Not managed by local storage.
  @override
  final DivVariableStorage? inheritedStorage;

  @override
  Set<String> get names => _storage.isClosed ? {} : _storage.value.keys.toSet();

  DefaultDivVariableStorage({
    this.inheritedStorage,
    List<DivVariableModel>? variables,
  }) {
    _storage.add(Map.fromIterable(variables ?? [], key: (v) => v.name));
  }

  @override
  bool update(DivVariableModel variable) {
    final isLocal = names.contains(variable.name);
    final isInherited =
        inheritedStorage?.names.contains(variable.name) ?? false;

    if (isLocal) {
      _storage.add(
        Map.of(_storage.value)..update(variable.name, (_) => variable),
      );
    } else if (isInherited) {
      inheritedStorage?.update(variable);
    }

    return isLocal || isInherited;
  }

  @override
  void put(DivVariableModel variable) {
    if (!update(variable)) {
      _storage.add(Map.of(_storage.value)..addAll({variable.name: variable}));
    }
  }

  @override
  Map<String, DivVariableModel> get value => _storage.isClosed
      ? {}
      : {
          ...?inheritedStorage?.value,
          ..._storage.value,
        };

  /// Caching the combined contextStream.
  BehaviorSubject<Map<String, DivVariableModel>>? _contextStreamSubject;
  StreamSubscription<Map<String, DivVariableModel>>? _contextStreamSubscription;

  @override
  Stream<Map<String, DivVariableModel>> get stream {
    if (_contextStreamSubject == null) {
      final inheritedStream = inheritedStorage?.stream;
      if (inheritedStream != null) {
        _contextStreamSubject = BehaviorSubject();
        _contextStreamSubscription ??= CombineLatestStream.combine2(
          inheritedStream,
          _storage.stream,
          (
            Map<String, DivVariableModel> inherited,
            Map<String, DivVariableModel> local,
          ) =>
              {
            ...inherited,
            ...local,
          },
        ).listen((value) {
          _contextStreamSubject?.add(value);
        });
      } else {
        _contextStreamSubject = _storage;
      }
    }

    return _contextStreamSubject?.stream ?? const Stream.empty();
  }

  @override
  Future<void> dispose() => _storage.close();

  @override
  List<Object?> get props => [_storage, inheritedStorage];
}
