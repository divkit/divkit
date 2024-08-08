import 'package:divkit/src/core/action/models/action.dart';
import 'package:equatable/equatable.dart';

class DivVisibilityActionModel with EquatableMixin {
  const DivVisibilityActionModel({
    required this.divAction,
    this.visibilityDuration = 800,
    this.visibilityPercentage = 50,
  });

  final DivActionModel divAction;

  final int visibilityDuration;

  final int visibilityPercentage;

  @override
  List<Object?> get props => [
        divAction,
        visibilityDuration,
        visibilityPercentage,
      ];
}
