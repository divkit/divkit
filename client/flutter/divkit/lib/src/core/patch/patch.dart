import 'package:divkit/src/generated_sources/div_patch.dart';
import 'package:equatable/equatable.dart';

class DivPatchModel with EquatableMixin {
  const DivPatchModel({
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
