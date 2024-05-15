import 'package:divkit/src/core/timer/timer.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;

extension PassDivTimer on dto.DivTimer {
  DivTimer get pass => DivTimer(
        id: id,
        duration: duration,
        tickInterval: tickInterval,
        tickActions: tickActions,
        endActions: endActions,
        valueVariable: valueVariable,
      );
}
