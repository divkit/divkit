import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/state/div_id_provider.dart';
import 'package:divkit/src/generated_sources/div_state.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivStateModel with EquatableMixin {
  final List<DivStateState> states;
  final String? divId;
  final String? stateId;
  final String? defaultStateId;

  const DivStateModel({
    required this.states,
    this.divId,
    this.stateId,
    this.defaultStateId,
  });

  static Stream<DivStateModel> from(
    BuildContext context,
    DivState data,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    final inheritedDivId = InheritedDivId.of(context);

    String divId;
    if (inheritedDivId != null) {
      if (data.divId != null) {
        divId = '$inheritedDivId/${data.divId}';
      } else if (data.id != null) {
        divId = '$inheritedDivId/${data.id}';
      } else {
        divId = inheritedDivId;
      }
    } else {
      divId = data.divId ?? data.id ?? '';
    }

    final state = DivKitProvider.watch<DivContext>(context)!.stateManager;
    state.registerState(divId);

    return state
        .watch<DivStateModel>(
          (context) async => DivStateModel(
            divId: divId,
            stateId: context[divId],
            defaultStateId: await data.defaultStateId?.resolveValue(
              context: variables.context,
            ),
            states: data.states,
          ),
        )
        .distinct();
  }

  DivStateState? get state {
    if (states.isEmpty) {
      return null;
    }

    return states.firstWhere(
      (element) {
        if (stateId != null) {
          return element.stateId == stateId;
        }

        return element.stateId == defaultStateId;
      },
      orElse: () => states.first,
    );
  }

  String get path {
    if (divId != null) {
      return '$divId/${stateId ?? defaultStateId}';
    }
    return stateId ?? defaultStateId ?? '';
  }

  @override
  List<Object?> get props => [
        divId,
        stateId,
        defaultStateId,
        states,
      ];
}
