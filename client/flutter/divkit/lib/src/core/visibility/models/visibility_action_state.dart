import 'dart:async';

import 'package:divkit/src/core/visibility/models/visibility_action.dart';

import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/visibility/models/action_visibility_state_type.dart';

class VisibilityActionState {
  DivVisibilityActionModel divVisibilityAction;

  ActionVisibilityStateType actionVisibilityState =
      ActionVisibilityStateType.notStart;
  Timer? _timer;

  VisibilityActionState(
    this.divVisibilityAction,
  );

  /// Start waiting for [visibilityDuration] milliseconds to pass to perform the action
  void startWait(DivContext divContext) {
    actionVisibilityState = ActionVisibilityStateType.wait;
    _timer = Timer(
      Duration(
        milliseconds: divVisibilityAction.visibilityDuration,
      ),
      () async {
        actionVisibilityState = ActionVisibilityStateType.end;
        await divVisibilityAction.divAction.execute(divContext);
      },
    );
  }

  /// Resetting the action execution expectations
  void stopWait() {
    _timer?.cancel();
    _timer = null;
    actionVisibilityState = ActionVisibilityStateType.notStart;
  }

  /// Updating the action
  void updateAction(DivVisibilityActionModel newAction) {
    divVisibilityAction = newAction;
  }
}
