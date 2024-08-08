import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';
import 'package:rxdart/rxdart.dart';

class DefaultDivVariableManager extends DivVariableManager with EquatableMixin {
  final DivVariableStorage storage;

  /// The flag is responsible for whether subscribers will be notified.
  bool _notify = true;

  @override
  late Stream<DivVariableContext> contextStream;

  @override
  DivVariableContext get context => DivVariableContext(
        current: Map.fromEntries(
          storage.value.values.map(
            (v) => v.raw,
          ),
        ),
      );

  DefaultDivVariableManager({
    required this.storage,
  }) {
    contextStream = storage.stream
        .where((_) => _notify)
        .map<Map<String, dynamic>>(
          (context) => Map.fromEntries(
            context.values.map((v) => v.raw),
          ),
        )
        .scan<DivVariableContext>(
          (previous, current, index) => DivVariableContext(
            current: current,
            last: previous.current,
          ),
          DivVariableContext.empty(),
        )
        .shareValue();
  }

  @override
  void updateVariable(
    String name,
    dynamic value, [
    bool notify = true,
  ]) {
    _notify = notify;
    final variable = storage.value[name];
    if (variable != null) {
      if (variable.value.runtimeType == value.runtimeType) {
        storage.update(variable..value = value);
      } else {
        // ToDo: Need fix that knowledge about the type of the variable has been lost
        final parsedValue = variable.safeParse?.call(value);
        if (parsedValue != null) {
          storage.update(variable..value = parsedValue);
        } else {
          logger.error('Illegal variable type change!');
        }
      }
    } else {
      logger.error('Illegal variable creation!');
    }

    _notify = true;
  }

  @override
  void updateVariables(
    Map<String, dynamic> variables, [
    bool notify = true,
  ]) {
    final names = variables.keys.toList(growable: false);

    _notify = false;
    for (int i = 0; i < names.length; ++i) {
      final name = names[i];
      final value = variables[name];

      // Last update update the combined stream.
      if (i == names.length - 1) {
        _notify = notify;
      }

      updateVariable(name, value);
    }

    _notify = true;
  }

  @override
  void putVariable(
    String name,
    dynamic value, [
    bool notify = true,
  ]) {
    _notify = notify;
    final variable = storage.value[name];
    if (variable != null) {
      if (variable.value.runtimeType == value.runtimeType) {
        storage.put(variable..value = value);
      } else {
        // ToDo: Need fix that knowledge about the type of the variable has been lost
        final parsedValue = variable.safeParse?.call(value);
        if (parsedValue != null) {
          storage.put(variable..value = parsedValue);
        } else {
          logger.error('Illegal variable type change!');
        }
      }
    } else {
      // Create new variable
      storage.put(
        DivVariableModel(
          name: name,
          value: value,
          safeParse: safeParseTyped(value.runtimeType),
        ),
      );
    }

    _notify = true;
  }

  @override
  Stream<R> watch<R>(
    FutureOr<R> Function(DivVariableContext context) mapper,
  ) =>
      contextStream.asyncMap<R>(mapper).shareValue().distinct();

  @override
  StreamSubscription<DivVariableContext> listen(
    FutureOr<void> Function(DivVariableContext context) callback,
  ) =>
      contextStream.shareValue().distinct().listen(callback);

  @override
  Future<void> dispose() async {
    await storage.dispose();
  }

  @override
  List<Object?> get props => [storage];
}
