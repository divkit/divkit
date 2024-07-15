import 'package:divkit/src/core/timer/timer.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';

extension PassDivTimer on DivTimer {
  DivTimerModel get pass => DivTimerModel(
        id: id,
        duration: duration,
        tickInterval: tickInterval,
        tickActions: tickActions,
        endActions: endActions,
        valueVariable: valueVariable,
      );
}
