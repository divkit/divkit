import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivRootModel with EquatableMixin {
  final List<DivDataState> states;
  final String stateId;

  const DivRootModel({
    required this.states,
    required this.stateId,
  });

  static DivRootModel value(
    BuildContext context,
    DivData data,
  ) {
    final state = read<DivContext>(context)!.stateManager;
    state.registerState('root', data.states.first.stateId.toString());

    return DivRootModel(
      stateId: state.states['root']!,
      states: data.states,
    );
  }

  static Stream<DivRootModel> from(
    BuildContext context,
    DivData data,
  ) {
    final variables = watch<DivContext>(context)!.variableManager;
    final state = watch<DivContext>(context)!.stateManager;
    return state.watch<DivRootModel>((states) async {
      final model = DivRootModel(
        stateId: states['root']!,
        states: data.states,
      );

      // To avoid errors in the first frame due to outdated variable
      // context data, needs to preload all the variables used in state.
      await model.state.preload(variables.context.current);

      return model;
    }).distinct();
  }

  DivDataState get state => states.firstWhere(
        (element) => element.stateId.toString() == stateId,
        orElse: () => states.first,
      );

  String get path => stateId;

  @override
  List<Object?> get props => [
        stateId,
        states,
      ];
}
