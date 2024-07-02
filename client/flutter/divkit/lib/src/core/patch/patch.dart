import 'package:divkit/src/generated_sources/div_patch.dart';
import 'package:equatable/equatable.dart';

class DivPatch with EquatableMixin {
  const DivPatch({
    required this.changes,
    this.mode = DivPatchMode.partial,
  });

  final List<DivPatchChange> changes;

  final DivPatchMode mode;

  @override
  List<Object?> get props => [
        changes,
        mode,
      ];
}
