import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/state/state_id.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/cupertino.dart';

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

extension DivStateConverter on DivState {
  String resolveDivId(BuildContext context) {
    final inheritedDivId = read<DivStateId>(context)?.id;

    String divId;
    if (inheritedDivId != null) {
      if (this.divId != null) {
        divId = '$inheritedDivId/${this.divId}';
      } else if (id != null) {
        divId = '$inheritedDivId/$id';
      } else {
        divId = inheritedDivId;
      }
    } else {
      divId = this.divId ?? id ?? '';
    }
    return divId;
  }

  DivStateModel resolve(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    final state = divContext.stateManager;
    final variables = divContext.variables;

    final resolvedId = resolveDivId(context);

    return DivStateModel(
      divId: resolvedId,
      stateId: state.states[resolvedId],
      defaultStateId: defaultStateId?.resolve(variables),
      states: states,
    );
  }
}
