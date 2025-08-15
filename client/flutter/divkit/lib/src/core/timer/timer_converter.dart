import 'package:divkit/divkit.dart';

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
