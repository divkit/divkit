import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/generated_sources/div_data.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivCardStateModel with EquatableMixin {
  final List<DivDataState> states;
  final String stateId;

  const DivCardStateModel({
    required this.states,
    required this.stateId,
  });

  static Stream<DivCardStateModel> from(
    BuildContext context,
    DivData data,
  ) {
    final state = DivKitProvider.watch<DivContext>(context)!.stateManager;
    state.registerState('root', data.states.first.stateId.toString());

    return state
        .watch<DivCardStateModel>(
          (context) async => DivCardStateModel(
            stateId: context['root']!,
            states: data.states,
          ),
        )
        .distinct();
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
