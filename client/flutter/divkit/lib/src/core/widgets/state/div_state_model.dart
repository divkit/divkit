import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/state/state_id.dart';
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

  static DivStateModel? value(
    BuildContext context,
    DivState data,
  ) {
    try {
      final inheritedDivId = read<DivStateId>(context)?.id;

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

      final state = read<DivContext>(context)!.stateManager;
      state.registerState(divId);

      return DivStateModel(
        divId: divId,
        stateId: state.states[divId],
        defaultStateId: data.defaultStateId?.value!,
        states: data.states,
      );
    } catch (e, st) {
      logger.warning(
        'Expression cache is corrupted! Instant rendering is not available for div',
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  static Stream<DivStateModel> from(
    BuildContext context,
    DivState data,
  ) {
    final variables = watch<DivContext>(context)!.variableManager;
    final inheritedDivId = watch<DivStateId>(context)?.id;

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

    final state = watch<DivContext>(context)!.stateManager;
    return state.watch<DivStateModel>((states) async {
      // To avoid errors in the first frame due to outdated variable
      // context data, needs to preload all the variables used in div.
      await data.preload(variables.context.current);
      return DivStateModel(
        divId: divId,
        stateId: states[divId],
        defaultStateId: await data.defaultStateId?.resolveValue(
          context: variables.context,
        ),
        states: data.states,
      );
    }).distinct();
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
