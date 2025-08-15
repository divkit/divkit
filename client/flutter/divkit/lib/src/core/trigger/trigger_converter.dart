import 'package:divkit/divkit.dart';

extension PassDivTrigger on DivTrigger {
  DivTriggerModel get pass => DivTriggerModel(
        actions: actions,
        condition: condition,
        mode: mode,
      );
}
