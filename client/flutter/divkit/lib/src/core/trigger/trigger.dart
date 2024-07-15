import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:equatable/equatable.dart';

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
