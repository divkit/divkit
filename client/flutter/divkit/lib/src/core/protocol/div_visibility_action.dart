import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/visibility/models/visibility_action.dart';

abstract class DivVisibilityActionManager {
  /// Start tracking the execution of a new visibility action or update [divAction] of the old one
  void addOrUpdateActions(
    List<DivVisibilityActionModel> newActionList,
    String elementId,
  );

  /// Update the state of the actions depending on how much the element is shown
  void updateActionsStateIfNeed(
    int currentVisibilityPercentage,
    DivContext divContext,
    String elementId,
  );

  /// It will return true if there are actions with any [ActionVisibilityStateType] other than end
  bool hasNoEndActions(
    String elementId,
  );

  /// Resets the waiting for the actions of the element from the id == [elementId]
  void stopAllWaitIfNeed(
    String elementId,
  );

  /// Safely destroy manager.
  void dispose();
}
