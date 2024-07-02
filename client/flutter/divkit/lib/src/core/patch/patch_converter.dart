import 'package:divkit/src/core/patch/patch.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;

extension PassDivPatch on dto.DivPatch {
  Future<DivPatch> resolve({
    required DivVariableContext context,
  }) async =>
      DivPatch(
        changes: changes,
        mode: await mode.resolveValue(context: context),
      );
}
