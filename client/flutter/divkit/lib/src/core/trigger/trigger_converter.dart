import 'package:divkit/src/core/trigger/trigger.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;

extension PassDivTrigger on dto.DivTrigger {
  DivTrigger get pass => DivTrigger(
        actions: actions,
        condition: condition,
        mode: mode,
      );
}
