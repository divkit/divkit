import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/clockwork.dart';
import 'package:divkit/src/utils/duration_helper.dart';
import 'package:equatable/equatable.dart';

export 'timer_converter.dart';

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

  void init(DivContext divContext) {
    final context = divContext.variables;

    final duration = this.duration.resolve(context);
    final interval = tickInterval?.resolve(context);

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
      onTick: (elapsed) {
        final context = divContext.variables;

        if (valueVariable != null) {
          divContext.variableManager
              .updateVariable(valueVariable!, elapsed.inMilliseconds);
        }
        tickActions?.forEach(
          (a) => a.resolve(context).execute(divContext),
        );
      },
      onEnd: (elapsed) {
        final context = divContext.variables;

        if (valueVariable != null) {
          divContext.variableManager
              .updateVariable(valueVariable!, elapsed.inMilliseconds);
        }
        endActions?.forEach(
          (a) => a.resolve(context).execute(divContext),
        );
      },
    );

    // Update clockwork trigger values by variable context update.
    _sub = divContext.variableManager.listen(handleUpdate);
  }

  Future<void> handleUpdate(DivVariableContext context) async {
    final duration = this.duration.resolve(context);
    final interval = tickInterval?.resolve(context);

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
