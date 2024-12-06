import 'package:divkit/divkit.dart';

extension DivPatchConvert on DivPatch {
  DivPatchModel resolve(DivVariableContext context) => DivPatchModel(
        changes: changes,
        mode: mode.resolve(context),
      );
}
