import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/duration_helper.dart';
import 'package:fake_async/fake_async.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  late DivVisibilityActionManager actionManager;
  late DivRootContext divContext;
  const elementId1 = 'element1';
  const elementId2 = 'element2';
  const logId = 'id';

  setUp(
    () {
      actionManager = DefaultDivVisibilityActionManager();
      divContext = DivRootContext();
      divContext.actionHandler = DefaultDivActionHandler();
    },
  );

  group(
    'DivVisibilityActionManager test',
    () {
      test(
        'When there are no actions, there are no unfinished actions',
        () {
          final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
          expect(hasNoEndActions, false);
        },
      );

      test(
        'When there is an unfinished action check hasNoEndActions is true',
        () {
          actionManager.addOrUpdateActions(
            [
              const DivVisibilityActionModel(
                divAction: DivActionModel(
                  enabled: true,
                  logId: logId,
                ),
              ),
            ],
            elementId1,
          );
          final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
          expect(hasNoEndActions, true);
        },
      );

      test(
        'When there is an unfinished action check hasNoEndActions is true',
        () {
          actionManager.addOrUpdateActions(
            [
              const DivVisibilityActionModel(
                divAction: DivActionModel(
                  enabled: true,
                  logId: logId,
                ),
              ),
            ],
            elementId1,
          );

          final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
          expect(hasNoEndActions, true);
        },
      );

      test(
          'If the element is visible at less than visibilityPercentage, the action is not performed',
          () {
        fakeAsync(
          (async) {
            actionManager.addOrUpdateActions(
              [
                const DivVisibilityActionModel(
                  visibilityPercentage: 20,
                  visibilityDuration: 500,
                  divAction: DivActionModel(
                    enabled: true,
                    logId: logId,
                  ),
                ),
              ],
              elementId1,
            );

            actionManager.updateActionsStateIfNeed(
              10,
              divContext,
              elementId1,
            );

            async.elapse(
              1000.ms,
            );

            final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
            expect(hasNoEndActions, true);
          },
        );
      });

      test(
        'If the element is visible on the visibilityPercentage, the action is executed',
        () {
          fakeAsync(
            (async) {
              actionManager.addOrUpdateActions(
                [
                  const DivVisibilityActionModel(
                    visibilityPercentage: 20,
                    visibilityDuration: 500,
                    divAction: DivActionModel(
                      enabled: true,
                      logId: logId,
                    ),
                  ),
                ],
                elementId1,
              );

              actionManager.updateActionsStateIfNeed(
                20,
                divContext,
                elementId1,
              );

              async.elapse(
                1000.ms,
              );

              final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
              expect(hasNoEndActions, false);
            },
          );
        },
      );

      test(
        'If the element is visible by more than a visibilityPercentage, the action is executed',
        () {
          fakeAsync(
            (async) {
              actionManager.addOrUpdateActions(
                [
                  const DivVisibilityActionModel(
                    visibilityPercentage: 20,
                    visibilityDuration: 500,
                    divAction: DivActionModel(
                      enabled: true,
                      logId: logId,
                    ),
                  ),
                ],
                elementId1,
              );

              actionManager.updateActionsStateIfNeed(
                50,
                divContext,
                elementId1,
              );

              async.elapse(
                1000.ms,
              );

              final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
              expect(hasNoEndActions, false);
            },
          );
        },
      );

      test(
        'If the action is shown at the desired percentage, but the element id does not match, we do not perform the action',
        () {
          fakeAsync(
            (async) {
              actionManager.addOrUpdateActions(
                [
                  const DivVisibilityActionModel(
                    visibilityPercentage: 20,
                    visibilityDuration: 500,
                    divAction: DivActionModel(
                      enabled: true,
                      logId: logId,
                    ),
                  ),
                ],
                elementId2,
              );

              actionManager.updateActionsStateIfNeed(
                50,
                divContext,
                elementId1,
              );

              async.elapse(
                1000.ms,
              );

              final hasNoEndActions = actionManager.hasNoEndActions(elementId2);
              expect(hasNoEndActions, true);
            },
          );
        },
      );

      test(
        'If the action is in progress - hasNoEndActions is true',
        () {
          fakeAsync(
            (async) {
              actionManager.addOrUpdateActions(
                [
                  const DivVisibilityActionModel(
                    visibilityPercentage: 20,
                    visibilityDuration: 500,
                    divAction: DivActionModel(
                      enabled: true,
                      logId: logId,
                    ),
                  ),
                ],
                elementId1,
              );

              actionManager.updateActionsStateIfNeed(
                50,
                divContext,
                elementId1,
              );

              final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
              expect(hasNoEndActions, true);
            },
          );
        },
      );

      test(
        'If stopAllWaitIfNeed is called while waiting for an action to be executed, the action is not executed',
        () {
          fakeAsync(
            (async) {
              actionManager.addOrUpdateActions(
                [
                  const DivVisibilityActionModel(
                    visibilityPercentage: 20,
                    visibilityDuration: 500,
                    divAction: DivActionModel(
                      enabled: true,
                      logId: logId,
                    ),
                  ),
                ],
                elementId1,
              );

              actionManager.updateActionsStateIfNeed(
                50,
                divContext,
                elementId1,
              );

              async.elapse(
                400.ms,
              );
              actionManager.stopAllWaitIfNeed(
                elementId1,
              );
              async.elapse(
                500.ms,
              );

              final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
              expect(hasNoEndActions, true);
            },
          );
        },
      );

      test(
        'If stopAllWaitIfNeed is called while waiting for an action to be executed, but elemetId did not match the action is executed',
        () {
          fakeAsync(
            (async) {
              actionManager.addOrUpdateActions(
                [
                  const DivVisibilityActionModel(
                    visibilityPercentage: 20,
                    visibilityDuration: 500,
                    divAction: DivActionModel(
                      enabled: true,
                      logId: logId,
                    ),
                  ),
                ],
                elementId1,
              );

              actionManager.updateActionsStateIfNeed(
                50,
                divContext,
                elementId1,
              );

              async.elapse(
                400.ms,
              );
              actionManager.stopAllWaitIfNeed(
                elementId2,
              );
              async.elapse(
                500.ms,
              );

              final hasNoEndActions = actionManager.hasNoEndActions(elementId1);
              expect(hasNoEndActions, false);
            },
          );
        },
      );

      test(
        'There are no unfulfilled actions after calling the dispose',
        () {
          actionManager.addOrUpdateActions(
            [
              const DivVisibilityActionModel(
                visibilityPercentage: 20,
                visibilityDuration: 500,
                divAction: DivActionModel(
                  enabled: true,
                  logId: logId,
                ),
              ),
            ],
            elementId1,
          );

          bool hasNoEndActions = actionManager.hasNoEndActions(elementId1);
          expect(hasNoEndActions, true);

          actionManager.dispose();
          hasNoEndActions = actionManager.hasNoEndActions(elementId1);
          expect(hasNoEndActions, false);
        },
      );
    },
  );

  group(
    'VisibilityActionModel test',
    () {
      test(
        'The new VisibilityActionModel has the following state notStart',
        () {
          const action = DivVisibilityActionModel(
            visibilityPercentage: 20,
            visibilityDuration: 500,
            divAction: DivActionModel(
              enabled: true,
              logId: logId,
            ),
          );
          final visibilityActionModel = VisibilityActionState(
            action,
          );
          expect(
            visibilityActionModel.actionVisibilityState,
            ActionVisibilityStateType.notStart,
          );
        },
      );

      test(
        'The running VisibilityActionModel has the following state wait',
        () {
          const action = DivVisibilityActionModel(
            visibilityPercentage: 20,
            visibilityDuration: 500,
            divAction: DivActionModel(
              enabled: true,
              logId: logId,
            ),
          );
          final visibilityActionModel = VisibilityActionState(
            action,
          );
          visibilityActionModel.startWait(divContext);
          expect(
            visibilityActionModel.actionVisibilityState,
            ActionVisibilityStateType.wait,
          );
        },
      );

      test(
        'The end VisibilityActionModel has the following state end',
        () {
          fakeAsync(
            (async) {
              const action = DivVisibilityActionModel(
                visibilityPercentage: 20,
                visibilityDuration: 500,
                divAction: DivActionModel(
                  enabled: true,
                  logId: logId,
                ),
              );
              final visibilityActionModel = VisibilityActionState(
                action,
              );
              visibilityActionModel.startWait(divContext);
              async.elapse(
                1000.ms,
              );
              expect(
                visibilityActionModel.actionVisibilityState,
                ActionVisibilityStateType.end,
              );
            },
          );
        },
      );

      test(
        'If stopWait was called while waiting, the state of the action is notStart',
        () {
          fakeAsync(
            (async) {
              const action = DivVisibilityActionModel(
                visibilityPercentage: 20,
                visibilityDuration: 500,
                divAction: DivActionModel(
                  enabled: true,
                  logId: logId,
                ),
              );
              final visibilityActionModel = VisibilityActionState(
                action,
              );
              visibilityActionModel.startWait(divContext);
              async.elapse(
                400.ms,
              );
              visibilityActionModel.stopWait();
              async.elapse(
                500.ms,
              );
              expect(
                visibilityActionModel.actionVisibilityState,
                ActionVisibilityStateType.notStart,
              );
            },
          );
        },
      );
    },
  );
}
