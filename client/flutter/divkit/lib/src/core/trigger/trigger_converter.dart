import 'package:divkit/src/core/trigger/trigger.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';

extension PassDivTrigger on DivTrigger {
  DivTriggerModel get pass => DivTriggerModel(
        actions: actions,
        condition: condition,
        mode: mode,
      );
}
