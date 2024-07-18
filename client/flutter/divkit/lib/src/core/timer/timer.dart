import 'dart:async';

import 'package:divkit/src/core/action/action_converter.dart';
import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:divkit/src/utils/clockwork.dart';
import 'package:divkit/src/utils/duration_helper.dart';
import 'package:equatable/equatable.dart';

class DivTimerModel with EquatableMixin {
  final String id;

  final Expression<int> duration;
  final Expression<int>? tickInterval;

  final List<DivAction>? tickActions;
  final List<DivAction>? endActions;

  final String? valueVariable;

  StreamSubscription? _sub;
  Clockwork? _clockwork;

  Clockwork get clockwork => _clockwork!;

  DivTimerModel({
    required this.id,
    required this.duration,
    this.endActions,
    this.tickActions,
    this.tickInterval,
    this.valueVariable,
  });

  /// Asynchronous initialization.
  Future<void> init(DivContext divContext) async {
    final context = divContext.variableManager.context;

    final duration = await this.duration.resolveValue(context: context);
    final interval = await tickInterval?.resolveValue(context: context);

    _clockwork = Clockwork(
      duration: duration.ms,
      interval: interval?.ms,
      onStart: () {
        if (valueVariable != null) {
          divContext.variableManager.updateVariable(valueVariable!, 0);
        }
      },
      onPause: (elapsed) {
        if (valueVariable != null) {
          divContext.variableManager
              .updateVariable(valueVariable!, elapsed.inMilliseconds);
        }
      },
      onReset: () {
        if (valueVariable != null) {
          divContext.variableManager.updateVariable(valueVariable!, 0);
        }
      },
      onTick: (elapsed) async {
        final context = divContext.variableManager.context;

        if (valueVariable != null) {
          divContext.variableManager
              .updateVariable(valueVariable!, elapsed.inMilliseconds);
        }
        tickActions?.forEach(
          (a) async => (await a.resolve(context: context)).execute(divContext),
        );
      },
      onEnd: (elapsed) {
        final context = divContext.variableManager.context;

        if (valueVariable != null) {
          divContext.variableManager
              .updateVariable(valueVariable!, elapsed.inMilliseconds);
        }
        endActions?.forEach(
          (a) async => (await a.resolve(context: context)).execute(divContext),
        );
      },
    );

    // Update clockwork trigger values by variable context update.
    _sub = divContext.variableManager.listen(update);
  }

  Future<void> update(DivVariableContext context) async {
    final duration = await this.duration.resolveValue(context: context);
    final interval = await tickInterval?.resolveValue(context: context);

    _clockwork?.update(
      duration: duration.ms,
      interval: interval?.ms,
    );
  }

  Future<void> dispose() async {
    await _sub?.cancel();
    _sub = null;
    _clockwork?.dispose();
    _clockwork = null;
  }

  @override
  List<Object?> get props => [
        id,
        duration,
        tickInterval,
        tickActions,
        endActions,
        valueVariable,
      ];
}
