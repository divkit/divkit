import 'package:divkit/divkit.dart';

extension PassDivPatch on DivPatch {
  Future<DivPatchModel> resolve({
    required DivVariableContext context,
  }) async =>
      DivPatchModel(
        changes: changes,
        mode: await mode.resolveValue(context: context),
      );

  DivPatchModel value() => DivPatchModel(
        changes: changes,
        mode: mode.value!,
      );
}
