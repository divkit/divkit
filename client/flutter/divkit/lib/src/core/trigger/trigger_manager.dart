import 'dart:async';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/protocol/div_trigger.dart';

class DefaultDivTriggerManager extends DivTriggerManager {
  final List<DivTriggerModel>? triggers;

  DefaultDivTriggerManager({
    required super.divContext,
    this.triggers,
  });

  @override
  Future<void> handleUpdate(DivVariableContext context) async {
    if (triggers != null) {
      for (final trigger in triggers!) {
        final condition = trigger.condition;

        if (condition is ResolvableExpression &&
            (condition as ResolvableExpression).variables == null) {
          // So if we have no [variables], then  initially resolve expression.
          await condition.resolveValue(context: context);
        }

        final hasCorrectCondition = condition is ResolvableExpression &&
            ((condition as ResolvableExpression).variables?.isNotEmpty ??
                false);

        if (hasCorrectCondition) {
          ResolvableExpression condition =
              trigger.condition as ResolvableExpression;
          final hasUpdate =
              condition.variables?.intersection(context.update).isNotEmpty ??
                  false;

          if (hasUpdate) {
            final mode = await trigger.mode.resolveValue(context: context);
            final condition = await trigger.condition.resolveValue(
              context: context,
            );

            mode.map(
              onCondition: () async {
                if (!trigger.prevConditionResult && condition) {
                  for (final action in trigger.actions) {
                    (await action.resolve(context: context))
                        .execute(divContext);
                  }
                }
              },
              onVariable: () async {
                if (condition) {
                  for (final action in trigger.actions) {
                    (await action.resolve(context: context))
                        .execute(divContext);
                  }
                }
              },
            );

            // Update previous value.
            trigger.prevConditionResult = condition;
          }
        } else {
          logger.error(
            "Trigger does not use variables in condition: ${trigger.condition}",
          );
        }
      }
    }
  }
}
