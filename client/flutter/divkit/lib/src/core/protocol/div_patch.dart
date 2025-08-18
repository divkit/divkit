import 'package:divkit/divkit.dart';

abstract class DivPatchManager {
  const DivPatchManager();

  /// Applies changes in the patch format to the [DivData].
  ///
  /// Replaces the [div]s of the root structure with new ones by id.
  Future<bool> applyPatch(DivPatchModel patch);
}
