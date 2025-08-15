import 'package:divkit/divkit.dart';

class DefaultDivVisibilityActionManager implements DivVisibilityActionManager {
  final Map<String, Map<String, VisibilityActionState>> _map = {};

  @override
  void addOrUpdateActions(
    List<DivVisibilityActionModel> newActionList,
    String elementId,
  ) {
    for (var action in newActionList) {
      final actionId = action.divAction.logId;
      _map.putIfAbsent(
        elementId,
        () => {
          actionId: VisibilityActionState(action),
        },
      );

      final actionState = _map[elementId]?[actionId];
      if (actionState == null) {
        _map[elementId]?[actionId] = VisibilityActionState(action);
      } else if (actionState.divVisibilityAction.divAction !=
          action.divAction) {
        actionState.updateAction(action);
      }
    }
  }

  @override
  void updateActionsStateIfNeed(
    int currentVisibilityPercentage,
    DivContext divContext,
    String elementId,
  ) {
    final actionsIdList = _map[elementId]?.keys ?? [];
    for (final actionsId in actionsIdList) {
      final actionState = (_map[elementId]?[actionsId]);
      if (actionState == null) {
        continue;
      }

      final divVisibilityAction = actionState.divVisibilityAction;
      final actionVisibilityState = actionState.actionVisibilityState;

      final canStart = divVisibilityAction.visibilityPercentage <=
              currentVisibilityPercentage &&
          actionVisibilityState == ActionVisibilityStateType.notStart;

      final canStop = divVisibilityAction.visibilityPercentage >
              currentVisibilityPercentage &&
          actionVisibilityState == ActionVisibilityStateType.wait;

      if (canStart) {
        actionState.startWait(divContext);
      } else if (canStop) {
        actionState.stopWait();
      }
    }
  }

  @override
  bool hasNoEndActions(
    String elementId,
  ) {
    final actionsStateList = _map[elementId]?.values ?? [];
    for (var actionsState in actionsStateList) {
      if (actionsState.actionVisibilityState != ActionVisibilityStateType.end) {
        return true;
      }
    }
    return false;
  }

  @override
  void stopAllWaitIfNeed(
    String elementId,
  ) {
    final actionsStateList = _map[elementId]?.values ?? [];
    for (final actionState in actionsStateList) {
      actionState.stopWait();
    }
  }

  @override
  void dispose() {
    _map.forEach(
      (elementId, _) {
        stopAllWaitIfNeed(elementId);
      },
    );
    _map.clear();
  }
}
