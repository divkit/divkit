import 'package:divkit/divkit.dart';

extension DivPatchConvert on DivPatch {
  DivPatchModel convert() => DivPatchModel(
        changes: changes,
        mode: mode.value,
      );
}
