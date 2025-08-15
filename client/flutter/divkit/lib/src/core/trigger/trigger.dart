import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';

export 'trigger_converter.dart';

class DivTriggerModel with EquatableMixin {
  final List<DivAction> actions;

  final Expression<bool> condition;

  bool prevConditionResult = false;

  final Expression<DivTriggerMode> mode;

  DivTriggerModel({
    required this.actions,
    required this.condition,
    required this.mode,
  });

  @override
  List<Object?> get props => [
        actions,
        condition,
        mode,
        prevConditionResult,
      ];
}
