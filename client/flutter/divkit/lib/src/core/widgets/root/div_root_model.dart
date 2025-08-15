import 'package:collection/collection.dart';
import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';

class DivRootModel with EquatableMixin {
  final List<DivDataState> states;
  final String? stateId;

  const DivRootModel({
    required this.states,
    required this.stateId,
  });

  DivDataState? get state =>
      states.firstWhereOrNull(
        (element) => element.stateId.toString() == stateId,
      ) ??
      (states.isNotEmpty ? states.first : null);

  String? get path => stateId;

  @override
  List<Object?> get props => [
        stateId,
        states,
      ];
}

extension DivDataStateBinder on DivData {
  DivRootModel bind(String? stateId) => DivRootModel(
        stateId: stateId,
        states: states,
      );
}
