import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/timer/timer.dart';
import 'package:divkit/src/core/variable/variable_storage.dart';
import 'package:divkit/src/core/variable/variable_storage_manager.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('Timer test', () {
    final divContext = DivRootContext();
    divContext.variableManager = DefaultDivVariableManager(
      storage: DefaultDivVariableStorage(),
    );

    test("clockwork can not sync init", () async {
      final timer = DivTimer(
        id: 'Test',
        duration: const ValueExpression(1000),
        endActions: [
          const DivAction(
            logId: ValueExpression(''),
            payload: {
              'print_status': 'end',
            },
          ),
        ],
      );

      timer.init(divContext);
      expect(
        () => timer.clockwork.start(),
        throwsA(
          isA<TypeError>(),
        ),
      );
    });

    test("clockwork has async init", () async {
      final timer = DivTimer(
        id: 'Test',
        duration: const ValueExpression(1000),
        endActions: [
          const DivAction(
            logId: ValueExpression(''),
            payload: {
              'print_status': 'end',
            },
          ),
        ],
      );

      await timer.init(divContext);
      timer.clockwork.start();
    });
  });
}
