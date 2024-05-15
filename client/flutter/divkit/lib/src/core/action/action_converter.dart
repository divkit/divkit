import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;

extension PassDivAction on dto.DivAction {
  Future<DivAction> resolve({
    required DivVariableContext context,
  }) async =>
      DivAction(
        url: (await url?.resolveValue(context: context)),
        enabled: (await isEnabled.resolveValue(context: context)),
        typedAction: typed,
        payload: payload,
      );
}
