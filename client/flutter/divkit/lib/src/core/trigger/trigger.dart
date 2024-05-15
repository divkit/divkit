import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;
import 'package:equatable/equatable.dart';

class DivTrigger with EquatableMixin {
  final List<dto.DivAction> actions;

  final Expression<bool> condition;

  bool prevConditionResult = false;

  final Expression<dto.DivTriggerMode> mode;

  DivTrigger({
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
