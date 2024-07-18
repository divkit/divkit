import 'package:divkit/src/core/patch/patch.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';

extension PassDivPatch on DivPatch {
  Future<DivPatchModel> resolve({
    required DivVariableContext context,
  }) async =>
      DivPatchModel(
        changes: changes,
        mode: await mode.resolveValue(context: context),
      );
}
